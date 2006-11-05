/**
 * 
 */
package it.pisa.jade.agents.federatorAgent.behaviours;

import it.pisa.jade.agents.federatorAgent.FederatorAgent;
import it.pisa.jade.agents.multicastAgent.ConstantMulticastAgent;
import it.pisa.jade.data.activePlatform.ActivePlatform;
import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.AgentName;
import it.pisa.jade.util.ConstantForACL;
import it.pisa.jade.util.FactoryAgent;
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
					FactoryAgent.JAVASERIALIZATION_LANGUAGE)) {
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
