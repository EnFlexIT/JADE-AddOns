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

import java.util.HashMap;
import java.util.Vector;

/**
 *  The IRunnableAgentInctance-interface is used by the launcher-classes to prepare
 *  an agent to start on the agent-platform. It's 'super-interface' IAbstractAgent
 *  defines methods for getting variables used for the startup of agents.
 *  Objects implementing this interface are created at runtime and all neccessary
 *  information is passed to these objects from the IAgentType-objects.
 *  todo: clean-up as commented, add methods for service description handling
 *  todo: introduce parameter/set model elements
 */
public interface IRunnableAgentInstance extends IAbstractRunnable
{
    public static final String TOOLOPTION_SNIFF         = "sniff";
	public static final String TOOLOPTION_DEBUG         = "debug";
	public static final String TOOLOPTION_LOG           = "log";
    public static final String TOOLOPTION_BENCHMARK     = "benchmark";
	public static final String TOOLOPTION_INTROSPECTOR  = "introspector";

	/**
	 *  Get the agent's class-name.
	 *  @return  agent's class-name.
	 */
	public String getClassName();

	public String getPlatformType();
	/**
	 *  Get an agent's parameter.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */
	public IAgentParameter getParameter(String name);

	/**
	 *  Get all of the agent's parameters.
	 *  @return  All of the agent's parameters.
	 */
	public IAgentParameter[] getParameters();

	/**
	 * Set the parameters.
	 * This method may only be used by the RunnableFactory !!!
	 * @param newParameters  An Array of new parameters, overwriting the current ones.
	 */
	public void setParameters(IAgentParameter[] newParameters);
	
	/**
	 *  Get an agent's parameter-set.
	 *  @return  an agent's parameter-set.
	 */
	public IAgentParameterSet getParameterSet(String name);

	/**
	 *  Get all of the agent's parameter-sets.
	 *  @return  All of the agent's parameter-sets.
	 */
	public IAgentParameterSet[] getParameterSets();

	/**
	 * Set the parameterSets.
	 * This method may only be used by the RunnableFactory !!!
	 * @param newParameterSets  An Array of new parameterSets, overwriting the current ones.
	 */
	public void setParameterSets(IAgentParameterSet[] newParameterSets);

	/**
	 *  Check if a toolOption is set.
	 *  @param name  toolOption's name.
	 *  @return  'true' if tooloption is set, 'false' otherwise.
	 */
	public boolean hasToolOption(String name);

	/**
	 *  Get the agent's type-model.
	 *  @return  agent's type.
	 */
	public IAgentType getType();

	/**
	 *  Get the tool-options of this runnable model.
	 *  @return  runnable agentinstance's tool-options or null, if it has no tool-options
	 */	
	public HashMap<String, HashMap<String, Vector<String>>> getToolOptions();

	/**
	 * Get the property-Vector of a tooloption. This property-Vector is a Vector of Strings
	 * containing the properties for one toolOption.
	 * @param tooloptionType  The name of the toolOption as specified in IRunnableAgentInstance
	 * @return  A HashMap with the property-name as key and a String-Vector as value.
	 */
	public HashMap<String,Vector<String>> getToolOptionProperties(String tooloptionType);
        
	/**
	 * Set the tool options.
	 * This method may only be used by the RunnableFactory !!!
	 * @param toolOptions  A HashMap containing the name of the toolOption
	 * as key and the properties (String-Array) as value.
	 */
	public void setToolOptions(HashMap<String, HashMap<String, Vector<String>>> toolOptions);

}
