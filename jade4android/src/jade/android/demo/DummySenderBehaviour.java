package jade.android.demo;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class DummySenderBehaviour extends OneShotBehaviour {

	private ACLMessage theMsg;
	
	public DummySenderBehaviour(ACLMessage msg) {
		
		theMsg = msg;
	
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub		
		myAgent.send(theMsg);
	}

	public static int convertPerformative(String perf){
		
		String toTest = perf.toLowerCase();
		
		if (toTest.equals("accept-proposal"))
			return  ACLMessage.ACCEPT_PROPOSAL;
		else if (toTest.equals("inform"))
			return   ACLMessage.INFORM;
		else if (toTest.equals("propose") )
			return   ACLMessage.PROPOSE;
		else if (toTest.equals("refuse") )
			return   ACLMessage.REFUSE;
		else if (toTest.equals("request") )
			return   ACLMessage.REQUEST;
		else if (toTest.equals("agree"))
			return   ACLMessage.AGREE;
		else if (toTest.equals("cancel") )
			return   ACLMessage.CANCEL;
		else if (toTest.equals("cfp") )
			return   ACLMessage.CFP;
		else if (toTest.equals("confirm") )
			return   ACLMessage.CONFIRM;
		else if (toTest.equals("disconfirm"))
			return   ACLMessage.DISCONFIRM;
		else if (toTest.equals("failure") )
			return   ACLMessage.FAILURE;
		else if (toTest.equals("inform-if") )
			return   ACLMessage.INFORM_IF;
		else if (toTest.equals("inform-ref") )
			return   ACLMessage.INFORM_REF;
		else if (toTest.equals("not-understood") )
			return   ACLMessage.NOT_UNDERSTOOD;
		else if (toTest.equals("propagate") )
			return   ACLMessage.PROPAGATE;
		else if (toTest.equals("proxy"))
			return   ACLMessage.PROXY;
		else if (toTest.equals("query-if") )
			return   ACLMessage.QUERY_IF;
		else if (toTest.equals("query-ref") )
			return   ACLMessage.QUERY_REF;
		else if (toTest.equals("reject-proposal") )
			return   ACLMessage.REJECT_PROPOSAL;	
		else if (toTest.equals("request-when") )
			return   ACLMessage.REQUEST_WHEN;
		else if (toTest.equals("request-whenever"))
			return   ACLMessage.REQUEST_WHENEVER;
		else if (toTest.equals("subscribe") )
			return   ACLMessage.SUBSCRIBE;
		else 
			return ACLMessage.UNKNOWN;
	}
}
