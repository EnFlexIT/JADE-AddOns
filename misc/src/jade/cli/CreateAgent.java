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

import jade.core.ContainerID;
import jade.core.Specifier;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAService;
import jade.lang.acl.ACLMessage;

import java.util.Properties;
import java.util.Vector;

public class CreateAgent extends CLICommand {

	@Option(value="<name>", description="The name of the agent to be created")
	public static final String AGENT_NAME_OPTION = "agent-name";
	@Option(value="<class name>", description="The fully qualified class name of the agent to be created")
	public static final String CLASS_NAME_OPTION = "class-name";
	@Option(value="<argument list>", description="A list of comma-separated arguments. No spaces are allowed (optional)")
	public static final String ARGUMENTS_OPTION = "arguments";
	@Option(value="<container-name>", description="The container where to create the new agent")
	public static final String CONTAINER_OPTION = "container";
	
	public static void main(String[] args) {
		CLIManager.execute(new CreateAgent(), args);
	}

	@Override
	public Behaviour getBehaviour(Properties pp) throws IllegalArgumentException {
		final String agentName = CLIManager.getMandatoryOption(AGENT_NAME_OPTION, pp);
		final String className = CLIManager.getMandatoryOption(CLASS_NAME_OPTION, pp);
		final String argumentsList = pp.getProperty(ARGUMENTS_OPTION);
		final String containerName = CLIManager.getMandatoryOption(CONTAINER_OPTION, pp);
		return new OneShotBehaviour(null) {
			@Override
			public void action() {
				jade.domain.JADEAgentManagement.CreateAgent ca = new jade.domain.JADEAgentManagement.CreateAgent();
				ca.setAgentName(agentName);
				ca.setClassName(className);
				ca.setContainer(new ContainerID(containerName, null));
				if (argumentsList != null) {
					Vector v = Specifier.parseList(argumentsList, ',');
					for (Object a : v) {
						ca.addArguments(a);
					}
				}
				
				try {
					ACLMessage request = CLIManager.createAMSRequest(myAgent, ca);
					ACLMessage response = FIPAService.doFipaRequestClient(myAgent, request, 10000);
					if (response == null) {
						System.out.println("Timeout expired");
					}
				}
				catch (FIPAException fe) {
					CLIManager.printFipaException(myAgent, fe);
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			
		};
	}

}
