package jade.osgi;

import jade.core.AID;
import jade.core.Agent;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.osgi.service.runtime.internal.AgentInfo;
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
		System.out.println((created ? "Created" : "Accepted") + " agent " + agent + "(" + bundle.getSymbolicName()+")");
		String symbolicName = bundle.getSymbolicName();
		if(!agents.containsKey(symbolicName)) {
			agents.put(symbolicName, new ArrayList<AgentWrapper>());
		}
		agents.get(symbolicName).add(new AgentWrapper(agent, bundle, created));
		System.out.println("\nagents "+ agents+"\n");
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
		System.out.println("\nagents "+ agents+"\n");
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
				if(aw.restart) {
					agentsToRestart.get(symbolicName).add(new AgentInfo(a.getLocalName(), a.getClass().getName(), a.getArguments()));
					res = true;
				}
				System.out.println("Killing agent " + a.getLocalName());
				a.doDelete();
			}
		}
		System.out.println("\nagentsToRestart " + agentsToRestart);
		agents.remove(symbolicName);
		return res;
	}
	
	public synchronized void restartAgents(String symbolicName) {
		ServiceReference jrsReference = context.getServiceReference(JadeRuntimeService.class.getName());
		JadeRuntimeService jrs = (JadeRuntimeService) context.getService(jrsReference);
		if(agentsToRestart.containsKey(symbolicName)) {
    		for(AgentInfo ai: agentsToRestart.get(symbolicName)) {
    			try {
    				System.out.println("Restarting agent "+ai.getName());
    				jrs.createAgent(ai.getName(), ai.getClassName(), symbolicName, ai.getArgs());
    			} catch(Exception e) {
    				System.out.println("Agent " + ai.getName() + " cannot be restarted!");
    				e.printStackTrace();
    			}
    		}
    		agentsToRestart.remove(symbolicName);
		}
	}
	
	public synchronized void removeAgents(String symbolicName) throws Exception {
		System.out.println("Bundle "+symbolicName+" STOPPED ---> clean agentsToRestart list");
		agentsToRestart.remove(symbolicName);
		System.out.println("\nagentsToRestart "+ agentsToRestart+"\n");
		System.out.println("\nagents "+ agents+"\n");
	}
	
	private class AgentWrapper {
		private final Agent agent;
		private final Bundle bundle;
		private final boolean restart;
		
		public AgentWrapper(Agent agent, Bundle bundle, boolean restart) {
			this.agent = agent;
			this.bundle = bundle;
			this.restart = restart;
		}
	
	}
}
