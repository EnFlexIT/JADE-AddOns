package jade.core.remoteAgents.localImpl;

//#J2ME_EXCLUDE_FILE

import jade.core.remoteAgents.AgentConnector;
import jade.lang.acl.ACLMessage;

public class LocalAgentConnector implements AgentConnector {
	private AgentConnector.Listener myListener;
	private LocalPlatformConnector peer; 
	private String[] strings;
	
	public LocalAgentConnector(LocalPlatformConnector lpc) {
		peer = lpc;
	}
	public void setStrings(String[] ss) {
		strings = ss;
	}
	public String[] getStrings() {
		return strings;
	}
	public void setListener(Listener l) {
		myListener = l;		
	}
	public void notifyDead() throws Exception {
		peer.serveHandleDead();
		
	}
	public void msgIn(ACLMessage msg) throws Exception {
		peer.serveMsgIn(msg);
		
	}
	
	void serveLeavePlatform() {
		myListener.handleLeft();
	}
	
	void serveMsgOut(ACLMessage msg) {
		myListener.msgOut(msg);
	}

}
