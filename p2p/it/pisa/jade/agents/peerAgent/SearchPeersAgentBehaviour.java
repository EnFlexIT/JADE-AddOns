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
