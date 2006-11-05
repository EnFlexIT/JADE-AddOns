package it.pisa.jade.agents.peerAgent;

import it.pisa.jade.agents.peerAgent.ontologies.PeerVocabulary;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
/**
 * 
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class HandleAgreeChooseMessage extends OneShotBehaviour implements
		PeerVocabulary {
	private ACLMessage request = null;

	private PeerAgent peerAgent = null;

	public HandleAgreeChooseMessage(PeerAgent peerAgent, ACLMessage msg) {
		super(peerAgent);
		this.request = msg;
		this.peerAgent = peerAgent;
	}

	@Override
	public void action() {



	}

	PeerAgent getPeerAgent() {
		return peerAgent;
	}

	void setPeerAgent(PeerAgent peerAgent) {
		this.peerAgent = peerAgent;
	}

	ACLMessage getRequest() {
		return request;
	}

	void setRequest(ACLMessage request) {
		this.request = request;
	}

}