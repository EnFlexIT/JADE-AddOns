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
import jade.util.leap.*;
import jade.proto.AchieveREInitiator;
import jade.proto.states.ReplySender;

import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import test.common.*;
import test.common.behaviours.ListProcessor;
import test.common.testSuite.gui.*;
import test.common.testerAgentControlOntology.*;
import test.common.xml.*;

import java.util.Vector;
/**
 * @author Giovanni Caire - TiLab
 * @author Elisabetta Cortese - TiLab
 */

public class TestSuiteAgent extends GuiAgent {
	public static JadeController mainController;
	public static final String MAIN_SERVICES = "jade.core.mobility.AgentMobilityService;jade.core.event.NotificationService;jade.core.replication.MainReplicationService;jade.core.replication.AddressNotificationService;jade.core.messaging.PersistentDeliveryService";
	public static final String TEST_PLATFORM_NAME = "TestPlatform";
	
	private static final String NAME = "test-suite";
	private static final String TESTER_NAME = "tester";
	
	// Gui event types
	public static final int LOAD_EVENT = 0;
	public static final int RUN_EVENT = 2;
	public static final int RUNALL_EVENT = 9;
	public static final int DEBUG_EVENT = 3;
	public static final int CONFIGURE_EVENT = 4;
	public static final int SELECT_EVENT = 5;
	public static final int STEP_EVENT = 6;
	public static final int GO_EVENT = 7;
	public static final int EXIT_EVENT = 8;

	// Maps a tester with the results of the tests it performed 
	// Used in RUN_ALL to print the final report
	private HashMap runAllResults = new HashMap();
	private boolean runAllOngoing = false;
	
	// The tester agent that is currently loaded
	private String currentTester;
	
	private Codec codec;	
	private TestSuiteGui myGui;
	
	protected void setup() {
		// Register language and ontology used to control the 
		// execution of the tester agents
		codec = new SLCodec();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(TesterAgentControlOntology.getInstance());
		
		// Create the GUI
		myGui = new TestSuiteGui(this, "test/testerList.xml"); 
		myGui.showCorrect();				
	}	
		
	protected void takeDown() {
		myGui.dispose();
	}
	
	protected void onGuiEvent(GuiEvent ev) {
		switch (ev.getType()) {
		case LOAD_EVENT: 
			final String className = (String) ev.getParameter(0);
			System.out.println("TestSuiteAgent handling LOAD event. Class is "+className);
			// The user pressed "Open". If no tester is currently loaded, just load
			// the indicated one. Otherwise kill the currently loaded tester before
			if(myGui.getStatus() == TestSuiteGui.IDLE_STATE){
				loadTester(className);
			}
			else {
				// Add a behaviour that makes the currently loaded tester agent exit and,
				// on completion, loads the newly specified tester agent.
				addBehaviour(new Requester(this, new Exit()) {
					public int onEnd() {
						waitABit();
						loadTester(className);
						return 0;
					}
				} );
			}
			break;
		case RUN_EVENT: 
			System.out.println("TestSuiteAgent handling RUN event");
			// The user pressed "Run"
			// Add a behaviour that makes the currently loaded tester agent execute
			// its test group and, on completion, sets the GUI status back to READY
			addBehaviour(new Requester(this, new Execute(false)) {
				public int onEnd() {
					myGui.setStatus(TestSuiteGui.READY_STATE);
					return 0;
				}
			} );
			break;
		case RUNALL_EVENT:
			System.out.println("TestSuiteAgent handling RUNALL event");
			// The user pressed "RunAll". 
			FunctionalityDescriptor[] allFunc = (FunctionalityDescriptor[]) ev.getParameter(0);
			final ArrayList l = new ArrayList();
			if (allFunc != null) {
				for(int i = 0; i < allFunc.length; i++){
					l.add(i, allFunc[i]);
				}
				// Add a behaviour that executes all testers in sequence. If there is 
				// a tester currently loaded, kill it before.
				if(myGui.getStatus() == TestSuiteGui.IDLE_STATE){
					addBehaviour(new AllTesterExecutor(this, l));
				}
				else {
					addBehaviour(new Requester(this, new Exit()){
						public int onEnd() {
							waitABit();
							addBehaviour(new AllTesterExecutor(myAgent, l));
							return 0;
						}							
					});
				}
			}
			break;
		case DEBUG_EVENT: 
			System.out.println("TestSuiteAgent handling DEBUG event");
			// The user pressed "Debug"
			// Add a behaviour that makes the currently loaded tester agent execute
			// its test group in debug-mode and, on completion, sets the GUI status back to READY
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
			// the test group configuration gui and, on completion, sets the GUI status back to READY
			addBehaviour(new Requester(this, new Configure()) {
				public int onEnd() {
					myGui.setStatus(TestSuiteGui.READY_STATE);
					return 0;
				}
			} );
			break;
		case SELECT_EVENT: 
			System.out.println("TestSuiteAgent handling SELECT event");
			// The user pressed "Select"
			// Add a behaviour that makes the currently loaded tester agent show
			// the test selection gui and, on completion, sets the GUI status back to READY
			addBehaviour(new Requester(this, new SelectTests()) {
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
			// the execution of its test group in normal-mode
			addBehaviour(new Requester(this, new Resume(false)));
			break;
		case EXIT_EVENT: 
			System.out.println("TestSuiteAgent handling EXIT event");
			// The user pressed "Exit". If no tester is currently loaded, just exit.
			// Otherwise kill the currently loaded tester before
			if(myGui.getStatus() == TestSuiteGui.IDLE_STATE){
				doDelete();
			}
			else {
				// Add a behaviour that makes the currently loaded tester agent exit and,
				// on completion, exit.
				addBehaviour(new Requester(this, new Exit()) {
					public int onEnd() {
						doDelete();
						return 0;
					}
				} );
			}
			break;
		}
	}
	
	private void loadTester(String className) {
		try {
			TestUtility.createAgent(this, TESTER_NAME, className, new String[] {new String("true")}, getAMS(), null);
			myGui.setCurrentF(className);		
			if (!runAllOngoing) {
				myGui.setStatus(TestSuiteGui.READY_STATE);
			}
		}
		catch (Exception e) {
			System.out.println("Error loading tester agent. ");
			e.printStackTrace();
			if (!runAllOngoing) {
				myGui.setStatus(TestSuiteGui.IDLE_STATE);
			}
		}
	}

	/**
	 * INNER CLASS: AllTesterExecutor
	 * This behaviour executes a sequence of TesterAgents one by one.
	 * On completion, print a report and, as no tester is alive, 
	 * set the GUI status to IDLE.
	 */
	class AllTesterExecutor extends ListProcessor{
		
		public AllTesterExecutor(Agent a, ArrayList l){
			super(a, l);
		}
			
		public void onStart() {
			myGui.setStatus(TestSuiteGui.RUNNING_STATE);
			runAllOngoing = true;
		}
		
		// take tester to run and its order in the list
		protected void processItem(Object item, int index){
			FunctionalityDescriptor fDsc = (FunctionalityDescriptor) item;
			if (!fDsc.getSkip()) {
	  		myAgent.addBehaviour(new SingleTesterExecutor(myAgent, fDsc, this));
  			pause();
			}
		}
		
  	public int onEnd() {
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("************ FINAL REPORT ****************");
			System.out.println(" ");
			for(int i = 0; i < items.size(); i++){	
				FunctionalityDescriptor fDsc = (FunctionalityDescriptor) items.get(i);
				String className = fDsc.getTesterClassName();
				ExecResult res = (ExecResult) runAllResults.get(className);
				if (!fDsc.getSkip()) {
					System.out.println("Functionality:  "+fDsc.getName());
					if (res != null) {
						System.out.println("Passed:  "+res.getPassed());
						System.out.println("Failed:  "+res.getFailed());
						System.out.println("Skipped: "+res.getSkipped());
					}
					else {
						System.out.println("No result available");
					}
					System.out.println("-----------------------------------------------");
				}
			}
			runAllOngoing = false;
			myGui.setCurrentF(null);		
			myGui.setStatus(TestSuiteGui.IDLE_STATE);
  		return 0;
  	}  			
	}
	
	/**
	 * INNER CLASS: SingleTesterExecutor
	 */
	class SingleTesterExecutor extends SequentialBehaviour{
		
		private String testerName= null;
		private ListProcessor lpToBeResumed;
		
		public SingleTesterExecutor(Agent a, FunctionalityDescriptor f, ListProcessor l){
			super(a);
			testerName = f.getTesterClassName();
			lpToBeResumed = l;
			
			// 1st Step: Load the tester to run
			addSubBehaviour(new OneShotBehaviour(myAgent){
				public void action(){
					System.out.println("Loading TESTER: "+testerName);
					currentTester = testerName;
					loadTester(testerName);
				}
			});
			
			// 2nd Step: Run the tester
			addSubBehaviour(new Requester(myAgent, new Execute(false)));

			// 3th Step: kill the tester 
			addSubBehaviour(new Requester(myAgent, new Exit()){
				public int onEnd() {
					waitABit();
					return 0;
				}							
			}); 	
		}

		public int onEnd() {
			System.out.println("Test terminated");
			lpToBeResumed.resume();
			return 0;
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
			
		// Store results 
		protected void handleInform(ACLMessage inform) {
			if (requestedAction instanceof Execute) {
				try{
					Result res = (Result) getContentManager().extractContent(inform);
					ArrayList lRes = (ArrayList)res.getItems();
					ExecResult exRes = (ExecResult) lRes.get(0);
					runAllResults.put(currentTester, exRes);
				}catch(Exception e){
					e.printStackTrace();
				}
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
			// Arguments
      Profile p = new ProfileImpl(null, Test.DEFAULT_PORT, null);
      StringBuffer argsAsString = new StringBuffer();
			if (args != null) {
				for (int i = 0; i < args.length; ++i) {
					if (args[i].startsWith("-") && i < args.length-1) {
						argsAsString.append(' ');
						argsAsString.append(args[i]);
						p.setParameter(args[i].substring(1), args[i+1]);
						++i;
						argsAsString.append(' ');
						argsAsString.append(args[i]);
					}
				}
			}
			// Launch the Main container in a separated process
			mainController = TestUtility.launchJadeInstance("Main", null, "-gui -nomtp -local-port "+Test.DEFAULT_PORT+" -services "+MAIN_SERVICES+" -name "+TEST_PLATFORM_NAME+argsAsString, null);
			
      // Get a hold on JADE runtime
      Runtime rt = Runtime.instance();

      // Exit the JVM when there are no more containers around
      rt.setCloseVM(true);

			p.setParameter(Profile.MAIN, "false");
			p.setParameter(Profile.SERVICES, "jade.core.event.NotificationService;jade.core.mobility.AgentMobilityService;jade.core.replication.AddressNotificationService");
			p.setSpecifiers(Profile.MTPS, new ArrayList());
			
      AgentContainer mc = rt.createAgentContainer(p);
      
      AgentController testSuite = mc.createNewAgent(NAME, TestSuiteAgent.class.getName(), new Object[]{mc}); 
      testSuite.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	/** 
	   Silly utility method to wait one sec without annoying exceptions to catch
	 */
	private void waitABit() {
		try {
			Thread.sleep(1000);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}