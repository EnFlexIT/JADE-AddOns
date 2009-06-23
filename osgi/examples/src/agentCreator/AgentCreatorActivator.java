package agentCreator;

import jade.osgi.service.runtime.JadeRuntimeService;
import jade.wrapper.AgentController;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class AgentCreatorActivator implements BundleActivator {

	private ServiceReference jadeRef;

	public void start(BundleContext context) throws Exception {
		jadeRef = context.getServiceReference(JadeRuntimeService.class.getName());
		if(jadeRef != null) {
			JadeRuntimeService jrs = (JadeRuntimeService) context.getService(jadeRef);
			try {
				AgentController ac = jrs.createAgent("HelloAgent", "agentHolder.HelloAgent", null, "agentHolderBundle");
				ac.start();
			} catch(Exception e) {
				System.out.println("Cannot starting HelloAgent");
			}
		}
	}

	public void stop(BundleContext context) throws Exception {
		context.ungetService(jadeRef);
	}

}
