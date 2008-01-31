package jade.android.demo;

import android.util.Log;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class DummyReceiverBehaviour extends OneShotBehaviour {

	private ACLMessageListener msgList;
	
	public DummyReceiverBehaviour(ACLMessageListener listener) {
		msgList = listener;
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		MessageReceiverBehaviour msgRecBh = new MessageReceiverBehaviour(msgList);
		myAgent.addBehaviour(msgRecBh);
	}

	
	private class MessageReceiverBehaviour extends CyclicBehaviour{

		private ACLMessageListener msgListener;

		
		public MessageReceiverBehaviour(ACLMessageListener msgList) {
			msgListener = msgList;
		}

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive();
			Log.v(null, "Message received");
			
			//if a message is available
			if (msg != null){
				//callback the interface update function
				msgListener.OnMessageReceived(msg);
			} else {
				block();
			}
		}
	
	}
}
