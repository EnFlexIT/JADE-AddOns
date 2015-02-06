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

import com.tilab.wsig.soap.SOAPException;

import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;
import jade.lang.acl.ACLMessage;


public class OntologyBasedFaultConverter extends FaultBuilder {

	protected ACLMessage message;
	
	public OntologyBasedFaultConverter(Ontology ontoService, Ontology ontoAgent, String actionName) {
		super(ontoService, ontoAgent, actionName);
	}

	@Override
	public void prepare(ACLMessage message) throws Exception {
		this.message = message;
	}
	
	@Override
	public String getFaultString() {
		return message != null ? message.getContent() : null;
	}

	@Override
	public String getFaultCode() {
		return SOAPException.FAULT_CODE_SERVER;
	}

	@Override
	public String getFaultActor() {
		return message != null ? message.getSender().getName(): null;
	}

	@Override
	public ParameterInfo getDetailValue() throws Exception {
		return null;
	}

	@Override
	public ObjectSchema getDetailSchema() throws Exception {
		return null;
	}
}
