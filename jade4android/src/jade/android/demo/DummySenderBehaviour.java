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
		commAct = convertPerformative(cAct);
	
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub	
		
		
		ACLMessage msg  = new ACLMessage(commAct);
		msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));
		msg.setContent(content);
		
		myAgent.send(msg);
	}

	public static int convertPerformative(String perf){
		if (perf.equals("accept-proposal"))
			return  ACLMessage.ACCEPT_PROPOSAL;
		else if (perf.equals("inform"))
			return   ACLMessage.INFORM;
		else if (perf.equals("propose") )
			return   ACLMessage.PROPOSE;
		else if (perf.equals("refuse") )
			return   ACLMessage.REFUSE;
		else if (perf.equals("request") )
			return   ACLMessage.REQUEST;
		else if (perf.equals("agree"))
			return   ACLMessage.AGREE;
		else if (perf.equals("cancel") )
			return   ACLMessage.CANCEL;
		else if (perf.equals("cfp") )
			return   ACLMessage.CFP;
		else if (perf.equals("confirm") )
			return   ACLMessage.CONFIRM;
		else if (perf.equals("disconfirm"))
			return   ACLMessage.DISCONFIRM;
		else if (perf.equals("failure") )
			return   ACLMessage.FAILURE;
		else if (perf.equals("inform-if") )
			return   ACLMessage.INFORM_IF;
		else if (perf.equals("inform-ref") )
			return   ACLMessage.INFORM_REF;
		else if (perf.equals("not-understood") )
			return   ACLMessage.NOT_UNDERSTOOD;
		else if (perf.equals("propagate") )
			return   ACLMessage.PROPAGATE;
		else if (perf.equals("proxy"))
			return   ACLMessage.PROXY;
		else if (perf.equals("query-if") )
			return   ACLMessage.QUERY_IF;
		else if (perf.equals("query-ref") )
			return   ACLMessage.QUERY_REF;
		else if (perf.equals("reject-proposal") )
			return   ACLMessage.REJECT_PROPOSAL;	
		else if (perf.equals("request-when") )
			return   ACLMessage.REQUEST_WHEN;
		else if (perf.equals("request-whenever"))
			return   ACLMessage.REQUEST_WHENEVER;
		else if (perf.equals("subscribe") )
			return   ACLMessage.SUBSCRIBE;
		else 
			return ACLMessage.UNKNOWN;
	}
}
