package it.pisa.jade.agents.peerAgent;

import it.pisa.jade.agents.peerAgent.ontologies.LookFor;
import it.pisa.jade.util.WrapperErrori;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
/**
 * 
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class StartNewSearchBehaviour extends OneShotBehaviour {
	private LookFor lookfor;

	private PeerAgent peerAgent = null;

	public StartNewSearchBehaviour(PeerAgent peerAgent, LookFor lookfor) {
		super(peerAgent);
		this.peerAgent = peerAgent;
		this.lookfor = lookfor;
	}

	@Override
	public void action() {
		if (peerAgent.listPeers == null || peerAgent.listPeers.isEmpty()) {
			return;
		}
		peerAgent.searchElements.newSearch(lookfor.getSearchKey());
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setLanguage(peerAgent.codec.getName());
		msg.setOntology(peerAgent.ontology.getName());
		msg.setConversationId(lookfor.getSearchKey());
		for (AID aid : peerAgent.listPeers)
			if(!aid.equals(myAgent.getAID()))msg.addReceiver(aid);
		try {
			Action action=new Action(myAgent.getAID(),lookfor);
			peerAgent.getContentManager().fillContent(msg,action);
		} catch (CodecException e) {
			WrapperErrori.wrap("",e);
		} catch (OntologyException e) {
			WrapperErrori.wrap("",e);
		}
		peerAgent.send(msg);

	}
}