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

import jade.core.behaviours.Behaviour;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Properties;

public abstract class CLICommand {
	
	public static final String HELP_OPTION = "help";
	public static final String LOGS_OPTION = "logs";
	// Allows specifying a character that will be substituted with ' '. This is useful when the value of a property 
	// contains one or more white spaces e.g.
	// -x AAA BBB  can be specified as
	// -x AAA#BBB -blanc #
	public static final String BLANC_OPTION = "blanc";
	
	// Allows specifying that a value of a given option is the content of a given file
	public static final String FILE_PREFIX = "[FILE]";
	
	protected PrintStream out;
	public Shell shell;
	
	void setPrintStream(PrintStream out) {
		this.out = out;
	}
	
	void setShell(Shell shell) {
		this.shell = shell;
	}
	
	public void printUsage() {
		out.println("USAGE: java -cp ... "+getClass().getName()+" [options]");
		out.println();
		out.println("Valid options:");
		printCommandSpecificOptions();
		out.println("-"+HELP_OPTION+": Print this help and terminate");
		out.println("-"+LOGS_OPTION+": Enable normal JADE logs");
		out.println("All JADE Split-Container options (see JADE documentation)");
	}
	
	void printCommandSpecificOptions() {
		Field[] fields = getClass().getFields();
		for (Field f : fields) {
			Option opt = f.getAnnotation(Option.class);
			if (opt != null) {
				// This  field is an option. Check that it is a String field (ignore it otherwise)
				if (String.class == f.getType()) {
					try {
						String optionName = (String) f.get(this);
						out.println("-"+optionName+" "+opt.value()+": "+opt.description());
					}
					catch (IllegalAccessException e) {
						// Field not accessible. Should never happen as getFields() returns public fields only 
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public abstract Behaviour getBehaviour(Properties pp) throws IllegalArgumentException;
}
