package jade.android;

import java.io.InputStream;

import jade.core.MicroRuntime;
import jade.util.Event;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import android.app.Service;
import android.os.BinderNative;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.content.AssetManager;
import android.content.Resources;

public class MicroRuntimeService extends Service {

	private String myAgentName;
	private String TAG = "MicroRuntimeService";
	private final IBinder mBinder; 

	
	@Override
	protected void onCreate() {
		Log.v(TAG, "onCreate");
		// Start Jade
		try{
			Properties props = getProperties();
			String agents = props.getProperty(MicroRuntime.AGENTS_KEY);
			if(agents == null){
				Log.w(TAG, "No agents specified !!");
			}else{
				int index = agents.indexOf(":");
				myAgentName = agents.substring(0, index);
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
	}

	@Override
	protected void onDestroy() {
		// stop Jade
		Log.v(null, "Stopping Jade");
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

		public boolean execute(Object command) {
			boolean result = true;
			if(!MicroRuntime.isRunning()) {
				return false;
			}
			else {
				Event e = null;
				// incapsulate the command into an Event
				e = new Event(-1, command);
				try {
					AgentController agent = MicroRuntime.getAgent(myAgentName);
					agent.putO2AObject(e, AgentController.ASYNC);
					e.waitUntilProcessed();
					

				}  catch (Exception ex) {
					result = false;
					Log.e (null,ex.getMessage(),ex);				}
			}
			return result;

		}
	}
}
