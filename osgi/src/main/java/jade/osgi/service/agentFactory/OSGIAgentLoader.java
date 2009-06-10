package jade.osgi.service.agentFactory;

import jade.core.Agent;
import jade.core.management.AgentLoader;
import jade.osgi.AgentManager;
import jade.util.leap.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class OSGIAgentLoader implements AgentLoader {
	
	private BundleContext context;
	private AgentManager agentManager;

	private static final String BUNDLE_NAME_PROPERTY = "bundle-name";

	public OSGIAgentLoader(BundleContext bc, AgentManager am) {
		this.context = bc;
		this.agentManager = am;
	}

	public Agent loadAgent(String className, Properties pp) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Agent agent = null;
		String bundleName = pp.getProperty(BUNDLE_NAME_PROPERTY);
		if(bundleName != null) {
			AgentFactoryService af = getAgentFactory(bundleName);
			if(af != null) {
				agent = af.createAgent(className);
				System.out.println("AFS("+bundleName+") created agent of class "+className);
				agentManager.addAgent(af.getBundle(), agent, true);
			}
		}
		return agent;
	}

	private AgentFactoryService getAgentFactory(String bundleName) throws ClassNotFoundException {
		AgentFactoryService af = null;
		try {
			String filter = "("+AgentFactoryService.SERVICE_NAME+"="+AgentFactoryService.AFS_PREFIX+bundleName+")"; 
			ServiceReference[] srs = context.getServiceReferences(AgentFactoryService.class.getName(), filter);
			if(srs != null) {
				af = (AgentFactoryService) context.getService(srs[0]);
			}
		} catch(InvalidSyntaxException e) {
			e.printStackTrace();
		}
		return af;
	}
}
