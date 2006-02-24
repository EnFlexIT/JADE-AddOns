/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.gui.dialogs;

import jade.tools.ascml.repository.Repository;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 *  Presents a dialog especially for choosing Societies and AgentTypes.
 *  This dialog-class is instantiated by the GUIEventHandler once the user
 *  chooses 'Add Society' or 'Add AgentType' from the MenuBar or the JTree-contextmenu.
 *  Furthermore this class is instantiated by the ModelNotFoundDialog to let the
 *  user manually choose an appropiate model-file. 
 */
public class ChooseIconDialog extends AbstractDialog
{
	private JFileChooser iconFileChooser;

	/**
	 *
	 * @param repository  The Repository, needed to access the active project,
	 * cause Agents/Societies can only be added to the active project.
	 */
	public ChooseIconDialog(Repository repository)
	{
		super(repository);

		// create and initialize the fileChooser, but do not show it yet...
		iconFileChooser = new JFileChooser(repository.getProject().getWorkingDirectory());
		iconFileChooser.setAcceptAllFileFilterUsed(false);
		iconFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FileFilter filter = new FileFilter()
		{
			public String getDescription()
			{
				return "Image-Files (*.gif, *.jpg, *.png)";
			}

			public boolean accept(File f)
			{
				String name = f.getName();
				//return f.isDirectory() || name.endsWith(".properties") || name.endsWith(".xml") ||
				//		(name.endsWith(".class") && name.indexOf("Model")!=-1);
				return name.endsWith(".gif") ||  name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
			}
		};
		iconFileChooser.setFileFilter(filter);
		iconFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
	}

	/**
	 *
	 * @param parentFrame
	 * @return  The model-object of the model to add
	 */
	public Object showDialog(JFrame parentFrame)
	{
		// show the dialog
		if(iconFileChooser.showDialog(parentFrame, "Select Icon ...") == JFileChooser.APPROVE_OPTION)
		{
			File file = iconFileChooser.getSelectedFile();
			String fileName = ""+file;

			if (fileName != null)
			{
				/*
				try
				{
				System.err.println(ClassLoader.getSystemClassLoader().getResource(file.getAbsoluteFile()+""));
				System.err.println(ClassLoader.getSystemClassLoader().getResource(file.getCanonicalFile()+""));
				System.err.println(ClassLoader.getSystemClassLoader().getResource(""+file.toURI()));
				System.err.println(ClassLoader.getSystemClassLoader().getResource(""+file.toURL()));
				System.err.println(ClassLoader.getSystemResource(""+file.getAbsoluteFile()));
				System.err.println(ClassLoader.getSystemResource(""+file.getCanonicalFile()));
				System.err.println(ClassLoader.getSystemResource(""+file.toURI()));
				System.err.println(ClassLoader.getSystemResource(""+file.toURL()));
				}
				catch(Exception e)
				{}
                */
				return fileName;
			}
		}
		return null;
	}
}

