package jade.osgi;


import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class JadeActivator implements BundleActivator {


	public static final String PROFILE_PARAMETER_PREFIX = "jade.";

	public static final String JADE_CONF =PROFILE_PARAMETER_PREFIX +"conf";

	private static BundleContext context; 

	private AgentContainer container;
	
	

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		try{
			/*java.util.Properties properties = System.getProperties();
			Enumeration<Object> itProp = properties.keys();
			while(itProp.hasMoreElements()){
				String property = (String)itProp.nextElement();
				System.out.println("***************************************Property--> " + property + " value-->" + properties.getProperty(property));
			}*/
	
			
			
			this.context = context;
			Properties props = new Properties();

			//find jade properties 
			Set<Entry<Object, Object>> entrySet = System.getProperties().entrySet();
			for (Entry<Object,Object> entry : entrySet){
				String key = (String)entry.getKey();
				if (key.startsWith(PROFILE_PARAMETER_PREFIX)){
					props.setProperty(key.substring(PROFILE_PARAMETER_PREFIX.length()), (String)entry.getValue());
				}

			}
			String profileConf = System.getProperty(JADE_CONF);
			if (profileConf != null){
				//find profile configuration in classpath
				InputStream input = ClassLoader.getSystemResourceAsStream(profileConf);
				if (input == null){
					File f = new File(profileConf);
					if (f.exists()){
						input = new FileInputStream(f);
					}
				}
				if (input !=  null){
					Properties pp = new Properties();
					pp.load(input);
					Iterator it = pp.keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						if (!props.containsKey(key)) {
							props.setProperty(key, pp.getProperty(key));
						}
					}

				}
			}
			
			Profile profile = new ProfileImpl(props);
			Runtime.instance().setCloseVM(true);

			if (profile.getBooleanProperty(Profile.MAIN, true)) {
				container = Runtime.instance().createMainContainer(profile);
			} else {
				container = Runtime.instance().createAgentContainer(profile);
				
			}
		}catch(Exception e ){
			e.printStackTrace();
			stop(context);
		}
		
	}


	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		container.kill();
	}

	public static BundleContext getBundleContext() {
		return context;
	}


}
