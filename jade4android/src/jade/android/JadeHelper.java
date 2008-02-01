package jade.android;



import java.net.ConnectException;

import jade.core.MicroRuntime;
import jade.wrapper.StaleProxyException;
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
       myContext.startService(new Intent(myContext, 
                MicroRuntimeService.class), null);
		myContext.bindService(new Intent(myContext, 
                MicroRuntimeService.class),
            null, mConnection, Context.BIND_AUTO_CREATE);

	}
	
	public boolean isConnected(){
		return jadeBinder != null;
	}
	public boolean isRunning(){
		return MicroRuntime.isRunning();
	}
	
	public void disconnect() {
        myContext.unbindService(mConnection);
        jadeBinder = null;
	}
	
	public void stop() {
		myContext.stopService(new Intent(myContext, 
                MicroRuntimeService.class));
	}
	
	public void execute(Object command) throws ConnectException, Exception {
		
		if(jadeBinder != null) {
			try {
				jadeBinder.execute(command);
			} catch(StaleProxyException spe) {
				throw new Exception("Failed to connect to jade runtime",spe);
			}
			catch (Exception e) {
				throw new Exception("Unexpected error",e);
			}
		} else {
			throw new ConnectException("Error: not connected to jade service");
		}		
	}
	
	
	 private ServiceConnection mConnection = new ServiceConnection() {
	    
	        public void onServiceConnected(ComponentName className, IBinder service) {
	        	Log.v(null,"JadeHelper onServiceConnected");
	        	jadeBinder = (Command)service;
	        	if(connectionListener != null) {
	        		connectionListener.onConnected(isRunning());
	        	}
	        }

	        public void onServiceDisconnected(ComponentName className){
	        	Log.v(null,"JadeHelper onServiceDisconnected");
	        	jadeBinder = null;
	        	if(connectionListener != null) {
		        	connectionListener.onDisconnected();
	        	}
	        }
	    };


}
