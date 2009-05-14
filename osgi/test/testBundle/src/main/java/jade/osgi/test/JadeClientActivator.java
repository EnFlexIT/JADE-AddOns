package jade.osgi.test;

import jade.core.Agent;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.wrapper.AgentController;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class JadeClientActivator implements BundleActivator {

	ServiceReference jadeRef;
	@Override
	public void start(BundleContext context) throws Exception {
		jadeRef = context.getServiceReference(JadeRuntimeService.class.getName());  

		if (jadeRef != null)
		{
		    JadeRuntimeService jade = (JadeRuntimeService)  context.getService(jadeRef);
		    Agent myAgent = new MyBundleAgent();
		    AgentController ac = jade.acceptAgent(context.getBundle().getSymbolicName(), myAgent);
		    ac.start();
		    //		    AgentController agent = jade.getAgent("pippo");
//		    System.out.println("agent name" +agent.getName());
//		    System.out.println("killing agent "+agent.getName());
//		    agent.kill();
		    
		}

	}	

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("JadeClientActivator#stop");
		context.ungetService(jadeRef);
	}

}
