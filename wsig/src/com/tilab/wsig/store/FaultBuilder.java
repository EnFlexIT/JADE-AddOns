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

import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;
import jade.lang.acl.ACLMessage;

public abstract class FaultBuilder extends Builder {

	public abstract String getFaultString();
	public abstract String getFaultCode();
	public abstract String getFaultActor();
	public abstract ParameterInfo getDetailValue() throws Exception;
	public abstract ObjectSchema getDetailSchema() throws Exception;

	public abstract void prepare(ACLMessage message) throws Exception;
	
	public FaultBuilder(Ontology ontoService, Ontology ontoAgent, String actionName) {
		super(ontoService, ontoAgent, actionName);
	}
}
