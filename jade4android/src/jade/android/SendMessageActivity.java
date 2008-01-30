package jade.android;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class SendMessageActivity extends Activity implements ConnectionListener{


	public static final String KEY_RECEIVER = "receiver";
	public static final String KEY_CONTENT = "content";
	private EditText receiverText, contentText;
	private Spinner spn;
	private JadeHelper helper;
	
	private List<MessageInfo> messageList;
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
        
		helper = new JadeHelper(this, this);
		helper.connect();
		
		setContentView(R.layout.send_message);
		receiverText = (EditText) findViewById(R.id.receiver);
		contentText = (EditText) findViewById(R.id.content);;

		spn = (Spinner) findViewById(R.id.commAct);
		ArrayAdapter<CharSequence> comActList = ArrayAdapter.createFromResource(this, R.array.comActList, android.R.layout.simple_spinner_item);
		comActList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn.setAdapter(comActList);
		
		ListView lv = (ListView) findViewById(R.id.messageList);
		
		ArrayAdapter<CharSequence> msgListItems = new ArrayAdapter<CharSequence>(this,android.R.layout.list_content,new ArrayList<CharSequence>());
		lv.setAdapter(msgListItems);
	
		messageList = new ArrayList<MessageInfo>();
		
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
	
	private void sendMessage(String receiver, String content, String comAct) {				
		
		if (helper.isConnected()) {
			DummySenderBehaviour dsb = new DummySenderBehaviour(receiver,content,comAct);
			helper.execute(dsb);
			Log.v(null,"Message sent!!!");
		}	else {
			Log.v(null,"ERRRORRRR!!!!!! SEND WAS CALLED WITHOUT CONNECTION TO SERVICE!!!");
		}	
	}

	public void onConnected() {
		
		GUIUpdater updater = new GUIUpdater(this);

		DummyReceiverBehaviour drb = new DummyReceiverBehaviour(updater);
        helper.execute(drb);
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
