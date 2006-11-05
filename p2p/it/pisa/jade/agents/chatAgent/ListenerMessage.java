/**
 * 
 */
package it.pisa.jade.agents.chatAgent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * @author Domenico Triboli
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
