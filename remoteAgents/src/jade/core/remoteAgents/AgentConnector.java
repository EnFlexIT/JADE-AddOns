package jade.core.remoteAgents;

import jade.lang.acl.ACLMessage;

/**
 * The interface to be implemented by modules connecting a "Remote Agent Proxy" to a Remote Agent  
 * @see jade.core.AgentRuntime
 * @see RemoteAgentManager
 */
public interface AgentConnector {
	
	public interface Listener {
		void msgOut(ACLMessage msg);
		void handleLeft();
	}

	void setListener(Listener l);
	void notifyDead() throws Exception;
	void msgIn(ACLMessage msg) throws Exception;

}
