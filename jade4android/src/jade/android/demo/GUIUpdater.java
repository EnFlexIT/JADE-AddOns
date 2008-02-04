package jade.android.demo;

import java.util.ArrayList;

import java.util.List;

import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import jade.android.R;
import jade.android.R.id;
import jade.lang.acl.ACLMessage;

class GUIUpdater implements ACLMessageListener {

	private Handler handl;
	private SendMessageActivity act;
	
	public GUIUpdater(SendMessageActivity baseActivity) {
		
		handl = new Handler();
		act = baseActivity;
		
	}
	
	public void OnMessageReceived(ACLMessage msg) {
		// TODO Auto-generated method stub
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
		
			MessageInfo mf = new MessageInfo(message);
			
			sendMsgAct.addMessage(mf);
			IconifiedTextListAdapter adapter = sendMsgAct.getListAdapter();
			IconifiedText txt = new IconifiedText(mf.toString(),sendMsgAct.getResources().getDrawable(R.drawable.downmod));
			txt.setTextColor(sendMsgAct.getResources().getColor(R.color.listitem_received_msg));
			adapter.addItem(txt);
			
			ListView lv = (ListView) sendMsgAct.findViewById(R.id.messageList);
			lv.setAdapter(adapter);
		    
		}
	}
}
	
