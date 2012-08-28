/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package jade.cli;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.ContentException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.MicroRuntime;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;


public class CLIManager {
	
	public static final String CLI_ADMIN_EXECUTOR_NAME = "cliAdminExecutor";
	
	private static String cliAdminExecutorName = CLI_ADMIN_EXECUTOR_NAME;
	
	/**
	 * Parse a command line of the form -<key1> <value1> -<key2> <value2> ... into a 
	 * Properties object
	 */
	public static Properties parseCommandLine(String[] args) throws IllegalArgumentException {
		Properties props = new ExtendedProperties();
		if (args != null) {
			int i = 0;
			while (i < args.length) {
				if (args[i].startsWith("-")) {
					// Parse next option
					String name = args[i].substring(1);
					if (name.equals(CLICommand.HELP_OPTION) || name.equals(CLICommand.LOGS_OPTION)) {
						props.setProperty(name, "true");
					}
					else {
						if (++i < args.length) {
							props.setProperty(name, args[i]);
						}
						else {
							throw new IllegalArgumentException("No value specified for property \""+name+"\"");
						}
					}
					++i;
				}
				else {
					throw new IllegalArgumentException("Invalid argument "+args[i]+": options must have the form \"-key value\"");
				}
			}
		}

		return props;
	}
	
	public static String getMandatoryOption(String optionName, Properties pp) throws IllegalArgumentException {
		String value = pp.getProperty(optionName);
		if (value == null) {
			throw new IllegalArgumentException("Missing mandatory option -"+optionName);
		}
		return value;
	}
		
	public static void disableLogs() {
		java.util.logging.LogManager logManager = java.util.logging.LogManager.getLogManager();
		Logger rootLogger = logManager.getLogger("");
		rootLogger.setLevel(Level.WARNING);
	}
	
	public static void execute(CLICommand command, String[] args) {
		command.setPrintStream(System.out);
		try {
			Properties pp = parseCommandLine(args);
			if ("true".equalsIgnoreCase(pp.getProperty(CLICommand.HELP_OPTION, null))) {
				command.printUsage();
			}
			else {
				Behaviour b = command.getBehaviour(pp);
				execute(pp, b, true);
			}
		}
		catch (IllegalArgumentException iae) {
			System.out.println(iae.getMessage());
			command.printUsage();
		}
		catch (ControllerException ce) {
			System.out.println("Error connecting to the remote JADE system. Use the -"+CLICommand.LOGS_OPTION+" option to see JADE specific logs");
		}
	}
	
	public static void execute(String agentName, Properties pp, Behaviour b, boolean exitOnTermination) throws ControllerException {
		cliAdminExecutorName = agentName;
		execute(pp, b, exitOnTermination);
	}

	public static void execute(Properties pp, Behaviour b, final boolean exitOnTermination) throws ControllerException {
		if (!"true".equalsIgnoreCase(pp.getProperty(CLICommand.LOGS_OPTION, null))) {
			disableLogs();
		}
		pp.setProperty("exitwhenempty", "true");
		pp.setProperty(MicroRuntime.AGENTS_KEY, cliAdminExecutorName+":"+CLIAdminExecutorAgent.class.getName());
		MicroRuntime.startJADE((jade.util.leap.Properties) pp, new Runnable() {
			public void run() {
				if (exitOnTermination) {
					try {
						Thread.sleep(1000);
						System.exit(0);
					}
					catch (Exception e) {
						// Should never happen
						e.printStackTrace();
					}
				}
			}
		} );
		
		AgentController executor = MicroRuntime.getAgent(cliAdminExecutorName);
		executor.putO2AObject(b, AgentController.ASYNC);
	}
	
	public static ACLMessage createAMSRequest(Agent a, AgentAction act) throws ContentException {
		Action actExpr = new Action();
		actExpr.setActor(a.getAMS());
		actExpr.setAction(act);
		
		SLCodec codec = new SLCodec();
		a.getContentManager().registerOntology(JADEManagementOntology.getInstance());
		a.getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);

		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(a.getAMS());
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		request.setOntology(JADEManagementOntology.NAME);
		
		a.getContentManager().fillContent(request, actExpr);
		return request;
	}
	
	public static void printFipaException(Agent a, FIPAException fe) {
		ACLMessage msg = fe.getACLMessage();
		try {
			ContentElement ce = (ContentElement) a.getContentManager().extractContent(msg);
			if (ce instanceof Exception) {
				System.out.println(((Exception) ce).getMessage());
				return;
			}
			else if (ce instanceof ContentElementList) {
				ContentElementList l = (ContentElementList) ce;
				if (l.size() == 2) {
					ce = (ContentElement) l.get(1);
					if (ce instanceof Exception) {
						System.out.println(((Exception) ce).getMessage());
						return;
					}
				}
			}
		}
		catch (Exception e) {
		}
		System.out.println(fe.getMessage());
	}
	
}
