/**
 * 
 */
package it.pisa.jade.agents.federatorAgent.behaviours;

import it.pisa.jade.agents.federatorAgent.FederatorAgent;
import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.ConstantForACL;
import it.pisa.jade.util.FactoryAgent;
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
				if(msg.getPerformative()==ACLMessage.INFORM&&msg.getLanguage().equals(FactoryAgent.JAVASERIALIZATION_LANGUAGE)){
					//remove federation
					removeFederation(msg);
				}else if(msg.getPerformative()==ACLMessage.REQUEST&&msg.getLanguage().equals(FactoryAgent.JAVASERIALIZATION_LANGUAGE)){
					//add federation
					addFederation(msg);
				}else{
					//reject message
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
			String sChildDF="df@"+rp.getPlatformName()+", "+rp.getPlatformAddress();
			AID childDF=FactoryUtil.createAID(sChildDF);
			fa.requestFederationRemoval(childDF,parentDF);
		} catch (UnreadableException e) {
			WrapperErrori.wrap("",e);
		}
	}

	private void addFederation(ACLMessage msg) {
		FederatorAgent fa = (FederatorAgent) this.myAgent;
		AID parentDF = fa.getDefaultDF();		
		RecordPlatform rp;
		try {
			rp = (RecordPlatform) msg.getContentObject();
			String sChildDF="df@"+rp.getPlatformName()+", "+rp.getPlatformAddress();
			AID childDF=FactoryUtil.createAID(sChildDF);
			fa.requestFederation(childDF,parentDF);
		} catch (UnreadableException e) {
			WrapperErrori.wrap("",e);
		}

	}

}
