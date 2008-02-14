package jade.android.demo;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class DummySenderBehaviour extends OneShotBehaviour {

	private ACLMessage theMsg;
	private String receiver;
	private boolean isGUID;
	
	public DummySenderBehaviour(ACLMessage msg, String receiver, boolean isGUID) {
		
		theMsg = msg;
		this.receiver = receiver;
		this.isGUID = isGUID;
	}
	
	@Override
	public void action() {
		theMsg.addReceiver(new AID(receiver, isGUID));
		myAgent.send(theMsg);
	}

}
