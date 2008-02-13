package jade.android.demo;

import java.net.ConnectException;

import jade.android.ConnectionListener;
import jade.android.JadeGateway;
import jade.android.R;
import jade.core.Profile;
import jade.imtp.leap.JICP.JICPProtocol;
import jade.util.leap.Properties;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Menu.Item;
import android.widget.EditText;

public class TestActivity extends Activity  implements ConnectionListener {

	private JadeGateway jg;
	
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.message_details);
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SendMessageActivity.JADE_CONNECTED_ID, R.string.menu_item_connect);
		menu.add(0, SendMessageActivity.JADE_DISCONNECTED_ID, R.string.menu_item_disconnect);
		menu.add(0, SendMessageActivity.JADE_EXIT_ID, R.string.menu_item_exit);
		menu.add(0, SendMessageActivity.JADE_SHUTDOWN_ID,R.string.menu_item_shutdown);
		menu.add(0, SendMessageActivity.JADE_CHECK_ID,R.string.menu_item_restart);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, Item item) {
		super.onMenuItemSelected(featureId, item);
		
		switch(item.getId()) {
			case SendMessageActivity.JADE_CONNECTED_ID:
				Properties props = new Properties();
				props.setProperty(Profile.MAIN_HOST, getResources().getString(R.string.host));
				props.setProperty(Profile.MAIN_PORT, getResources().getString(R.string.port));
				props.setProperty(JICPProtocol.MSISDN_KEY, getResources().getString(R.string.msisdn));
				//Connect to the service and get the gateway
				JadeGateway.connect(DummyAgent.class.getName(), props, this, this);
							
			break;
				
			case SendMessageActivity.JADE_DISCONNECTED_ID:
				jg.disconnect(this);
			break;
			
			case SendMessageActivity.JADE_EXIT_ID:
				finish();
				try {
					jg.shutdownJADE();
				} catch (ConnectException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				jg.disconnect(this);
			break;
			
			case SendMessageActivity.JADE_SHUTDOWN_ID:
				try {
					jg.shutdownJADE();
				} catch (ConnectException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			break;
			
			case SendMessageActivity.JADE_CHECK_ID:
				try {
					jg.checkJADE();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			break;
			
			case SendMessageActivity.JADE_SUBACTIVITY_ID:
				Intent it = new Intent();
				it.setClass(this, TestActivity.class);
				startSubActivity(it, 5);
			break;
		}
		return true;
	}
	
	public void onConnected(JadeGateway gateway) {
		jg = gateway;
		
		EditText et = (EditText) findViewById(R.id.senderDet);
		try {
			et.setText(jg.getAgentName());
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	
}
