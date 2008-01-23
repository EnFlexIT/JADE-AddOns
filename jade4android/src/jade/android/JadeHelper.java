package jade.android;



import android.app.ApplicationContext;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.DeadObjectException;
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
        myContext.bindService(new Intent(myContext, 
                MicroRuntimeService.class),
            null, mConnection, Context.BIND_AUTO_CREATE);

	}
	public void disconnect() {
        myContext.unbindService(mConnection);

	}
	public void stop() {
		myContext.stopService(new Intent(myContext, 
                MicroRuntimeService.class));
	}
	public Parcel sendParcel(Parcel parcel) {
    	Parcel reply = Parcel.obtain();
		if(jadeBinder != null) {
			
			try {
				synchronized(jadeBinder) {
				jadeBinder.transact(IBinder.FIRST_CALL_TRANSACTION, parcel, reply, 0);
					jadeBinder.wait();
				}
			} catch (DeadObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return reply;
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
