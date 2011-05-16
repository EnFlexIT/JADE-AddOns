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
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAService;
import jade.lang.acl.ACLMessage;

import java.util.Properties;

public class KillContainer extends CLICommand {

	@Option(value="<name>", description="The name of the container to be killed")
	public static final String CONTAINER_OPTION = "container";
	
	public static void main(String[] args) {
		CLIManager.execute(new KillContainer(), args);
	}

	@Override
	public Behaviour getBehaviour(Properties pp) throws IllegalArgumentException {
		final String containerName = CLIManager.getMandatoryOption(CONTAINER_OPTION, pp);
		return new OneShotBehaviour(null) {
			@Override
			public void action() {
				jade.domain.JADEAgentManagement.KillContainer kc = new jade.domain.JADEAgentManagement.KillContainer();
				kc.setContainer(new ContainerID(containerName, null));
				
				try {
					ACLMessage request = CLIManager.createAMSRequest(myAgent, kc);
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
