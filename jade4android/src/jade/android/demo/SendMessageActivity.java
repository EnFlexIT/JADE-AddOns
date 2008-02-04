package jade.android.demo;

import jade.android.ConnectionListener;
import jade.android.JadeHelper;
import jade.android.R;
import jade.core.AID;
import jade.core.MicroRuntime;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Properties;

import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.AssetManager;
import android.content.Intent;
import android.content.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
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
	
	//Codes for menu items
	private final int JADE_CONNECTED_ID = Menu.FIRST;
	private final int JADE_DISCONNECTED_ID = Menu.FIRST+1;
	private final int JADE_EXIT_ID = Menu.FIRST+2;
	
	
	private EditText receiverText, contentText, senderText;
	private Spinner spn;
	private ListView lv;
	private JadeHelper helper;
    private NotificationManager nManager; 

	private List<MessageInfo> messageList;
	
	private String senderName;
	
	@Override
	protected void onCreate(Bundle icicle) {
	
		super.onCreate(icicle);
		
		//Create the list of messages
		nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		messageList = new ArrayList<MessageInfo>();
		
		//Create the helper
		helper = new JadeHelper(this, this);
		
		//Set the xml layout from resource
		setContentView(R.layout.send_message);
		
		//Retrieve all components
		senderText = (EditText) findViewById(R.id.sender);
		receiverText = (EditText) findViewById(R.id.receiver);
		contentText = (EditText) findViewById(R.id.content);;
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
		
		
		//SEND BUTTON: retrieve and handle click event
		Button sendButton = (Button) findViewById(R.id.sendBtn);
		sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Log.v(null,"receiver: "+receiverText.getText().toString());
            	Log.v(null,"content: "+contentText.getText().toString());
            	sendMessage(receiverText.getText().toString(), contentText.getText().toString(), (String)spn.getSelectedItem() );
       
            }
        });
	
		//read property file
		Resources resources = this.getResources(); 
        AssetManager aM = resources.getAssets(); 
		
        try {
        
	        InputStream iS = aM.open("run.properties");
			Properties props = new Properties();
			props.load(iS);
			String property = props.getProperty(MicroRuntime.AGENTS_KEY);
			
			//FIXME: Here we get the agent name from property file by string manipulation
			//we should probably find a better way
			senderName=property.substring(0,property.indexOf(':'));
		//	senderName = firstPart.substring(firstPart.lastIndexOf('@')+1,firstPart.length());
			
        } catch (Exception e){
        	Log.e("jade.android.demo", e.getMessage(), e);
        }
        
        //put the agent name into sender box
        senderText.setText(senderName);
        
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
		
			ACLMessage msg = new ACLMessage(DummySenderBehaviour.convertPerformative(comAct));
				
			msg.setSender(new AID(senderName, AID.ISLOCALNAME));
			msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));
			msg.setContent(content);
			
			DummySenderBehaviour dsb = new DummySenderBehaviour(msg);
			
			try {
				helper.execute(dsb);
				
				MessageInfo info = new MessageInfo(msg);
				messageList.add(info);
				
				List<String> strlist = new ArrayList<String>();
				
				for (MessageInfo minfo : messageList)
				{
					strlist.add(minfo.toString());
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, strlist);
				lv.setAdapter(adapter);

			}
			catch (ConnectException e) {
				Log.e("jade.android.demo",e.getMessage(),e);
				nManager.notifyWithText(R.string.execute_command_error,getText(R.string.execute_command_error) + "connect to jadeService",NotificationManager.LENGTH_SHORT,null);
			}
			catch (Exception e) {
				Log.e("jade.android.demo",e.getMessage(),e);
				nManager.notifyWithText(R.string.execute_command_error,getText(R.string.execute_command_error),NotificationManager.LENGTH_SHORT,null);
			}
			
		
			
	}

	public void onConnected(boolean isStarted) {
		//FIXME: gestione isStarted
		GUIUpdater updater = new GUIUpdater(this);

		DummyReceiverBehaviour drb = new DummyReceiverBehaviour(updater);
        try {
			helper.execute(drb);
		} catch (Exception e) {
			Log.e("jade.android.demo",e.getMessage(),e);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, JADE_CONNECTED_ID, R.string.menu_item_connect);
		menu.add(0, JADE_DISCONNECTED_ID, R.string.menu_item_disconnect);
		menu.add(0, JADE_EXIT_ID, R.string.menu_item_exit);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, Item item) {
		super.onMenuItemSelected(featureId, item);
		
		switch(item.getId()) {
			case JADE_CONNECTED_ID:
				if (!helper.isConnected())
					helper.connect();
			break;
				
			case JADE_DISCONNECTED_ID:
				if (helper.isConnected())
					helper.disconnect();
			break;
			
			case JADE_EXIT_ID:
				helper.stop();
				finish();
			break;
		}
		return true;
	}
}
