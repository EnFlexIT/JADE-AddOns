package jade.android;

import jade.core.behaviours.OneShotBehaviour;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class WelcomeActivity extends Activity implements ConnectionListener {
    
	private JadeHelper helper;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        Log.v(null,"creating helper...");
        helper = new JadeHelper(this,this);
        Log.v(null,"connecting to jade runtime...");
        helper.connect();
    }

	public void onConnected() {
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