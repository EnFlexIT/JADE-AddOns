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

import jade.content.onto.AggregateHelper;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class Builder {
	protected static Logger log = Logger.getLogger(Builder.class.getName());

	protected Ontology onto;
	protected String actionName;
	protected Map<String, ParameterInfo> parameters = new HashMap<String, ParameterInfo>();

	public Builder(Ontology onto, String actionName) {
		this.onto = onto;
		this.actionName = actionName;
	}

	public Ontology getOntology() {
		return onto;
	}

	public String getActionName() {
		return actionName;
	}
	
	public Map<String, ParameterInfo> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, ParameterInfo> parameters) {
		this.parameters = parameters;
	}

	public AgentActionSchema getActionSchema() throws OntologyException {
		return (AgentActionSchema) onto.getSchema(actionName);
	}
	
	public static Object adjustValue(Object value, Class<?> destClass) throws Exception {
		value = BasicOntology.adjustPrimitiveValue(value, destClass);
		value = AggregateHelper.adjustAggregateValue(value, destClass);
		return value;
	}
	
	public static boolean isCollection(Class parameterClass) {
		return 	parameterClass.isArray() ||
		 		Collection.class.isAssignableFrom(parameterClass) ||
		 		jade.util.leap.Collection.class.isAssignableFrom(parameterClass);		
	}

	public static boolean isSequence(Class parameterClass) {
		return 	parameterClass.isArray() ||
				List.class.isAssignableFrom(parameterClass) ||
		 		jade.util.leap.List.class.isAssignableFrom(parameterClass);		
	}
}