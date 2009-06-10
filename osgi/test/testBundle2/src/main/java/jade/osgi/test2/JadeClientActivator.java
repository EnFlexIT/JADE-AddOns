package jade.osgi.test2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class JadeClientActivator implements BundleActivator {

	ServiceReference jadeRef;
	private MyBundleAgentFactory agentFactory;

	//@Override
	public void start(BundleContext context) throws Exception {
//		jadeRef = context.getServiceReference(JadeRuntimeService.class.getName());
//
//		if(jadeRef != null) {
//			JadeRuntimeService jade = (JadeRuntimeService) context.getService(jadeRef);
//			Object[] args = new Object[1];
//			AgentController ac = jade.createAgent("createbyTestBundle2", "jade.osgi.test.MyBundleAgent", "testBundle.testBundle", args);
//			ac.start();
//		}

		agentFactory = new MyBundleAgentFactory();
		agentFactory.init(context.getBundle());

	}

	//@Override
	public void stop(BundleContext context) throws Exception {
		context.ungetService(jadeRef);
		agentFactory.stop();
	}

}
