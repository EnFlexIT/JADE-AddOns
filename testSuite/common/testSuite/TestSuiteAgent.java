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
import jade.util.leap.ArrayList;
import jade.proto.AchieveREInitiator;
import jade.proto.states.ReplySender;

import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import test.common.*;
import test.common.testSuite.gui.*;
import test.common.testerAgentControlOntology.*;

import java.util.Vector;

public class TestSuiteAgent extends GuiAgent {
	private static final String NAME = "test-suite";
	private static final String TESTER_NAME = "tester";
	
	// Gui event types
	public static final int LOAD_EVENT = 0;
	public static final int RELOAD_EVENT = 1;
	public static final int RUN_EVENT = 2;
	public static final int DEBUG_EVENT = 3;
	public static final int CONFIGURE_EVENT = 4;
	public static final int STEP_EVENT = 5;
	public static final int GO_EVENT = 6;
	public static final int EXIT_EVENT = 7;
	public static final int CLOSE_AND_EXIT_EVENT = 8;
	
	private Codec codec;	
	private TestSuiteGui myGui;
	
	protected void setup() {
		// Register language and ontology used to control the 
		// execution of the tester agents
		codec = new SLCodec();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(TesterAgentControlOntology.getInstance());
		
		// Create and show the GUI
		myGui = new TestSuiteGui(this, new String[] {
			"test.content.ContentTesterAgent",
			"test.content.SLOperatorsTesterAgent",
			"test.interPlatform.InterPlatformCommunicationTesterAgent",
			"test.behaviours.BlockTimeoutTesterAgent",
			"test.behaviours.PerformanceTesterAgent",
			"test.domain.df.DFTesterAgent",
			"test.domain.JADEManagementOntologyTesterAgent",
			"test.mobility.MobilityTesterAgent",
			"test.roundTripTime.RoundTripTimeTesterAgent",
			"test.proto.ContractNetTesterAgent",
			"test.proto.AchieveRETesterAgent" } );
		myGui.showCorrect();				
	}	
		
	protected void takeDown() {
		myGui.dispose();
	}
	
	protected void onGuiEvent(GuiEvent ev) {
		switch (ev.getType()) {
		case LOAD_EVENT: 
			// The user pressed "Open" while no tester agent is currently loaded
			String cn = (String) ev.getParameter(0);
			System.out.println("TestSuiteAgent handling RELOAD event. Class is "+cn);
			loadTester(cn);
			break;
		case RELOAD_EVENT: 
			// The user pressed "Open" while a tester agent is currently loaded
			// Add a behaviour that makes the currently loaded tester agent exit and,
			// on completion, loads the newly specified tester agent.
			final String className = (String) ev.getParameter(0);
			System.out.println("TestSuiteAgent handling RELOAD event. Class is "+className);
			addBehaviour(new Requester(this, new Exit()) {
				public int onEnd() {
					try {
						Thread.sleep(1000);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					loadTester(className);
					return 0;
				}
			} );
			break;
		case RUN_EVENT: 
			System.out.println("TestSuiteAgent handling RUN event");
			// The user pressed "Run"
			// Add a behaviour that makes the currently loaded tester agent execute
			// its test group and, on completion, sets the GUI status to READY
			addBehaviour(new Requester(this, new Execute(false)) {
				public int onEnd() {
					myGui.setStatus(TestSuiteGui.READY_STATE);
					return 0;
				}
			} );
			break;
		case DEBUG_EVENT: 
			System.out.println("TestSuiteAgent handling DEBUG event");
			// The user pressed "Debug"
			// Add a behaviour that makes the currently loaded tester agent execute
			// its test group in debug-mode and, on completion, sets the GUI status to READY
			addBehaviour(new Requester(this, new Execute(true)) {
				public int onEnd() {
					myGui.setStatus(TestSuiteGui.READY_STATE);
					return 0;
				}
			} );
			break;
		case CONFIGURE_EVENT: 
			System.out.println("TestSuiteAgent handling CONFIGURE event");
			// The user pressed "Configure"
			// Add a behaviour that makes the currently loaded tester agent show
			// the test group configuration gui and, on completion, sets the GUI status to READY
			addBehaviour(new Requester(this, new Configure()) {
				public int onEnd() {
					myGui.setStatus(TestSuiteGui.READY_STATE);
					return 0;
				}
			} );
			break;
		case STEP_EVENT: 
			System.out.println("TestSuiteAgent handling STEP event");
			// The user pressed "Step"
			// Add a behaviour that makes the currently loaded tester agent resume
			// the execution of its test group in debug-mode
			addBehaviour(new Requester(this, new Resume(true)));
			break;
		case GO_EVENT: 
			System.out.println("TestSuiteAgent handling GO event");
			// The user pressed "Run" while the currently loaded tester agent is executing his test group 
			// Add a behaviour that makes the currently loaded tester agent resume
			// the execution of its test group in non-debug-mode
			addBehaviour(new Requester(this, new Resume(false)));
			break;
		case EXIT_EVENT: 
			System.out.println("TestSuiteAgent handling EXIT event");
			// The user pressed "Exit" while no tester agent is currently loaded
			doDelete();
			break;
		case CLOSE_AND_EXIT_EVENT: 
			System.out.println("TestSuiteAgent handling CLOSE_AND_EXIT event");
			// The user pressed "Exit" while a tester agent is currently loaded
			// Add a behaviour that makes the currently loaded tester agent exit and,
			// on completion, quits.
			addBehaviour(new Requester(this, new Exit()) {
				public int onEnd() {
					doDelete();
					return 0;
				}
			} );
			break;
		}
	}
	
	private void loadTester(String className) {
		try {
			TestUtility.createAgent(this, TESTER_NAME, className, new String[] {new String("true")}, getAMS(), null);			
		}
		catch (Exception e) {
			System.out.println("Error loading tester agent. ");
			e.printStackTrace();
			myGui.setStatus(TestSuiteGui.IDLE_STATE);
		}
	}
	
	/**
	   Inner class Requester
	 */
	class Requester extends AchieveREInitiator {
		private AgentAction requestedAction;
		
		Requester(Agent a, AgentAction act) {
			super(a, null);
			requestedAction = act;
		}
		
		protected Vector prepareRequests(ACLMessage request) {
			Vector v = new Vector();
			try {
				AID tester = new AID(TESTER_NAME, AID.ISLOCALNAME);
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(tester);
				msg.setLanguage(codec.getName());
				msg.setOntology(TesterAgentControlOntology.getInstance().getName());
				Action aa = new Action(tester, requestedAction);
				getContentManager().fillContent(msg, aa);
				v.addElement(msg);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return v;
		}
			
		protected void handleInform(ACLMessage inform) {
			if (requestedAction instanceof Execute) {
				// FIXME: parse the message and get passed, failed and skipped
			}	
		}
	
		protected void handleRefuse(ACLMessage refuse) {
			System.out.println("Tester agent action refused. Message is:");
			System.out.println(refuse);
		}
	
		protected void handleNotUnderstood(ACLMessage notUnderstood) {
			System.out.println("Tester agent action not understood. Message is:");
			System.out.println(notUnderstood);
		}
	
		protected void handleFailure(ACLMessage failure) {
			System.out.println("Tester agent action failed. Message is:");
			System.out.println(failure);
		}
	}  // END of inner class Requester
				
				
	// Main method that allows launching the TestSuiteAgent as a 
	// stand-alone program 	
	public static void main(String[] args) {
		try {
      // Get a hold on JADE runtime
      Runtime rt = Runtime.instance();

      // Exit the JVM when there are no more containers around
      rt.setCloseVM(true);

      Profile pMain = new ProfileImpl(null, Test.DEFAULT_PORT, null);
			pMain.setSpecifiers(Profile.MTPS, new ArrayList());
			
      MainContainer mc = rt.createMainContainer(pMain);
      
      try {
	      AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
  	    rma.start();
      }
      catch (Exception ex) {
      	System.out.println("Error launching the RMA agent");
      	ex.printStackTrace();
      }

      AgentController testSuite = mc.createNewAgent(NAME, TestSuiteAgent.class.getName(), new Object[]{mc}); 
      testSuite.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
 
}