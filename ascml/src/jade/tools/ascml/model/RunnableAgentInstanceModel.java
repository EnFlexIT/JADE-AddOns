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

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.onto.Status;
import jade.tools.ascml.onto.Born;

/**
 *  Model-object containing all required information about a running SocietyInstance. 
 */
public class RunnableAgentInstanceModel extends AbstractRunnable implements IRunnableAgentInstance
{
	protected String className;
	protected String platformType;
	protected IAgentDescription agentDescription;
	protected IAgentParameter[] parameters;
	protected IAgentParameterSet[] parameterSets;
	protected HashMap<String, HashMap<String, Vector<String>>> toolOptions;

	/**
	 *  Instantiate a new model and initialize some variables 
	 */
	public RunnableAgentInstanceModel(String name, Object parentModel, Vector<IDependency> dependencies, Vector modelChangedListener,
									  IAbstractRunnable parentRunnable, String className, String platformType, IAgentParameter[] parameters, IAgentParameterSet[] parameterSets,
									  IAgentDescription agentDescription, HashMap<String, HashMap<String, Vector<String>>> toolOptions)
	{
		super(name, parentModel, dependencies, modelChangedListener, parentRunnable);

		this.className = className;
		this.platformType = platformType;
		this.parameters = parameters;
		this.parameterSets = parameterSets;

		this.agentDescription = agentDescription;

		if (toolOptions != null)
			this.toolOptions = toolOptions;
		else
			this.toolOptions = new HashMap<String, HashMap<String, Vector<String>>>();

		this.status = new Born();
		this.detailedStatus = "Runnable agentinstance has been created";
	}

	public String getClassName()
	{
		return className;
	}

	public String getPlatformType()
	{
		return platformType;
	}

 	/**
	 *  There are two ways of describing an agent, the get-/setDescription-methods
	 *  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	 *  on the other hand deal with the FIPA-agentdescription, which is more technical
	 *  and used for registering and searching for agents at the directory facilitator for example.
	 *  @return the agentType's AgentDescriptionModel.
	 */
	public IAgentDescription getAgentDescription()
	{
		return agentDescription;
	}

	/**
	 *  Get the tool-options of this runnable model.
	 *  @return  runnable agentinstance's tool-options or null, if it has no tool-options
	 */	
	public HashMap<String, HashMap<String, Vector<String>>> getToolOptions()
	{
		return this.toolOptions;
	}

	/**
	 * Get the property-Vector of a tooloption. This property-Vector is a Vector of Strings
	 * containing the properties for one toolOption.
	 * @param tooloptionType  The name of the toolOption as specified in IRunnableAgentInstance
	 * @return  A HashMap with the property-name as key and a String-Vector as value.
	 */
	public HashMap<String, Vector<String>> getToolOptionProperties(String tooloptionType)
	{
		return (HashMap<String, Vector<String>>)toolOptions.get(tooloptionType);
	}

	/**
	 * Set the tool options.
	 * This method may only be used by the RunnableFactory !!!
	 * @param toolOptions  A HashMap containing the name of the toolOption
	 * as key and the properties (String-Array) as value.
	 */
	public void setToolOptions(HashMap<String, HashMap<String, Vector<String>>> toolOptions)
	{
		this.toolOptions = toolOptions;
	}

	/**
	 *  Get the agent's type-model.
	 *  @return  agent's type.
	 */
	public IAgentType getType()
	{
		return (IAgentType)parentModel;
	}

	/**
	 *  Check if a toolOption is set.
	 *  @param name  toolOption's name.
	 *  @return  'true' if tooloption is set, 'false' otherwise.
	 */
	public boolean hasToolOption(String name)
	{
		return getToolOptions().containsKey(name);
	}
	
	/**
	 *  Get an agent's parameter.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */
	public IAgentParameter getParameter(String name)
	{
		for (int i=0; i < parameters.length; i++)
		{
			if (name.equals(parameters[i].getName()))
				return parameters[i];
		}
		return null;
	}

	/**
	 *  Get all of the agent's parameters. The returned HashMap has parameter-names as keys and
	 *  the parameter's attributes as values. The Attributes of a parameter are represented by a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  So, this method returns a HashMap with name-String as key and attribute-HashMap as value.
	 *  @return  All of the agent's parameters.
	 */
	public IAgentParameter[] getParameters()
	{
		return parameters;
	}

	/**
	 * Set the parameters.
	 * This method may only be used by the RunnableFactory !!!
	 * @param newParameters  An Array of new parameters, overwriting the current ones.
	 */
	public void setParameters(IAgentParameter[] newParameters)
	{
		this.parameters = newParameters;
	}

	/**
	 *  Get an agent's parameter set.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */
	public IAgentParameterSet getParameterSet(String name)
	{
		System.err.println("RunnableAgentInstanceModel.getParameterSet: Implement parameterValues !!!");
		for (int i=0; i < parameterSets.length; i++)
		{
			if (name.equals(parameterSets[i].getName()))
				return parameterSets[i];
		}
		return null;
	}

	/**
	 *  Get all agent-parameter-sets.
	 *  @return  The agent's parameter-sets.
	 */
	public IAgentParameterSet[] getParameterSets()
	{
		return parameterSets;
	}

	/**
	 * Set the parameterSets.
	 * This method may only be used by the RunnableFactory !!!
	 * @param newParameterSets  An Array of new parameterSets, overwriting the current ones.
	 */
	public void setParameterSets(IAgentParameterSet[] newParameterSets)
	{
		this.parameterSets = newParameterSets;
	}

    public Vector getModelChangedListener()
	{
		return modelChangedListener;
	}
        
}
