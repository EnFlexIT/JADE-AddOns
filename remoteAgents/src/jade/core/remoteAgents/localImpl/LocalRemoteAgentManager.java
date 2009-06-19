package jade.core.remoteAgents.localImpl;

import jade.core.remoteAgents.RemoteAgentManager;

//#J2ME_EXCLUDE_FILE

public class LocalRemoteAgentManager extends RemoteAgentManager {
	
	private static LocalRemoteAgentManager theInstance;
	
	public static LocalRemoteAgentManager getInstance() {
		return theInstance;
	}
	
	protected void setup() {
		super.setup();
		
		theInstance = this;
	}

	LocalAgentConnector serveJoinPlatform(String agentName, String platformName, LocalPlatformConnector pc) throws Exception {
		LocalAgentConnector lac = new LocalAgentConnector(pc);
		String[] result = handleNewAgent(agentName, platformName, lac);
		lac.setStrings(result);
		return lac;
	}
}
