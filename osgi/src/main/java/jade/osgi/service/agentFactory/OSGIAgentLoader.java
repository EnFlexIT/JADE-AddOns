package jade.osgi.service.agentFactory;

import jade.core.Agent;
import jade.osgi.AgentManager;
import jade.util.ObjectManager;
import jade.util.leap.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class OSGIAgentLoader implements ObjectManager.Loader {
	
	private BundleContext context;
	private AgentManager agentManager;

	private static final String BUNDLE_NAME_PROPERTY = "bundle-name";

	public OSGIAgentLoader(BundleContext bc, AgentManager am) {
		this.context = bc;
		this.agentManager = am;
	}

	public Object load(String className, Properties pp) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Agent agent = null;
		String bundleName = pp.getProperty(BUNDLE_NAME_PROPERTY);
		if(bundleName != null) {
			ServiceReference sr = getAgentFactoryService(bundleName);
			if(sr != null) {
				AgentFactoryService afs = (AgentFactoryService) context.getService(sr);
				agent = afs.createAgent(className);
				// System.out.println("AFS("+bundleName+") created agent of class "+className);
				agentManager.addAgent(afs.getBundle(), agent, true);
			}
			context.ungetService(sr);
		}
		return agent;
	}

	private ServiceReference getAgentFactoryService(String bundleName) throws ClassNotFoundException {
		ServiceReference af = null;
		try {
			String filter = "("+AgentFactoryService.SERVICE_NAME+"="+AgentFactoryService.AFS_PREFIX+bundleName+")"; 
			ServiceReference[] srs = context.getServiceReferences(AgentFactoryService.class.getName(), filter);
			if(srs != null) {
				af = srs[0];
			}
		} catch(InvalidSyntaxException e) {
			e.printStackTrace();
		}
		return af;
	}
}
