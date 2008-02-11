package jade.android;

import java.net.ConnectException;

import jade.core.MicroRuntime;
import jade.wrapper.StaleProxyException;
import jade.wrapper.ControllerException;

import android.app.ApplicationContext;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class JadeHelper {
	
	private ApplicationContext myContext;
	private Command jadeBinder;
	private ConnectionListener connectionListener;
	private MyHandler handl;

	
	public JadeHelper(ApplicationContext context, ConnectionListener cnl) {
		myContext = context;
		connectionListener = cnl; 
	}
	
	public void connect() {
		Log.v("jade.android","connecting to jade service. Thread: " + Thread.currentThread().getId());
		
		handl =  new MyHandler();
		
		Runnable rr = new Runnable(){
			public void run() {
				Log.v("jadeHelper", "Thread ID: " + Thread.currentThread().getId() );
				myContext.startService(new Intent(myContext, MicroRuntimeService.class), null);
				myContext.bindService(new Intent(myContext, MicroRuntimeService.class),null, mConnection, Context.BIND_AUTO_CREATE);
			}
		};
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
		Log.v("jade.android","stopping jade service");
		myContext.stopService(new Intent(myContext, MicroRuntimeService.class));
	}
	
	public void execute(Object command) throws ConnectException, StaleProxyException, ControllerException, InterruptedException {
		
		if(jadeBinder != null) {
			jadeBinder.execute(command);
		} else {
			//jadeBinder null so retry to connect to the service
			throw new ConnectException("Error: not connected to jade service");
		}		
	}
	
	//FIXME: questo metodo e' stato aggiunto per riattivare jade dopo che il container e' stato
	//killato da remoto. Viene richiamato a livello applicativo. non abbiamo trovato un modo per 
	//far ripartire jade e sottomettere nuovamente il comando (fino a quando la onServiceConnected non viene
	//richiamata non e' possibile fare la execute, ma in quel modo viene richiamato il codice del listener con il quale l' helper e' stato inizializzato)
	public void reconnectToJADE(){
		disconnect();
		stop();
		connect();
	}
	
	
	
	 private ServiceConnection mConnection = new ServiceConnection() {
	    
	        public void onServiceConnected(ComponentName className, IBinder service) {
	        	Log.v("jadeHelper", "Sono in thread: " + Thread.currentThread().getId());
	        	Log.v("JadeHelper","JadeHelper onServiceConnected");
	        	jadeBinder = (Command)service;
	     
	        	
	        	if(connectionListener != null) {
	        		connectionListener.onConnected(isRunning());
	        	}
	        }

	        //is called only when the connection to the service fails. 
	        //i.e. crash of the service process.
	        public void onServiceDisconnected(ComponentName className){
	        	Log.v("JadeHelper","JadeHelper onServiceDisconnected");
	        	jadeBinder = null;
	        	if(connectionListener != null) {
		        	connectionListener.onDisconnected();
	        	}
	        }
	    };
	    
	    private class MyHandler extends Handler {
	    	 public void handleMessage(Message msg){
	    		 Log.v("MyHandler", "Thread " + Thread.currentThread().getId() + " received message " + msg.toString());
	    	 }
	    }
}
