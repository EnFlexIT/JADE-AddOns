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

import jade.content.ContentElement;
import jade.content.onto.Ontology;
import jade.util.Logger;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.logging.Level;


public class MapperBasedActionBuilder extends ActionBuilder {
	
	private static Logger logger = Logger.getMyLogger(MapperBasedActionBuilder.class.getName());

	private Method method;
	private Object mapperObj;

	public MapperBasedActionBuilder(Ontology ontoService, Ontology ontoAgent, String actionName, Object mapperObj, Method method) {
		super(ontoService, ontoAgent, actionName);
		
		this.method = method;
		this.mapperObj = mapperObj;
	}

	public ContentElement getAgentAction(LinkedHashMap<String, ParameterInfo> soapParams) throws Exception {
		Object[] parameterValues = new Object[0];
		String parameterList = "";

		// Prepare mapper-method parameters
		if (soapParams != null) {
				
			Collection<ParameterInfo> parametersInfo = getParameters().values();
			parameterValues = new Object[parametersInfo.size()];
			int index = 0;
			for (ParameterInfo parameterInfo : parametersInfo) {
				
				ParameterInfo soapParameter;
				try {
					soapParameter = getSoapParameter(soapParams, parameterInfo);
				} catch(Exception e) {
					logger.log(Level.SEVERE, e.getMessage());
					throw e;
				}
				
				try {
					Object javaValue;
					if (soapParameter != null) {
						parameterList += parameterInfo.getSchema().getTypeName()+",";
						javaValue = ontoService.toObject(soapParameter.getValue());
					} else {
						parameterList += "null,";
						javaValue = null;
					}
					parameterValues[index++] = adjustValue(javaValue, parameterInfo.getParameterClass());
				} catch(Exception e) {
					logger.log(Level.SEVERE, "Method "+method.getName()+", param "+parameterInfo.getName()+" error decoding value");
					throw e;
				}
			}
			
			if (parameterList.endsWith(",")) {
				parameterList = parameterList.substring(0, parameterList.length()-1);
			}
		}
		
		ContentElement actionObj = null;
		try {
			// Invoke mapper method
			actionObj = (ContentElement)method.invoke(mapperObj, parameterValues);
			logger.log(Level.FINE, "Invoked method "+method.getName()+"("+parameterList+") in mapper");
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Method "+method.getName()+"("+parameterList+") error invocation");
			throw e;
		}
		
		return actionObj;
	}

	private ParameterInfo getSoapParameter(LinkedHashMap<String, ParameterInfo> soapParams, ParameterInfo operationParam) throws Exception {

		// Try to get soap parameter
		ParameterInfo soapParam = soapParams.get(operationParam.getName());
		if (soapParam != null) {
			return soapParam;
		}

		// If not found check mandatory
		if (operationParam.isMandatory()) {
			throw new Exception("Mapper method "+method.getName()+", mandatory param "+operationParam.getName()+" not found in soap request");
		}
		
		// Optional parameter not found 
		return null;
	}
}
