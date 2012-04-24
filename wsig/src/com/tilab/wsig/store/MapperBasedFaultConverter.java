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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.tilab.wsig.soap.SOAPException;

import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;
import jade.lang.acl.ACLMessage;

public class MapperBasedFaultConverter extends FaultBuilder {

	private static final String GET_FAULT_CODE = "getFaultCode";
	private static final String GET_FAULT_STRING = "getFaultString";
	private static final String GET_FAULT_ACTOR = "getFaultActor";
	
	private Object mapperObj;
	private Class faultConverterClass;
	private Constructor faultConverterConstructor;
	
	private String faultCode = SOAPException.FAULT_CODE_SERVER;
	private String faultString = "";
	private String faultActor = SOAPException.FAULT_ACTOR_WSIG;
	
	
	public MapperBasedFaultConverter(Object mapperObj, Class faultConverterClass, Ontology ontoService, Ontology ontoAgent, String actionName) throws Exception {
		super(ontoService, ontoAgent, actionName);

		this.mapperObj = mapperObj;
		this.faultConverterClass = faultConverterClass;

		// Check and get FaultConverter constructor
		Constructor[] faultConverterConstructors = faultConverterClass.getConstructors();
		if (faultConverterConstructors.length != 1) {
			throw new Exception("FaultConverter with more than one constructor defined in "+faultConverterClass.getName());
		}
		this.faultConverterConstructor = faultConverterConstructors[0];

		// The constructor con be of the form:
		// Xxx(ACLMessage message)
		Class[] constructorParameterClasses = faultConverterConstructor.getParameterTypes();
		if (constructorParameterClasses == null || constructorParameterClasses.length != 2 || constructorParameterClasses[1] != ACLMessage.class) {
			throw new Exception("FaultConverter constructor with no ACLMessage parameter defined in "+faultConverterClass.getName());
		}
	}

	@Override
	public void prepare(ACLMessage message) throws Exception {
		// Instance FaultConverter
		Object faultConverterObj = faultConverterConstructor.newInstance(mapperObj, message);
		
		// Get value of method getFaultCode()
		try {
			Method getFaultCodeMethod = faultConverterClass.getDeclaredMethod(GET_FAULT_CODE, null);
			if (getFaultCodeMethod.getReturnType() == String.class) {
				faultCode = (String)getFaultCodeMethod.invoke(faultConverterObj, null);
			}
		} catch(NoSuchMethodException e) {
			// getFaultCode method not present -> use default
		}
			
		// Get value of method getFaultString()
		try {
			Method getFaultStringMethod = faultConverterClass.getDeclaredMethod(GET_FAULT_STRING, null);
			if (getFaultStringMethod.getReturnType() == String.class) {
				faultString = (String)getFaultStringMethod.invoke(faultConverterObj, null);
			}
		} catch(NoSuchMethodException e) {
			// getFaultString method not present -> use default
		}
		
		// Get value of method getFaultActor()
		try {
			Method getFaultActorMethod = faultConverterClass.getDeclaredMethod(GET_FAULT_ACTOR, null);
			if (getFaultActorMethod.getReturnType() == String.class) {
				faultActor = (String)getFaultActorMethod.invoke(faultConverterObj, null);
			}
		} catch(NoSuchMethodException e) {
			// getFaultActor method not present -> use default
		}
	}
	
	@Override
	public String getFaultString() {
		return faultString;
	}

	@Override
	public String getFaultCode() {
		return faultCode;
	}

	@Override
	public String getFaultActor() {
		return faultActor;
	}

	@Override
	public ParameterInfo getDetailValue() throws Exception {
		// TODO define how to manage (jjj)
		return null;
	}

	@Override
	public ObjectSchema getDetailSchema() throws Exception {
		// TODO define how to manage (jjj)
		return null;
	}
}
