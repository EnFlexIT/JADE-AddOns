package jade.android;


import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendMessage extends Activity implements ConnectionListener{


	public static final String KEY_RECEIVER = "receiver";
	public static final String KEY_CONTENT = "content";
	private EditText receiverText, contentText;
	private JadeHelper helper;
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
        setContentView(R.layout.send_message);
		receiverText = (EditText) findViewById(R.id.receiver);
		contentText = (EditText) findViewById(R.id.content);;

		Button confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	Log.v(null,"receiver: "+receiverText.getText().toString());
            	Log.v(null,"content: "+contentText.getText().toString());
            	sendMessage(receiverText.getText().toString(), contentText.getText().toString());
            }
           
        });

		Button closeButton = (Button) findViewById(R.id.close);
		closeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	finish();
            }
           
        });

		
	}
	
	private void sendMessage(String receiver, String content) {				
			helper = new JadeHelper(this, this);
			helper.connect();
	}

	public void onConnected() {
		SendMessageBehaviour smb = new SendMessageBehaviour(receiverText.getText().toString(), 
															contentText.getText().toString());
		helper.execute(smb);
		String replyContent = smb.getReplyContent();
		NotificationManager nm =  (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.notifyWithText(R.string.message_received,
        getText(R.string.message_received) + replyContent,
        NotificationManager.LENGTH_LONG, null);
	}

	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}	
	
	private class SendMessageBehaviour  extends OneShotBehaviour {
		String receiver;
		String content;
		String replyContent;
		
		public SendMessageBehaviour(String receiver, String content) {
			this.receiver = receiver;
			this.content = content;
		}
		
		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));
			msg.setSender(myAgent.getAID());
			msg.setContent(content);
			
			myAgent.send(msg);
			ACLMessage reply = myAgent.blockingReceive();
			if(reply!= null){
				replyContent = reply.getContent();
			}			
		}

		public String getReplyContent() {
			return replyContent;
		}
		
	} 

	
}
