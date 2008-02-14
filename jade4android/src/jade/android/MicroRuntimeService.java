package jade.android;

import jade.core.MicroRuntime;
import jade.core.NotFoundException;
import jade.imtp.leap.JICP.JICPProtocol;
import jade.util.Event;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.util.leap.Properties;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;


public class MicroRuntimeService extends Service {

	private String myAgentName;	
	private String gatewayClassName;
	private String[] gatewayAgentArgs;
	private Properties jadeProperties;
	
	private final IBinder mBinder = new JadeBinder(); 
	
	//Instance of the Logger
	private static final Logger myLogger = Logger.getMyLogger(MicroRuntimeService.class.getName()); 

	
	protected void onCreate() {
		myLogger.log(Logger.INFO, "MicroRuntimeService.onCreate(): called");	
	}


	protected void onStart(int startId, Bundle arguments) {
		myLogger.log(Logger.INFO, "MicroRuntimeService.onStart(): called");
		gatewayClassName = arguments.getString(JadeGateway.GATEWAY_CLASS_NAME);
		ArrayList<String> args = (ArrayList<String>)arguments.getSerializable(JadeGateway.GATEWAY_AGENT_ARGS);
		if (args != null){
			gatewayAgentArgs = new String[args.size()]; 
			args.toArray(gatewayAgentArgs);
			myLogger.log(Logger.INFO, "AGENT ARGS: " + gatewayAgentArgs);
		}else
			gatewayAgentArgs = null;

		myLogger.log(Logger.INFO, "properties: " + arguments.getSerializable(JadeGateway.PROPERTIES_BUNDLE).getClass().getName());	
		jadeProperties = (Properties) arguments.getSerializable(JadeGateway.PROPERTIES_BUNDLE);
	}

	

	protected void onDestroy() {
		// stop Jade
		myLogger.log(Logger.INFO, "MicroRuntimeService.onDestroy(): called");
		if (MicroRuntime.isRunning()) {
			MicroRuntime.stopJADE();
			myLogger.log(Logger.INFO, "MicroRuntimeService.onDestroy(): JADE stopped");
		}
	}


	private class JadeBinder extends Binder implements Command {
		
		public JadeBinder() {
			myLogger.log(Logger.FINE, "JadeBinder(): constructor");
		}

		public void execute(Object command) throws StaleProxyException,ControllerException,InterruptedException {
			execute(command,0);
		}

		
		public void checkJADE() throws Exception {
			myLogger.log(Logger.INFO, "JadeBinder.checkJade(): starting checkJade");
			if (!MicroRuntime.isRunning()){
				Properties props = (Properties)jadeProperties.clone();
				MicroRuntime.startJADE(props, null);
				myAgentName = (String)props.get(JICPProtocol.MEDIATOR_ID_KEY);
				//FIXME: Indagare da dove è meglio prendere il nome dell'agente
				myLogger.log(Logger.INFO, "JadeBinder.checkJade() : Agent name is " + myAgentName);
				MicroRuntime.startAgent(myAgentName, gatewayClassName, gatewayAgentArgs);
			}
		}

		public void execute(Object command, long timeout) throws StaleProxyException, ControllerException, InterruptedException {
			Event e = null;
			//incapsulate the command into an Event
			e = new Event(-1, command);
			AgentController agent = MicroRuntime.getAgent(myAgentName);
			agent.putO2AObject(e, AgentController.ASYNC);
			e.waitUntilProcessed(timeout);
		}

		public void shutdownJADE() {
			try {
				MicroRuntime.killAgent(myAgentName);
			} catch (NotFoundException e) {
				myLogger.log(Logger.SEVERE, "JadeBinder.shutdownJADE(): agent not found.", e);
			}
			MicroRuntime.stopJADE();			
		}

		
		public String getAgentName() {
			return myAgentName;
		}
	}


	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
}
