package jade.osgi.service.agentFactory;

import jade.core.Agent;
import jade.core.management.AgentLoader;
import jade.util.leap.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class OSGIAgentLoader implements AgentLoader {
	
	private BundleContext context;

	private static final String BUNDLE_NAME_PROPERTY = "bundle-name";

	public OSGIAgentLoader(BundleContext ctx) {
		this.context = ctx;
	}

	public Agent loadAgent(String className, Properties pp) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Agent agent = null;
		String bundleName = pp.getProperty(BUNDLE_NAME_PROPERTY);
		System.out.println("OSGIAgentLoader trying to load agent " + className + " of bundle " + bundleName);
		if(bundleName != null) {
			AgentFactory af = getAgentFactory(bundleName);
			if(af != null) {
				System.out.println("OSGIAgentLoader retrieved AgentFactory for " + bundleName + " bundle");
				agent = af.createAgent(className);
			}
		}
		return agent;
	}

	private AgentFactory getAgentFactory(String bundleName) throws ClassNotFoundException {
		AgentFactory af = null;
		try {
			String filter = "("+AgentFactory.SERVICE_NAME+"="+AgentFactory.AFS_PREFIX+bundleName+")"; 
			ServiceReference[] srs = context.getServiceReferences(AgentFactory.class.getName(), filter);
			if(srs != null) {
				af = (AgentFactory) context.getService(srs[0]);
			}
		} catch(InvalidSyntaxException e) {
			e.printStackTrace();
		}
		return af;
	}
}
