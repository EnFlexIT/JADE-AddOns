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
import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.ConstantForACL;
import it.pisa.jade.util.FactoryUtil;
import it.pisa.jade.util.WrapperErrori;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 * 
 */
@SuppressWarnings("serial")
public class ManageFederation extends CyclicBehaviour {

	private MessageTemplate mt = MessageTemplate
			.MatchConversationId(ConstantForACL.MANAGE_FEDERATION.value());

	public ManageFederation(Agent myAgent) {
		super(myAgent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void action() {
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.INFORM
					&& msg.getLanguage().equals(
							FactoryUtil.JAVASERIALIZATION_LANGUAGE)) {
				// remove federation
				removeFederation(msg);
			} else if (msg.getPerformative() == ACLMessage.REQUEST
					&& msg.getLanguage().equals(
							FactoryUtil.JAVASERIALIZATION_LANGUAGE)) {
				// add federation
				addFederation(msg);
			} else {
				// reject message
			}
		} else
			block();
	}

	private void removeFederation(ACLMessage msg) {
		FederatorAgent fa = (FederatorAgent) this.myAgent;
		AID parentDF = fa.getDefaultDF();
		RecordPlatform rp;
		try {
			rp = (RecordPlatform) msg.getContentObject();
			String sChildDF = "df@" + rp.getPlatformName() + ", "
					+ rp.getPlatformAddress();
			AID childDF = FactoryUtil.createAID(sChildDF);
			fa.requestFederationRemoval(childDF, parentDF);
		} catch (UnreadableException e) {
			WrapperErrori.wrap("", e);
		}
	}

	private void addFederation(ACLMessage msg) {
		FederatorAgent fa = (FederatorAgent) this.myAgent;
		AID parentDF = fa.getDefaultDF();
		RecordPlatform rp;
		try {
			rp = (RecordPlatform) msg.getContentObject();
			String sChildDF = "df@" + rp.getPlatformName() + ", "
					+ rp.getPlatformAddress();
			AID childDF = FactoryUtil.createAID(sChildDF);
			fa.requestFederation(childDF, parentDF);
		} catch (UnreadableException e) {
			WrapperErrori.wrap("", e);
		}

	}

}
