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


import it.pisa.jade.agents.chatAgent.GuiAgentChat.PeerItem;
import it.pisa.jade.util.WrapperErrori;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Iterator;

/**
 * Very simple chat over platform
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class ChatAgent extends GuiAgent {
	
	private GuiAgentChat gui;
	public static final int SEND=1;
	
	@Override
	protected void setup() {
		init();
		ListenerMessage listener=new ListenerMessage(this,gui);
		this.addBehaviour(listener);

	}
	/**
	 * 
	 */
	private void init() {
		DFAgentDescription dfad=new DFAgentDescription();
		dfad.setName(getAID());
		ServiceDescription sd=new ServiceDescription();
		sd.setName(ConstantChat.nameService.getValue());
		sd.setType(ConstantChat.typeService.getValue());
		dfad.addServices(sd);
		try {
			DFService.register(this,dfad);
		} catch (FIPAException e) {
			WrapperErrori.wrap("",e);
			doDelete();
		}
		gui=new GuiAgentChat(this);
		gui.setVisible(true);
	}
	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			WrapperErrori.wrap("",e);
		}
		gui.dispose();
	}
	@Override
	protected void beforeMove() {
		gui.dispose();
	}
	@Override
	protected void afterMove() {
		init();
	}
	@Override
	protected void onGuiEvent(GuiEvent ev) {
		int event=ev.getType();
		if(event==SEND){
			Iterator it=ev.getAllParameter();
			String msg=(String)it.next();
			PeerItem dest=(PeerItem)it.next();
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.setContent(msg);
			message.setConversationId(ConstantChat.convesationId.getValue());
			message.addReceiver(dest.getPeerAID());
			send(message);
		}
	}

}
