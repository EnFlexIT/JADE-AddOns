package it.pisa.jade.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

@SuppressWarnings("serial")
public class SearchServiceAgent extends Agent {

	@SuppressWarnings("serial")
	protected void setup() {
		this.addBehaviour(new TickerBehaviour(this, 10000) {

			@Override
			public void onTick() {

				try {
					DFAgentDescription dfd = new DFAgentDescription();
					SearchConstraints sc=new SearchConstraints();
					sc.setMaxDepth(new Long(2));
					DFAgentDescription[] result = DFService
							.search(myAgent, dfd);

					System.out.println("Search returns: " + result.length
							+ " elements");
					for (int i=0; i<result.length;i++)
						System.out.println(" " + result[i].getName());
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}

			}

		});

	}

	void register(ServiceDescription sd) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	AID getService(String service) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(service);
		dfd.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, dfd);
			if (result.length > 0)
				return result[0].getName();
		} catch (Exception fe) {
		}
		return null;
	}

}