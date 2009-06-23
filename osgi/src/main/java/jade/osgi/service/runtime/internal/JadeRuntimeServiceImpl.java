package jade.osgi.service.runtime.internal;

import jade.core.Agent;
import jade.osgi.internal.AgentManager;
import jade.osgi.service.agentFactory.AgentFactoryService;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

public class JadeRuntimeServiceImpl implements JadeRuntimeService {

	private ContainerController container;
	private AgentManager agentManager;
	private Bundle bundle;

	public JadeRuntimeServiceImpl(ContainerController container, AgentManager agentManager, Bundle bundle) {
		this.container = container;
		this.agentManager = agentManager;
		this.bundle = bundle;
	}

	public AgentController createAgent(String name, String className, Object[] args) throws Exception {
		return createAgent(name, className, args, bundle.getSymbolicName(), (String) bundle.getHeaders().get(Constants.BUNDLE_VERSION));
	}

	public AgentController createAgent(String name, String className, Object[] args, String bundleSymbolicName) throws Exception {
		System.out.println("createAgent(name = "+name+" bundle = "+bundleSymbolicName+") via JRS");
		String classNameMod = className + "["+AgentFactoryService.BUNDLE_NAME+"=" + bundleSymbolicName + "]";
		return container.createNewAgent(name, classNameMod, args);
	}
	
	public AgentController createAgent(String name, String className, Object[] args, String bundleSymbolicName, String bundleVersion) throws Exception {
		System.out.println("createAgent(name = "+name+" bundle = "+bundleSymbolicName+") via JRS");
		String classNameMod = className + "["+AgentFactoryService.BUNDLE_NAME+"=" + bundleSymbolicName + ";"+
			AgentFactoryService.BUNDLE_VERSION+"=" + bundleVersion + "]";
		return container.createNewAgent(name, classNameMod, args);
	}

	public AgentController getAgent(String localAgentName) throws Exception {
		System.out.println("Agent Controller requested by " + bundle.getSymbolicName());
		return container.getAgent(localAgentName);
	}

	public AgentController acceptAgent(String name, Agent agent) throws Exception {
		System.out.println("acceptAgent(name = "+name+" bundle = "+bundle.getSymbolicName() +")");
		AgentController myAgent = container.acceptNewAgent(name, agent);
		agentManager.addAgent(bundle, agent, false);
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

}
