package jade.distribution;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

public interface AgentSelectionPolicy<Item> {
	
	public static interface AgentSelector {
		AID getAgent();
	}
	
	AID select(Item item) throws AsynchSelection;
	void handleAgentRegistered(DFAgentDescription dfd);
	void handleAgentDeregistered(DFAgentDescription dfd);
}
