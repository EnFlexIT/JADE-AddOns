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
package it.pisa.jade.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
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