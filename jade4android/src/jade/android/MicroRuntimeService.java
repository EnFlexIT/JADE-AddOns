package jade.android;

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

public class MicroRuntimeService extends Service {

	private String myAgentName = "gateway";

	@Override
	protected void onCreate() {

		// put my ref to be accessed by the agent and the activities
		Log.v(null, "MicroRuntimeService onCreate");
		// Start Jade
		String agentOptions = "gateway:jade.android.GatewayAgent";
		Properties pp = new Properties();
		pp.setProperty(MicroRuntime.HOST_KEY, "163.162.224.13");
		pp.setProperty(MicroRuntime.PORT_KEY, "2199");
		pp.setProperty("msisdn", "android");
		pp.setProperty(MicroRuntime.AGENTS_KEY, agentOptions);
		Log.v(null, "Starting Jade");
		MicroRuntime.startJADE(pp, null);

	}

	@Override
	protected void onStart(int startId, Bundle arguments) {

		Log.v(null, "MicroRuntimeService: onStart()");
	}

	@Override
	protected void onDestroy() {

		// stop Jade
		Log.v(null, "Stopping Jade");
		if (MicroRuntime.isRunning()) {
			MicroRuntime.stopJADE();
			Log.v(null, "Jade stopped");

		}
	}

	@Override
	public IBinder getBinder() {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new JadeBinder(); 

	private class JadeBinder extends BinderNative implements Command {

		public void execute(Object command) {
			if (MicroRuntime.isRunning()) {

				Event e = null;
				// incapsulate the command into an Event
				e = new Event(-1, command);
				try {
					AgentController agent = MicroRuntime.getAgent(myAgentName);
					agent.putO2AObject(e, AgentController.ASYNC);
					e.waitUntilProcessed();

				} catch (StaleProxyException exc) {
					exc.printStackTrace();
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}

		}

	}
}
