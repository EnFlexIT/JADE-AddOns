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

import it.pisa.jade.agents.peerAgent.ontologies.PeerVocabulary;
import it.pisa.jade.util.Parameter;
import it.pisa.jade.util.Values;
import it.pisa.jade.util.WrapperErrori;
import it.pisa.jade.util.behaviours.TickerBehaviourWithoutStartDelay;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
/**
 * 
 * @author Fabrizio Marozzo
 * @author Domenico Trimboli
 *
 */
@SuppressWarnings("serial")
public class SearchPeersAgentBehaviour extends TickerBehaviourWithoutStartDelay
		implements PeerVocabulary {

	private PeerAgent peerAgent = null;

	private boolean promptActivityPlatform = (Parameter
			.get(Values.promptConsoleMessage) != null && Parameter.get(
			Values.promptConsoleMessage).equals(Parameter.YES)) ? true : false;

	public SearchPeersAgentBehaviour(PeerAgent peerAgent, long period) {
		super(peerAgent, period);
		this.peerAgent = peerAgent;

	}

	protected void onTick() {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(TYPE_SERVICE);
		dfd.addServices(sd);
		SearchConstraints c = new SearchConstraints();
		c.setMaxDepth(new Long(1));
		c.setMaxResults(new Long(50));
		DFAgentDescription[] result;
		try {
			result = DFService.search(myAgent, dfd, c);
			if (promptActivityPlatform)
				System.out.println("Search for PeerAgent : " + result.length
						+ " elements");
			peerAgent.listPeers.clear();
			if (result.length > 0) {
				for (int i = 0; i < result.length; i++) {
					peerAgent.listPeers.add(result[i].getName());
				}
				peerAgent.notifyObservers();
			}

		} catch (FIPAException e) {
			WrapperErrori.wrap("", e);
		}

	}

}
