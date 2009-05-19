package jade.osgi;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.osgi.service.agentFactory.AgentFactory;
import jade.osgi.service.agentFactory.AgentFactoryService;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.osgi.service.runtime.internal.JadeRuntimeServiceFactory;
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
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;

public class JadeActivator implements BundleActivator {

	public static final String PROFILE_PARAMETER_PREFIX = "jade.";

	public static final String JADE_CONF = PROFILE_PARAMETER_PREFIX + "conf";

	private static BundleContext context;

	private ContainerController container;

	public void start(BundleContext context) throws Exception {

		try {

			this.context = context;

			Properties props = new Properties();
			addJadeSystemProperties(props);
			addJadeFileProperties(props);
			addAgentFactoryService(props);
			startJadeContainer(props);
			ServiceFactory factory = registerJadeRuntimeService();
			addAgentFactoryListener(factory);

		} catch(Exception e) {
			e.printStackTrace();
			stop(context);
		}

	}

	private void addAgentFactoryListener(ServiceFactory factory) throws InvalidSyntaxException {
		String filter = "(objectclass=" + AgentFactory.class.getName() + ")";

		context.addServiceListener((ServiceListener) factory,filter);
		//aggiungere pseudoregistrazioni di Agentfactory gia' esistenti
//		ServiceReference[] refs =
//	          context.getServiceReferences(null, filter);
//		if (refs != null) {
//	        for (ServiceReference r : refs) {
//	          factory.serviceChanged(
//	              new ServiceEvent(ServiceEvent.REGISTERED, r));             
//	        }
//
		context.addBundleListener((BundleListener)factory);
		
	}

	private void addAgentFactoryService(Properties pp) {
		String services = pp.getProperty(Profile.SERVICES);
		if(services == null) {
			pp.setProperty(Profile.SERVICES, AgentFactoryService.NAME);
		} else if(services.indexOf(AgentFactoryService.NAME) == -1) {
			pp.setProperty(Profile.SERVICES, services+";"+AgentFactoryService.NAME);
		}
	}

	public void stop(BundleContext context) throws Exception {
		container.kill();
	}

	public static BundleContext getBundleContext() {
		return context;
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
		ServiceFactory factory = new JadeRuntimeServiceFactory(container);
		context.registerService(JadeRuntimeService.class.getName(), factory, null);
		return factory;
	}

}
