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


package jade.tools.ascml.launcher.behaviours;

import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.abs.AbsVariable;
import jade.content.lang.sl.SL2Vocabulary;
import jade.content.onto.OntologyException;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.exceptions.ResourceNotFoundException;
import jade.tools.ascml.launcher.*;
import jade.tools.ascml.onto.*;
public class GetStatusRequestListener extends SimpleAchieveREResponder {
	
	private AgentLauncher al;
	private String fqn;
	private AbsIRE absIota;
	
	public GetStatusRequestListener(AgentLauncher al, MessageTemplate mt) {
		super(al, mt);
		this.al = al;
		fqn = "";
	}
	
	protected ACLMessage prepareResponse(ACLMessage request)
	throws NotUnderstoodException, RefuseException {
		fqn = "";
		ACLMessage response = request.createReply();
		response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		absIota = null;
		try {
			absIota = (AbsIRE) al.getContentManager().extractAbsContent(request);
			AbsPredicate absEQ = absIota.getProposition();
			AbsConcept absModel = (AbsConcept) absEQ.getAbsObject("left");
			fqn = absModel.getString("Name");
			response.setPerformative(ACLMessage.AGREE);		
		} catch (Exception e) {
			absIota = null;
			e.printStackTrace();
			System.err.println("GetStatusRequestListener: Msg not understood:"+ request.getContent());
			// Wir haben die Nachricht nicht verstanden -> Default abschicken
			response.setContent(e.getMessage());
		}
		return response;
	}
	
	
	protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
	throws FailureException {
		ACLMessage result = request.createReply();
		result.setPerformative(ACLMessage.INFORM_REF);
		Status status = new Unknown();		
		if (fqn!="") {			
			status = al.getRepository().getRunnableStatus(fqn);
            try {
                if (status == null && al.getRepository().getModelManager().getModel(fqn) != null) {
                    status = new Known();
                }
            } catch (ModelException e) {
                System.err.println("GetStatusRequestListener: ModelException:");
                e.printStackTrace();
            } catch (ResourceNotFoundException e) {
                System.err.println("GetStatusRequestListener: ResourceNotFoundException:");
                e.printStackTrace();
            }
		}
		//This is an example of how to answer these messages:
		AbsPredicate abseq2 = new AbsPredicate(SL2Vocabulary.EQUALS);
		abseq2.set("left",absIota);
		AbsConcept absStat;
		try {
			absStat = (AbsConcept) ASCMLOntology.getInstance().fromObject(status);
			abseq2.set("right",absStat);
			al.getContentManager().fillContent(result,abseq2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setContent(e.getMessage());			
		}
		return result;
	}
	
}
