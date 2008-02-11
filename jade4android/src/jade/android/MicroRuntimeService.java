package jade.android;

import jade.core.MicroRuntime;
import jade.util.Event;
import java.util.Map;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import android.app.Service;
import android.content.AssetManager;
import android.content.Resources;
import android.os.BinderNative;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MicroRuntimeService extends Service {

	private String myAgentName;
	private String TAG = "jade.android";
	
	private String gatewayClassName;
	private String[] gatewayAgentArgs;
	private Properties jadeProperties;
	
	private final IBinder mBinder = new JadeBinder(); 
	

	
	@Override
	protected void onCreate() {
		Log.v(TAG, "onCreate");
		// Start Jade
		try{
			Properties props = getProperties();
			String agents = props.getProperty(MicroRuntime.AGENTS_KEY);
			if((agents == null) || agents.length() == 0){
				Log.w(TAG, "No agents specified !!");
			}else{
				StringTokenizer st = new StringTokenizer(agents, ";");
				if(st.countTokens() > 1){
					Log.w(TAG, "Only the first agent will be started !!!");
				}
				String firstAgent = st.nextToken();
				int index = firstAgent.indexOf(":");
				myAgentName = firstAgent.substring(0, index);
				props.put(MicroRuntime.AGENTS_KEY, firstAgent);
			}
			Log.v(TAG, "Starting Jade with agent " + myAgentName);
			MicroRuntime.startJADE(props, null);
			
		}catch(Exception e){
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	protected void onStart(int startId, Bundle arguments) {
		Log.v(TAG, "MicroRuntimeService: onStart()");
		
		gatewayClassName = arguments.getString(JadeGateway.GATEWAY_CLASS_NAME);
		gatewayAgentArgs = arguments.getStringArray(JadeGateway.GATEWAY_AGENT_ARGS);
		jadeProperties = fillProperties(arguments.getBundle(JadeGateway.PROPERTIES_BUNDLE));
		
	}

	private Properties fillProperties(Bundle propBundle) {
		Properties pr = new Properties();
		
		Map m = propBundle.getAsMap();
		
		Set s = m.keySet();
		
		for (Iterator it = s.iterator(); it.hasNext(); ){
			String key = (String) it.next();
			pr.setProperty(key, (String) m.get(key) );
		}
		
		return pr;
	}
	
	@Override
	protected void onDestroy() {
		// stop Jade
		Log.v(TAG, "Stopping Jade");
		if (MicroRuntime.isRunning()) {
			MicroRuntime.stopJADE();
			Log.v(TAG, "Jade stopped");

		}
	}
	
	//read the run.properties from the assets folder
	private Properties getProperties() throws Exception {
		Resources resources = this.getResources(); 
        AssetManager aM = resources.getAssets(); 
		InputStream iS = aM.open("run.properties");
		Properties props = new Properties();
		props.load(iS);
		Log.v(TAG, "properties: " + props);
		return  props;
	}

	@Override
	public IBinder getBinder() {
		return mBinder;
	}


	private class JadeBinder extends BinderNative implements Command {
		
		public JadeBinder() {
			Log.v(TAG, "creating JadeBinder...");
		}

		public void execute(Object command) throws StaleProxyException,ControllerException,InterruptedException {
			execute(command,0);
		}

		
		//FIXME: Dobbiamo finire l'implementazione!!!!!!
		public void checkJADE() throws StaleProxyException, ControllerException, Exception {
			if (!MicroRuntime.isRunning()){
				MicroRuntime.startJADE(jadeProperties, null);
				
				//FIXME: Verifica argomenti startAgent()
				MicroRuntime.startAgent(null, gatewayClassName, gatewayAgentArgs);
			}
		}

		public void execute(Object command, long timeout) throws StaleProxyException, ControllerException, InterruptedException {
			Event e = null;
			//incapsulate the command into an Event
			e = new Event(-1, command);
			AgentController agent = MicroRuntime.getAgent(myAgentName);
			agent.putO2AObject(e, AgentController.ASYNC);
			e.waitUntilProcessed(timeout);
		}

		public void shutdownJADE() {
			// TODO Auto-generated method stub
			
		}
	}
}
