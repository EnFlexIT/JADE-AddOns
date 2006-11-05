package it.pisa.jade.agents.peerAgent;

import it.pisa.jade.agents.peerAgent.ontologies.Found;
import it.pisa.jade.agents.peerAgent.ontologies.PeerVocabulary;
import jade.content.ContentElement;
import jade.content.onto.basic.Result;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
import jade.util.leap.List;
/**
 * 
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class HandleFoundMessage extends OneShotBehaviour implements
		PeerVocabulary {
	private ACLMessage request = null;

	private PeerAgent peerAgent = null;

	public HandleFoundMessage(PeerAgent peerAgent, ACLMessage msg) {
		super(peerAgent);
		this.request = msg;
		this.peerAgent = peerAgent;
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
				if (result.getValue() instanceof List) {
					 List list = (List) (result
							.getValue());
					 for(Iterator it=list.iterator();it.hasNext();)
					peerAgent.searchElements.addNewSearchElement(searchKey,
							(Found)it.next());
				}// 2 if
			}// 1 if
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
