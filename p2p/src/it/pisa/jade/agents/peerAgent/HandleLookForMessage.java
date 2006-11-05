/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
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
 * @author Domenico Trimboli
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
