package jade.osgi.service.agentFactory;

import jade.core.Agent;
import jade.core.FEService;
import jade.core.ServiceHelper;
import jade.osgi.AgentManager;
import jade.osgi.JadeActivator;

public class OSGIBridgeFEService extends FEService {
	
	private AgentManager agentManager;

	@Override
	public String getBEServiceClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceHelper getHelper(Agent a) {
		return new OSGIBridgeHelperImpl(getAgentManager());
	}

	@Override
	public String getName() {
		return OSGIBridgeHelper.SERVICE_NAME;
	}
	
	private AgentManager getAgentManager() {
		if(agentManager == null) {
			agentManager = JadeActivator.getInstance().getAgentManager();
		}
		return agentManager;
	}

}
