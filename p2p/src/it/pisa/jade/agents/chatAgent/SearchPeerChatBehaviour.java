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
package it.pisa.jade.agents.chatAgent;

/**
 * 
 */


import it.pisa.jade.util.WrapperErrori;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 * 
 */
@SuppressWarnings("serial")
public class SearchPeerChatBehaviour extends OneShotBehaviour {
	private GuiAgentChat gui;

	public SearchPeerChatBehaviour(Agent a, GuiAgentChat g) {
		super(a);
		myAgent.getContentManager().registerOntology(FIPAManagementOntology.getInstance());
		myAgent.getContentManager().registerLanguage(new SLCodec(0),FIPANames.ContentLanguage.FIPA_SL0);
		gui = g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void action() {
		AID df = myAgent.getDefaultDF();
		DFAgentDescription dfad = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ConstantChat.typeService.getValue());
		dfad.addServices(sd);
		SearchConstraints c=new SearchConstraints();
		c.setMaxDepth(new Long(1));
		c.setMaxResults(new Long(50));
		try {			
			DFAgentDescription[] peers = DFService.search(myAgent,df,dfad,c);
			gui.setPeers(peers);
		} catch (FIPAException e) {
			WrapperErrori.wrap("", e);
		}

	}

}
