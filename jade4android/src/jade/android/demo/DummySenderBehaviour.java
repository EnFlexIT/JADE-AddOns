package jade.android.demo;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class DummySenderBehaviour extends OneShotBehaviour {

	private String receiver;
	private int commAct;
	private String content;
	
	public DummySenderBehaviour(String rcv,  String cnt, String cAct) {
		
		receiver = rcv;
		content = cnt;
		
		if (cAct.equals("accept-proposal"))
			commAct = ACLMessage.ACCEPT_PROPOSAL;
		else if (cAct.equals("inform"))
			commAct = ACLMessage.INFORM;
		else if (cAct.equals("propose") )
			commAct = ACLMessage.PROPOSE;
		else if (cAct.equals("refuse") )
			commAct = ACLMessage.REFUSE;
		else if (cAct.equals("request") )
			commAct = ACLMessage.REQUEST;
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub	
		
		
		ACLMessage msg = new ACLMessage(commAct);
		msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));
		msg.setContent(content);
		
		myAgent.send(msg);
	}

}
