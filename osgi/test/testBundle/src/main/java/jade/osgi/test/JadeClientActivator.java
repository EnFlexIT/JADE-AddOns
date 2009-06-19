package jade.osgi.test;

import jade.core.Agent;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.wrapper.AgentController;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class JadeClientActivator implements BundleActivator {

	ServiceReference jadeRef;
	private MyBundleAgentFactory agentFactory;

	public void start(BundleContext context) throws Exception {
		// Register AgentFactory service
		Bundle b = context.getBundle();
		agentFactory = new MyBundleAgentFactory();
		agentFactory.init(b);

		jadeRef = context.getServiceReference(JadeRuntimeService.class.getName());
		if(jadeRef != null) {
			JadeRuntimeService jade = (JadeRuntimeService) context.getService(jadeRef);
			try {
    			Agent myAgent = new MyBundleAgent();
    			AgentController ac = jade.acceptAgent("accepted", myAgent);
    			ac.start();
			} catch(Exception e) {
				e.printStackTrace();
				// throw e;
			}
		}
	}

	public void stop(BundleContext context) throws Exception {
		context.ungetService(jadeRef);
		agentFactory.stop();
	}

}
