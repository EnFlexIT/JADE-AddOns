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
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsHelper;
import jade.content.abs.AbsObject;
import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;

import java.util.LinkedHashMap;

public class OntologyBasedActionBuilder extends ActionBuilder {

	public OntologyBasedActionBuilder(Ontology ontoService, Ontology ontoAgent, String actionName) {
		super(ontoService, ontoAgent, actionName);
	}

	public ContentElement getAgentAction(LinkedHashMap<String, ParameterInfo> soapParams) throws Exception {

		ObjectSchema schema = (ObjectSchema)ontoService.getSchema(getActionName());
		AbsContentElement actionAbsObj = (AbsContentElement) schema.newInstance();
		if (soapParams != null) {
			for (ParameterInfo param : soapParams.values()) {
				String slotName = param.getName();
				AbsObject slotValue = param.getValue();
				AbsHelper.setAttribute(actionAbsObj, slotName, slotValue);
			}
		}
		return actionAbsObj;
	}
}
