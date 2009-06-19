package jade.core;

//#J2ME_EXCLUDE_FILE

import jade.core.remoteAgents.localImpl.LocalPlatformConnector;
import jade.tools.DummyAgent.DummyAgent;
import jade.util.leap.Properties;

public class TestRemoteAgent {
	public static void main(String[] args) {
		try {
			// Start a JADE Split container		
			Properties pp = new Properties();
			pp.setProperty(MicroRuntime.AGENTS_KEY, "larm:jade.core.remoteAgents.localImpl.LocalRemoteAgentManager");
			MicroRuntime.startJADE(pp, null);
			String name = MicroRuntime.getAgent("larm").getName();
			
			// Wait a bit
			Thread.sleep(5000);
			
			// Activate a "remote agent" using the LocalPlatformConnector
			Agent da = new DummyAgent();
			AgentRuntime.getInstance().start("da", da, null, AID.getPlatformID(), new LocalPlatformConnector(), null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
