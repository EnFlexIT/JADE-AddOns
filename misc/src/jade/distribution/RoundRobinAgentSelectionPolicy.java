package jade.distribution;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.util.leap.RoundList;

public class RoundRobinAgentSelectionPolicy<Item> implements AgentSelectionPolicy<Item> {
	private RoundList availableAgents = new RoundList();
	
	@Override
	public AID select(Item item) throws AsynchSelection {
		return (AID) availableAgents.get();
	}

	@Override
	public void handleAgentRegistered(DFAgentDescription dfd) {
		AID aid = dfd.getName();
		if (!availableAgents.contains(aid)) {
			availableAgents.add(aid);
		}
	}

	@Override
	public void handleAgentDeregistered(DFAgentDescription dfd) {
		availableAgents.remove(dfd.getName());
	}
}
