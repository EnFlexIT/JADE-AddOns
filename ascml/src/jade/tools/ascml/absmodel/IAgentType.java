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


package jade.tools.ascml.absmodel;

import javax.swing.ImageIcon;

/**
 *  The IAgentType-interface is exclusivly used by the gui- and loader-classes, 
 *  it is not passed down to the launcher-level. Classes at the launcher-level use
 *  objects, that implement the IRunnableAgentInstance-interface, which has the same
 *  super-interface (IAbstractAgent) as this IAgentType-interface, but miss several 
 *  methods for setting variables. These setter-methods are subject to the IAgentType-interface
 *  and it's implementation.
 *  todo: clean-up as commented, add methods for service description handling
 *  todo: introduce parameter/set model elements
 */
public interface IAgentType extends IAbstractAgent
{
	public final static String PLATFORM_TYPE_JADE = "jade";
	public final static String PLATFORM_TYPE_JADEX = "jadex";

	/**
	 *  Returns the fully qualified agentType-name. The name is composed of
	 *  the package-name and the agentType-name, for example my.packageName.agentTypeName
	 *  would be a correct 'fully qualified' agentTypeName.
	 *  @return  fully qualified name of the agentType.
	 */
	public String getFullyQualifiedName();

	/**
	 *  Set the description of this agentType.
	 *  @param description  agentType's description.
	 */
	public void setDescription(String description);

	/**
	 *  Get the agent's description.
	 *  @return  agent's description.
	 */
	public String getDescription();

	/**
	 *  Set the classname of this agentType.
	 *  @param className  agentType's classname.
	 */
	public void setClassName(String className);

	/**
	 *  Get the classname of this agentType.
	 *  @return  className  agentType's classname.
	 */
	public String getClassName();

	/**
	 * Set the type of agent-platform (e.g. jade) this agenttype is designed to run on.
	 * @param platformType  The platformType as String, possible values may be
	 * PLATFORM_TYPE_JADE, PLATFORM_TYPE_JADEX
	 */
	public void setPlatformType(String platformType);

	/**
	 * Get the type of agent-platform (e.g. jade) this agenttype is designed to run on.
	 * return The platformType as String, possible values may be PLATFORM_TYPE_JADE, PLATFORM_TYPE_JADEX
	 */
	public String getPlatformType();

    /**
	 *  Set the document.
	 *  @return  The document.
	 */
	public IDocument getDocument();
	
	/**
	 *  Set the document.
	 *  @param document  The document.
	 */
	public void setDocument(IDocument document);

	/**
	 *  todo: change to addAgentDesc
	 *  There are two ways of describing an agent, the get-/setDescription-methods
	 *  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	 *  on the other hand deal with the FIPA-agentdescription, which is more technical
	 *  and used for registering and searching for agents at the directory facilitator for example.
	 *  @param agentDescription  the agentType's AgentDescriptionModel.
	 */
	public void setAgentDescription(IAgentDescription agentDescription);

	/**
	 *  Set the image-icon.
	 *  @param icon  The image-icon.
	 */
	public void setIcon(ImageIcon icon);

	/**
	 *  Get the image-icon.
	 *  @return The image-icon.
	 */
	public ImageIcon getIcon();
	
	/**
	 *  Add a parameter.
	 *  @param parameter The parameter.
	 */
	public void addParameter(IAgentParameter parameter);

	/**
	 *  Add a parameter set.
	 *  @param paramset The parameter set.
	 */
	public void addParameterSet(IAgentParameterSet paramset);

	/**
	 *  Get all of the agent's parameter-sets. The returned HashMap has parameter-names as keys and
	 *  the parameter's attributes as values. A single parameter is represented as a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  @return  All of the agent's parameter-sets.
	 */
	public IAgentParameterSet[] getParameterSets();

	/**
	 *  This method returns a formatted String showing the model.
	 *  @return  formatted String showing ALL information about this model.
	 */
	public String toFormattedString();
	
}
