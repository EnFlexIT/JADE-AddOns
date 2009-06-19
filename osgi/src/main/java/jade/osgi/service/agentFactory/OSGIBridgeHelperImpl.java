package jade.osgi.service.agentFactory;

import jade.core.Agent;
import jade.osgi.AgentManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class OSGIBridgeHelperImpl implements OSGIBridgeHelper {
	private AgentManager agentManager;
	private BundleContext bundleContext;
	
	public OSGIBridgeHelperImpl(AgentManager agentManager) {
		this.agentManager = agentManager;
	}

	public void init(Agent a) {
		Bundle agentBundle = agentManager.getAgentBundle(a.getAID());
		if(agentBundle != null) {
			this.bundleContext = agentBundle.getBundleContext();
		}
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

}
