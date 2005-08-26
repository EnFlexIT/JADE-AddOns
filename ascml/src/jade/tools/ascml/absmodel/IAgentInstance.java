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

import java.util.HashMap;
import java.util.Vector;
/**
 *  The agent instance interface that corresponds directly to the xml schema.
 *  todo: clean-up as commented
 */
public interface IAgentInstance extends IAbstractAgent
{

	/**
	 *  Returns the fully qualified agentType-name. The name is composed of
	 *  the package-name and the agentType-name, for example my.packageName.agentTypeName
	 *  would be a correct 'fully qualified' agentTypeName.
	 *  @return  fully qualified name of the agentType.
	 */
	//public String getFullyQualifiedName();

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
	 *  This method returns a formatted String showing the model.
	 *  @return  formatted String showing ALL information about this model.
	 */
	public String toFormattedString();

	/**
	 *  Get the agent's type-model.
	 *  @return  agent's type.
	 */
	public IAgentType getType();

	/**
	 *  Set the agent's type.
	 *  @param type  agent's type.
	 */
	public void setType(IAgentType type);

	/**
	 * Get the agent's type-name. The type-name is used to identify the type by it's name
	 * in order to set the correct type-object later on.
	 * @return agent's type-name.
	 */
	public String getTypeName();

	/**
	 * Set the agent's type-name. The type-name is used to identify the type by it's name
	 * in order to set the correct type-object later on.
	 * @param typeName agent's type-name.
	 */
	public void setTypeName(String typeName);

	/**
	 *  Add a tool-option to this agent, i.e. turn on logging or sniffing.
	 *  @param name  toolOption's name (TOOLOPTION_SNIFF, TOOLOPTION_DEBUG, TOOLOPTION_LOG, ...)
	 * @param properties  optional toolOption's properties, null if no properties are provided
	 */
	public void addToolOption(String name, HashMap properties);

	/**
	 * Remove a tool-option from this agent, e.g. turn off logging or sniffing.
	 * @param name       toolOption's name (TOOLOPTION_SNIFF, TOOLOPTION_DEBUG, TOOLOPTION_LOG, ...)
	 */
	public void removeToolOption(String name);

	/**
	 *  Check if a toolOption is set.
	 *  @param name  toolOption's name.
	 *  @return  'true' if tooloption is set, 'false' otherwise.
	 */
	public boolean hasToolOption(String name);

	/**
	 *  Get the properties of a toolOption.
	 *  Return-value may be null if no properties are specified.
	 *  @param name  toolOption's name.
	 *  @return  The toolOption's properties as a HashMap, key in this HashMap is the propertyName,
	 *           value is the property's value as a String
	 */
	public HashMap<String,Vector<String>> getToolOptionProperties(String name);

	/**
	 *  Get all toolOption as a HashMap.
	 *  @return  A HashMap with toolOption-names as key and the
	 *           toolOption-properties (Vector) as values.
	 */
	public HashMap<String,HashMap<String,Vector<String>>> getToolOptions();

	/**
	 *  Add a dependency to this agent's dependencies.
	 *  @param dependency  The dependency.
	 */
	public void addDependency(IDependency dependency);

	/**
	 *  Get all of the agent's dependency-models.
	 *  @return  A Vector containing all of the agent's dependency-models.
	 */
	public IDependency[] getDependencies();

	/**
	 *  Add a parameter to this agent. The parameter overwrites the value of the
	 *  matching type-parameter of this agent's type.
	 *  @param name  parameter's name.
	 *  @param value  parameters's value.
	 */
	public void setParameterValue(String name, String value);

	/**
	 *  Get an agent's parameter. A parameter is represented as a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description,
	 *  value, constraints </i>.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter or null if no parameter with the given name exists.
	 */
	public Object getParameterValue(String name) throws ModelException;

	/**
	 *  This method returns a cloned IAgentParameter-object. The parameter-object
	 *  within this class contains only key-value pairs and depends on the parameter-object
	 *  of this class' AgentType. The parameter-object in the Type-class is an instance
	 *  of IAgentParameter and it's values may be overwritten by this class in case
	 *  the User modelled an AgentInstance with it's own parameter-values. To merge these two kinds
	 *  of parameter-objects a clone of the Type's parameter is created and it's value-field
	 *  is eventually overwritten with new values contained in this class' parameter-object.
	 *  @return  clone of the the AgentType-parameter-object merged with this AgentInstance's 
	 *           new parameter-values.
	 */
	public IAgentParameter getParameterClone(String name) throws ModelException;

	/**
	 *  This method returns an Array of cloned IAgentParameter-objects. The parameter-objects
	 *  within this class contain only key-value pairs and depends on the parameter-objects
	 *  of this class' AgentType. The parameter-objects in the Type-class are instances
	 *  of IAgentParameter and it's values may be overwritten by this class in case
	 *  the User modelled an AgentInstance with it's own parameter-values. To merge these two kinds
	 *  of parameter-objects a cloned array of the Type's parameters is created and it's value-fields
	 *  are eventually overwritten with new values contained in this class' parameter-objects.
	 *  @return  An array of cloned AgentType-parameter-objects merged with this AgentInstance's
	 *           new parameter-values.
	 */
	public IAgentParameter[] getParameterClones() throws ModelException;

	/**
	 *  Add a value to a parameter set.
	 *  @param name The name.
	 *  @param value The value. 
	 */
	public void addParameterSetValue(String name, Object value);

	/**
	 *  Get an agent's parameter-set. A parameter-set is represented as a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description,
	 *  value, constraints </i>.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */
	public Object[] getParameterSetValues(String name) throws ModelException;

	/**
	 *  This method returns a cloned IAgentParameterSet-object. The parameterSet-object
	 *  within this class contains only key-value pairs and depends on the parameterSet-object
	 *  of this class' AgentType. The parameterSet-object in the Type-class is an instance
	 *  of IAgentParameterSet and it's values may be overwritten by this class in case
	 *  the User modelled an AgentInstance with it's own parameterSet-values. To merge these two kinds
	 *  of parameterSet-objects a clone of the Type's parameterSet is created and it's value-field
	 *  is eventually overwritten with new values contained in this class' parameterSet-object.
	 *  @return  clone of the the AgentType-parameterSet-object merged with this AgentInstance's 
	 *           new parameterSet-values.
	 */
	public IAgentParameterSet getParameterSetClone(String name) throws ModelException;

	/**
	 *  This method returns an array of cloned IAgentParameterSet-objects. The parameterSet-objects
	 *  within this class contain only key-value pairs and depend on the parameterSet-object
	 *  of this class' AgentType. The parameterSet-objects in the Type-class are instances
	 *  of IAgentParameterSet and their values may be overwritten by this class in case
	 *  the User modelled an AgentInstance with it's own parameterSet-values. To merge these two kinds
	 *  of parameterSet-objects a clone of the Type's parameterSet is created and it's value-field
	 *  is eventually overwritten with new values contained in this class' parameterSet-object.
	 *  @return  Array of clones of the the AgentType-parameterSet-object merged with this AgentInstance's
	 *           new parameterSet-values.
	 */
	public IAgentParameterSet[] getParameterSetClones() throws ModelException;
	
	/**
	 *  Set the number of society-instances to start.
	 *  @param quantity  the number of society-instances to start.
	 */	
	public void setQuantity(String quantity);
	
	/**
	 *  Get the number of society-instances to start.
	 *  @return  number of society-instances to start.
	 */	
	public long getQuantity();
	
	/**
	 *  Set the naming scheme for the case multiple instances of this
	 *  societyinstance should be started.
	 *  @param namingScheme  the naming-scheme used to name society- and agentinstances
	 *                       in case multiple instances should be started.
	 */	
	public void setNamingScheme(String namingScheme);
	
	/**
	 *  Get the naming scheme for the case multiple instances of this
	 *  societyinstance should be started.
	 *  @return the naming-scheme used to name society- and agentinstances.
	 */	
	public String getNamingScheme();
}
