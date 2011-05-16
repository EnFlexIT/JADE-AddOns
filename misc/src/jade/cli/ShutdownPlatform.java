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

import java.util.Properties;

import jade.content.ContentException;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;


public class ShutdownPlatform extends CLICommand {
	
	public static void main(String[] args) {
		CLIManager.execute(new ShutdownPlatform(), args);
	}

	public Behaviour getBehaviour(Properties pp) throws IllegalArgumentException {
		return new OneShotBehaviour(null) {
			private static final long serialVersionUID = 6329705571783285365L;

			public void action() {
				try {
					ACLMessage request = CLIManager.createAMSRequest(myAgent, new jade.domain.JADEAgentManagement.ShutdownPlatform());
					myAgent.send(request);
				}
				catch (ContentException ce){
					ce.printStackTrace();
				}
			}
		};
	}
}
