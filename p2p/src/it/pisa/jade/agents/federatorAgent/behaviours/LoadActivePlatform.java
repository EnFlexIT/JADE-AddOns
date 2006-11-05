/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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
package it.pisa.jade.agents.federatorAgent.behaviours;

import it.pisa.jade.agents.federatorAgent.FederatorAgent;
import it.pisa.jade.agents.multicastAgent.ConstantMulticastAgent;
import it.pisa.jade.data.activePlatform.ActivePlatform;
import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.AgentName;
import it.pisa.jade.util.ConstantForACL;
import it.pisa.jade.util.FactoryUtil;
import it.pisa.jade.util.WrapperErrori;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.Iterator;

/**
 * @author Fabrizio Marozzo
 * @author Domenico Trimboli
 * 
 */
@SuppressWarnings("serial")
public class LoadActivePlatform extends WakerBehaviour {

	public LoadActivePlatform(Agent a, long timeout) {
		super(a, timeout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	
	public void onWake() {
		// send a message to request the active platform
		ACLMessage message = new ACLMessage(ACLMessage.CFP);
		message.setConversationId(ConstantForACL.MANAGE_PLATFORM.value());
		message.addReceiver(new AID(AgentName.multicastManagerAgent.name(), false));
		myAgent.send(message);
		MessageTemplate mt = MessageTemplate.and(MessageTemplate
				.MatchConversationId(ConstantForACL.MANAGE_PLATFORM.value()),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage reply = myAgent.blockingReceive(mt);
		if (reply != null) {
			if (reply.getLanguage().equals(
					FactoryUtil.JAVASERIALIZATION_LANGUAGE)) {
				try {
					ActivePlatform ap = (ActivePlatform) reply
							.getContentObject();
					federateWithActivePlatform(ap);
					// add cyclic behaviour
					myAgent.addBehaviour(new ManageFederation(myAgent));
					
				} catch (UnreadableException e) {
					WrapperErrori
							.wrap(
									"Impossibile caricare le piattaforme attive arrivate via message",
									e);
				}
			}

		}// if
		message.setPerformative(ACLMessage.SUBSCRIBE);
		message.setContent(ConstantMulticastAgent.SUBSCRIBE.value());
		myAgent.send(message);
	}

	private void federateWithActivePlatform(ActivePlatform ap) {
		FederatorAgent fa = (FederatorAgent) this.myAgent;
		AID parentDF = fa.getDefaultDF();
		for(Iterator<RecordPlatform> it=ap.iterator(); it.hasNext(); ){
			RecordPlatform rp=it.next();
			String sChildDF="df@"+rp.getPlatformName()+", "+rp.getPlatformAddress();
			AID childDF=FactoryUtil.createAID(sChildDF);
			fa.requestFederation(childDF,parentDF);
			//TODO 
		}
		

	}
}
