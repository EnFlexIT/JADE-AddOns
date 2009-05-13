package jade.osgi.service.runtime.internal;

import java.util.ArrayList;
import java.util.List;

import jade.core.Agent;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import org.osgi.framework.Bundle;

public class JadeRuntimeServiceImpl implements JadeRuntimeService {

	private List<AgentController> agents = new ArrayList<AgentController>(); 
	private ContainerController container;
	private Bundle bundle;
	public JadeRuntimeServiceImpl(ContainerController container, Bundle bundle) {
		this.container = container;
		this.bundle = bundle;
	}
	public AgentController createAgent(String name, String className, Object[] args) throws Exception {
		System.out.println("CreateAgent: Agent Creation requested by bundle "+bundle.getSymbolicName());
		AgentController agent = container.createNewAgent(name, className, args);
		if(agent != null) {
			agents.add(agent);
		}
		return agent;
	}

	
	
	public AgentController getAgent(String localAgentName) throws Exception {
		System.out.println("Agent Controller requested by bundle "+bundle.getSymbolicName());
		return container.getAgent(localAgentName);
	}
	public AgentController acceptAgent(String name, Agent agent) throws Exception {
		System.out.println("AcceptAgent: Agent Creation requested by bundle "+bundle.getSymbolicName());
		System.out.println("agent classloader "+agent.getClass().getClassLoader());
		AgentController myAgent = container.acceptNewAgent(name, agent);
		System.out.println("AgentController classloader "+myAgent.getClass().getClassLoader());
		if(myAgent!= null) {
			agents.add(myAgent);
		}
		return myAgent;
	}
	public String getContainerName() throws Exception {
			return container.getContainerName();
	}
	public String getPlatformName() {
		return container.getPlatformName();
	}
	public void kill() throws Exception {
		container.kill();
		
	}
	
	public void removeAgents() throws Exception {
		System.out.println("JadeRuntimeServiceImpl#removeAgents");
		for (AgentController agent : agents) {
			System.out.println("Killing agent "+agent.getName());
			System.out.println("Current ClassLoader "+this.getClass().getClassLoader());
			agent.kill();
		}
	}
	
	

}
