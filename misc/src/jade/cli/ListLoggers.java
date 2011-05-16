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

import jade.cli.behaviours.CreateRemoteLoggerControllerBehaviour;
import jade.cli.behaviours.GetLoggerInfoBehaviour;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import java.util.Properties;

/**
 * Create a remote logger controller and then ask for logger information
 * 
 * @author Salvatore Soldatini - TILAB
 *
 */
public class ListLoggers extends CLICommand {
	
	@Option(value="<container-name>", description="The container where to get log information from")
	public static final String CONTAINER_OPTION = "container";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CLIManager.execute(new ListLoggers(), args);
	}
	
	
	@Override
	public Behaviour getBehaviour(Properties pp) throws IllegalArgumentException {
		String containerName = CLIManager.getMandatoryOption(CONTAINER_OPTION, pp);

		SequentialBehaviour sb = new SequentialBehaviour();	
		sb.addSubBehaviour(new CreateRemoteLoggerControllerBehaviour(containerName));
		sb.addSubBehaviour(new GetLoggerInfoBehaviour());
		
		return sb;	
	}		
	
}
