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
