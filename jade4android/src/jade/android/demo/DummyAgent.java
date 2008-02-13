package jade.android.demo;

import android.util.Log;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.gateway.GatewayAgent;
import jade.wrapper.gateway.GatewayBehaviour;

public class DummyAgent extends GatewayAgent {

	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	private ACLMessageListener updater;
	
	@Override
	protected void setup() {
		super.setup();
		addBehaviour(new MessageReceiverBehaviour());
	}
	
	@Override
	protected void processCommand(final Object command) {
		if (command instanceof Behaviour) {
			SequentialBehaviour sb = new SequentialBehaviour(this);
			sb.addSubBehaviour((Behaviour) command);
			sb.addSubBehaviour(new OneShotBehaviour(this) {
				public void action() {
					DummyAgent.this.releaseCommand(command);
				}
			});
			addBehaviour(sb);
		} else if (command instanceof ACLMessageListener) {
			myLogger.log(Logger.INFO, "New GUI updater received and registered!");
			
			ACLMessageListener listener =(ACLMessageListener) command;
			
			updater = listener;
			releaseCommand(command);
		}
		
		else {
			myLogger.log(Logger.WARNING, "Unknown command "+command);
		}
	}

	
	
	private class MessageReceiverBehaviour extends CyclicBehaviour{

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive();
			Log.v("jade.android.demo", "Message received: " + this.hashCode());
			
			//if a message is available and a listener is available
			if (msg != null && updater != null){
				//callback the interface update function
				updater.OnMessageReceived(msg);				
			} else {
				block();
			}
		}
	
	}

}
