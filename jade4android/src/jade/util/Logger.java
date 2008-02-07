package jade.util;

import java.util.Hashtable;

import jade.util.leap.Properties;
import jade.util.leap.Serializable;
import android.util.Log;

public class Logger implements Serializable{

	//SEVERE is a message level indicating a serious failure.
	public static final int SEVERE	=	10;
	//WARNING is a message level indicating a potential problem.
	public static final int WARNING	=	9;
	//INFO is a message level for informational messages
	public static final int INFO	=	8;
	//CONFIG is a message level for static configuration messages.
	public static final int CONFIG	=	7;
	//FINE is a message level providing tracing information.
	public static final int FINE	=	5;
	//FINER indicates a fairly detailed tracing message.
	public static final int FINER	=	4;
	//FINEST indicates a highly detailed tracing message
	public static final int FINEST	=	3;
	//ALL indicates that all messages should be logged.
	public static final int ALL		=	-2147483648;
	//Special level to be used to turn off logging
	public static final int OFF		=	2147483647;

	//private static int MAX_TAG_LENGTH = 23; 
	
	private static Hashtable logManager = new Hashtable();
	private String name;
	
	private Logger(String name){
		this.name = name;
	}

	public static void println(String log) {
		//FIXME: name dovrebbe essere static
		Log.println(Log.ASSERT, "", log);
	}
	
	public synchronized static Logger getMyLogger(String name){
		
		//String value = ((name != null) ? ((name.length() > MAX_TAG_LENGTH) ? name.substring(0,MAX_TAG_LENGTH) : name) : "jade.util.Logger");
		Object myLogger = logManager.get(name);
		if (myLogger == null){
			myLogger = new Logger(name);
			logManager.put(name, myLogger);
		}
		return (Logger)myLogger;
	}
	
	public static void initialize(Properties pp) {
		//TODO	
	}
	
	public boolean isLoggable(int level){
		//FIXME: da capire come gestire OFF e ALL. dovrebbe esserci un valore SUPPRESS, ma non e' implementato al momento
		
		if (level == OFF)
			return false;
		else if(level ==ALL)
			return true;
		else 
			return Log.isLoggable(this.getClass().getName(), getAndroidLogLevel(level));
	}
	
	public void log(int level, String msg) {
		//FIXME se level e' OFF ?
		Log.println(getAndroidLogLevel(level), this.getClass().getName(), name + ": " + msg);
	}
	
	public void log(int level, String msg, Throwable t) {
		//FIXME se level e' OFF  e ALL?
		int ll = getAndroidLogLevel(level);
		switch(ll){
			case  Log.ERROR: 
				Log.e(this.getClass().getName(), name + ": " + msg, t);
				break;
			case Log.WARN:
				Log.w(this.getClass().getName(), name + ": " + msg, t);
				break;
			case Log.INFO:
				Log.i(this.getClass().getName(), name + ": " + msg, t);
				break;
			case Log.VERBOSE:
				Log.v(this.getClass().getName(), name + ": " + msg, t);
				break;
			case Log.DEBUG:
				Log.d(this.getClass().getName(), name + ": " + msg, t);
				break;
		}
	}
	
	private int getAndroidLogLevel(int level){
		int out = Log.INFO;
		switch (level) {
			case  SEVERE: 
				out = Log.ERROR;
				break;
			case WARNING:
				out = Log.WARN;
				break;
			case INFO:
				out = Log.INFO;
				break;
			case CONFIG:
				out = Log.VERBOSE;
				break;
			case FINE:
				out = Log.DEBUG;
				break;
			case FINER:
				out = Log.DEBUG;
				break;	
			case FINEST:
				out = Log.DEBUG;
				break;
		}
		return out;
	}
}
