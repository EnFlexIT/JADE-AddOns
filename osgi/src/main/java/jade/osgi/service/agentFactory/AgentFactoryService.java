package jade.osgi.service.agentFactory;

import jade.core.Agent;
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public abstract class AgentFactoryService {
	
	public static final String SERVICE_NAME = "SERVICE_NAME";
	public static final String AFS_PREFIX = "AFS_";

	private Bundle myBundle;
	private ServiceRegistration serviceRegistration;

	public void init(Bundle bundle) {
		this.myBundle = bundle;
		BundleContext context = myBundle.getBundleContext();
		Dictionary<String, String> properties = new Hashtable<String, String>();
		properties.put(SERVICE_NAME, AFS_PREFIX+myBundle.getSymbolicName());
		this.serviceRegistration = context.registerService(AgentFactoryService.class.getName(), this, properties);
		System.out.println(myBundle.getSymbolicName()+ ": registered AgentFactoryService");
	}
	
	public void stop() {
		serviceRegistration.unregister();
	}

	Bundle getBundle() {
		return myBundle;
	}

	public Agent createAgent(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class c = myBundle.loadClass(className);
		return (Agent) c.newInstance();
	}
}
