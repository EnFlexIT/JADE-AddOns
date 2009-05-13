package jade.osgi.service.runtime;

import jade.core.Agent;
import jade.wrapper.AgentController;

public interface JadeRuntimeService {

	public AgentController createAgent(String name, String className, Object [] args) throws Exception;
	public AgentController acceptAgent(String name, Agent agent) throws Exception;
	public AgentController getAgent(String localName) throws Exception;
	public void kill() throws Exception;
	public String getContainerName() throws Exception;
	public String getPlatformName();
}
