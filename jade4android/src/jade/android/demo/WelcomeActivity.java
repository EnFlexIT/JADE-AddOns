package jade.android.demo;

import jade.android.ConnectionListener;
import jade.android.JadeHelper;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class WelcomeActivity extends Activity implements ConnectionListener {
    
	private JadeHelper helper;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        helper = new JadeHelper(this,this);
        helper.connect();
    }

	@Override
	public void onConnected() {
		MyBehaviour b = new MyBehaviour();
		helper.execute(b);
		String result = b.getResult();
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	private class MyBehaviour extends OneShotBehaviour {
		String result;
		@Override
		public void action() {
			result = "executing command";
			Log.v(null,result);
		}
		public String getResult() {
			return result;
		}
		
	}
    
}