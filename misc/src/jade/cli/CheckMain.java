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

import jade.core.MainContainerChecker;

import java.util.Properties;

public class CheckMain {

	/**
	 * CheckMain don't extend CLICommand because the mechanism work with behavior
 	 * that require the gateway JADE to be performed
	 */
	public static void main(String[] args) {
		Properties pp = CLIManager.parseCommandLine(args);
		if ("true".equalsIgnoreCase(pp.getProperty(CLICommand.HELP_OPTION, null))) {
			printUsage();
		}
		else {
			boolean mainPresent = MainContainerChecker.check(jade.util.leap.Properties.toLeapProperties(pp));
			if (mainPresent) {
				System.out.println("Main-container up and running");
			} else {
				System.out.println("Main-container down");
			}
		}
	}
	
	public static void printUsage() {
		System.out.println("USAGE: java -cp ... "+CheckMain.class.getName()+" [options]");
		System.out.println();
		System.out.println("Valid options:");
		System.out.println("-help: Print this help and terminate");
		System.out.println("-logs: Enable normal JADE logs");
		System.out.println("All JADE Split-Container options (see JADE documentation)");
	}
}
