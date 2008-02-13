package jade.android.demo;

import java.util.ArrayList;

import java.util.List;

import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import jade.android.JadeGateway;
import jade.android.R;
import jade.android.R.id;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

class GUIUpdater implements ACLMessageListener {

	private Handler handl;
	private SendMessageActivity act;	
	private static final Logger myLogger = Logger.getMyLogger(JadeGateway.class.getName());
	
	public GUIUpdater(SendMessageActivity baseActivity) {
		
		handl = new Handler();
		act = baseActivity;
		
	}
	
	public void OnMessageReceived(ACLMessage msg) {
		// TODO Auto-generated method stub
		myLogger.log(Logger.INFO, "GuiUpdater has received message");
		Updater up = new Updater(act,msg);
		handl.post(up);
	}

	private class Updater implements Runnable {
		
		private SendMessageActivity sendMsgAct;
		private ACLMessage message;
		
		
		public Updater(SendMessageActivity sm, ACLMessage msg) { 
			sendMsgAct = sm;
			message = msg;
			
		}
		
		public void run() {
		
			MessageInfo mi = new MessageInfo(message);
			
			sendMsgAct.addFirstMessage(mi);
			IconifiedTextListAdapter adapter = sendMsgAct.getListAdapter();
			IconifiedText txt = new IconifiedText(mi.toString(),sendMsgAct.getResources().getDrawable(R.drawable.downmod));
			txt.setTextColor(sendMsgAct.getResources().getColor(R.color.listitem_received_msg));
			adapter.addFirstItem(txt);
			
			ListView lv = (ListView) sendMsgAct.findViewById(R.id.messageList);
			lv.setAdapter(adapter);
		    
		}
	}
}
	
