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
		    
	 	AgentController ac2 = jade.createAgent(context.getBundle().getSymbolicName()+"2", "jade.osgi.test.MyBundleAgent[bundle-name=testBundle.testBundle]",null);
	  	ac2.start();

		}
		
	
	}	

	//@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("JadeClientActivator#stop");
		context.ungetService(jadeRef);
		agentFactory.stop();
	}

}
