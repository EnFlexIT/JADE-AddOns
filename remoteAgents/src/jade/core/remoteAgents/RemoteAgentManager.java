package jade.core.remoteAgents;

//#MIDP_EXCLUDE_FILE

import jade.core.MicroRuntime;
import jade.core.Agent;
import jade.util.Event;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/**
 * Base class for agents managing "remote agents"
 * @see jade.core.AgentRuntime
 */
public abstract class RemoteAgentManager extends Agent {

	private static final String REMOTE_AGENT_PROXY_CLASSNAME = "jade.core.RemoteAgentProxy";
	
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	private String dfName;
	
	protected void setup() {
		dfName = handleLocalDF();
		
		// Also initialize the logger for the RemoteAgentProxy Class so that settings can be done before
		// the first Remote Agent is created
		Logger.getMyLogger(REMOTE_AGENT_PROXY_CLASSNAME);
	}
	
	protected String[] handleNewAgent(String agentName, String platformName, AgentConnector connector) throws Exception {
		myLogger.log(Logger.INFO, "Remote Agent "+agentName+" trying to joing the platform...");
		// If a platform name is specified, it must match that of the local platform  
		String currentPlatformName = getAID().getHap();
		if (platformName == null || platformName.equals(currentPlatformName)) {
			AgentController ac = createAgent(agentName, REMOTE_AGENT_PROXY_CLASSNAME);
			Event ev = new Event(0, this, connector);
			ac.putO2AObject(ev, false);
			ev.waitUntilProcessed();
			myLogger.log(Logger.CONFIG, "Proxy for remote agent "+agentName+" successfully started");
			
			String[] addresses = getAID().getAddressesArray();
			String[] platformInfo = new String[addresses.length+3]; 
			platformInfo[0] = currentPlatformName;
			platformInfo[1] = here().getName();
			platformInfo[2] = dfName;
			for (int i = 0; i < addresses.length; ++i) {
				platformInfo[i+3] = addresses[i];
			}
			return platformInfo;
		}
		else {
			throw new Exception("Wrong platform name: expected "+getAID().getHap()+", found "+platformName);
		}
	}
	
	private AgentController createAgent(String agentName, String className) throws Exception {
		AgentController ac = null;
		ContainerController cc = getContainerController();
		if (cc != null) {
			ac = cc.createNewAgent(agentName, className, null);
			ac.start();
		}
		else {
			// We must be running on a Front-end
	 		MicroRuntime.startAgent(agentName, className, null);
			ac = MicroRuntime.getAgent(agentName);
		}
		return ac;
	}
	
	private String handleLocalDF() {
		// FIXME: Check if a local DF must be started and, return its name 
		return getDefaultDF().getLocalName();
	}
}
