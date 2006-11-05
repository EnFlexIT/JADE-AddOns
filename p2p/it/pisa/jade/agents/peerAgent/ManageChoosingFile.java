package it.pisa.jade.agents.peerAgent;

import it.pisa.jade.agents.peerAgent.ontologies.Choose;
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
public class ManageChoosingFile extends OneShotBehaviour {
	private Choose choose=null;

	private PeerAgent peerAgent;

	private AID peerDest;

	public ManageChoosingFile(PeerAgent peerAgent, Choose choose, AID peerDest) {
		super(peerAgent);
		this.choose = choose;
		this.peerAgent = peerAgent;
		this.peerDest=peerDest;
	}

	@Override
	public void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setLanguage(peerAgent.codec.getName());
		msg.setOntology(peerAgent.ontology.getName());
		msg.setConversationId(choose.getSearchKey());
		msg.addReceiver(peerDest);
		try {
			Action action=new Action(peerAgent.getAID(),choose);
			peerAgent.getContentManager().fillContent(msg,action);
		} catch (CodecException e) {
			WrapperErrori.wrap("",e);
		} catch (OntologyException e) {
			WrapperErrori.wrap("",e);
		}
		peerAgent.send(msg);

	}

}