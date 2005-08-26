/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.launcher.Subscriptions;

import jade.content.abs.AbsAgentAction;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPredicate;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.BasicOntology;
import jade.content.onto.OntologyException;
import jade.core.*;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import jade.tools.ascml.absmodel.AbstractRunnable;
import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.launcher.*;
import jade.tools.ascml.onto.*;

public class StatusSubscriptionInitiator extends SubscriptionInitiator {

	AgentLauncher al;
	AbsModel model;

	public StatusSubscriptionInitiator(AgentLauncher al, ACLMessage msg, AbsModel model) {
		super(al, msg);
		this.model=model;
		this.al=al;
	}	
	
	@Override
	protected void handleInform(ACLMessage inform) {		
		AbsPredicate absEquals;
		try {
			absEquals = (AbsPredicate)  al.getContentManager().extractAbsContent(inform);
			AbsObject absStatus = absEquals.getAbsObject(SLVocabulary.EQUALS_RIGHT);
			Status ms = (Status) ASCMLOntology.getInstance().toObject(absStatus);
			model.setModelStatus(ms);
		} catch (Exception e) {
		}		
		super.handleInform(inform);
	}

}
