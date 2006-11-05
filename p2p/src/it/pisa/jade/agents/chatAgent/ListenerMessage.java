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
package it.pisa.jade.agents.chatAgent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * @author Domenico Triboli
 * @author Fabrizio Marozzo
 * 
 */
@SuppressWarnings("serial")
public class ListenerMessage extends CyclicBehaviour {
	private GuiAgentChat gui;
	private MessageTemplate mt=MessageTemplate.MatchConversationId(ConstantChat.convesationId.getValue());

	public ListenerMessage(Agent a, GuiAgentChat gui) {
		super(a);
		this.gui = gui;
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
			if(msg.getPerformative()==ACLMessage.INFORM){
				gui.arrivedMessage(msg.getSender(),msg.getContent());
			}else {
				gui.errorMessage(msg.getSender(),msg.getContent());
			}
		} else {
			block();

		}
	}

}
