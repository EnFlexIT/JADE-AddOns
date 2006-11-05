/**
 * 
 */
package it.pisa.jade.agents.guiAgent;

import it.pisa.jade.agents.guiAgent.action.ActionGui;
import it.pisa.jade.agents.guiAgent.behaviours.ConstantBehaviourAction;
import it.pisa.jade.agents.guiAgent.behaviours.ManageActivityPlatformBehaviour;
import it.pisa.jade.gui.ApplicationInterplatformService;
import it.pisa.jade.util.JadeObserver;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.DFGUIManagement.DFAppletOntology;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;

import java.util.Observable;

/**
 * Agent
 * 
 * @author Domenico Trimboli
 * 
 */
@SuppressWarnings("serial") public class GuiAgent extends Agent {
	private class ObservableGui extends Observable {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#notifyObservers(java.lang.Object)
		 */
		public void notifyObservers(Object arg) {
			setChanged();
			super.notifyObservers(arg);
		}
	}

	private ObservableGui observableByDelebation = new ObservableGui();

	private ManageActivityPlatformBehaviour current = null;

	/**
	 * standard ACLMessage for AMS request
	 */
	private ACLMessage AMSRequest = new ACLMessage(ACLMessage.REQUEST);

	@Override protected void setup() {
		// Register the DFAppletOntology with the content manager
		getContentManager().registerOntology(DFAppletOntology.getInstance());

		// Register the JADEManagementOntology with the content manager
		getContentManager().registerOntology(
				JADEManagementOntology.getInstance());

		// Register the SL0 content language
		getContentManager().registerLanguage(new SLCodec(0),
				FIPANames.ContentLanguage.FIPA_SL0);

		// set default AMS request
		AMSRequest.setSender(getAID());
		AMSRequest.clearAllReceiver();
		AMSRequest.addReceiver(getAMS());
		AMSRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		AMSRequest.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);

		// start application
		ApplicationInterplatformService application = new ApplicationInterplatformService(
				this);
		application.setVisible(true);
	}

	@Override 
	protected void takeDown() {
		System.exit(0);
	}

	public void doAction(ConstantBehaviourAction action, Object parameter) {
		ManageActivityPlatformBehaviour temp = new ManageActivityPlatformBehaviour(
				this, action, parameter);
		if (action.equals(ConstantBehaviourAction.SUBSCRIBE))
			current = temp;
		addBehaviour(temp);
	}

	public void stopAction() {
		if (current != null) {
			removeBehaviour(current);
		}
	}

	public void notifyObservers(ActionGui o) {
		observableByDelebation.notifyObservers(o);
	}

	public void addObserver(JadeObserver o) {
		observableByDelebation.addObserver(o);
	}

	public int countObservers() {
		return observableByDelebation.countObservers();
	}

	public void deleteObserver(JadeObserver o) {
		observableByDelebation.deleteObserver(o);
	}

	public void deleteObservers() {
		observableByDelebation.deleteObservers();
	}

	public void doAction(ConstantBehaviourAction action) {
		doAction(action, null);

	}

	/**
	 * @return Returns the aMSRequest.
	 */
	public ACLMessage getAMSRequest() {
		return AMSRequest;
	}
}
