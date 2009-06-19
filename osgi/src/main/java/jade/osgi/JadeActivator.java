package jade.osgi;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.osgi.service.agentFactory.OSGIAgentLoader;
import jade.osgi.service.agentFactory.OSGIBridgeService;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.osgi.service.runtime.internal.JadeRuntimeServiceFactory;
import jade.osgi.service.runtime.internal.OsgiEventHandler;
import jade.osgi.service.runtime.internal.OsgiEventHandlerFactory;
import jade.util.ObjectManager;
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
import org.osgi.framework.ServiceFactory;

public class JadeActivator implements BundleActivator, BundleListener {

	private static final String PROFILE_PARAMETER_PREFIX = "jade.";
	private static final String JADE_CONF = PROFILE_PARAMETER_PREFIX + "conf";
	private static final String RESTART_AGENTS_ON_UPDATE_KEY = "restart-agents-on-update";
	private static final String RESTART_AGENTS_TIMEOUT_KEY = "restart-agents-timeout";
	private static final boolean RESTART_AGENTS_ON_UPDATE_DEFAULT = true;
	private static final long RESTART_AGENTS_TIMEOUT_DEFAULT = 10000;

	private BundleContext context;
	private static JadeActivator instance;
	private ContainerController container;
	private AgentManager agentManager;
	private OsgiEventHandlerFactory handlerFactory;
	
	public void start(BundleContext context) throws Exception {
		try {
			instance = this;
			this.context = context;
			this.agentManager = new AgentManager(context);
			
			// Create OsgiEventHandlerFactory 
			boolean restartAgents = RESTART_AGENTS_ON_UPDATE_DEFAULT;
			String restartAgentsOnUpdateS = System.getProperty(RESTART_AGENTS_ON_UPDATE_KEY);
			if(restartAgentsOnUpdateS != null) {
				restartAgents = Boolean.parseBoolean(restartAgentsOnUpdateS);
			}
			long restartTimeout = RESTART_AGENTS_TIMEOUT_DEFAULT;
			String restartAgentsTimeoutS = System.getProperty(RESTART_AGENTS_TIMEOUT_KEY);
			if(restartAgentsTimeoutS != null) {
				try {
					restartTimeout = Long.parseLong(restartAgentsTimeoutS);
				} catch(NumberFormatException e) {}
			}
			System.out.println(RESTART_AGENTS_ON_UPDATE_KEY + " " + restartAgents);
			System.out.println(RESTART_AGENTS_TIMEOUT_KEY + " " + restartTimeout);
			this.handlerFactory = new OsgiEventHandlerFactory(agentManager, restartAgents, restartTimeout);
			
			// Initialize jade container profile and start it
			Properties jadeProperties = new Properties();
			addJadeSystemProperties(jadeProperties);
			addJadeFileProperties(jadeProperties);
			addOSGIBridgeService(jadeProperties);
			startJadeContainer(jadeProperties);
			
			// Register an osgi agent loader
			ObjectManager.addLoader(ObjectManager.AGENT_TYPE, new OSGIAgentLoader(context, agentManager));

			// Register JRS service
			registerJadeRuntimeService();

			// Listen to bundle events
			context.addBundleListener(this);

		} catch(Exception e) {
			e.printStackTrace();
			stop(context);
		}
	}

	public void stop(BundleContext context) throws Exception {
		container.kill();
		handlerFactory.stop();
	}

	public static JadeActivator getInstance() {
		return instance;
	}
	
	public AgentManager getAgentManager() {
		return agentManager;
	}
	
	public BundleContext getBundleContext() {
		return context;
	}
	
	public synchronized void bundleChanged(BundleEvent event) {
		OsgiEventHandler handler = handlerFactory.getOsgiEventHandler(event.getBundle().getSymbolicName());
		handler.handleEvent(event);
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
			// find profile configuration in classpath
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
