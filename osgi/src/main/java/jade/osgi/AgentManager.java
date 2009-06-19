package jade.osgi;

import jade.core.AID;
import jade.core.Agent;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.osgi.service.runtime.internal.AgentInfo;
import jade.wrapper.AgentController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class AgentManager {

	private BundleContext context;
	
	private Map<String, List<AgentWrapper>> agents = new HashMap<String, List<AgentWrapper>>();
	private Map<String, List<AgentInfo>> agentsToRestart = new HashMap<String, List<AgentInfo>>();
	
	public AgentManager(BundleContext context) {
		this.context = context;
	}

	public synchronized void addAgent(Bundle bundle, Agent agent, boolean created) {
		String symbolicName = bundle.getSymbolicName();
		if(!agents.containsKey(symbolicName)) {
			agents.put(symbolicName, new ArrayList<AgentWrapper>());
		}
		agents.get(symbolicName).add(new AgentWrapper(agent, bundle, created));
		System.out.println("ADDED agent: agents "+ agents);
	}

	public synchronized void removeAgent(AID aid) {
		found:
		for(Entry<String, List<AgentWrapper>> entry: agents.entrySet()) {
			List<AgentWrapper> agentWrappers = entry.getValue();
			Iterator<AgentWrapper> it = agentWrappers.iterator();
			while(it.hasNext()) {
				AgentWrapper aw = it.next();
				if(aw.agent.getAID().equals(aid)) {
					System.out.println("Killed agent "+ aw);
					it.remove();
					break found;
				}
			}
		}
		System.out.println("REMOVED agent: agents "+ agents);
	}
	
	public synchronized Bundle getAgentBundle(AID aid) {
		Bundle result = null;
		found: 
		for(Entry<String, List<AgentWrapper>> entry : agents.entrySet()) {
			for(AgentWrapper aw : entry.getValue()) {
				if(aw.agent.getAID().equals(aid)) {
					result = aw.bundle;
					break found;
				}
			}
		}
		return result;
	}

	public synchronized boolean killAgents(String symbolicName) {
		boolean res = false;
		agentsToRestart.put(symbolicName, new ArrayList<AgentInfo>());
		if(agents.containsKey(symbolicName)) {
			List<AgentWrapper> agentWrappers = agents.get(symbolicName);
			for(AgentWrapper aw: agentWrappers) {
				Agent a = aw.agent;
				if(aw.created) {
					agentsToRestart.get(symbolicName).add(new AgentInfo(a.getLocalName(), a.getClass().getName(), a.getArguments()));
					res = true;
				}
				System.out.println("KILLING " + a.getLocalName());
				a.doDelete();
			}
		}
		System.out.println("KILL AGENTS: agentsToRestart " + agentsToRestart);
		agents.remove(symbolicName);
		return res;
	}
	
	public synchronized void bundleUpdated(String symbolicName) {
		if(agentsToRestart.containsKey(symbolicName)) {
			List<AgentInfo> agents = agentsToRestart.get(symbolicName);
			for(AgentInfo ai: agents) {
				ai.setUpdated(true);
			}
		}
		System.out.println("BUNDLE UPDATED: agentsToRestart " + agentsToRestart);
	}

	public synchronized void restartAgents(String symbolicName) {
		System.out.println("RESTART AGENTS: agentsToRestart " + agentsToRestart);
		if(agentsToRestart.containsKey(symbolicName)) {
			ServiceReference jrsReference = context.getServiceReference(JadeRuntimeService.class.getName());
			JadeRuntimeService jrs = (JadeRuntimeService) context.getService(jrsReference);
			if(jrs != null) {
	    		for(AgentInfo ai: agentsToRestart.get(symbolicName)) {
	    			try {
	    				if(ai.isUpdated()) {
	        				System.out.println("RESTARTING agent "+ai);
	        				AgentController ac = jrs.createAgent(ai.getName(), ai.getClassName(), symbolicName, ai.getArgs());
	        				ac.start();
	    				}
	    			} catch(Exception e) {
	    				System.out.println("Agent " + ai.getName() + " cannot be restarted!");
	    				e.printStackTrace();
	    			}
	    		}
			} else {
				System.out.println("JadeRuntimeService for "+symbolicName+" no more active! Cannot restart agents!");
			}
    		agentsToRestart.remove(symbolicName);
		}
	}
	
	public synchronized void removeAgents(String symbolicName) throws Exception {
		agentsToRestart.remove(symbolicName);
		System.out.println("CLEAR AGENTS: agentsToRestart "+ agentsToRestart);
		System.out.println("CLEAR AGENTS: agents "+ agents);
	}
	
	private class AgentWrapper {
		private final Agent agent;
		private final Bundle bundle;
		private final boolean created;
		
		public AgentWrapper(Agent agent, Bundle bundle, boolean created) {
			this.agent = agent;
			this.bundle = bundle;
			this.created = created;
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("(");
			sb.append("agent: " + agent.getLocalName());
			sb.append(" bundle: " + bundle.getSymbolicName());
			sb.append(" created: " + created);
			sb.append(")");
			return sb.toString();
		}
	
	}

}
