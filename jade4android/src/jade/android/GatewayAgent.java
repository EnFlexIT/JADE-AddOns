package jade.android;

import android.util.Log;
import jade.core.Agent;


public class GatewayAgent extends Agent {
	public GatewayAgent() {
		Log.v(null, "GatewayAgent constructor");
		// enable object2agent communication with queue of infinite length
		setEnabledO2ACommunication(true, 0);
	}

	@Override
	protected void setup() {
		Log.v(null, "GatewayAgent setup...");
		CommandHandlerBehaviour myHandler = new CommandHandlerBehaviour();
		addBehaviour(myHandler);
		Log.v(null, "GatewayAgent setup completed.");
	}

	
}
