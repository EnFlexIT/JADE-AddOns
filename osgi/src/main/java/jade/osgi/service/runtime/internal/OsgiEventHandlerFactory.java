package jade.osgi.service.runtime.internal;

import jade.osgi.AgentManager;
import java.util.HashMap;
import java.util.Map;

public class OsgiEventHandlerFactory {

	private boolean restartAgents;
	private long restartAgentsTimeout;
	private AgentManager agentManager;
	private Map<String, OsgiEventHandler> handlers = new HashMap<String, OsgiEventHandler>();

	public OsgiEventHandlerFactory(AgentManager am, boolean restartAgents, long restartAgentsTimeout) {
		this.agentManager = am;
		this.restartAgents = restartAgents;
		this.restartAgentsTimeout = restartAgentsTimeout;
	}

	public OsgiEventHandler getOsgiEventHandler(String symbolicName) {
		OsgiEventHandler handler = null;
		if(!handlers.containsKey(symbolicName)) {
			handler = new OsgiEventHandler(symbolicName, agentManager, restartAgents, restartAgentsTimeout);
			handlers.put(symbolicName, handler);
		} else {
			handler = handlers.get(symbolicName);
		}
		return handler;
	}
	
	public void stop() {
		for(OsgiEventHandler h: handlers.values()) {
			h.stop();
		}
		handlers.clear();
	}

}
