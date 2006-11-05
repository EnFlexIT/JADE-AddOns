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
package it.pisa.jade.agents.federatorAgent;

import it.pisa.jade.agents.federatorAgent.behaviours.LoadActivePlatform;
import it.pisa.jade.util.Parameter;
import it.pisa.jade.util.Values;
import it.pisa.jade.util.WrapperErrori;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.DFGUIManagement.DFAppletOntology;
import jade.domain.DFGUIManagement.Federate;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.util.Logger;

import java.util.Observable;



/**
 * 
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class FederatorAgent extends Agent{
	Observable o=new Observable();
	
	//DFFederatorAgentGUI myGui;
	//FederatorFrame federatorFrame;

	private static Logger logger = Logger.getMyLogger(FederatorAgent.class
			.getName());

	protected void setup() {
		// Register the DFAppletOntology with the content manager
		getContentManager().registerOntology(DFAppletOntology.getInstance());
		// Register the JADEManagementOntology with the content manager
		getContentManager().registerOntology(
				JADEManagementOntology.getInstance());
		// Register the SL0 content language
		getContentManager().registerLanguage(new SLCodec(0),
				FIPANames.ContentLanguage.FIPA_SL0);

		//myGui = new DFFederatorAgentGUI(this);
		//myGui.showCorrect();

		//federatorFrame=new FederatorFrame(this);
		//federatorFrame.setVisible(true);
		
		this.addBehaviour(new LoadActivePlatform(this,Parameter.getLong(Values.federatorTimeSleep)));
		


	}

	protected void takeDown() {
		/*if (myGui != null) {
			myGui.dispose();
		}
*/
	}

	public void requestFederation(final AID childDF, final AID parentDF) {
		logger.log(Logger.INFO, "Federating " + childDF.getName() + " with "
				+ parentDF.getName());
		Federate f = new Federate();
		f.setDf(parentDF);
		Action action = new Action(childDF, f);
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(childDF);
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		request.setOntology(DFAppletOntology.NAME);
		try {
			getContentManager().fillContent(request, action);
			addBehaviour(new AchieveREInitiator(this, request) {
				protected void handleInform(ACLMessage inform) {
					//myGui.notifyFederationOK(childDF, parentDF);
				}

				protected void handleFailure(ACLMessage failure) {
					//String msg = new String("Federation between "
						//	+ childDF.getName() + " and " + parentDF.getName()
							//+ " failed [" + failure.getContent() + "]");
					//myGui.notifyFailure(msg);
					
					
					
				}
				
			});
		} catch (Exception e) {
			
			String msg = new String("Federation between " + childDF.getName()
					+ " and " + parentDF.getName() + " failed ["
					+ e.getMessage() + "]");
			WrapperErrori.wrap(msg,e);
			//myGui.notifyFailure(msg);
		}
	}

	public void requestFederationRemoval(final AID childDF, final AID parentDF) {
		sendRequestDeregister(childDF,parentDF);
		/*logger.log(Logger.INFO, "Removing federation between "
				+ childDF.getName() + " and " + parentDF.getName());
		DeregisterFrom d = new DeregisterFrom();
		d.setDf(parentDF);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(childDF);
		d.setDescription(dfd);
		Action action = new Action(childDF, d);
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(childDF);
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		request.setOntology(DFAppletOntology.NAME);
		try {
			getContentManager().fillContent(request, action);
			addBehaviour(new AchieveREInitiator(this, request) {
				protected void handleInform(ACLMessage inform) {
					//myGui.notifyFederationRemoved(childDF, parentDF);
				}

				protected void handleFailure(ACLMessage failure) {
					//String msg = new String("Federation removal between "
						//	+ childDF.getName() + " and " + parentDF.getName()
							//+ " failed [" + failure.getContent() + "]");
					//myGui.notifyFailure(msg);
					
					//fabrizio
					sendRequestDeregister(childDF,parentDF);
					//fabrizio
					
				}
			});
		} catch (Exception e) {
			
			String msg = new String("Federation removal between "
					+ childDF.getName() + " and " + parentDF.getName()
					+ " failed [" + e.getMessage() + "]");
			WrapperErrori.wrap(msg,e);
			//myGui.notifyFailure(msg);
		}*/
	}
	
	private void sendRequestDeregister(final AID childDF, final AID parentDF){
		try {
			DFAgentDescription d=new DFAgentDescription();
			d.setName(childDF);
			DFService.deregister(this,d);
			System.out.println("INFO:"+"platform deregister federation with "+childDF.getName());
		} catch (FIPAException e) {
			WrapperErrori.wrap("",e);
		}
		//Object old = agentDescriptions.deregister(dfd.getName());
		
	}

}
