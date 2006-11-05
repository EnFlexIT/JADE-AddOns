package it.pisa.jade.agents.peerAgent;

import it.pisa.jade.agents.peerAgent.ontologies.Choose;
import it.pisa.jade.agents.peerAgent.ontologies.RefuseChoose;
import it.pisa.jade.agents.peerAgent.util.PeerClient;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.File;
import java.net.InetAddress;
/**
 * 
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class HandleChooseMessage extends OneShotBehaviour {
	private ACLMessage request = null;

	private PeerAgent peerAgent = null;

	public HandleChooseMessage(PeerAgent peerAgent, ACLMessage msg) {
		super(peerAgent);
		this.request = msg;
		this.peerAgent = peerAgent;

	}

	@Override
	public void action() {
		try {
			ContentElement content = myAgent.getContentManager()
					.extractContent(request);
			Choose choose = (Choose) ((Action) content).getAction();
			String searchKey = choose.getSearchKey();
			String url = choose.getUrl();
			String address=choose.getIp();
			int port=choose.getPort();
			if (peerAgent.files.contains(new File(url))) {
				System.out.println("Download File" + url);
				File f = new File(url);
				InetAddress add=InetAddress.getByName(address);
				PeerClient p = new PeerClient(f,add,port);
				p.start();
				// TODO open socket
			} else {
				RefuseChoose refuseChoose = new RefuseChoose();
				refuseChoose.setSearchKey(choose.getSearchKey());
				refuseChoose.setUrl(choose.getUrl());
				ACLMessage reply = request.createReply();
				reply.setPerformative(ACLMessage.REFUSE);
				reply.setConversationId(searchKey);
				Result result = new Result((Action) content, refuseChoose);
				myAgent.getContentManager().fillContent(reply, result);
				myAgent.send(reply);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
