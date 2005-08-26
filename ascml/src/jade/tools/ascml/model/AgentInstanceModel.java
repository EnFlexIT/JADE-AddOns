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
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.events.ModelChangedEvent;

/**
 *  Model-object containing all required information about an agent-instance within a scenario.
 */
public class AgentInstanceModel extends AbstractAgentModel implements IAgentInstance
{
	private HashMap toolOptions;
	private HashMap parameters;
	private HashMap parameterSets;
	private Vector dependencies;
	private IAgentType type;
	private String typeName; // used before a type-object is set
	private String description;
	private long quantity;
	private String namingScheme;

	/**
	 * Instantiate a new agent-instance model and initialise some local variables
	 */
	public AgentInstanceModel(Vector modelChangedListener)
	{
		super(modelChangedListener);
		toolOptions = new HashMap();
		parameters = new HashMap();
		parameterSets = new HashMap();
		dependencies = new Vector();
		quantity = 1;
		namingScheme = "";
	}

	/**
	 *  Set the name of this agentInstance.
	 *  @param name agentInstance's name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the name of this agentInstance.
	 *  @return agentInstance's name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Update the status of this model.
	 * The status indicates, whether this model is ready to run or if it contains errors.
	 */
	public void updateStatus()
	{
		String newStatus = STATUS_OK;
		statusException = new ModelException("The agentinstance '"+getName()+"' is errorneous.", "The AgentInstance contains errors.");

		if ((getName() == null) || getName().equals(""))
		{
			statusException.addExceptionDetails("A name for this agentinstance is missing", "write me!");
			newStatus = STATUS_ERROR;
		}

		if ((getType().getName() == IAgentType.NAME_UNKNOWN) || (getType().getStatus() == STATUS_ERROR))
		{
			if (getType().getName() == IAgentType.NAME_UNKNOWN)
				statusException.addExceptionDetails("The agenttype '"+getTypeName()+"' is unknown and most possibly has not been loaded into the repository yet.", "The agenttype on which this agentinstance relies has not been loaded into the repository. Please first add the appropiate agentinstance to the repository (use Project-->Add new AgentType or AutoSearch) and then try again.");
			else
			{
				// statusException.addExceptionDetails("The type-object of this agentinstance has errors", "The agenttype on which this agentinstance relies contains errors, which anticipate loading the agenttype into the repository. Please first correct these errors and then try to reload the societytype.");
				statusException.addNestedException(getType().getStatusException());
			}
			newStatus = STATUS_REFERENCE_ERROR;
		}

		if (getType() == null)
		{
			statusException.addExceptionDetails("The type-object for this agentinstance is missing", "write me!");
			newStatus = STATUS_ERROR;
		}
        else
		{
			// now check if this instance provides all necessary parameter/-sets specified by the agenttype
			// and does not specify any parameter/-sets not specified by the agenttype

			IAgentParameter[] parameters = getType().getParameters();
			for (int i=0; i < parameters.length; i++)
			{
				// test, if a parameter is mandatory and not provided with any value neither by the
				// agenttype nor this agentinstance
				if (!parameters[i].isOptional() && (parameters[i].getValue() == null) && (getParameterValue(parameters[i].getName()) == null))
				{
					statusException.addExceptionDetails("The parameter '"+parameters[i].getName()+"' is mandatory (see type-definition), but no value has been specified", "write me!");
					newStatus = STATUS_ERROR;
				}
			}

			IAgentParameterSet[] parameterSets = getType().getParameterSets();
			for (int i=0; i < parameterSets.length; i++)
			{
				// test, if a parameterSet is mandatory and not provided with any value neither by the
				// agenttype nor this agentinstance
				if (!parameterSets[i].isOptional() && (parameterSets[i].getValues() == null) && (getParameterSetValues(parameterSets[i].getName()) == null))
				{
					statusException.addExceptionDetails("The parameterset '"+parameterSets[i].getName()+"' is mandatory (see type-definition), but no values have been specified", "write me!");
					newStatus = STATUS_ERROR;
				}
			}
		}

		if (getStatus() != newStatus)
		{
			status = newStatus;
			throwModelChangedEvent(ModelChangedEvent.STATUS_CHANGED);
		}
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
	 * Add a tool-option to this agent, e.g. turn on logging or sniffing.
	 * @param name       toolOption's name (TOOLOPTION_SNIFF, TOOLOPTION_DEBUG, TOOLOPTION_LOG, ...)
	 * @param properties optional toolOption's properties, null if no properties are provided
	 */
	public void addToolOption(String name, HashMap properties)
	{
		toolOptions.put(name, properties);
	}

	/**
	 * Remove a tool-option from this agent, e.g. turn off logging or sniffing.
	 * @param name       toolOption's name (TOOLOPTION_SNIFF, TOOLOPTION_DEBUG, TOOLOPTION_LOG, ...)
	 */
	public void removeToolOption(String name)
	{
		toolOptions.remove(name);
	}
	
	/**
	 * Check if a toolOption is set.
	 * @param name toolOption's name.
	 * @return 'true' if tooloption is set, 'false' otherwise.
	 */
	public boolean hasToolOption(String name)
	{
		return toolOptions.containsKey(name);
	}

	/**
	 *  Get the properties of a toolOption.
	 *  Return-value may be null if no properties are specified.
	 *  @param name  toolOption's name.
	 *  @return  The toolOption's properties as a HashMap, key in this HashMap is the propertyName,
	 *           value is the property's value as a String
	 */
	public HashMap<String,Vector<String>> getToolOptionProperties(String name)
	{
		if(toolOptions.get(name)!=null)
			return (HashMap<String,Vector<String>>)toolOptions.get(name);
		else
			return null;
	}

	/**
	 * Get all toolOption as a HashMap.
	 * @return A HashMap with toolOption-names as key and the
	 *         toolOption-properties (HashMap with key=propertyName, value=propertyValue) as values.
	 */
	public HashMap<String,HashMap<String,Vector<String>>> getToolOptions()
	{
		return toolOptions;
	}

	/**
	 * Add a dependency to this agent's dependencies.
	 * @param dependency The dependency.
	 */
	public void addDependency(IDependency dependency)
	{
		dependencies.add(dependency);
	}

	/**
	 *  Get all of the agent's dependency-models.
	 *  @return A Vector containing all of the agent's dependency-models.
	 */
	public IDependency[] getDependencies()
	{
		return (IDependency[])dependencies.toArray(new IDependency[dependencies.size()]);
	}

	/**
	 * Add a parameter to this agent. The parameter overwrites the value of the
	 * matching type-parameter of this agent's type. A check if it really overwrites
	 * a parameter (which it has to) is not done here.
	 * @param name  parameter's name.
	 * @param value parameters's value.
	 */
	public void setParameterValue(String name, String value)
	{
		parameters.put(name.trim(), value.trim());
	}

	/**
	 * Get an agent's parameter. A parameter is represented as a HashMap.
	 * Possible keys in this HashMap are <i> name, type, optional, description,
	 * value, constraints </i>.
	 *
	 * @param name The parameter's name.
	 * @return an agent's parameter or null if no parameter with the given name exists.
	 */
	public Object getParameterValue(String name)
	{
		Object value = null;
		IAgentParameter param = getType().getParameter(name);
		if (param == null)
			return null;
		if(this.parameters.containsKey(name))
		{
			value = parameters.get(name);
		}
		else
		{
			value = param.getValue();
		}
		return value;
	}

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
	public IAgentParameter getParameterClone(String name) throws ModelException
	{
		IAgentParameter parameterClone = (IAgentParameter)((IAgentType)type).getParameter(name).clone();
		if (parameterClone == null)
			throw new ModelException("<html>Sorry, it seems the parameter <i>"+name+"</i> of agent <i>"+getName()+"</i> doesn't support cloning !<br>This parameter cannot not be passed on to the running instance.</html>");
		
		parameterClone.setValue(this.getParameterValue(name));
		return parameterClone;
	}

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
	public IAgentParameter[] getParameterClones() throws ModelException
	{
        IAgentParameter[] parameters = ((IAgentType)type).getParameters();
		IAgentParameter[] returnParameters = new IAgentParameter[parameters.length];

		for (int i=0; i < returnParameters.length; i++)
		{
			returnParameters[i] = getParameterClone(parameters[i].getName());
		}

		return returnParameters;
	}

	/**
	 * Add a value to this agent.
	 * @param name  parameter set's name.
	 * @param value The value.
	 */
	public void addParameterSetValue(String name, Object value)
	{
		List ps = (List)parameterSets.get(name);
		if(ps==null)
		{
			ps = new ArrayList();
			parameterSets.put(name, ps);
		}
		ps.add(value);
	}

	/**
	 *  Get all values of a parameter set.
	 *  @param name The parameter set's name.
	 *  @return The values or null, if no parameterSet with the given name has been specified
	 */
	public Object[] getParameterSetValues(String name)
	{
		Object[] value = null;
		IAgentParameterSet param = ((IAgentType)getType()).getParameterSet(name);
		if(param == null)
			return null;

		if(parameterSets.containsKey(name))
		{
			value = ((List)parameterSets.get(name)).toArray();
		}
		else
		{
			value = param.getValues();
		}
		return value;
	}

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
	public IAgentParameterSet getParameterSetClone(String name) throws ModelException
	{
		IAgentParameterSet parameterSetClone = (IAgentParameterSet)((IAgentType)type).getParameterSet(name).clone();
		if (parameterSetClone == null)
			throw new ModelException("<html>Sorry, it seems the parameterSet <i>"+name+"</i> of agent <i>"+getName()+"</i> doesn't support cloning !<br>This parameter-set cannot be passed on to the running instance.</html>");
		parameterSetClone.setValues(this.getParameterSetValues(name));
		return parameterSetClone;
	}

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
	public IAgentParameterSet[] getParameterSetClones() throws ModelException
	{
        IAgentParameterSet[] parameterSets = ((IAgentType)type).getParameterSets();
		IAgentParameterSet[] returnParameterSets = new IAgentParameterSet[parameterSets.length];

		for (int i=0; i < returnParameterSets.length; i++)
		{
			returnParameterSets[i] = getParameterSetClone(parameterSets[i].getName());
		}

		return returnParameterSets;
	}

	/**
	 * Get the agent's type.
	 * @return agent's type.
	 */
	public IAgentType getType()
	{
		return type;
	}

	/**
	 * Set the agent's type.
	 * @param type agent's type.
	 */
	public void setType(IAgentType type)
	{
		if ((typeName != null) && (type.getName() != IAgentType.NAME_UNKNOWN))
			typeName = null; // set typeName = null to indicate, that the type-object has been set
		this.type = type;
	}

	/**
	 * Get the agent's type-name. The type-name is used to identify the type by it's name
	 * in order to set the correct type-object later on.
	 * @return agent's type-name.
	 */
	public String getTypeName()
	{
		return typeName;
	}

	/**
	 * Set the agent's type-name. The type-name is used to identify the type by it's name
	 * in order to set the correct type-object later on.
	 * @param typeName agent's type-name.
	 */
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	/**
	 *  Set the number of society-instances to start.
	 *  @param quantity  the number of society-instances to start.
	 */	
	public void setQuantity(String quantity)
	{
		if ((quantity == null) || (quantity.trim().equals("")))
			this.quantity = 1;
		else
			this.quantity = new Long(quantity.trim());
	}
	
	/**
	 *  Get the number of society-instances to start.
	 *  @return  number of society-instances to start.
	 */	
	public long getQuantity()
	{
		return this.quantity;
	}
	
	/**
	 *  Set the naming scheme for the case multiple instances of this
	 *  societyinstance should be started.
	 *  @param namingScheme  the naming-scheme used to name society- and agentinstances
	 *                       in case multiple instances should be started.
	 */	
	public void setNamingScheme(String namingScheme)
	{
		if (namingScheme == null)
            this.namingScheme = "";
        else
            this.namingScheme = namingScheme.trim();
	}
	
	/**
	 *  Get the naming scheme for the case multiple instances of this
	 *  societyinstance should be started.
	 *  @return the naming-scheme used to name society- and agentinstances.
	 */	
	public String getNamingScheme()
	{
		if ((getQuantity() > 1) && (namingScheme.equals("")))
			return "%N"; // default scheme
		else
			return this.namingScheme; // user-scheme
	}

	/**
	 * This method returns a short String with the agentInstance-name.
	 * It is used by the RepositoryTree for example, to name the nodes.
	 *
	 * @return String containing the name of this agentInstance
	 */
	public String toString()
	{
		String str = "";
		if (getQuantity() > 1)
			str += "(" + getQuantity() + ") ";
		 str += getName() + " : " + getType();
		return str;
	}

	/**
	 * This method returns a formatted String showing the agentInstance-model.
	 *
	 * @return formatted String showing ALL information about this agentInstance.
	 */
	public String toFormattedString()
	{
		String str = "\n";

		str += "Agent-Instance: name = "+getName()+" type = "+type+"\n";

		
		Iterator keys = parameters.keySet().iterator();
		while(keys.hasNext())
		{
			String oneKey = (String)keys.next();
			Object oneValue = getParameterValue(oneKey);
			str += "overwritten parameter: key=" + oneKey + " value=" + oneValue;
		}

		keys = parameterSets.keySet().iterator();
		while(keys.hasNext())
		{
			String oneKey = (String)keys.next();
			Object[] oneParameterSet = getParameterSetValues(oneKey);
			str += "overwritten parameterset: key=" + oneKey + "\n";
			for (int i=0; i < oneParameterSet.length; i++)
			{
				str += "    value=" + oneParameterSet[i] + "\n";
			}
		}

		keys = toolOptions.keySet().iterator();
		while(keys.hasNext())
		{
			String oneKey = (String)keys.next();
			str += "ToolOption: name = "+oneKey+"\n";
			str += "     properties = "+toolOptions.get(oneKey)+"\n";
		}

		for(int i = 0; i<dependencies.size(); i++)
		{
			str += ((IDependency)dependencies.elementAt(i)).toString();
		}

		return str;
	}
}