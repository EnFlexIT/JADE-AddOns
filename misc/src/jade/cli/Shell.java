package jade.cli;

import jade.core.AgentContainer;
import jade.core.Profile;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.mobility.AgentMobilityHelper;
import jade.core.mobility.AgentMobilityService;
import jade.wrapper.gateway.DynamicJadeGateway;
import jade.wrapper.gateway.GatewayListener;
import jade.wrapper.gateway.JadeGateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class Shell implements Runnable {
	
	private InputStream inp;
	private PrintStream out;	
	private DynamicJadeGateway myGateway;
	private ClassLoader loader;

	public static void main(String[] args) {	
		Properties pp = CLIManager.parseCommandLine(args);
		// Unless the -logs option is set to true, disable all logs > than WARNING
		if (!"true".equalsIgnoreCase(pp.getProperty(CLICommand.LOGS_OPTION, null))) {
			CLIManager.disableLogs();
		}

		// Initialize the JADE Gateway
		if (pp.getProperty(Profile.CONTAINER_NAME, null) == null) {
			pp.setProperty(Profile.CONTAINER_NAME, "GW-JCLI-Shell");
		}
		jade.util.leap.Properties leapPP = new jade.util.leap.Properties();
		leapPP.putAll(pp);
		JadeGateway.init(null, leapPP);
		
		try {
			JadeGateway.checkJADE();
			
			// Run the shell
			Shell s = new Shell(System.in, System.out);
			s.run();
			
			JadeGateway.shutdown();
		}
		catch (Exception e) {
			// JADE startup error
			e.printStackTrace();
			System.out.println("Cannot connect to the platform at "+leapPP.getProperty(Profile.MAIN_HOST)+":"+leapPP.getProperty(Profile.MAIN_PORT));
		}
	}
	
	
	public Shell(InputStream inp, PrintStream out) {
		this(inp, out, JadeGateway.getDefaultGateway());
	}
	
	public Shell(InputStream inp, PrintStream out, DynamicJadeGateway gw) {
		this.inp = inp;
		this.out = out;
		myGateway = (gw != null ? gw : JadeGateway.getDefaultGateway());
		myGateway.addListener(new GatewayListener() {
			@Override
			public void handleGatewayConnected() {
				// Nothing to do 
			}

			@Override
			public void handleGatewayDisconnected() {
				loader = null;
			}		
		});
	}
	
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inp));
			String command = "";
			out.println();
			while (true) {
				out.print("JCLI>");
				command = br.readLine();
				if (command != null && !command.equals("exit")) {
					command = command.trim();
					if (command.length() > 0) {
						processCommand(command);
					}
				}
				else {
					break;
				}
			}
		} catch (IOException e) {
		}
	}
	
	/**
	 * Parse, load and execute a command of the form
	 * <fully-qualified-CLICommand-classname> -option1 value1 -option2 value2 ...
	 */
	public void processCommand(String command) {
		StringTokenizer st = new StringTokenizer(command, " ");
		String commandClass = st.nextToken();
		List<String> argsList = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			argsList.add(st.nextToken());
		}
		String[] args = argsList.toArray(new String[]{});

		CLICommand cmd = null;
		try {
			cmd = loadCLICommand(commandClass);
			cmd.setPrintStream(out);
			Properties commandProperties = CLIManager.parseCommandLine(args);
			if ("true".equalsIgnoreCase(commandProperties.getProperty(CLICommand.HELP_OPTION, null))) {
				printUsage(cmd);
			}
			else {
				Behaviour b = cmd.getBehaviour(commandProperties);
				try {
					myGateway.execute(b);
				} 
				catch (Exception e) {
					out.println("Error executing command "+commandClass);
					e.printStackTrace(out);
				} 
			}
		}
		catch (IllegalArgumentException iae) {
			out.println(iae.getMessage());
			printUsage(cmd);
		}
		catch (ClassNotFoundException cnfe) {
			out.println("Unrecognized command "+commandClass);
		}
		catch (Exception e) {
			out.println("Error loading command "+commandClass);
			e.printStackTrace(out);
		}
	}
	
	
	private CLICommand loadCLICommand(String commandClass) throws Exception {
		if (loader == null) {
			loader = getLoader();
		}
		return (CLICommand) Class.forName(commandClass, true, loader).newInstance();
	}
	
	private ClassLoader getLoader() throws Exception {
		ClassLoaderRetriever clr = new ClassLoaderRetriever();
		myGateway.execute(clr);
		ServiceException exception = clr.getException();
		if (exception != null) {
			throw exception;
		}
		else {
			return clr.getLoader();
		}
	}
	
	private void printUsage(CLICommand cmd) {
		out.println("USAGE: "+cmd.getClass().getName()+" [options]");
		out.println();
		out.println("Valid options:");
		cmd.printCommandSpecificOptions();
		out.println("-"+CLICommand.HELP_OPTION+": Print this help and terminate");
	}
	
	
	private class ClassLoaderRetriever extends OneShotBehaviour {
		private ServiceException exception;
		private ClassLoader cl;
		
		public void action() {
			try {
				AgentMobilityHelper mh = (AgentMobilityHelper) myAgent.getHelper(AgentMobilityService.NAME);
				cl = mh.getContainerClassLoader(AgentContainer.MAIN_CONTAINER_NAME, Remote.class.getClassLoader());
			}
			catch (ServiceException e) {
				exception = e;
			}
		}
		
		public ServiceException getException() {
			return exception;
		}
		
		public ClassLoader getLoader() {
			return cl;
		}
	}
}
