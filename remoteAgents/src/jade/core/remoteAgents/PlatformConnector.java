package jade.core.remoteAgents;

import jade.lang.acl.ACLMessage;

/**
 * The interface to be implemented by modules connecting an AgentRuntime to a platform
 * @see jade.core.AgentRuntime
 * @see RemoteAgentManager
 */
public interface PlatformConnector {

	public interface Listener {
		void msgIn(ACLMessage msg);
		void handleDead();
	}
	
	void initialize(Listener l) throws Exception;
	void shutdown();
	
	String[] joinPlatform(String agentName, String platformName) throws Exception;
	void leavePlatform() throws Exception;
	void msgOut(ACLMessage msg) throws Exception;
}
