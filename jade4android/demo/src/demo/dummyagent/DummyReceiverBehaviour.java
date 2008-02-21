package demo.dummyagent;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class DummyReceiverBehaviour extends OneShotBehaviour {

	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	private ACLMessageListener msgList;
	
	public DummyReceiverBehaviour(ACLMessageListener listener) {
		msgList = listener;
	}
	
	public void action() {
		MessageReceiverBehaviour msgRecBh = new MessageReceiverBehaviour(msgList);
		myAgent.addBehaviour(msgRecBh);
	}

	
	private class MessageReceiverBehaviour extends CyclicBehaviour{

		private ACLMessageListener msgListener;

		
		public MessageReceiverBehaviour(ACLMessageListener msgList) {
			msgListener = msgList;
		}

		public void action() {
			ACLMessage msg = myAgent.receive();
			myLogger.log(Logger.INFO, "action().Message received: " + this.hashCode());
			
			//if a message is available
			if (msg != null){
				//callback the interface update function
				msgListener.onMessageReceived(msg);
			} else {
				block();
			}
		}
	
	}
}
