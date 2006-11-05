package it.pisa.jade.agents;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


@SuppressWarnings("serial")
public class SubDFRemote extends jade.domain.df {

	public void setup() {
		try {
			super.setup();

			// Use this method to modify the current description of this df.
			setDescriptionOfThisDF(getDescription());

			// Show the default Gui of a df.
			super.showGui();
			AID localDF = getDefaultDF();
			DFService.register(this, localDF, getDescription());
			addParent(localDF, getDescription());
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private DFAgentDescription getDescription() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName() + "-sub-df");
		sd.setType("fipa-df");
		sd.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST);
		sd.addOntologies("fipa-agent-management");
		sd.setOwnership("JADE");
		dfd.addServices(sd);
		return dfd;
	}

}
