package demo.dummyagent;


import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.gateway.GatewayAgent;


public class DummyAgent extends GatewayAgent {

	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	private ACLMessageListener updater;
	
	protected void setup() {
		super.setup();
		addBehaviour(new MessageReceiverBehaviour());
	}
	
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
			myLogger.log(Logger.INFO, "processCommand(): New GUI updater received and registered!");
			
			ACLMessageListener listener =(ACLMessageListener) command;
			
			updater = listener;
			releaseCommand(command);
		}
		
		else {
			myLogger.log(Logger.WARNING, "processCommand().Unknown command "+command);
		}
	}

	
	private class MessageReceiverBehaviour extends CyclicBehaviour{

		public void action() {
			ACLMessage msg = myAgent.receive();
			myLogger.log(Logger.INFO, "MessageReceiverBehaviour().Message received: " + this.hashCode());
			
			//if a message is available and a listener is available
			if (msg != null && updater != null){
				//callback the interface update function
				updater.onMessageReceived(msg);				
			} else {
				block();
			}
		}
	
	}

}
