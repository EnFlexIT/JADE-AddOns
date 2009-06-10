package jade.osgi.service.agentFactory;

import jade.core.ServiceHelper;
import org.osgi.framework.BundleContext;

public interface OSGIBridgeHelper extends ServiceHelper {
	
	public static final String SERVICE_NAME = "jade.osgi.service.agentFactory.OSGIBridge";
	
	public BundleContext getBundleContext();

}
