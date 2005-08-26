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


package jade.tools.ascml.model;

import java.util.*;
import javax.swing.ImageIcon;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.exceptions.ModelException;

/**
 *  Model-object containing all required information about an agent-type. 
 */
public class AgentTypeModel extends AbstractAgentModel implements IAgentType
{
	/** The agent type description. */
	private String description;

	/** The ImageIcon used to represent this agents in the repository-tree */
	private ImageIcon icon;

	/**
	 * The platform-identifier (e.g. jade).
	 */
	private String platformType;

	/**
	 *  Instantiate a new model and initialise some variables 
	 */
	public AgentTypeModel(Vector modelChangedListener)
	{
		super(modelChangedListener);
		description = "";
		platformType = PLATFORM_TYPE_JADE;
	}

	//-------- methods --------

	/**
	 *  Set the name of this agentType.
	 *  @param name  agentType's name.
	 */
	public void setName(String name)
	{
		if(name == null)
			name = "";
		super.name = name.trim();
	}

	/**
	 *  Get the name of this agentType.
	 *  @return  agentType's name.
	 */	
	public String getName()
	{
		return super.name;
	}

	/**
	 * Set the type of agent-platform (e.g. jade) this agenttype is designed to run on.
	 * @param platformType  The platformType as String, possible values may be
	 * PLATFORM_TYPE_JADE, PLATFORM_TYPE_JADEX
	 */
	public void setPlatformType(String platformType)
	{
		if(platformType == null)
			platformType = "";
		this.platformType = platformType.trim();
	}

	/**
	 * Get the type of agent-platform (e.g. jade) this agenttype is designed to run on.
	 * return The platformType as String, possible values may be PLATFORM_TYPE_JADE, PLATFORM_TYPE_JADEX
	 */
	public String getPlatformType()
	{
		return platformType;
	}

	/**
	 * Update the status of this model.
	 * The status indicates, whether this model is ready to run or if it contains errors.
	 */
	public void updateStatus()
	{
		String newStatus = STATUS_OK;

		if (getName().equals(AbstractAgentModel.NAME_UNKNOWN))
		{
		    statusException = new ModelException("The agenttype is unknown (possibly not contained within the repository).", "The agenttype is unkwown, which means, that it couldn't be found in the repository. Please first load the agenttype before going on.");
			newStatus = STATUS_ERROR;
		}
		else
		{
			statusException = new ModelException("The agenttype '" + getName() + "' is errorneous.", "The AgentType contains errors.");

			if ((getName() == null) || getName().equals(""))
			{
				statusException.addExceptionDetails("A name for this agenttype is missing", "write me!");
				newStatus = STATUS_ERROR;
			}
			if ((getFullyQualifiedName() == null) || getFullyQualifiedName().equals(""))
			{
				statusException.addExceptionDetails("A fully qualified name for this agenttype is missing", "write me!");
				newStatus = STATUS_ERROR;
			}
			if ((getClassName() == null) || getClassName().equals(""))
			{
				statusException.addExceptionDetails("A class-name for this agenttype is missing", "write me!");
				newStatus = STATUS_ERROR;
			}
			if ((getPlatformType() == null) || getPlatformType().equals(""))
			{
				statusException.addExceptionDetails("The platform-type of this agent is missing", "write me!");
				newStatus = STATUS_ERROR;
			}
			if (getDocument() == null)
			{
				statusException.addExceptionDetails("The source-document for this agenttype is missing", "write me!");
				newStatus = STATUS_ERROR;
			}
		}

		if (getStatus() != newStatus)
		{
			status = newStatus;
			throwModelChangedEvent(ModelChangedEvent.STATUS_CHANGED);
		}
	}

	/**
	 *  Returns the fully qualified agentType-name. The name is composed of 
	 *  the package-name and the agentType-name, for example my.packageName.agentTypeName
	 *  would be a correct 'fully qualified' agentTypeName.
	 *  @return  fully qualified name of the agentType.
	 */
	public String getFullyQualifiedName()
	{
		if ((getDocument().getPackageName() == null) || getDocument().getPackageName().equals(""))
			return name;
		else
			return getDocument().getPackageName()+"."+name;
	}
	
	/**
	 *  Set the description of this agentType.
	 *  @param description  agentType's description.
	 */
	public void setDescription(String description)
	{
		if (description == null)
			description = "";
		this.description = description.trim();
	}

	/**
	 *  Get the agent's description.
	 *  @return  agent's description.
	 */		
	public String getDescription()
	{
		return description;
	}

	/**
	 *  There are two ways of describing an agent, the get-/setDescription-methods
	 *  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	 *  on the other hand deal with the FIPA-agentdescription, which is more technical
	 *  and used for registering and searching for agents at the directory facilitator for example.
	 *  @param agentDescription  the agentType's AgentDescriptionModel.
	 */
	public void setAgentDescription(IAgentDescription agentDescription)
	{
		super.agentDescription = agentDescription;
	}
	
	/**
	 *  Set the classname of this agentType.
	 *  @param className  agentType's classname.
	 */	
	public void setClassName(String className)
	{
		if (className == null)
			className = "";
		super.className = className.trim();
	}

	/**
	 *  Set the document.
	 *  @param document  The document.
	 */
	public void setDocument(IDocument document)
	{
		super.document = document;
	}

	/**
	 *  Set the image-icon.
	 *  @param icon  The image-icon.
	 */
	public void setIcon(ImageIcon icon)
	{
		this.icon = icon;
	}

	/**
	 *  Get the image-icon.
	 *  @return The image-icon.
	 */          
	public ImageIcon getIcon()
	{
		if (icon == null)
			icon = ImageIconLoader.createImageIcon(ImageIconLoader.AGENTTYPE); // get default-icon
		return icon;
	}
	
	public void addParameter(IAgentParameter parameter)
	{
		super.parameters.put(parameter.getName(), parameter);
	}

	/**
	 *  Add a parameter set.
	 *  @param paramset The parameter set.
	 */
	public void addParameterSet(IAgentParameterSet paramset)
	{
		super.parameterSets.put(paramset.getName(), paramset);
	}

	/**
	 *  This method returns a short String with the agentType-name.
	 *  It is used by the RepositoryTree for example, to name the nodes.
	 *  @return  String containing the name of this agentType 
	 */
	public String toString()
	{		
		return getName(); // + " " + super.toString();
	}
	
	/**
	 *  This method returns a formatted String showing the agentType-model.
	 *  @return  formatted String showing ALL information about this agentType.
	 */
	public String toFormattedString()
	{		
		String str = "";
		
		/*str += "  Agent-Type : name = " + getName() + "\n";
		str += "     package = " + getDocument().getPackageName() + "\n";
		str += "     class = " + className + "\n";
		str += "     desc = " + description + "\n";

		Iterator keys = parameters.keySet().iterator();
		while (keys.hasNext())
		{
			String oneKey = (String)keys.next();
			HashMap optionMap = (HashMap)parameters.get(oneKey);
			
			str +=  "   parameter : name = " + optionMap.get("name")        + "\n";
			str +=  "     type = " + optionMap.get("type")        + "\n";
			str +=  "     optional = " + optionMap.get("optional")    + "\n";
			str +=  "     description = " + optionMap.get("description") + "\n";
			str +=  "     value = " + optionMap.get("value")       + "\n";
			str +=  "     constraints = " + optionMap.get("constraints") + "\n";
			
		}

		keys = parameterSets.keySet().iterator();		
		while (keys.hasNext())
		{
			String oneKey   = (String)keys.next();
			HashMap optionMap = (HashMap)parameterSets.get(oneKey);
			
			str +=  "   param-set : name = " + optionMap.get("name")        + "\n";
			str +=  "     type = " + optionMap.get("type")        + "\n";
			str +=  "     optional = " + optionMap.get("optional")    + "\n";
			str +=  "     description = " + optionMap.get("description") + "\n";
			str +=  "     value = " ;
						
			Iterator oneSet = ((Collection)optionMap.get("value")).iterator();
			str += "[";
			while (oneSet.hasNext())
			{
				str += oneSet.next() + ",";
			}
			str += "]\n";
			
			str +=  "     constraints = ";
			oneSet = ((Collection)optionMap.get("constraints")).iterator();
			str += "[";
			while (oneSet.hasNext())
			{
				str += oneSet.next() + ",";
			}
			str += "]\n";			
		} */
		return str;
	}
}