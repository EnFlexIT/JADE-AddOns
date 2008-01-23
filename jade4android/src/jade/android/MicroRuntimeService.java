package jade.android;

import jade.core.MicroRuntime;
import jade.util.Event;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;
import android.app.Service;
import android.os.BinderNative;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;

public class MicroRuntimeService extends Service {

	private String myAgentName = "myDummy2";

	@Override
	protected void onCreate() {

		// put my ref to be accessed by the agent and the activities
		Log.v(null, "LocalJadeService onCreate");
		// Start Jade
		String agentOptions = "myDummy2:com.ughetti.DummyAgent(pippo pluto)";
		Properties pp = new Properties();
		pp.setProperty(MicroRuntime.HOST_KEY, "localhost");
		pp.setProperty(MicroRuntime.PORT_KEY, "1099");
		pp.setProperty("msisdn", "android");
		pp.setProperty(MicroRuntime.AGENTS_KEY, agentOptions);
		Log.v(null, "Starting Jade");
		MicroRuntime.startJADE(pp, null);

	}

	@Override
	protected void onStart(int startId, Bundle arguments) {

		Log.v(null, "LocalJadeService: onStart()");
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
	private final IBinder mBinder = new BinderNative() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) {

			if (MicroRuntime.isRunning()) {

				Event e = null;
				// incapsulate the command into an Event
				e = new Event(-1, data, reply);
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
				// wait until the answer is ready
				synchronized (mBinder) {
					mBinder.notifyAll();
				}
			}
			return true;

		}
	};

}
