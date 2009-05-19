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

	private List<AgentController> agentsToremove = new ArrayList<AgentController>(); 
	private List<AgentInfo> agentsTorestart = new ArrayList<AgentInfo>();
	private ContainerController container;
	private Bundle bundle;
	public JadeRuntimeServiceImpl(ContainerController container, Bundle bundle) {
		this.container = container;
		this.bundle = bundle;
	}
	public AgentController createAgent(String name, String className, Object[] args) throws Exception {
		System.out.println("CreateAgent: Agent Creation requested by agentBundle "+bundle.getSymbolicName());
		String classNameMod = className+"[bundle-name="+bundle.getSymbolicName()+"]";
		AgentController agent = container.createNewAgent(name, classNameMod, args);
		if(agent != null) {
			agentsToremove.add(agent);
			//agentsTorestart.add(new AgentInfo(name,className,args));
		}
		return agent;
	}

	public AgentController createAgent(String name, String className, String bundleSymbolicName,Object[] args) throws Exception {
		System.out.println("CreateAgent: Agent Creation requested by agentBundle "+bundle.getSymbolicName());
		String classNameMod = className+"[bundle-name="+bundleSymbolicName+"]";
		AgentController agent = container.createNewAgent(name, classNameMod, args);
		if(agent != null) {
			agentsToremove.add(agent);
			if(!bundleSymbolicName.equals(bundle.getSymbolicName())) {
				//FIXME questo agente viene inserito nel JadeRuntime service del bundle sbagliato (serve un ref alla factory?)
				System.out.println("Adding "+agent.getName()+"to restart it later");
				agentsTorestart.add(new AgentInfo(name,className,args));
			}
		}
		return agent;
	}
	
	
	public AgentController getAgent(String localAgentName) throws Exception {
		System.out.println("Agent Controller requested by agentBundle "+bundle.getSymbolicName());
		return container.getAgent(localAgentName);
	}
	public AgentController acceptAgent(String name, Agent agent) throws Exception {
		System.out.println("AcceptAgent: Agent Creation requested by agentBundle "+bundle.getSymbolicName());
		System.out.println("agent classloader "+agent.getClass().getClassLoader());
		AgentController myAgent = container.acceptNewAgent(name, agent);
		System.out.println("AgentController classloader "+myAgent.getClass().getClassLoader());
		if(myAgent!= null) {
			agentsToremove.add(myAgent);
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
	
	public void killAgents() throws Exception {
		System.out.println("JadeRuntimeServiceImpl#killAgents");
		for (AgentController agent : agentsToremove) {
			System.out.println("Killing agent "+agent.getName());
			System.out.println("Current ClassLoader "+this.getClass().getClassLoader());
			agent.kill();
		}
	}
	public void removeAgents() throws Exception {
		System.out.println("JadeRuntimeServiceImpl#removeAgents");
		agentsTorestart.clear();
	}
	
	public void restartAgents() throws Exception {
		System.out.println("JadeRuntimeServiceImpl#restartAgents");
		for (AgentInfo agentInfo : agentsTorestart) {
			createAgent(agentInfo.getName(), agentInfo.getClassName(), agentInfo.getArgs());
		}
	}

}
