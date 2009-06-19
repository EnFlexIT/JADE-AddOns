package jade.core.remoteAgents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Event;
import jade.util.Logger;

//#MIDP_EXCLUDE_FILE

/**
 * This agent lives in the same container of a RemoteAgentManager and acts as a counterpart for a Remote Agent 
 */ 
public class RemoteAgentProxy extends Agent implements AgentConnector.Listener {
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	private AgentConnector myConnector;
	private boolean remoteAgentActive = true;
	
	public RemoteAgentProxy() {
		this.setEnabledO2ACommunication(true, 10);
	}
	
	protected void setup() {
		// Read the AgentConnector through O2A communication
		myLogger.log(Logger.CONFIG, "RemoteAgentProxy "+getName()+" starting...");
		while (true) {
			Event ev = (Event) getO2AObject();
			if (ev != null) {
				myConnector = (AgentConnector) ev.getParameter(0);
				myConnector.setListener(this);
				ev.notifyProcessed(null);
				break;
			}
			else {
				try {Thread.sleep(50);}catch (Exception e) {e.printStackTrace();}
			}
		}
		
		// The behaviour forwarding incoming messages to the real agent
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					// Forward the received message to the real agent
					try {
						myLogger.log(Logger.FINE, "RemoteAgentProxy "+getName()+": received incoming message --> pass it to the remote agent");
						myConnector.msgIn(msg);
					}
					catch (Exception e) {
						// Forward the exception so that the sender can get a FAILURE message back
						throw new RuntimeException("Remote agent unreachable", e);
					}
				}
				else {
					block();
				}
			}
		});
		myLogger.log(Logger.INFO, "RemoteAgentProxy "+getName()+" started");
	}

	// Mutual exclusion with handleLeft()
	protected synchronized void takeDown() {
		myLogger.log(Logger.INFO, "RemoteAgentProxy "+getName()+" taking down...");
		// I'm terminating --> notify the real agent if active
		if (remoteAgentActive) {
			if (myConnector != null) {
				try {
					myConnector.notifyDead();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				myConnector = null;
			}
		}
	}
	
	// This method is executed by the AgentConnector --> it requires mutual exclusion with takeDown()
	public synchronized void handleLeft() {
		myLogger.log(Logger.INFO, "RemoteAgentProxy "+getName()+": remote agent left the platform --> suicide");
		// The real agent left the platform --> terminates
		remoteAgentActive = false;
		doDelete();
	}
	
	public void msgOut(ACLMessage msg) {
		myLogger.log(Logger.FINE, "RemoteAgentProxy "+getName()+": received outgoing message from remote agent --> forward it");
		// The real agent sent a message --> forwards it
		send(msg);
	}
}
 