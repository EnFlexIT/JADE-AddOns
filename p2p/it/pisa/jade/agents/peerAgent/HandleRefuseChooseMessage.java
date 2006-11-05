package it.pisa.jade.agents.peerAgent;

import it.pisa.jade.agents.peerAgent.ontologies.PeerVocabulary;
import it.pisa.jade.agents.peerAgent.ontologies.RefuseChoose;
import jade.content.ContentElement;
import jade.content.onto.basic.Result;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
/**
 * 
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class HandleRefuseChooseMessage extends OneShotBehaviour implements
		PeerVocabulary {
	private ACLMessage request = null;

	//private PeerAgent peerAgent = null;

	public HandleRefuseChooseMessage(PeerAgent peerAgent, ACLMessage msg) {
		super(peerAgent);
		this.request = msg;
		//this.peerAgent = peerAgent;
	}

	@Override
	public void action() {
		try {
			String searchKey = request.getConversationId();
			if (searchKey == null)
				return;
			ContentElement content = myAgent.getContentManager()
					.extractContent(request);
			if (content instanceof Result) {
				Result result = (Result) content;
				if (result.getValue() instanceof RefuseChoose) {
					@SuppressWarnings("unused")
					RefuseChoose refuse = (RefuseChoose) result.getValue();
					refuse.getSearchKey();
				}
				// TODO Manage Refuse Choose
				System.out.println("Impossibile fare il download");

			}// 1 if
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}