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

package test.common.testSuite;

import jade.core.Agent;
import jade.core.AID;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.wrapper.*;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import test.common.*;
import test.common.testSuite.gui.*;

public class TestSuiteAgent extends GuiAgent {
	private static final String NAME = "test-suite";
	private static final String TESTER_NAME = "tester";
	
	// Gui event types
	public static final int EXIT_EVENT = 0;
	public static final int START_TESTER_EVENT = 1;
	public static final int STEP_EVENT = 2;
	
	private AgentContainer containerController;
	private TestSuiteGui myGui;
	private MessageTemplate testerExitNotificationTemplate = MessageTemplate.MatchContent(TesterAgent.TESTER_EXITED);
	
	TestSuiteAgent(AgentContainer containerController) {
		super();
		this.containerController = containerController;
	}
	
	protected void setup() {	
		myGui = new TestSuiteGui(this, new String[] {
			"test.content.ContentTesterAgent",
			"test.content.SLOperatorsTesterAgent",
			"test.interPlatform.InterPlatformCommunicationTesterAgent",
			"test.behaviours.BlockTimeoutTesterAgent",
			"test.domain.JADEManagementOntologyTesterAgent",
			"test.proto.ContractNetTesterAgent",
			"test.proto.AchieveRETesterAgent" } );
		myGui.showCorrect();		
		
		// This behaviour receives exit notifications from the tester agents
		addBehaviour(new CyclicBehaviour() {
			public void action() {
				ACLMessage msg = receive(testerExitNotificationTemplate);
				if (msg != null) {
					myGui.setStatus(TestSuiteGui.READY_STATE);
				}
				else {
					block();
				}
			}
		} );
	}	
		
	protected void takeDown() {
		myGui.dispose();
	}
	
	protected void onGuiEvent(GuiEvent ev) {
		switch (ev.getType()) {
		case START_TESTER_EVENT:
			try {
				TesterAgent ta = (TesterAgent) ev.getParameter(0);
  	    AgentController tester = containerController.acceptNewAgent(TESTER_NAME, ta);
  	    tester.start();
			}
			catch (Exception e) {
				System.out.println("Error starting tester agent. "+e);
				myGui.setStatus(TestSuiteGui.IDLE_STATE);
			}
			break;
		case STEP_EVENT:
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(new AID(TESTER_NAME, AID.ISLOCALNAME));
			msg.setContent(TesterAgent.STEP);
			send(msg);
			break;
		case EXIT_EVENT:
			doDelete();
			break;
		}
	}
	
	// Main method that allows launching the TestSuiteAgent as a 
	// stand-alone program 	
	public static void main(String[] args) {
		try {
      // Get a hold on JADE runtime
      Runtime rt = Runtime.instance();

      // Exit the JVM when there are no more containers around
      rt.setCloseVM(true);

      Profile pMain = new ProfileImpl(null, 8888, null);

      MainContainer mc = rt.createMainContainer(pMain);

      AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
      rma.start();

      TestSuiteAgent tsa = new TestSuiteAgent(mc);
      AgentController testSuite = mc.acceptNewAgent(NAME, tsa);
      testSuite.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
 
}