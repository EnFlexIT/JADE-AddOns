package jade.android.demo;

import jade.android.R;
import jade.android.R.id;
import jade.android.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MessageDetailsActivity extends Activity {

	protected void onCreate(Bundle icicle) {
		
		super.onCreate(icicle);
		setContentView(R.layout.message_details);
		
		Intent it =getIntent();
		
		
		EditText sender = (EditText) this.findViewById(R.id.senderDet);
		sender.setText( (String)it.getExtra(SendMessageActivity.KEY_INTENT_SENDER) );
		
		EditText comAct = (EditText) this.findViewById(R.id.comActDet);
		comAct.setText( (String)it.getExtra(SendMessageActivity.KEY_INTENT_COM_ACT)  );
		
		EditText content = (EditText) this.findViewById(R.id.contentDet);
		content.setText( (String)it.getExtra(SendMessageActivity.KEY_INTENT_CONTENT) );
	
		Button btn = (Button) this.findViewById(R.id.backBtnDet);
		btn.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
			
		} );
	}
	
	
}
