package jade.osgi;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.osgi.service.agentFactory.AgentFactoryService;
import jade.osgi.service.agentFactory.OSGIBridgeService;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.osgi.service.runtime.internal.JadeRuntimeServiceFactory;
import jade.osgi.service.runtime.internal.OsgiEventHandler;
import jade.osgi.service.runtime.internal.OsgiEventHandlerFactory;
import jade.util.leap.Properties;
import jade.wrapper.ContainerController;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;

public class JadeActivator implements BundleActivator, BundleListener, ServiceListener {

	public static final String PROFILE_PARAMETER_PREFIX = "jade.";
	public static final String JADE_CONF = PROFILE_PARAMETER_PREFIX + "conf";

	private static BundleContext context;
	private static JadeActivator instance;
	private ContainerController container;
	private static AgentManager agentManager;
	
	public void start(BundleContext context) throws Exception {
		try {
			instance = this;
			this.context = context;
			this.agentManager = new AgentManager(context);
			Properties props = new Properties();
			addJadeSystemProperties(props);
			addJadeFileProperties(props);
			addOSGIBridgeService(props);
			startJadeContainer(props);
			ServiceFactory factory = registerJadeRuntimeService();
			addAgentFactoryListener(factory);
			context.addBundleListener(this);
		} catch(Exception e) {
			e.printStackTrace();
			stop(context);
		}
	}
	
	public static JadeActivator getInstance() {
		return instance;
	}
	
	public static AgentManager getAgentManager() {
		return agentManager;
	}
	
	public void stop(BundleContext context) throws Exception {
		container.kill();
	}

	public static BundleContext getBundleContext() {
		return context;
	}
	
	public synchronized void bundleChanged(BundleEvent event) {
		OsgiEventHandler handler =  OsgiEventHandlerFactory.getOsgiEventHandler(event.getBundle().getSymbolicName(), agentManager);
		handler.handleEvent(event);
	}
	
	public synchronized void serviceChanged(ServiceEvent event) {
		OsgiEventHandler handler =  OsgiEventHandlerFactory.getOsgiEventHandler(event.getServiceReference().getBundle().getSymbolicName(), agentManager);
		handler.handleEvent(event);
	}
	
	private void addAgentFactoryListener(ServiceFactory factory) throws InvalidSyntaxException {
		String filter = "(objectclass=" + AgentFactoryService.class.getName() + ")";

		context.addServiceListener(this, filter);
		//aggiungere pseudoregistrazioni di Agentfactory gia' esistenti
//		ServiceReference[] refs =
//	          context.getServiceReferences(null, filter);
//		if (refs != null) {
//	        for (ServiceReference r : refs) {
//	          factory.serviceChanged(
//	              new ServiceEvent(ServiceEvent.REGISTERED, r));             
//	        }
//
		context.addBundleListener(this);
		
	}

	private void addOSGIBridgeService(Properties pp) {
		String services = pp.getProperty(Profile.SERVICES);
		String serviceName = OSGIBridgeService.class.getName();
		if(services == null) {
			pp.setProperty(Profile.SERVICES, serviceName);
		} else if(services.indexOf(serviceName) == -1) {
			pp.setProperty(Profile.SERVICES, services+";"+serviceName);
		}
	}

	private void addJadeSystemProperties(Properties props) {
		Set<Entry<Object, Object>> entrySet = System.getProperties().entrySet();
		for(Entry<Object, Object> entry : entrySet) {
			String key = (String) entry.getKey();
			if(key.startsWith(PROFILE_PARAMETER_PREFIX)) {
				props.setProperty(key.substring(PROFILE_PARAMETER_PREFIX.length()), (String) entry.getValue());
			}
		}
	}

	private void addJadeFileProperties(Properties props) throws Exception {
		String profileConf = System.getProperty(JADE_CONF);
		if(profileConf != null) {
			//find profile configuration in classpath
			InputStream input = ClassLoader.getSystemResourceAsStream(profileConf);
			if(input == null) {
				File f = new File(profileConf);
				if(f.exists()) {
					input = new FileInputStream(f);
				}
			}
			if(input != null) {
				Properties pp = new Properties();
				pp.load(input);
				Iterator it = pp.keySet().iterator();
				while(it.hasNext()) {
					String key = (String) it.next();
					if(!props.containsKey(key)) {
						props.setProperty(key, pp.getProperty(key));
					}
				}

			}
		}

	}

	private void startJadeContainer(Properties props) {
		Profile profile = new ProfileImpl(props);
		Runtime.instance().setCloseVM(false);
		if(profile.getBooleanProperty(Profile.MAIN, true)) {
			container = Runtime.instance().createMainContainer(profile);
		} else {
			container = Runtime.instance().createAgentContainer(profile);
		}
	}
	
	private ServiceFactory registerJadeRuntimeService() {
		ServiceFactory factory = new JadeRuntimeServiceFactory(container, agentManager);
		context.registerService(JadeRuntimeService.class.getName(), factory, null);
		return factory;
	}
	
}
