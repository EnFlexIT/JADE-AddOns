package jade.osgi.test2;

import jade.core.Agent;
import jade.core.ServiceException;
import jade.osgi.OSGIBridgeHelper;
import org.osgi.framework.BundleContext;

public class MyBundleAgent extends Agent {

	@Override
	protected void setup() {
		System.out.println("Agent "+this.getLocalName() +" starting");
		
		try {
			OSGIBridgeHelper afHelper = (OSGIBridgeHelper) getHelper(OSGIBridgeHelper.SERVICE_NAME);
			BundleContext context = afHelper.getBundleContext();
			if(context != null) {
				System.out.println("Agent "+this.getLocalName() +" context of bundle "+context.getBundle().getSymbolicName());
			} else {
				System.out.println("Agent "+this.getLocalName() +" context null");
			}
		} catch(ServiceException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void takeDown() {
		System.out.println("Agent "+this.getLocalName() +" stopped");
	}

}
