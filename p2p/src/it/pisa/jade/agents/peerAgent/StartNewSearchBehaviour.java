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
 * @author Domenico Trimboli
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