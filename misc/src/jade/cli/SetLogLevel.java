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

import jade.cli.behaviours.CreateRemoteLoggerControllerBehaviour;
import jade.cli.behaviours.InitializeLogManagerBehaviour;
import jade.cli.behaviours.SetLogLevelBehaviour;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import java.util.Properties;

/**
 * SetLogLevel command line
 * 
 * Create a remote logger controller (LogHelperAgent), initialize the LogManager
 * and then set the logger log level.
 * 
 * @author Salvatore Soldatini - TILAB
 *
 */

public class SetLogLevel extends CLICommand {

	@Option(value="<logger-name>", description="The name of the logger whose level must be set")
	public static final String LOGGER_OPTION = "logger";
	@Option(value="<container-name>", description="The container containing the target logger")
	public static final String CONTAINER_OPTION = "container";
	@Option(value="<SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST|ALL>", description="The new level (see java.util.loggiong.Level)")
	public static final String LEVEL_OPTION = "level";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CLIManager.execute(new SetLogLevel(), args);
	}

	@Override
	public Behaviour getBehaviour(Properties pp) throws IllegalArgumentException {
		String loggerName = CLIManager.getMandatoryOption(LOGGER_OPTION, pp);
		String containerName = CLIManager.getMandatoryOption(CONTAINER_OPTION, pp);
		String levelStr = CLIManager.getMandatoryOption(LEVEL_OPTION, pp);
		Level level = Level.parse(levelStr);
		
		SequentialBehaviour sb = new SequentialBehaviour();
				
		sb.addSubBehaviour(new CreateRemoteLoggerControllerBehaviour(containerName));
		sb.addSubBehaviour(new InitializeLogManagerBehaviour());
		sb.addSubBehaviour(new SetLogLevelBehaviour(loggerName, level.intValue()));
					
		return sb;
	}
}
