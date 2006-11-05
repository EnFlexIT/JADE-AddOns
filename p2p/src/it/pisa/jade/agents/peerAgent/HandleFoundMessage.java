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
 * @author Domenico Trimboli
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
