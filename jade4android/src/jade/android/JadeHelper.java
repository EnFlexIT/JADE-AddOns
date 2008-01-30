package jade.android;



import android.app.ApplicationContext;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class JadeHelper {
	private ApplicationContext myContext;
	private Command jadeBinder;
	private ConnectionListener connectionListener;
	
	public JadeHelper(ApplicationContext context, ConnectionListener cnl) {
		myContext = context;
		connectionListener = cnl; 
	}
	
	public void connect() {
        myContext.bindService(new Intent(myContext, 
                MicroRuntimeService.class),
            null, mConnection, Context.BIND_AUTO_CREATE);

	}
	
	public boolean isConnected(){
		return jadeBinder != null;
	}
	
	public void disconnect() {
        myContext.unbindService(mConnection);

	}
	public void stop() {
		myContext.stopService(new Intent(myContext, 
                MicroRuntimeService.class));
	}
	public void execute(Object command) {
		if(jadeBinder != null) {
				jadeBinder.execute(command);
		}
	}
	
	
	 private ServiceConnection mConnection = new ServiceConnection() {
	    
	        public void onServiceConnected(ComponentName className, IBinder service) {
	        	Log.v(null,"JadeHelper onServiceConnected");
	        	jadeBinder = (Command)service;
	        	if(connectionListener != null) {
	        		connectionListener.onConnected();
	        	}
	        }

	        public void onServiceDisconnected(ComponentName className){
	        	Log.v(null,"JAdeHelper onServiceDisconnected");
	        	jadeBinder = null;
	        	if(connectionListener != null) {
		        	connectionListener.onDisconnected();
	        	}
	        }
	    };


}
