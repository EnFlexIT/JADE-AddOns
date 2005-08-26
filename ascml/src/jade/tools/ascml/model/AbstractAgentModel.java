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
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.exceptions.ModelException;

/**
 *  This abstract class has to be implemented by the AgentTypeModel as well as the
 *  RunnableAgentInstanceModel. This class defines some methods used for both classes, the
 *  only methods which are declared abstract are 'get/setName'. In both implementations 
 *  the methods look the same, but are used for different purposes and to highlight this
 *  circumstance they are declared abstract.
 */
public abstract class AbstractAgentModel
{
	public final static String NAME_UNKNOWN = "Unknown";

	/**
	 * The status indicates if this model has successfully been loaded.
	 * Possible stati are STATUS_OK, STATUS_ERROR, STATUS_WARNING.
	 */
	protected String status;

	/**
	 * This Exception may contain a set of detailed String-messages in case the status is != STATUS_OK
	 */
	protected ModelException statusException;

	/** The agent type name. */
	protected String name;

	/** The agent class name. */
	protected String className;
	
	/** The document. */
	protected IDocument document;

	/** The agent's parameters. */
	protected HashMap parameters;

	/** The agent's parameter sets. */
	protected HashMap parameterSets;

	/** The agent's agent description. */
	// todo: extend to have more than one
	protected IAgentDescription agentDescription;

	protected Vector modelChangedListener;

	//-------- constrcutors --------

	/**
	 *  Instantiate a new model and initialise some variables with default-values.
	 */
	public AbstractAgentModel(Vector modelChangedListener)
	{
		parameters    = new HashMap();
		parameterSets = new HashMap();
		this.modelChangedListener = modelChangedListener;
		document = new DocumentModel();
		className = "";
		this.name = NAME_UNKNOWN;
	}

	/**
	 *  Instantiate a new model and initialise some variables with default-values.
	 */
	public AbstractAgentModel(String className, IDocument document, IAgentParameter[] parameters, 
	                          IAgentParameterSet[] parameterSets, IAgentDescription agentDescription, Vector modelChangedListener)
	{
		super();
		this.className			= className;
		this.document			= document;
		this.parameters			= new HashMap();
		this.parameterSets		= new HashMap();
		
		for (int i=0; i < parameters.length; i++)
		{
			this.parameters.put(parameters[i].getName(), parameters[i]);
		}
		for (int i=0; i < parameterSets.length; i++)
		{
			this.parameterSets.put(parameterSets[i].getName(), parameterSets[i]);
		}
		
		this.agentDescription	= agentDescription;
		this.modelChangedListener = modelChangedListener;
	}
	
	/**
	 *  Set the name of this agentType.
	 *  @param name  agentType's name.
	 */
	abstract void setName(String name);

	/**
	 *  Get the name of this agentType.
	 *  @return  agentType's name.
	 */	
	abstract String getName();

	/**
	 * Update the status of this model.
	 * The status indicates, whether this model is ready to run or if it contains errors.
	 */
	abstract void updateStatus();

	/**
	 * Get the status-exception, which describe errors or warnings in detail.
	 * @return  A ModelException containing detailed status-messages as Strings.
	 */
	public ModelException getStatusException()
	{
		if (statusException.hasExceptionDetails() || statusException.hasNestedExceptions())
			return statusException;
		else
			return null;
	}

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_WARNING.
	 */
	public String getStatus()
	{
		return this.status;
	}

	/**
	 *  Get the document.
	 *  @return The document.
	 */
	public IDocument getDocument()
	{
		return document;
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
	 *  Get the agent's fully qualified class-name.
	 *  @return  agent's class-name.
	 */
	public String getClassName()
	{
		String ret = className;

		if (!className.equals(""))
		{
			String packname = getDocument().getPackageName();
			if ((packname != null) && (packname.length() != 0))
				ret = packname + "." + className;
		}
		return ret;
	}
	
	/**
	 *  Get an agent's parameter. A parameter is represented as a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */		
	public IAgentParameter getParameter(String name)
	{
		return (IAgentParameter)parameters.get(name);
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
		return (IAgentParameter[])parameters.values().toArray(new IAgentParameter[parameters.size()]);
	}

	/**
	 *  Get an agent's parameter set.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */
	public IAgentParameterSet getParameterSet(String name)
	{
		return (IAgentParameterSet)parameterSets.get(name);
	}

	/**
	 *  Get all of the agent's parameter-sets. The returned HashMap has parameter-names as keys and
	 *  the parameter's attributes as values. A single parameter is represented as a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  @return  All of the agent's parameter-sets.
	 */
	public IAgentParameterSet[] getParameterSets()
	{
		IAgentParameterSet[] returnArray = new IAgentParameterSet[parameterSets.values().size()];
		parameterSets.values().toArray(returnArray);
		return returnArray;
	}

	public Vector getModelChangedListener()
	{
		return modelChangedListener;
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode)
	{
		throwModelChangedEvent(eventCode, null);
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 * @param userObject  This object is stored in the ModelChangedEvent. For example: When runnables
	 * are added or removed, these RunnableModels are stored as userObjects to access them later on.
	 */
	public void throwModelChangedEvent(String eventCode, Object userObject)
	{
		ModelChangedEvent event = new ModelChangedEvent(this, eventCode, userObject);

		ModelChangedListener[] listener = new ModelChangedListener[modelChangedListener.size()];
		modelChangedListener.toArray(listener);

		for (int i=0; i < listener.length; i++)
		{
			listener[i].modelChanged(event);
		}
	}

	/**
	 *  This method returns a short String with the agent's address in memory.
	 *  @return  String containing the memory-address.
	 */
	public String toString()
	{	
		return super.toString();
	}
	
	/**
	 *  This method returns a formatted String showing the agentType-model.
	 *  @return  formatted String showing ALL information about this agentType.
	 */
	public String toFormattedString()
	{		
		String str = "no formatted output available";
		
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
		}*/
		return str;
	}
}
