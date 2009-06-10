package jade.osgi.service.agentFactory;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.BaseService;
import jade.core.Filter;
import jade.core.IMTPException;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.ServiceException;
import jade.core.ServiceHelper;
import jade.core.VerticalCommand;
import jade.core.management.AgentManagementService;
import jade.core.management.AgentManagementSlice;
import jade.osgi.AgentManager;
import jade.osgi.JadeActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class OSGIBridgeService extends BaseService {

	public static final String NAME = OSGIBridgeHelper.SERVICE_NAME;

	private AgentManager agentManager;

	private AgentContainer myContainer;

	private CommandIncomingFilter incomingFilter;

	public void init(AgentContainer ac, Profile p) throws ProfileException {
		super.init(ac, p);
		myContainer = ac;
		incomingFilter = new CommandIncomingFilter();
	}

	@Override
	public void boot(Profile p) throws ServiceException {
		try {
			agentManager = JadeActivator.getAgentManager();
			AgentManagementService ams = (AgentManagementService) myContainer.getServiceFinder().findService(AgentManagementService.NAME);
			ams.addAgentLoader(new OSGIAgentLoader(JadeActivator.getBundleContext(), agentManager));
			System.out.println("OSGIBridgeService started");
		} catch(IMTPException e) {
			throw new ServiceException("Cannot retrieve AgentManagementService", e);
		}
	}

	public String getName() {
		return NAME;
	}
	
	public ServiceHelper getHelper(Agent a) throws ServiceException {
		return new OSGIBridgeHelperImpl();
	}
	
	public Filter getCommandFilter(boolean direction) {
		if(direction == Filter.INCOMING) {
			return incomingFilter;
		} else {
			return null;
		}
	}
	
	private class OSGIBridgeHelperImpl implements OSGIBridgeHelper {
		private BundleContext bundleContext;
		
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
	
	private class CommandIncomingFilter extends Filter {
		public boolean accept(VerticalCommand cmd) {
			if(cmd.getName().equals(AgentManagementSlice.INFORM_KILLED)) {
				handleInformKilled(cmd);
			}
			return true;
		}
		
		private void handleInformKilled(VerticalCommand cmd) {
			Object[] params = cmd.getParams();
			AID aid = (AID) params[0];
			agentManager.removeAgent(aid);
		}
	}
	
}
