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

	//@Override
	public void start(BundleContext context) throws Exception {
		jadeRef = context.getServiceReference(JadeRuntimeService.class.getName());
		// Register AgentFactory service
		Bundle b = context.getBundle();
		agentFactory = new MyBundleAgentFactory();
		agentFactory.init(b);

		if(jadeRef != null) {
			JadeRuntimeService jade = (JadeRuntimeService) context.getService(jadeRef);

			Agent myAgent = new MyBundleAgent();
			AgentController ac = jade.acceptAgent(context.getBundle().getSymbolicName() + "_accepted", myAgent);
			ac.start();

			try {
    			AgentController ac2 = jade.createAgent(context.getBundle().getSymbolicName() + "_created", "jade.osgi.test.MyBundleAgent", null);
    			ac2.start();
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Cannot create agent: "+e);
			}
		}
	}

	//@Override
	public void stop(BundleContext context) throws Exception {
		context.ungetService(jadeRef);
		agentFactory.stop();
	}

}
