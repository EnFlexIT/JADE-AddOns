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
	private static final String NAME = "test-suite";
	private static final String TESTER_NAME = "tester";
	
	// Gui event types
	public static final int LOAD_EVENT = 0;
	public static final int RUN_EVENT = 2;
	public static final int RUNALL_EVENT = 9;
	public static final int DEBUG_EVENT = 3;
	public static final int CONFIGURE_EVENT = 4;
	public static final int STEP_EVENT = 5;
	public static final int GO_EVENT = 6;
	public static final int EXIT_EVENT = 7;

	// for each tester we save results, on the end 
	// print the content in a report
	private HashMap risultati = new HashMap();
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
		myGui = new TestSuiteGui(this, "test//testerList.xml"); 
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
				System.out.println("LOAD");
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
			// its test group and, on completion, sets the GUI status to READY
			addBehaviour(new Requester(this, new Execute(false)) {
				public int onEnd() {
					myGui.setStatus(TestSuiteGui.READY_STATE);
					return 0;
				}
			} );
			break;
		case RUNALL_EVENT:
			System.out.println("TestSuiteAgent handling RUNALL event");
			// The user pressed "RunAll"
			FunctionalityDescriptor[] allFunc = (FunctionalityDescriptor[]) ev.getParameter(0);
			ArrayList l = new ArrayList();
			if (allFunc != null) {
				for(int i = 0; i < allFunc.length; i++){
					l.add(i, allFunc[i]);
				}
			// Add a behaviour that execute all testers, that are listed in 
			// xml file and, on completion, sets the GUI status to IDLE
				addBehaviour(new AllTesterExecutor(this, l));
			}
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
			myGui.setStatus(TestSuiteGui.READY_STATE);
		}
		catch (Exception e) {
			System.out.println("Error loading tester agent. ");
			e.printStackTrace();
			myGui.setStatus(TestSuiteGui.IDLE_STATE);
		}
	}

	private void waitABit() {
		try {
			Thread.sleep(1000);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * INNER CLASS: AllTesterExecutor
	 * This class extends <code>ListProcessor</code>, it used to 
	 * execute all tester in sequence: each tester start only when
	 */
	class AllTesterExecutor extends ListProcessor{
		
		ArrayList functionalities;
		
		public AllTesterExecutor(Agent a, ArrayList l){
			super(a, l);
			functionalities = l;
		}

		// take tester to run and its order in the list
		protected void processItem(Object item, int index){
		  		int order = index;
		  		FunctionalityDescriptor func = (FunctionalityDescriptor) item;
		  		myAgent.addBehaviour(new SingleTesterExecutor(myAgent, func, order, this));
		  		pause();
		}
		
  		public int onEnd() {
			// FIXME: PRINT A REPORT
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("************ FINAL REPORT ****************");
			System.out.println(" ");
			for(int i = 0; i < functionalities.size(); i++){	
				String tester = ((FunctionalityDescriptor)functionalities.get(i)).getTesterClassName();
				try{
					ArrayList ar = (ArrayList)risultati.get(tester);
					System.out.println("Tester:  "+tester);
					System.out.println("Passed:  "+((Integer)ar.get(0)).intValue());
					System.out.println("Falled:  "+((Integer)ar.get(1)).intValue());
					System.out.println("Skipped: "+((Integer)ar.get(2)).intValue());
					System.out.println("-----------------------------------------------");
				}catch(Exception ex){
					System.out.println("Some errore is verified.");
					System.out.println("-----------------------------------------------");
				}
			}
			myGui.setStatus(TestSuiteGui.IDLE_STATE);
			myGui.setEnabled(true);
  			return 0;
  		}  			
	}
	
	/**
	 * INNER CLASS: SingleTesterExecutor
	 */
	class SingleTesterExecutor extends SequentialBehaviour{
		
		private String testerName= null;
		private ListProcessor lpToBeResumed;
		private int testerOrd = 0;
		
		public SingleTesterExecutor(Agent a, FunctionalityDescriptor f, int i, ListProcessor l){
			super(a);
			testerName = f.getTesterClassName();
			testerOrd = i;
			lpToBeResumed = l;
			
			// Once called
			if( i == 0 && myGui.getStatus() != TestSuiteGui.IDLE_STATE){
				System.out.println("KILL!!!");
				addSubBehaviour(new Requester(myAgent, new Exit()){
					public int onEnd() {
						try {
							Thread.sleep(1000);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						return 0;
					}							
				});
			} 	

			// 1td Step: Load a tester ti run
			addSubBehaviour(new OneShotBehaviour(myAgent){
				public void action(){
					System.out.println("LOADED TESTER: "+testerName);
					currentTester = testerName;
					loadTester(testerName);
				}
			});
			
			// 2td Step: Run the tester
			addSubBehaviour(new Requester(myAgent, new Execute(false)));

			// 3th Step: kill the tester which isn't the first
			addSubBehaviour(new Requester(myAgent, new Exit()){
				public int onEnd() {
					try {
						Thread.sleep(1000);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
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
			
		// Elisabetta: modified handleIngform to print report
		protected void handleInform(ACLMessage inform) {
			if (requestedAction instanceof Execute) {
				try{
					Result res = (Result) getContentManager().extractContent(inform);
					ArrayList lRes = (ArrayList)res.getItems();
					ExecResult exRes = (ExecResult) lRes.get(0);
					
					ArrayList ls = new ArrayList();
					ls.add(new Integer(exRes.getPassed()));
					ls.add(new Integer(exRes.getFailed()));
					ls.add(new Integer(exRes.getSkipped()));
					risultati.put(currentTester, ls);
					
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