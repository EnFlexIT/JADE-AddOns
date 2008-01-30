package jade.android;

import jade.core.behaviours.OneShotBehaviour;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Menu.Item;

public class WelcomeActivity extends Activity implements ConnectionListener {
    
	private JadeHelper helper;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);        
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, R.string.menu_jade_controller);
		menu.add(0, Menu.FIRST+1, R.string.send_message);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, Item item) {
		super.onMenuItemSelected(featureId, item);
		switch(item.getId()) {
		case Menu.FIRST:
			connectService();
			break;
		case Menu.FIRST+1:
			forwarding(SendMessageActivity.class);
			break;
		}

		return true;
	}
	
	private void forwarding(Class nextActivity) {
        Intent intent = new Intent();
        intent.setClass(this, nextActivity);
        startActivity(intent);
	}

	private void connectService() {
		Log.v(null,"creating helper...");
        helper = new JadeHelper(this,this);
        Log.v(null,"connecting to jade runtime...");
        helper.connect();		
	}

	public void onConnected() {
		NotificationManager nm =  (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.notifyWithText(R.string.local_service_connected,
                getText(R.string.local_service_connected),
                NotificationManager.LENGTH_SHORT,
                null);
		Log.v(null,"onConnected");
		MyBehaviour b = new MyBehaviour();
		helper.execute(b);
		String result = b.getResult();
		Log.v(null, result);
	}

	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	private class MyBehaviour extends OneShotBehaviour {
		String result;
		@Override
		public void action() {
			Log.v(null,"Executing command...");
			result = "done!";

		}
		public String getResult() {
			return result;
		}
		
	}
    
}