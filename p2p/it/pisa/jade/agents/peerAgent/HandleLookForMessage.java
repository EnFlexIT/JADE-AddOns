package it.pisa.jade.agents.peerAgent;

import it.pisa.jade.agents.peerAgent.ontologies.Found;
import it.pisa.jade.agents.peerAgent.ontologies.LookFor;
import it.pisa.jade.agents.peerAgent.ontologies.NoFound;
import it.pisa.jade.agents.peerAgent.ontologies.PeerVocabulary;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.LinkedList;

import java.io.File;
/**
 * 
 * @author Fabrizio Marozzo
 *
 */

@SuppressWarnings("serial")
public class HandleLookForMessage extends OneShotBehaviour implements
		PeerVocabulary {
	private ACLMessage request = null;
	private PeerAgent peerAgent=null;

	public HandleLookForMessage(PeerAgent peerAgent, ACLMessage msg) {
		super(peerAgent);
		this.request = msg;
		this.peerAgent=peerAgent;

	}

	@Override
	public void action() {
		try {
			ContentElement content = myAgent.getContentManager()
					.extractContent(request);
			LookFor lookFor = (LookFor) ((Action) content).getAction();
			String searchKey = lookFor.getSearchKey();
			String searchString = lookFor.getSearchString();
			//String type = lookFor.getType();
			//String extension = lookFor.getExtension();

			// TODO Query to DB

			LinkedList list = new LinkedList();
			// TODO Receive Message FOUND Message
			for(File file:peerAgent.files){
				String name=file.getName().toLowerCase();
				if(name.indexOf(searchString)!=-1){
					Found f = new Found();
					f.setSearchKey(searchKey);
					f.setAgent(peerAgent.getAID().getName()+", "+myAgent.getAID().getAddressesArray()[0]);
					f.setExtension(name.substring(name.lastIndexOf(".")));
					f.setName(name);
					f.setSummary(name);
					f.setType(TYPE_FILE_DOCUMENT);
					f.setUrl(file.getAbsolutePath());
					list.add(f);
					
				}
			}
			

			if (list.isEmpty()) {
				//NOFOUND Message
				NoFound noFound=new NoFound();
				noFound.setSearchKey(searchKey);
				Result result=new Result((Action)content, noFound);
				ACLMessage reply = request.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setConversationId(searchKey);
				myAgent.getContentManager().fillContent(reply, result);
				myAgent.send(reply);
				

			} 			
			else {
				//list of FOUND Message
				Result result = new Result((Action) content, (Object)list);
				ACLMessage reply = request.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setConversationId(searchKey);
				myAgent.getContentManager().fillContent(reply, result);
				myAgent.send(reply);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
