package jade.android.demo;

import jade.android.ConnectionListener;
import jade.android.JadeGateway;
import jade.android.R;
import jade.core.AID;
import jade.core.Profile;
import jade.imtp.leap.JICP.JICPProtocol;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.net.ConnectException;
import java.util.LinkedList;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;


public class SendMessageActivity extends Activity implements ConnectionListener{


	public static final String KEY_RECEIVER = "receiver";
	public static final String KEY_CONTENT = "content";
	
	//Keys for parameters to Message details activity
	public static final String KEY_INTENT_SENDER="key_sender";
	public static final String KEY_INTENT_RECEIVER="key_receiver";
	public static final String KEY_INTENT_COM_ACT="key_commAct";
	public static final String KEY_INTENT_CONTENT="key_content";
	
	//Duration of error notification
	public static final int ERROR_MSG_DURATION=1000;
	
	//keys to index the tabs
	public static final String SEND_MSG_TAB_TAG="SendMsg";
	public static final String RECV_MSG_TAB_TAG="RecvMsg";
	
	//notification ID
	private final int STATUSBAR_NOTIFICATION= R.layout.send_message;
	
	//Codes for menu items
	public static final int JADE_EXIT_ID = Menu.FIRST;

	private EditText receiverText, contentText, senderText;
	private Spinner spn;
	private ListView lv;
	private JadeGateway gateway;
    private Button sendButton;
    private Button clearButton;
	private NotificationManager nManager; 

	private GUIUpdater updater;
	
	private LinkedList<MessageInfo> messageList;
	private IconifiedTextListAdapter listAdapter;
	
	
	@Override
	protected void onCreate(Bundle icicle) {
	
		super.onCreate(icicle);
		
		
		Log.v("jade.android.demo","SendMessageActivity.onCreate() : starting onCreate method");
		
		//Set the xml layout from resource
		setContentView(R.layout.send_message);
		
		TabHost host = (TabHost) findViewById(R.id.tabs);
	
		host.setup();
		
		TabSpec sendMsgSpecs = host.newTabSpec(SEND_MSG_TAB_TAG);
		sendMsgSpecs.setIndicator(getText(R.string.send_msg_tab_indicator));
		sendMsgSpecs.setContent(R.id.content1);
		host.addTab(sendMsgSpecs);
		
		TabSpec recvMsgSpecs = host.newTabSpec(RECV_MSG_TAB_TAG);
		recvMsgSpecs.setIndicator(getText(R.string.recv_msg_tab_indicator));
		recvMsgSpecs.setContent(R.id.content2);
		host.addTab(recvMsgSpecs);
		
		host.setCurrentTab(0);
	
	
		//Create the list of messages
		nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		messageList = new LinkedList<MessageInfo>();
		//Retrieve all components
		senderText = (EditText) findViewById(R.id.sender);
		receiverText = (EditText) findViewById(R.id.receiver);
		contentText = (EditText) findViewById(R.id.content);
		spn = (Spinner) findViewById(R.id.commAct);
		lv = (ListView) findViewById(R.id.messageList);
		//SPINNER: fill with data
		String[] performatives= ACLMessage.getAllPerformativeNames();
		ArrayAdapter<CharSequence> comActList = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item, performatives);
		comActList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn.setAdapter(comActList);
		//LISTVIEW: handle click event
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				forwarding(MessageDetailsActivity.class, position);
			}
		});
	
		//SEND BUTTON: retrieve and handle click event (Initially disabled)
		sendButton = (Button) findViewById(R.id.sendBtn);
		sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Log.v("jade.android.demo","receiver: "+receiverText.getText().toString());
            	Log.v("jade.android.demo","content: "+contentText.getText().toString());
            	if (sendButton.isEnabled())
            		sendMessage(receiverText.getText().toString(), contentText.getText().toString(), (String)spn.getSelectedItem() );
       
            }
        });

		//CLEAR BUTTON: retrieve and handle click event (Initially disabled)
		clearButton = (Button) findViewById(R.id.clearbtn);		
		clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {      	
            	if (clearButton.isEnabled()){
            		messageList.clear();           	
            	    listAdapter.clear();
            	    lv.setAdapter(listAdapter);
            	 }
            }
        });
		
        
        listAdapter = new IconifiedTextListAdapter(this);
        senderText.setEnabled(false);
		updater = new GUIUpdater(this);
		
		Properties props = new Properties();
		props.setProperty(Profile.MAIN_HOST, getResources().getString(R.string.host));
		props.setProperty(Profile.MAIN_PORT, getResources().getString(R.string.port));
		props.setProperty(JICPProtocol.MSISDN_KEY, getResources().getString(R.string.msisdn));
	
		JadeGateway.connect(DummyAgent.class.getName(), new String[]{"pippo", "pluto"}, props, this, this);	
			
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

		Log.v("jade.android.demo","SendMessageActivity.onStart() : calling onStart method");
		

		
		ACLMessage msg = new ACLMessage(ACLMessage.getInteger(comAct));		
		msg.setContent(content);
		//FIXME: il booleano dovrebbe essere ricavato da una checkbox accanto al nome.
		DummySenderBehaviour dsb = new DummySenderBehaviour(msg, receiver, AID.ISLOCALNAME);

		try {
			gateway.execute(dsb);

			MessageInfo info = new MessageInfo(msg);
			addFirstMessage(info);

			IconifiedText IT = new IconifiedText (info.toString(), getResources().getDrawable(R.drawable.upmod));
			IT.setTextColor(getResources().getColor(R.color.listitem_sent_msg));
			listAdapter.addFirstItem(IT);
			lv.setAdapter(listAdapter);
		}
		
		catch (Exception e) {
			Log.e("jade.android.demo",e.getMessage(),e);
			Toast.makeText(this,e.getMessage(), ERROR_MSG_DURATION);
		}
	}

	public void onConnected(JadeGateway gw) {

		gateway = gw;		
		sendButton.setEnabled(true);
		try{
			senderText.setText(gateway.getAgentName());
			gateway.execute(updater);
			CharSequence txt = getResources().getText(R.string.statusbar_msg_connected);
			Notification notification = new Notification(this,R.drawable.dummyagent,getResources().getText(R.string.statusbar_msg_connected),1000,"Ciao","Anna",null,R.drawable.dummyagent,"DummyAgent",null);
			nManager.notify(STATUSBAR_NOTIFICATION, notification);
		}catch(ConnectException ce){
			Log.e("jade.android", ce.getMessage(), ce);
			Toast.makeText(this,ce.getMessage(), ERROR_MSG_DURATION);
		}catch(Exception e1){
			Log.e("jade.android", e1.getMessage(), e1);
			Toast.makeText(this,e1.getMessage(), ERROR_MSG_DURATION);
		}
	}

	public void onDisconnected() {
		// TODO Auto-generated method stub
		Log.v("jade.android.demo","OnDisconnected has been called!!!!");
		
	}	
		
	public void addFirstMessage(MessageInfo msg) { 
		messageList.addFirst(msg);
	}
	
	public final LinkedList<MessageInfo> getMessageList() {
		return messageList;
	}
	
	public  IconifiedTextListAdapter getListAdapter() {
		return listAdapter;
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, JADE_EXIT_ID, R.string.menu_item_exit);
		return true;
	}
	
	public boolean onMenuItemSelected(int featureId, Item item) {
		super.onMenuItemSelected(featureId, item);
		
		switch(item.getId()) {
			case JADE_EXIT_ID:
				finish();
				break;
		}
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) {         
		 if (resultCode == MessageDetailsActivity.REPLY_TO_RESULT) {             
			 receiverText.setText(data);
		 }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("jade.android.demo","SendMessageActivity.onDestroy() : calling onDestroy method");
		nManager.cancel(STATUSBAR_NOTIFICATION);
		try {
			if (gateway != null)
				gateway.shutdownJADE();
				
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), ERROR_MSG_DURATION);
		}
			if (gateway != null )
				gateway.disconnect(this);
	}
	
	@Override
	protected void onFreeze(Bundle out) {
		super.onFreeze(out);
		Log.v("jade.android.demo","SendMessageActivity.onFreeze() : calling onFreeze method");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.v("jade.android.demo","SendMessageActivity.onResume() : calling onResume method");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v("jade.android.demo","SendMessageActivity.onPause() : calling onPause method");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.v("jade.android.demo","SendMessageActivity.onStop() : calling onStop method");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.v("jade.android.demo","SendMessageActivity.onRestart() : calling onRestart method");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		
	}
	
}
