package jade.android;

import jade.core.MicroRuntime;
import jade.core.NotFoundException;
import jade.imtp.leap.JICP.JICPProtocol;
import jade.util.Event;
import jade.util.Logger;

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
	
	//Instance of the Logger
	private static final Logger myLogger = Logger.getMyLogger(MicroRuntimeService.class.getName()); 


	
	@Override
	protected void onCreate() {
		Log.v(TAG, "onCreate");
		// Start Jade
		
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

		

		public void checkJADE() throws Exception {
			myLogger.log(Logger.INFO, "JadeBinder.checkJade(): starting checkJade");
			if (!MicroRuntime.isRunning()){
				MicroRuntime.startJADE(jadeProperties, null);
				myAgentName = (String)jadeProperties.get(JICPProtocol.MEDIATOR_ID_KEY);
				//FIXME: Indagare da dove è meglio prendere il nome dell'agente
				MicroRuntime.startAgent(myAgentName, gatewayClassName, gatewayAgentArgs);
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
			try {
				MicroRuntime.killAgent(myAgentName);
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				myLogger.log(Logger.SEVERE, "JadeBinder.shutdownJADE(): Wrong agent name!");
			}
			MicroRuntime.stopJADE();			
		}

		
		public String getAgentName() {
			return myAgentName;
		}
	}
}
