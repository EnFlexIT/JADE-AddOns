package jade.android;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import jade.core.Profile;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;


public class JadeGateway   {
	
	private static HashMap map = new HashMap();
	private  Command jadeBinder = null;
	
	//Instance of the Logger
	private static final Logger myLogger = Logger.getMyLogger(JadeGateway.class.getName()); 
	
	//Property variables for JADE

	private static Properties jadeProps;

	//Constants for keys inside the Bundle
	static final String GATEWAY_CLASS_NAME="GATEWAY_CLASS_NAME";
	static final String GATEWAY_AGENT_ARGS="GATEWAY_AGENT_ARGS";
	static final String PROPERTIES_BUNDLE="PROPERTIES_BUNDLE";
	
	
	private JadeGateway(Command cmd){
		jadeBinder = cmd;
	}
	
	/** Searches for the property with the specified key in the JADE Platform Profile. 
	 *	The method returns the default value argument if the property is not found. 
	 * @param key - the property key. 
	 * @param defaultValue - a default value
	 * @return the value with the specified key value
	 * @see java.util.Properties#getProperty(String, String)
	 **/
	public final static String getProfileProperty(String key, String defaultValue) {
		return jadeProps.getProperty(key, defaultValue);
	}
	
	/**
	 * execute a command. 
	 * This method first check if the executor Agent is alive (if not it
	 * creates container and agent), then it forwards the execution
	 * request to the agent, finally it blocks waiting until the command
	 * has been executed (i.e. the method <code>releaseCommand</code> 
	 * is called by the executor agent)
	 * @throws StaleProxyException if the method was not able to execute the Command
	 * @see jade.wrapper.AgentController#putO2AObject(Object, boolean)
	 **/
	public final void execute(Object command) throws StaleProxyException,ControllerException,InterruptedException, Exception {
		execute(command, 0);
	}
	
	/**
	 * Execute a command specifying a timeout. 
	 * This method first check if the executor Agent is alive (if not it
	 * creates container and agent), then it forwards the execution
	 * request to the agent, finally it blocks waiting until the command
	 * has been executed. In case the command is a behaviour this method blocks 
	 * until the behaviour has been completely executed. 
	 * @throws InterruptedException if the timeout expires or the Thread
	 * executing this method is interrupted.
	 * @throws StaleProxyException if the method was not able to execute the Command
	 * @see jade.wrapper.AgentController#putO2AObject(Object, boolean)
	 **/
	public final void execute(Object command, long timeout) throws StaleProxyException,ControllerException,InterruptedException, ConnectException, Exception {
	
		checkJADE();
		try {
			jadeBinder.execute(command,timeout);
		} 
		catch (StaleProxyException exc)  
		{
			exc.printStackTrace();
			// in case an exception was thrown, restart JADE
			// and then re-execute the command
			restartAfterFailure(command, timeout);
		}
		catch (ControllerException exc) 
		{
			exc.printStackTrace();
			// in case an exception was thrown, restart JADE
			// and then reexecute the command
			restartAfterFailure(command, timeout);
		}
	}
	
	private void restartAfterFailure(Object command, long timeout) throws StaleProxyException, ControllerException, InterruptedException, Exception {
		jadeBinder.shutdownJADE();
		jadeBinder.checkJADE();
		jadeBinder.execute(command,timeout);
	}
	
	/**
	 * This method checks if both the container, and the agent, are up and running.
	 * If not, then the method is responsible for renewing myContainer.
	 * Normally programmers do not need to invoke this method explicitly.
	 **/
	public final void checkJADE() throws ConnectException, Exception {
		if (jadeBinder != null)
			jadeBinder.checkJADE();
		else 
			throw new ConnectException("Not connected to MicroRuntimeService!!");
	}
	
	/**
	 * Initialize this gateway by passing the proper configuration parameters
	 * It connects to the MicroRuntime service and set the jade properties.
	 * @param agentClassName is the fully-qualified class name of the JadeGateway internal agent. If null is passed
	 * the default class will be used.
	 * @param agentArgs is the list of string agent arguments 
	 * @param jadeProfile the properties that contain all parameters for running JADE (see jade.core.Profile).
	 * Typically these properties will have to be read from a JADE configuration file.
	 * If jadeProfile is null, then a JADE container attaching to a main on the local host is launched
	 **/
	public final static void connect(String agentClassName, String[] agentArgs, Properties jadeProfile, Context ctn, ConnectionListener list) throws Exception{
		myLogger.log(Logger.FINE,"connect(): called");
	
		String agentType = agentClassName;
		if (agentType == null) {
			agentType = GatewayAgent.class.getName();
		}

		Properties jadeProps = jadeProfile;
		if (jadeProps != null) {
			// Since we will create a non-main container --> force the "main" property to be false
			jadeProps.setProperty(Profile.MAIN, "false");
		}else{
			myLogger.log(Logger.SEVERE,"connect(): jade properties cannot be null.");
			throw new Exception("Jade properties cannot be null.");
		}
		
		MicroRuntimeServiceConnection sConn = new MicroRuntimeServiceConnection(list);
		map.put(ctn, sConn);
		Bundle b = prepareBundle(agentType,agentArgs,jadeProps);
		
		ctn.startService(new Intent(ctn, MicroRuntimeService.class), b);
		ctn.bindService(new Intent(ctn, MicroRuntimeService.class), sConn,Context.BIND_AUTO_CREATE);
	
	}
	
	public String getAgentName() throws ConnectException {
		if (jadeBinder != null){
			return jadeBinder.getAgentName();
		} else {
			throw new ConnectException("Not connected to MicroRuntimeService!!");
		}
	}
	
	private static Bundle prepareBundle(String agentClassName, String[] agentArgs, Properties jadeProfile){
		Bundle b = new Bundle();
		
		b.putString(GATEWAY_CLASS_NAME, agentClassName);
		
		if (agentArgs != null){
			ArrayList<String> aArgs =  new ArrayList<String>(Arrays.asList(agentArgs));
			b.putSerializable(GATEWAY_AGENT_ARGS, aArgs);
		}
		//since there are some serialization problems, we create an hashmap 
		//to store the properties that be to be put into to bundle 
		HashMap theProps = new HashMap();
		for(Enumeration e = jadeProfile.keys(); e.hasMoreElements();){
			String key = (String)e.nextElement();
			theProps.put(key, jadeProfile.get(key));
		}
		
		b.putSerializable(PROPERTIES_BUNDLE, theProps);
		return b;
	}
	

	public final static void connect(String agentClassName, Properties jadeProfile, Context ctn, ConnectionListener list) throws Exception{
		connect(agentClassName, null, jadeProfile, ctn, list);
	}
	
	/**
	 * Kill the JADE Container in case it is running.
	 */
	public final void shutdownJADE() throws ConnectException {
		if (jadeBinder != null)
			jadeBinder.shutdownJADE();
		else 
			throw new ConnectException("Not connected to MicroRuntimeService!!");
		
	}
	
	public final void disconnect(Context ctn) {
		//FIXME: Il metodo non e' statico poiche' la unbind non invalida il binder
		//che quindi viene impostato a null per impedire l'esecuzione di comandi
		myLogger.log(Logger.FINE, "disconnect(): disconnecting from service");
		ctn.unbindService((ServiceConnection)map.get(ctn));
		jadeBinder = null;
		map.remove(ctn);
	}
	
	
	private static class MicroRuntimeServiceConnection implements ServiceConnection {

		private ConnectionListener conListener;

		public MicroRuntimeServiceConnection(ConnectionListener listener) {
			this.conListener = listener;
		}

		public void onServiceConnected(ComponentName className, IBinder service) {
			myLogger.log(Logger.FINE,"onServiceConnected(): called");
			if(conListener != null) {
				conListener.onConnected(new JadeGateway((Command)service));
			}
		}

		//is called only when the connection to the service fails. 
		//i.e. crash of the service process.
		public void onServiceDisconnected(ComponentName className){
			myLogger.log(Logger.FINE,"onServiceDisconnected(): called");
			if(conListener != null) {
				conListener.onDisconnected();
			}
		}
	}
}
