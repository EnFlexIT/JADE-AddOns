/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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

package test.common.testSuite.gui;

import javax.swing.*;
import java.util.Properties;
import java.io.*;

/**
 * This class encapsulates some informations related to the GUI
 */
class GuiProperties {
	protected static UIDefaults myDefaults;
	protected static GuiProperties foo = new GuiProperties();
	public static final String imagePath = "";
 	static {
		Object[] icons = {
			"exit",LookAndFeel.makeIcon(foo.getClass(), "images/exit.gif"),
			"open",LookAndFeel.makeIcon(foo.getClass(), "images/open.gif"),
			"run",LookAndFeel.makeIcon(foo.getClass(), "images/run.gif"),
			"runAll",LookAndFeel.makeIcon(foo.getClass(), "images/runall.gif"),
			"debug", LookAndFeel.makeIcon(foo.getClass(), "images/debug.gif"),
			"step", LookAndFeel.makeIcon(foo.getClass(), "images/step.gif"),
			"config", LookAndFeel.makeIcon(foo.getClass(), "images/config.gif"),
			"connect", LookAndFeel.makeIcon(foo.getClass(), "images/connect.gif")
		};

		myDefaults = new UIDefaults (icons);
	}

	// synchronized to allows several instances to use the same code. 
	synchronized public static final Icon getIcon(String key) {
		Icon i = myDefaults.getIcon(key);
		return i;
	}
}

