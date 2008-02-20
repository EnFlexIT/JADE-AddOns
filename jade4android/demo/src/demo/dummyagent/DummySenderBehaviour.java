package demo.dummyagent;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class DummySenderBehaviour extends OneShotBehaviour {

	private ACLMessage theMsg;
	
	public DummySenderBehaviour(ACLMessage msg) {
		theMsg = msg;
	}
	
	public void action() {
		myAgent.send(theMsg);
	}

}
