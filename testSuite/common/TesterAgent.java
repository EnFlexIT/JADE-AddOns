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

package test.common;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.util.leap.List;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import test.common.testerAgentControlOntology.*;

public abstract class TesterAgent extends Agent {
	private Codec codec;
	private TestGroupExecutor executor;
	
	private boolean debugMode = false;
	private boolean remoteControlMode = false;
	private String remoteControllerName;
	
	protected void setup() {	
		// Register language and ontology used to communicate with other
		// agents (if any) controlling the execution of this TesterAgent 
		// (usually the JADE TestSuiteAgent)
		codec = new SLCodec();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(TesterAgentControlOntology.getInstance());
		
		// Retrieve the TestGroup to execute
		TestGroup tg = getTestGroup();
		
		// Get test group parameters
		List args = tg.getArgumentsSpecification();
		if (args != null && args.size() > 0) { 
			InsertArgumentsDlg.insertValues(args);
		}
		tg.setArguments(args);
		
		// Add the behaviour to interact with other agents (if any) 
		// controlling the execution of this TesterAgent 
		// (usually the JADE TestSuiteAgent)
		addBehaviour(new Controller(this));
		
		// Add the behaviour to execute the test group
		executor = new TestGroupExecutor(this, tg) {
			public int onEnd() {
				myAgent.doDelete();
				return 0;
			}
		};
		addBehaviour(executor);
	}	
		
	protected void takeDown() {
		if (remoteControlMode) {
			// If the execution of this tester agent was controlled by 
			// another agent (usually the JADE TestSuiteAgent),
			// notify it about termination
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID(remoteControllerName, AID.ISLOCALNAME));
			msg.setLanguage(codec.getName());
			msg.setOntology(TesterAgentControlOntology.getInstance().getName());
			try {
				getContentManager().fillContent(msg, new Terminated(getAID()));
				send(msg);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			// Otherwise notify the user via stdout
			System.out.println("Exit...");
		}
	}
	
	/**
	   Subclasses should implement this method to define the test
	   group to be executed by this TesterAgent
	 */
	protected abstract TestGroup getTestGroup();
		
	
	private void perform(Configure config) {
		Boolean b = config.getDebugMode();
		if (b != null) {
			debugMode = b.booleanValue();
		}
		b = config.getRemoteControlMode();
		if (b != null) {
			remoteControlMode = b.booleanValue();
		}
		String s = config.getRemoteControllerName();
		if (s != null) {
			remoteControllerName = s;
		}
	}
	
	private void perform(Resume r) {
		executor.resume();
	}
		
	/**
	   Called by the TestGroupExecutor behaviour before the execution
	   of each test in the group to know whether to execute the test
	   or pause.
	 */
	boolean getDebugMode() {
		return debugMode;
	} 
	
	/**
	   Inner class Controller. This is the behaviour that handles 
	   execution control requests from other agents (if any) 
		 controlling the execution of this TesterAgent (usually the 
		 JADE TestSuiteAgent)
	 */
	class Controller extends CyclicBehaviour {
		private MessageTemplate mt = MessageTemplate.MatchOntology(TesterAgentControlOntology.getInstance().getName());
		
		Controller (Agent a) {
			super(a);
		}
		
		public void action() {
			ACLMessage msg = receive(mt);
			if (msg != null) {
				try {
					switch (msg.getPerformative()) {
					case ACLMessage.REQUEST:
						Action act = (Action) myAgent.getContentManager().extractContent(msg);
						AgentAction a = (AgentAction) act.getAction();
						if (a instanceof Configure) {
							perform((Configure) a);
						}
						else if (a instanceof Resume) {
							perform((Resume) a);
						}
						else {
							System.out.println("Unknown action: "+a);
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}
		}
	}
}