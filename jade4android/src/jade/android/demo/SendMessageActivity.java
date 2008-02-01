package jade.android.demo;

import jade.android.ConnectionListener;
import jade.android.JadeHelper;
import jade.android.R;
import jade.android.R.array;
import jade.android.R.id;
import jade.android.R.layout;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;


public class SendMessageActivity extends Activity implements ConnectionListener{


	public static final String KEY_RECEIVER = "receiver";
	public static final String KEY_CONTENT = "content";
	
	//Keys for parameters to Message details activity
	public static final String KEY_INTENT_SENDER="key_sender";
	public static final String KEY_INTENT_RECEIVER="key_receiver";
	public static final String KEY_INTENT_COM_ACT="key_commAct";
	public static final String KEY_INTENT_CONTENT="key_content";
	
	
	private EditText receiverText, contentText;
	private Spinner spn;
	private ListView lv;
	private JadeHelper helper;
    private NotificationManager nManager; 

	private List<MessageInfo> messageList;
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
        
		nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		messageList = new ArrayList<MessageInfo>();
		
		helper = new JadeHelper(this, this);
		helper.connect();
		
		setContentView(R.layout.send_message);
		receiverText = (EditText) findViewById(R.id.receiver);
		contentText = (EditText) findViewById(R.id.content);;

		spn = (Spinner) findViewById(R.id.commAct);
		ArrayAdapter<CharSequence> comActList = ArrayAdapter.createFromResource(this, R.array.comActList, android.R.layout.simple_spinner_item);
		comActList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn.setAdapter(comActList);
		
		lv = (ListView) findViewById(R.id.messageList);
		
		ArrayAdapter<CharSequence> msgListItems = new ArrayAdapter<CharSequence>(this,android.R.layout.list_content,new ArrayList<CharSequence>());
		lv.setAdapter(msgListItems);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void  onItemClick(AdapterView parent, View v, int position, long id) {
				forwarding(MessageDetailsActivity.class, position);
			}
		});
		
		Button sendButton = (Button) findViewById(R.id.sendBtn);
		sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	Log.v(null,"receiver: "+receiverText.getText().toString());
            	Log.v(null,"content: "+contentText.getText().toString());
            	sendMessage(receiverText.getText().toString(), contentText.getText().toString(), (String)spn.getSelectedItem() );
       
            }
          
        });
		
		
		
		

		Button backButton = (Button) findViewById(R.id.backBtn);
		backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	finish();
            }
           
        });     

	}
	
	private void forwarding(Class nextActivity, int position) {
        Intent intent = new Intent();
        intent.setClass(this, nextActivity);
        ACLMessage msg =  messageList.get(position).getMessage();
        
        intent.putExtra(SendMessageActivity.KEY_INTENT_SENDER, msg.getSender().getName());
        AID aid = (AID) msg.getAllReceiver().next();
        intent.putExtra(SendMessageActivity.KEY_INTENT_RECEIVER,  aid.getName());
        intent.putExtra(SendMessageActivity.KEY_INTENT_COM_ACT, ACLMessage.getPerformative(msg.getPerformative()));
        intent.putExtra(SendMessageActivity.KEY_INTENT_CONTENT, msg.getContent());
        
        startSubActivity(intent,1);
	}
	
	private void sendMessage(String receiver, String content, String comAct) {				
		
		if (helper.isConnected()) {
			DummySenderBehaviour dsb = new DummySenderBehaviour(receiver,content,comAct);
			try {
				helper.execute(dsb);
				ACLMessage msg = new ACLMessage(DummySenderBehaviour.convertPerformative(comAct));
				msg.setSender(new AID("gateway", AID.ISLOCALNAME));
				msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));
				msg.setContent(content);
			
				MessageInfo info = new MessageInfo(msg);
				messageList.add(info);
				
				List<String> strlist = new ArrayList<String>();
				
				for (MessageInfo minfo : messageList)
				{
					strlist.add(minfo.toString());
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, strlist);
				lv.setAdapter(adapter);

			} catch (Exception e) {
				Log.e(getClass().getPackage().getName(),e.getMessage(),e);
				nManager.notifyWithText(R.string.execute_command_error,getText(R.string.execute_command_error),NotificationManager.LENGTH_SHORT,null);
			}
			
		}	else {
			//fixme
			Log.v(null,"ERRRORRRR!!!!!! SEND WAS CALLED WITHOUT CONNECTION TO SERVICE!!!");
		}	
	}

	public void onConnected(boolean isStarted) {
		//FIXME: gestione isStarted
		GUIUpdater updater = new GUIUpdater(this);

		DummyReceiverBehaviour drb = new DummyReceiverBehaviour(updater);
        try {
			helper.execute(drb);
		} catch (Exception e) {
			Log.e(getClass().getPackage().getName(),e.getMessage(),e);
			nManager.notifyWithText(R.string.execute_command_error,getText(R.string.execute_command_error),NotificationManager.LENGTH_SHORT,null);
		}
	}

	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}	
		
	public void addMessage(MessageInfo msg) { 
		messageList.add(msg);
	}
	
	public final List<MessageInfo> getMessageList() {
		return messageList;
	}
}
