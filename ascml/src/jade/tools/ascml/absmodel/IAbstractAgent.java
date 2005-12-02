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

import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.events.ModelChangedListener;

import java.util.Vector;

/**
 *  The IAbstractAgent-interface is used as an 'super-interface'. This inteface describes the common methods
 *  for both interfaces. These methods allow access to class-variables that are needed
 *  to prepare the startup of an agent-instance on the platform.
 *  todo: clean-up as commented, add methods for service description handling
 */
public interface IAbstractAgent
{
    /**
	 * This constant is used to indicate that an AgentType or AgentInstance has no been provided with a name 
	 */
	public final static String NAME_UNKNOWN = "Unknown";

	/**
	 * This constant is used to indicate, that this model has successfully been loaded.
	 */
	public final static String STATUS_OK		= "successfully loaded";

	/**
	 * This constant is used to indicate, that at least one error occurred while loading the model.
	 */
	public final static String STATUS_ERROR		= "loading error";

	/**
	 * This constant is used to indicate, that at least one referenced agenttype
	 * has NOT been loaded successfully
	 */
	public final static String STATUS_REFERENCE_ERROR	= "erroneous reference";

	/**
	 *  Set the name of this agentType.
	 *  @param name  agentType's name.
	 */
	public void setName(String name);

	/**
	 *  Get the name of this agentType.
	 *  @return  agentType's name.
	 */
	public String getName();

	/**
	 *  Get the agent's class-name.
	 *  @return  agent's class-name.
	 */
	public String getClassName();

	/**
	 * Update the status of this model.
	 * The status indicates, whether this model is ready to run or if it contains errors.
	 */
	public void updateStatus();

	/**
	 * Get the status-exception, which describe errors or warnings in detail.
	 * @return  A ModelException containing detailed status-messages as Strings.
	 */
	public ModelException getStatusException();

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_WARNING.
	 */
	public String getStatus();
	
	/**
	 *  todo: change to getAgentDescription(String name) and/or getAgentDescriptions()
	 *  There are two ways of describing an agent, the get-/setDescription-methods
	 *  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	 *  on the other hand deal with the FIPA-agentdescription, which is more technical
	 *  and used for registering and searching for agents at the directory facilitator for example.
	 *  @return the agentType's AgentDescriptionModel.
	 */
	public IAgentDescription getAgentDescription();

	/**
	 *  Get an agent's parameter. A parameter is represented as a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */
	public IAgentParameter getParameter(String name);

	/**
	 *  Get all of the agent's parameters. The returned HashMap has parameter-names as keys and
	 *  the parameter's attributes as values. The Attributes of a parameter are represented by a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  So, this method returns a HashMap with name-String as key and attribute-HashMap as value.
	 *  @return  All of the agent's parameters.
	 */
	public IAgentParameter[] getParameters();

	/**
	 *  Get an agent's parameter-set. A parameter-set is represented as a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  @return  an agent's parameter-set.
	 */
	public IAgentParameterSet getParameterSet(String name);

	/**
	 *  Get all of the agent's parameter-sets. The returned HashMap has parameter-names as keys and
	 *  the parameter's attributes as values. A single parameter is represented as a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  @return  All of the agent's parameter-sets.
	 */
	public IAgentParameterSet[] getParameterSets();

	/**
	 * Get all the modelChangedListener registered for this model.
	 * @return  A Vector containing all ModelChangedListeners
	 */
	public Vector getModelChangedListener();

	/**
	 *  This method returns a formatted String showing the model.
	 *  @return  formatted String showing ALL information about this model.
	 */
	public String toFormattedString();
	
}
