package demo.dummyagent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jade.lang.acl.ACLMessage;

public class MessageInfo {
	
	private ACLMessage message;
	private String strMsg;

	public MessageInfo(ACLMessage msg){
		message = msg;
		Date timestamp = new Date();
		String pattern = "dd/MM/yy HH.mm.ss :  ";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern,Locale.ITALIAN);
		strMsg = formatter.format(timestamp) +  ACLMessage.getPerformative(message.getPerformative()); 
	}
	
	public final ACLMessage getMessage() {
		return message;
	}
	
	public String toString() {
		
		return strMsg;
	}
}
