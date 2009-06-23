package agentHolder;

import jade.core.Agent;
import jade.core.ServiceException;
import jade.osgi.service.agentFactory.OSGIBridgeHelper;
import org.osgi.framework.BundleContext;

public class HelloAgent extends Agent {
	
	@Override
	protected void setup() {
		System.out.println("Hello!");
		try {
			OSGIBridgeHelper afHelper = (OSGIBridgeHelper) getHelper(OSGIBridgeHelper.SERVICE_NAME);
			afHelper.init(this);
			BundleContext context = afHelper.getBundleContext();
			if(context != null) {
				System.out.println(this.getLocalName() +" is packaged in bundle " + context.getBundle().getSymbolicName());
			}
		} catch(ServiceException e) {
			System.out.println("HelloAgent cannot be started");
		}
	}

}
