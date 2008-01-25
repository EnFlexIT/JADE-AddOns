package jade.android;

import jade.core.Agent;


public class GatewayAgent extends Agent {
	public GatewayAgent() {
		// enable object2agent communication with queue of infinite length
		setEnabledO2ACommunication(true, 0);
	}

	@Override
	protected void setup() {
		CommandHandlerBehaviour myHandler = new CommandHandlerBehaviour();
		addBehaviour(myHandler);
	}

	
}
