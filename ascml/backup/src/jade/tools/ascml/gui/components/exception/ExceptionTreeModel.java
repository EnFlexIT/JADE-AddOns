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


package jade.tools.ascml.gui.components.exception;

import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.ISocietyType;
import jade.tools.ascml.absmodel.ISocietyInstance;
import jade.tools.ascml.absmodel.IRunnableSocietyInstance;
import jade.tools.ascml.gui.components.tree.*;
import jade.tools.ascml.gui.components.tree.model.AbstractRepositoryTreeModel;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.events.PropertyChangedEvent;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.exceptions.ASCMLException;

import javax.swing.tree.*;
import java.util.Vector;

/**
 *  Tree-model-object.
 */

public class ExceptionTreeModel extends DefaultTreeModel
{
	/**
	 *  Instantiate a new model and initialise some variables
	 */
	public ExceptionTreeModel(DefaultMutableTreeNode rootNode, ASCMLException rootException)
	{
		super(rootNode);

        createExceptionSubNode(rootNode, rootException);
	}

    private void createExceptionSubNode(DefaultMutableTreeNode rootNode, Exception rootException)
	{
		if (rootException instanceof ASCMLException)
		{
			DefaultMutableTreeNode exceptionNode = new DefaultMutableTreeNode(((ASCMLException)rootException).getShortMessage());
			rootNode.add(exceptionNode);

			for (int i=0; i < ((ASCMLException)rootException).getExceptionDetails().size(); i++)
			{
				DefaultMutableTreeNode exceptionDetailsNode = new DefaultMutableTreeNode(((ASCMLException)rootException).getExceptionDetails().elementAt(i));
				exceptionNode.add(exceptionDetailsNode);
			}

			Vector nestedExceptions = ((ASCMLException)rootException).getNestedExceptions();

			for (int i=0; i < nestedExceptions.size(); i++)
			{
				createExceptionSubNode(exceptionNode, (Exception)nestedExceptions.elementAt(i));
			}
		}
		else
		{
			DefaultMutableTreeNode exceptionNode = new DefaultMutableTreeNode(rootException.getMessage());
			rootNode.add(exceptionNode);
		}
	}
}
