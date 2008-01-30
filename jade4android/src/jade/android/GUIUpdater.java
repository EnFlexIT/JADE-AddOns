package jade.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
			
			List<MessageInfo> list = sendMsgAct.getMessageList();
			List<String> strlist = new ArrayList<String>();
			
			for (MessageInfo msg : list)
			{
				strlist.add(msg.toString());
			}
			
			ListView lv = (ListView) sendMsgAct.findViewById(R.id.messageList);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(sendMsgAct,android.R.layout.simple_list_item_1, strlist);
			lv.setAdapter(adapter);
			
		}
	}
	
}
