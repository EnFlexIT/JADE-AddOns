package it.pisa.jade.agents.peerAgent;

import it.pisa.jade.agents.peerAgent.ontologies.AgreeChoose;
import it.pisa.jade.agents.peerAgent.ontologies.Choose;
import it.pisa.jade.agents.peerAgent.ontologies.LookFor;
import it.pisa.jade.agents.peerAgent.ontologies.NoFound;
import it.pisa.jade.agents.peerAgent.ontologies.RefuseChoose;
import it.pisa.jade.util.WrapperErrori;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.List;
/**
 * 
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class ReceiverBehaviour extends CyclicBehaviour {
	private PeerAgent peerAgent = null;

	public ReceiverBehaviour(PeerAgent peerAgent) {
		super(peerAgent);
		// peerAgent.getContentManager().registerLanguage(codec);
		// peerAgent.getContentManager().registerOntology(ontology);
		this.peerAgent = peerAgent;
	}

	@Override
	public void action() {

		ACLMessage msg = myAgent.receive();
		if (msg == null) {
			block();
			return;
		}
		try {
			ContentElement content = myAgent.getContentManager()
					.extractContent(msg);
			Result result = null;
			switch (msg.getPerformative()) {

			case (ACLMessage.REQUEST):
				Concept action = ((Action) content).getAction();
				System.out.println("Request from "
						+ msg.getSender().getLocalName());
				if (action instanceof LookFor)
					peerAgent.pBehaviour
							.addSubBehaviour(new HandleLookForMessage(
									peerAgent, msg));
				else if (action instanceof Choose)
					peerAgent.pBehaviour
							.addSubBehaviour(new HandleChooseMessage(peerAgent,
									msg));
				else
					replyNotUnderstood(msg);
				break;

			case (ACLMessage.INFORM):

				System.out.println("Inform from "
						+ msg.getSender().getLocalName());
				result = (Result) content;
				if (result.getValue() instanceof List)
					peerAgent.pBehaviour
							.addSubBehaviour(new HandleFoundMessage(peerAgent,
									msg));
				else if (result.getValue() instanceof NoFound) {

				} else
					replyNotUnderstood(msg);
				break;

			case (ACLMessage.REFUSE):

				System.out.println("Refuse from "
						+ msg.getSender().getLocalName());
				result = (Result) content;
				if (result.getValue() instanceof RefuseChoose)
					peerAgent.pBehaviour
							.addSubBehaviour(new HandleRefuseChooseMessage(
									peerAgent, msg));
				else
					replyNotUnderstood(msg);
				break;
			
			case (ACLMessage.AGREE):
				System.out.println("Agree from "
						+ msg.getSender().getLocalName());
				result = (Result) content;
				if (result.getValue() instanceof AgreeChoose)
					peerAgent.pBehaviour
							.addSubBehaviour(new HandleAgreeChooseMessage(
									peerAgent, msg));
				else
					replyNotUnderstood(msg);
				break;
			case (ACLMessage.NOT_UNDERSTOOD):
				break;
			default:
				replyNotUnderstood(msg);
			}

		} catch (Exception e) {
			WrapperErrori.wrap("", e);
			// replyNotUnderstood(msg);
		}

	}

	private void replyNotUnderstood(ACLMessage msg) {
		try {
			ContentElement content = peerAgent.getContentManager()
					.extractContent(msg);
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			peerAgent.getContentManager().fillContent(reply, content);
			peerAgent.send(reply);
			System.out.println("Not understood!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
