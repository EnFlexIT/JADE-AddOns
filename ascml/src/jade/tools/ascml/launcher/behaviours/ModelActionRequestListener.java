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

import jade.content.ContentElement;
import jade.content.abs.AbsIRE;
import jade.content.onto.basic.Action;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import jade.tools.ascml.onto.*;
import jade.tools.ascml.launcher.*;
import jade.tools.ascml.launcher.abstracts.AbstractMARThread;

import java.util.*;

/**
 * This class listens for request to start/stop an
 * agent by name or type or to start/stop a scenario by name
 */

public class ModelActionRequestListener extends SimpleAchieveREResponder
{
	private AgentLauncher al;	
	private Vector ddVector;
	public ModelActionRequestListener(MessageTemplate arg1,AgentLauncher al)
	{
		super(al, arg1);
		this.al=al;
		ddVector = new Vector();
	}
	
	/**   
	 * If we receive a message matching the MessageTemplate arg1 from the constructor
	 * this method is called.
	 * If the message has usefull content we agree to process it, otherwise we refuse.
	 * If we don't understand the message at all, we reply with NotUnderstood.
	 * @param request the received message
	 * @return the ACLMessage to be sent as a response (i.e. one of
	 * <code>agree, refuse, not-understood, inform</code>. <b>Remind</b> to
	 * use the method createReply of the class ACLMessage in order
	 * to create a good reply message
	 * @see jade.lang.acl.ACLMessage#createReply()
	 **/
	protected ACLMessage prepareResponse(ACLMessage request)
	throws NotUnderstoodException, RefuseException
	{
		//Default Antwort erstellen. Die wird abgeschickt, wenn
		// eine Exception ausgelöst wird
		System.out.println("ModelActionRequestListener: received:");
		System.out.println(request.toString());		
		ACLMessage response = request.createReply();
		response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		try
		{
			ContentElement ce = myAgent.getContentManager().extractContent(request);
			//Nachricht dekodieren
			Action a = (Action)ce;
			jade.util.leap.List models = new jade.util.leap.ArrayList();
			if (a.getAction() instanceof Start) {
				Start s = (Start)a.getAction();
				models = s.getModels();
				
			} else if (a.getAction() instanceof Stop) {
				Stop s= (Stop)a.getAction();
				models= s.getModels();
			}
						
			//Wir haben die richtige Nachricht bekommen
			//Hat sie einen sinnvollen Inhalt (sind agenten angegeben?)
			if(models.size()>0)
			{
				AbsModel m = (AbsModel)models.get(0);
				//Überprüfung:
				//Welchen Typ hat das Model?
				if(m instanceof AgentInstance)
				{
					response.setPerformative(ACLMessage.AGREE);
				}
				else if(m instanceof AgentType)
				{
					response.setPerformative(ACLMessage.AGREE);
				}
				else if(m instanceof SocietyInstance)
				{
					response.setPerformative(ACLMessage.AGREE);
				}
				else
				{
					//Etwas anderes? Kennen wir nicht -> Not Understood schicken
					response.setContent("Neither StartAgent nor StartSociety nor StartType");
					response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				}
			}
			else
			{
				//Wir versthen die Nachricht zwar, aber sie hat keinen sinnvollen Inhalt
				response.setContent("Neither StartAgent nor StartSociety nor StartType");
				response.setPerformative(ACLMessage.REFUSE);
			}
			return response;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("MARListener: Msg not understood:"+request.getContent());
			//Wir haben die Nachricht nicht verstanden -> Default abschicken
			response.setContent(e.getMessage());
			return response;
		}
	}
	
	
	/**   
	 * This method is called after the response has been sent
	 * and only when one of the folliwing two cases arise:
	 * the response was an <code>agree</code> message OR no response
	 * message was sent.
	 * @param request the received message
	 * @param response the previously sent response message
	 * @return the ACLMessage to be sent as a result notification (i.e. one of
	 * <code>inform, failure</code>. <b>Remind</b> to
	 * use the method createReply of the class ACLMessage in order
	 * to create a good reply message
	 * @see jade.lang.acl.ACLMessage#createReply()
	 * @see #prepareResponse(ACLMessage)
	 **/
	protected ACLMessage prepareResultNotification(ACLMessage request,
			ACLMessage response) throws FailureException
			{
		ACLMessage result = request.createReply();
		result.setPerformative(ACLMessage.FAILURE);
		result.setContent(request.getContent());
		try
		{
			ContentElement ce = myAgent.getContentManager().extractContent(request);
			Action a = (Action)ce;
			AbsModel m;
			Iterator it;
			AbstractMARThread t;
			if (a.getAction() instanceof Start) {
				Start s = (Start)a.getAction();
				m = (AbsModel)s.getModels().get(0);
				it = s.getAllModels();
				t = new MARResolverThread (al, it,result,a,m);
				result = null;
			} else {
				Stop s = (Stop)a.getAction();
				m = (AbsModel)s.getModels().get(0);
				it = s.getAllModels();
				t = new MARStopperThread (al, it, result,a , m);
				result = null;
			}
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			result.setContent(e.getMessage());
			return result;
		}
			}
}