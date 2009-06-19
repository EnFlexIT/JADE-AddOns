package jade.core.remoteAgents.localImpl;

//#J2ME_EXCLUDE_FILE

import jade.core.remoteAgents.PlatformConnector;
import jade.lang.acl.ACLMessage;

public class LocalPlatformConnector implements PlatformConnector {
	private PlatformConnector.Listener myListener;
	private LocalAgentConnector peer;
	
	public void initialize(Listener l) throws Exception {
		myListener = l;
	}
	public void shutdown() {
	}
	
	public String[] joinPlatform(String agentName, String platformName) throws Exception {
		peer = LocalRemoteAgentManager.getInstance().serveJoinPlatform(agentName, platformName, this);
		return peer.getStrings();
	}
	
	public void leavePlatform() throws Exception {
		peer.serveLeavePlatform();
	}
	
	public void msgOut(ACLMessage msg) throws Exception {
		peer.serveMsgOut(msg);	
	}

	void serveHandleDead() {
		myListener.handleDead();
	}
	
	void serveMsgIn(ACLMessage msg) {
		myListener.msgIn(msg);
	}
}
