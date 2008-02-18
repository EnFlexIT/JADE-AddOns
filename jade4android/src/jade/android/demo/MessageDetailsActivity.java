package jade.android.demo;

import jade.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MessageDetailsActivity extends Activity {

	public static final int REPLY_TO_RESULT=0;
	public static final int BACK_RESULT=1;
	
	private EditText sender;
	
	protected void onCreate(Bundle icicle) {
		
		super.onCreate(icicle);
		setContentView(R.layout.message_details);
		
		Intent it =getIntent();
		
		
		sender = (EditText) this.findViewById(R.id.senderDet);
		sender.setText( (String)it.getSerializableExtra(SendMessageActivity.KEY_INTENT_SENDER) );
		
		
		EditText receiver = (EditText) this.findViewById(R.id.receiverDet);
		receiver.setText( (String)it.getSerializableExtra(SendMessageActivity.KEY_INTENT_RECEIVER) );
		
		
		EditText comAct = (EditText) this.findViewById(R.id.comActDet);
		comAct.setText( (String)it.getSerializableExtra(SendMessageActivity.KEY_INTENT_COM_ACT)  );
		
		
		EditText content = (EditText) this.findViewById(R.id.contentDet);
		content.setText( (String)it.getSerializableExtra(SendMessageActivity.KEY_INTENT_CONTENT) );
	
		
		Button btn = (Button) this.findViewById(R.id.backBtnDet);
		btn.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				finish();
			}
			
		});
		
		Button reply = (Button) this.findViewById(R.id.replyBtnDet);
		reply.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				String snd = sender.getText().toString();
				String nameToSend = snd.substring(0, snd.indexOf('@'));
				setResult(REPLY_TO_RESULT, nameToSend);
				finish();
			}
				
		});
	
	}	
}
