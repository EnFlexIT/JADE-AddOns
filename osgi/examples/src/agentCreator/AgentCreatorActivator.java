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
				AgentController ac = jrs.createAgent("HelloAgent", "agentHolder.HelloAgent", null, "agentHolder");
				ac.start();
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Cannot start HelloAgent");
			}
		} else {
			System.out.println("Cannot start HelloAgent: JadeRuntimeService cannot be found");
		}
	}

	public void stop(BundleContext context) throws Exception {
		if(jadeRef != null) {
			context.ungetService(jadeRef);
		}
	}

}
