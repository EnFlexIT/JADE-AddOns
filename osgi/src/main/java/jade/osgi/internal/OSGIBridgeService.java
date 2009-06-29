package jade.osgi.internal;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.BaseService;
import jade.core.Filter;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.ServiceException;
import jade.core.ServiceHelper;
import jade.core.VerticalCommand;
import jade.core.management.AgentManagementSlice;
import jade.osgi.OSGIBridgeHelper;

public class OSGIBridgeService extends BaseService {

	public static final String NAME = OSGIBridgeHelper.SERVICE_NAME;

	private AgentManager agentManager;
	private CommandIncomingFilter incomingFilter;

	public void init(AgentContainer ac, Profile p) throws ProfileException {
		super.init(ac, p);
		incomingFilter = new CommandIncomingFilter();
		agentManager = JadeActivator.getInstance().getAgentManager();
	}

	public String getName() {
		return NAME;
	}
	
	public ServiceHelper getHelper(Agent a) throws ServiceException {
		return new OSGIBridgeHelperImpl(agentManager);
	}
	
	public Filter getCommandFilter(boolean direction) {
		if(direction == Filter.INCOMING) {
			return incomingFilter;
		} else {
			return null;
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