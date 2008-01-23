package jade.android;


import android.app.ApplicationContext;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;

public class JadeHelper {
	private ApplicationContext myContext;
	private IBinder jadeBinder;
	
	public JadeHelper(ApplicationContext context) {
		myContext = context;
	}
	
	public void connect() {
		
	}
	public void disconnect() {
		
	}
	public void stop() {
		
	}
	public Parcel sendAndReceiveParcel(Parcel parcel) {
		return null;
	}
	public void sendParcelAsynch(Parcel parcel) {
		
	}
	public void onConnected() {
		
	}
	public void onDisconnected() {
		
	}
	
	
	 private ServiceConnection mConnection = new ServiceConnection() {
	    
	        public void onServiceConnected(ComponentName className, IBinder service) {
	        	Log.v(null,"JadeHelper onServiceConnected");
	        	jadeBinder = service;
	        	onConnected();
	        }

	        public void onServiceDisconnected(ComponentName className){
	        	Log.v(null,"JAdeHelper onServiceDisconnected");
	        	jadeBinder = null;
	        	onDisconnected();
	        }
	    };


}
