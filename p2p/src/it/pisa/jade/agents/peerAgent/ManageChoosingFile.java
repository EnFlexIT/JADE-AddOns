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
 * @author Domenico Trimboli
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