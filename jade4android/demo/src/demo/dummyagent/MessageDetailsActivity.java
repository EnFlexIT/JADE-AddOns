package demo.dummyagent;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MessageDetailsActivity extends Activity {

	private EditText sender;
	
	protected void onCreate(Bundle icicle) {
		
		super.onCreate(icicle);
		setContentView(R.layout.message_details);
		
		Intent it =getIntent();
		String senderName = (String) it.getSerializableExtra(DummyAgentActivity.KEY_INTENT_SENDER);

		sender = (EditText) this.findViewById(R.id.senderDet);
		sender.setText( senderName );
		
		
		ListView receiverList = (ListView) this.findViewById(R.id.receiverListDet);
		ArrayList<String> strList = (ArrayList<String>) it.getSerializableExtra(DummyAgentActivity.KEY_INTENT_RECEIVER_LIST);
		
		IconifiedTextListAdapter ita = new IconifiedTextListAdapter(this);
		
		Drawable icon = getResources().getDrawable(R.drawable.runtree);
		
		for (java.util.Iterator<String> iterator = strList.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			IconifiedText txt = new IconifiedText(string,icon);
			txt.setTextColor(R.color.listitem_receivers_text_color);
			ita.addLastItem(txt);
		}
		
		receiverList.setAdapter(ita);
		
		EditText comAct = (EditText) this.findViewById(R.id.comActDet);
		String comActName = (String) it.getSerializableExtra(DummyAgentActivity.KEY_INTENT_COM_ACT);
		comAct.setText(comActName);
		
		
		EditText content = (EditText) this.findViewById(R.id.contentDet);
		String contentName = (String) it.getSerializableExtra(DummyAgentActivity.KEY_INTENT_CONTENT);
		content.setText( contentName );
	
		
	
		
		Button reply = (Button) this.findViewById(R.id.replyBtnDet);
		reply.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				String snd = sender.getText().toString();
				setResult(Activity.RESULT_OK, snd);
				finish();
			}
				
		});
	
	}	
	
	
	protected void onDestroy(){
		super.onDestroy();
	}
}
