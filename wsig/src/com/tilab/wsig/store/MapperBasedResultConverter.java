/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
package com.tilab.wsig.store;

import jade.content.abs.AbsObject;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.AggregateHelper;
import jade.content.onto.Ontology;
import jade.content.schema.AggregateSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.TermSchema;
import jade.content.schema.facets.JavaTypeFacet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MapperBasedResultConverter extends ResultBuilder {

	private Object mapperObj;
	private Constructor resultConverterConstructor;
	private Class constructorParameterValueClass;
	private String mapperClassName;
	
	public MapperBasedResultConverter(Object mapperObj, Class mapperResultConverterClass, Ontology ontoService, Ontology ontoAgent, String actionName) throws Exception {
		super(ontoService, ontoAgent, actionName);

		this.mapperObj = mapperObj;
		this.mapperClassName = mapperResultConverterClass.getSimpleName();

		// Check and get ResultConverter constructor
		Constructor[] resultConverterConstructors = mapperResultConverterClass.getConstructors();
		if (resultConverterConstructors.length != 1) {
			throw new Exception("ResultConverter with more than one constructor defined in "+mapperResultConverterClass.getName());
		}
		this.resultConverterConstructor = resultConverterConstructors[0];

		// Check and get ResultConverter constructor first parameter
		// The constructor con be of the form:
		// Xxx(Yyy value)
		// Xxx(Yyy value, ACLMessage message)
		Class[] constructorParameterClasses = resultConverterConstructor.getParameterTypes();
		if (constructorParameterClasses == null || (constructorParameterClasses.length !=2 && constructorParameterClasses.length !=3)) {
			throw new Exception("ResultConverter constructor with no one parameter defined in "+mapperResultConverterClass.getName());
		}

		constructorParameterValueClass = constructorParameterClasses[1];
	}

	public List<ParameterInfo> getOperationResultValues(OperationResult opResult) throws Exception {
		
		// Convert action result in object
		Object value = ontoAgent.toObject(opResult.getValue());
		value = adjustValue(value, constructorParameterValueClass);
		
		// Try to create ResultConverter with constructor (Object value, ACLMessage message)
		Object resultConverterObj;
		try {
			resultConverterObj = resultConverterConstructor.newInstance(mapperObj, value, opResult.getMessage());
		} catch(Exception e) {
			// Try to create ResultConverter with constructor (Object value)
			resultConverterObj = resultConverterConstructor.newInstance(mapperObj, value);
		}
		
		// Prepare parameters
		List<ParameterInfo> operationResultValues = new ArrayList<ParameterInfo>();
		Iterator<ParameterInfo> parameterInfosIt = getParameters().values().iterator();
		while(parameterInfosIt.hasNext()) {
			ParameterInfo parameterInfo = parameterInfosIt.next();

			// Get parameter obj value (invoke method in result converter) 
			Method parameterMethod = parameterInfo.getMapperMethod();
			Object parameterValueObj = parameterMethod.invoke(resultConverterObj, null);
			
			// Check mandatory
			if (parameterValueObj == null && parameterInfo.isMandatory()) {
				throw new Exception("Mandatory parameter "+parameterInfo.getName()+" with null value");
			}
			 
			// Convert parameter obj value in abs
			if (parameterValueObj != null) {
				
				// For collection type must convert in leap
				if (isCollection(parameterValueObj.getClass())) {
					Class destClass;
					if (isSequence(parameterValueObj.getClass())) {
						destClass = jade.util.leap.List.class;
					} else {
						destClass = jade.util.leap.Set.class;
					}
					parameterValueObj = AggregateHelper.adjustAggregateValue(parameterValueObj, destClass);
				}

				AbsObject parameterValueAbs = ontoService.fromObject(parameterValueObj);
				parameterInfo.setValue(parameterValueAbs);
				operationResultValues.add(parameterInfo);
			}
		}		
		
		return operationResultValues;
	}

	public ObjectSchema getResponseSchema() throws Exception {
		ConceptSchema operationResultSchema = new ConceptSchema(mapperClassName);
		
		Iterator<ParameterInfo> parameterInfosIt = getParameters().values().iterator();
		while(parameterInfosIt.hasNext()) {
			ParameterInfo pi = parameterInfosIt.next();
			
			TermSchema parameterSchema = pi.getSchema();
			if (parameterSchema instanceof AggregateSchema) {
			
				operationResultSchema.add(pi.getName(), pi.getElementsSchema(), pi.getMinCard(), pi.getMaxCard(), pi.getSchema().getTypeName());
			} else {
				
				operationResultSchema.add(pi.getName(), pi.getSchema(), pi.isMandatory()?ObjectSchema.MANDATORY:ObjectSchema.OPTIONAL);
				
				// Manage preserve java type
				Class<?> paramClass = pi.getMapperMethod().getReturnType();
				if ("true".equalsIgnoreCase(System.getProperty(SLCodec.PRESERVE_JAVA_TYPES)) && 
						(	paramClass == Long.class || 
							paramClass == long.class || 
							paramClass == Double.class || 
							paramClass == double.class)) {
					operationResultSchema.addFacet(pi.getName(), new JavaTypeFacet(paramClass.getName()));
				}
			}
		}
		
		return operationResultSchema;
	}
}
