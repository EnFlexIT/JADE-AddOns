package jade.osgi.test;

import jade.core.Agent;
import jade.core.ServiceException;
import jade.osgi.OSGIBridgeHelper;
import org.osgi.framework.BundleContext;

public class MyBundleAgent extends Agent {

	@Override
	protected void setup() {
		//System.out.println(this.getLocalName() +" UPDATED!");
		//System.out.println(this.getLocalName() +" STARTING...");
		
		try {
			OSGIBridgeHelper afHelper = (OSGIBridgeHelper) getHelper(OSGIBridgeHelper.SERVICE_NAME);
			afHelper.init(this);
			BundleContext context = afHelper.getBundleContext();
//			if(context != null) {
//				System.out.println(this.getLocalName() +" retrieve bundle context");
//			}
		} catch(ServiceException e) {
			e.printStackTrace();
		}
		System.out.println(this.getLocalName() +" STARTED!");
	}

	@Override
	protected void takeDown() {
		//System.out.println(this.getLocalName() +" STOPPED!");
	}


}
