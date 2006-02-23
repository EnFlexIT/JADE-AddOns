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


package jade.tools.ascml.model.jibx;

import java.util.*;
import jade.tools.ascml.model.jibx.dependency.AbstractDependency;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.absmodel.*;

/**
 *  Model-object containing all information about an AgentInstance.
 */
public class AgentInstance implements IAgentInstance
{
	protected String name;
	protected String typeName;
	protected String quantity;
	protected String namingScheme;

	protected String status;

	protected ArrayList<IToolOption> toolOptionList = new ArrayList<IToolOption>();

	private ArrayList<IParameter> parameterList = new ArrayList<IParameter>();
	private ArrayList<IParameterSet> parameterSetList = new ArrayList<IParameterSet>();
	private ArrayList<IDependency> dependencyList = new ArrayList<IDependency>();
	private IAgentType type;
	private ISocietyInstance parentSocietyInstance;

	// ----------------------------------------------------------------------------

	/**
	 *  Get the SocietyInstance-model to which this AgentInstance belongs.
	 *  @return SocietyInstance-model to which this AgentInstance belongs.
	 */
	public ISocietyInstance getParentSocietyInstance()
	{
		return parentSocietyInstance;
	}

	/**
	 *  Set the SocietyInstance-model to which this AgentInstance belongs.
	 *  @param parentSocietyInstance SocietyInstance-model to which this AgentInstance belongs.
	 */
	public void setParentSocietyInstance(ISocietyInstance parentSocietyInstance)
	{
		this.parentSocietyInstance = parentSocietyInstance;
	}

	/**
	 *  Set the name of this AgentInstance.
	 *  @param name  The AgentInstance's name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the name of this AgentInstance.
	 *  @return AgentInstance's name.
	 */
	public String getName()
	{
		if (name == null)
			return NAME_UNKNOWN;
		return name;
	}

	/**
	 * Set the AgentInstance's type-name. The type-name is used to identify the type by it's name
	 * in order to set the correct type-object later on.
	 * @param typeName  The AgentInstance's type-name.
	 */
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	/**
	 * Get the AgentInstance's type-name. The type-name is used to identify the type by it's name
	 * in order to set the correct type-object later on.
	 * @return  The AgentInstance's type-name.
	 */
	public String getTypeName()
	{
		if (typeName == null)
			return NAME_UNKNOWN;
		return typeName;
	}

	/**
	 * Get the AgentInstance's AgentType.
	 * @return  The AgentType-object.
	 */
	public IAgentType getType()
	{
		return type;
	}

	/**
	 * Set the AgentInstance's AgentType.
	 * @param type  The AgentType-object.
	 */
	public void setType(IAgentType type)
	{
		this.type = type;
	}

	/**
	 *  Set the number of AgentInstances to start.
	 *  When an AgentInstance is going to start, multiple runnable-models
	 *  may be created out of this AgentInstance.
	 *  @param quantity  the number of AgentInstances to start.
	 */
	public void setQuantity(String quantity)
	{
		if ((quantity == null) || (quantity.trim().equals("")))
			this.quantity = "1";
		else
			this.quantity = quantity.trim();
	}

	/**
	 *  Get the number of AgentInstances to start.
	 *  When an AgentInstance is going to start, multiple runnable-models
	 *  may be created out of this AgentInstance.
	 *  @return  The number of AgentInstances to start.
	 */
	public long getQuantity()
	{
		if (quantity == null)
			return 1;
		return new Long(this.quantity);
	}

	/**
	 *  Set the naming-scheme in case multiple runnable-instances of this
	 *  AgentInstance should be started.
	 *  The naming-scheme is used to name the runnables in order to make sure
	 *  that their names are unique inside the ASCML. The String is appended at
	 *  the Name of the runnable. You may choose any String you like.
	 *  There are a few placeholders that might be used in order order to ensure
	 *  uniqueness of names.
	 *  '%N' is replaced with an ID, starting by 0 and ascending
	 *       to the number of runnable models of this type created so far
	 *  '%T' is replaced at the time of creation of a runnable by the
	 *       current time in milliseconds
	 *  '%R' is replaced with a pseudo random-number between 0 and 1,000,000,000
	 *  Depending on how much runnable-instances you want to create, you should combine
	 *  the placeholders.
	 *  One example for an naming-scheme could be:
	 *  '%N_(time:%Tms)'
	 *  ... resulting for example in '%runnableName%_0_(time:109873247ms)'
	 *  @param namingScheme  the naming-scheme used to name runnable AgentInstances
	 *                       in case multiple runnable-instances should be started.
	 */
	public void setNamingScheme(String namingScheme)
	{
		if (namingScheme == null)
            this.namingScheme = "";
        else
            this.namingScheme = namingScheme.trim();
	}

	/**
	 *  Get the naming-scheme in case multiple runnable-instances of this
	 *  AgentInstance should be started.
	 *  The naming-scheme is used to name the runnables in order to make sure
	 *  that their names are unique inside the ASCML. The String is appended at
	 *  the Name of the runnable. You may choose any String you like.
	 *  There are a few placeholders that might be used in order order to ensure
	 *  uniqueness of names.
	 *  '%N' is replaced with an ID, starting by 0 and ascending
	 *       to the number of runnable models of this type created so far
	 *  '%T' is replaced at the time of creation of a runnable by the
	 *       current time in milliseconds
	 *  '%R' is replaced with a pseudo random-number between 0 and 1,000,000,000
	 *  Depending on how much runnable-instances you want to create, you should combine
	 *  the placeholders.
	 *  One example for an naming-scheme could be:
	 *  '%N_(time:%Tms)'
	 *  ... resulting for example in '%runnableName%_0_(time:109873247ms)'
	 *  @return  The naming-scheme used to name runnable AgentInstances
	 *           in case multiple runnable-instances should be started.
	 */
	public String getNamingScheme()
	{
		if (namingScheme == null)
			namingScheme = "";

		if ((getQuantity() > 1) && (namingScheme.equals("")))
			return "%N"; // default scheme
		else
			return this.namingScheme; // user-scheme
	}

	// ------------------------------------------------------------------------------

	/**
	 * Add a ToolOption to this agent, e.g. turn on logging, sniffing, benchmarking, etc.
	 * @param toolOptionType  The ToolOption to add.
	 */
	public void addToolOption(String toolOptionType)
	{
		toolOptionList.add(new ToolOption(toolOptionType));
	}

	/**
	 * Remove a ToolOption from this agent, e.g. turn off logging sniffing, benchmarking, etc.
	 * @param toolOptionType  The ToolOption to remove.
	 */
	public void removeToolOption(String toolOptionType)
	{
		toolOptionList.remove(toolOptionType);
	}

	/**
	 * Get a ToolOption.
	 * @return The ToolOption contained in the internal ArrayList of ToolOptions at the specified index.
	 */
	public IToolOption getToolOption(int index)
	{
		return toolOptionList.get(index);
	}

	/**
	 * Get all ToolOptions.
	 * @return An array containing all ToolOptions specified for this AgentInstance.
	 */
	public IToolOption[] getToolOptions()
	{
		ToolOption[] returnArray = new ToolOption[toolOptionList.size()];
		toolOptionList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Check if a toolOption is set.
	 *  @param typeName  toolOption's type-name.
	 *  @return  'true' if tooloption is set, 'false' otherwise.
	 */
	public boolean hasToolOption(String typeName)
	{
		for (int i=0; i < toolOptionList.size(); i++)
		{
			if (toolOptionList.get(i).getType().equals(typeName))
				return true;
		}
		return false;
	}

	/**
	 *  Get all of the agent's dependency-models.
	 *  @return An array containing all of the agent's dependency-models.
	 */
	public IDependency[] getDependencies()
	{
		AbstractDependency[] returnArray = new AbstractDependency[dependencyList.size()];
		dependencyList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Get all of the agent's dependency-models as a list.
	 *  @return A list containing all of the agent's dependency-models.
	 */
	public List<IDependency> getDependencyList()
	{
		return dependencyList;
	}

	/**
	 * Add a dependency to this agent's dependencies.
	 * @param dependency  The DependencyModel.
	 */
	public void addDependency(AbstractDependency dependency)
	{
		dependencyList.add(dependency);
	}

	/**
	 * Remove a dependency from this agent's dependency-list.
	 * @param dependency  The Dependency-model to remove.
	 */
	public void removeDependency(IDependency dependency)
	{
		dependencyList.remove(dependency);
	}

	/**
	 * Set the status of this model. The status indicates, whether loading was successful or not.
	 * @param newStatus  The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	public void setStatus(String newStatus)
	{
		this.status = newStatus;
	}

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	public String getStatus()
	{
		if (status == null)
			status = STATUS_ERROR;
		return this.status;
	}

	// -----------------------------------------------------------------------------------

	/**
	 * Add a parameter to this agent. The parameter overwrites the value of the
	 * matching type-parameter of this agent's type. Note: The parameters set within the
	 * AgentInstance may only define a name and a value, and overwrite a possible value
	 * defined by the parameters of the AgentType.
	 * @param parameter  The Parameter-object to add.
	 */
	public void addParameter(Parameter parameter)
	{
		parameterList.add(parameter);
	}

	/**
	 * Remove a parameter from this agent.
	 * @param parameter  The Parameter-object to remove.
	 */
	public void removeParameter(IParameter parameter)
	{
		parameterList.remove(parameter);
	}

	/**
	 * Get an agent's parameter.
	 * When the instance does not overwrite a parametervalue of a parameter, which
	 * is already defined by the type, the type's parameter is returned.
	 * @param name  The name of the Parameter
	 * @return  An agent's parameter or null if no parameter with the given name exists.
	 */
	public IParameter getParameter(String name)
	{
		// get the instance's parameters
		IParameter[] parameterArray = getParameters();

		for (int i=0; i < parameterArray.length; i++)
		{
			if (name.equals(parameterArray[i].getName()))
			{
				// get the type's parameter, copy the parameter-type into the instances's
				// parameter and finally return the instance's parameter (with it's values)

				IParameter typeParameter = getType().getParameter(name);
				parameterArray[i].setType(typeParameter.getType());
				return parameterArray[i];
			}
		}
		// no parameter with the given name specified
		return null;
	}

	/**
	 * Get all of the agent's parameter.
	 * When the instance does not overwrite a parametervalue of a parameter, which
	 * is already defined by the type, the type's parameter is returned.
	 * @return  All of the agent's parameters as Parameter-Array or null if no parameters exist.
	 */
	public IParameter[] getParameters()
	{
	    ArrayList<IParameter> allParameters = new ArrayList<IParameter>();

		// first add all instance-parameters to the new parameter-list
		for (int i=0; i < parameterList.size(); i++)
		{
			allParameters.add(parameterList.get(i));
		}

		// then, add the type-parameters. Parameters, whose values are overwritten
		// by the instance's parameter-values are not added, instead only their variables
		// are copied to the instance's parameter.

		System.err.println("AgentInstance.getParameters: FixMe !!! instance and type-parameters have to be merged.");

		Parameter[] typeParameters = type.getParameters();
		for (int i=0; i < typeParameters.length; i++)
		{
			allParameters.add(typeParameters[i]);
		}

		/*Parameter[] typeParameters = type.getParameters();
		for (int i=0; i < typeParameters.length; i++)
		{
			for (int j=0; j < allParameters.size(); j++)
			{
				if (typeParameters[i].getName().equals(allParameters.get(j).getName()))
				{
					// copy the type's parameter into
				}
			}
		}
		*/
        Parameter[] returnArray = new Parameter[allParameters.size()];
		allParameters.toArray(returnArray);
		return returnArray;
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
	/* public IAgentParameter getParameterClone(String name) throws ModelException
	{
		IAgentParameter parameterClone = (IAgentParameter)((AgentType)type).getParameter(name).clone();
		if (parameterClone == null)
			throw new ModelException("<html>Sorry, it seems the parameter <i>"+name+"</i> of agent <i>"+getName()+"</i> doesn't support cloning !<br>This parameter cannot not be passed on to the running instance.</html>");
		
		parameterClone.setValue(this.getParameterValue(name));
		return parameterClone;
	}
    */
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
	/*public IAgentParameter[] getParameterClones() throws ModelException
	{
        IAgentParameter[] parameters = ((AgentType)type).getParameters();
		IAgentParameter[] returnParameters = new IAgentParameter[parameters.length];

		for (int i=0; i < returnParameters.length; i++)
		{
			returnParameters[i] = getParameterClone(parameters[i].getName());
		}

		return returnParameters;
	}*/

	/**
	 * Add a ParameterSet to this agent. The ParameterSet overwrites the values of the
	 * matching type-parameterSet of this agent's type. Note: The parameterSets within the
	 * AgentInstance may only define a name and a list of values, and overwrite possible values
	 * defined by the ParameterSets of the AgentType.
	 * @param parameterSet  The ParameterSet-object.
	 */
	public void addParameterSet(ParameterSet parameterSet)
	{
		parameterSetList.add(parameterSet);
	}

	/**
	 * Remove a parameterSet from this agent.
	 * @param parameterSet  The ParameterSet-object to remove.
	 */
	public void removeParameterSet(IParameterSet parameterSet)
	{
		parameterSetList.remove(parameterSet);
	}

	/**
	 * Get an agent's ParameterSet.
	 * When the instance does not overwrite parametervalues of a ParameterSet, which
	 * is already defined by the type, the type's ParameterSet is returned.
	 * @param name  The name of the ParameterSet
	 * @return  The agent's ParameterSet or null if no ParameterSet with the given name exists.
	 */
	public IParameterSet getParameterSet(String name)
	{
		// get the instance's ParameterSets
		IParameterSet[] parameterArray = getParameterSets();

		for (int i=0; i < parameterArray.length; i++)
		{
			if (name.equals(parameterArray[i].getName()))
			{
				// get the type's parameterSet, copy the parameter-type into the instances's
				// parameter and finally return the instance's parameter (with it's values)

				IParameterSet typeParameter = getType().getParameterSet(name);
				parameterArray[i].setType(typeParameter.getType());
				parameterArray[i].setValues(typeParameter.getValues());
				return parameterArray[i];
			}
		}
		// no parameter with the given name specified
		return null;
	}

	/**
	 * Get all of the agent's parameterSets.
	 * When the instance does not overwrite a parameterSet, which
	 * is already defined by the type, the type's parameterSet is returned.
	 * @return  All of the agent's parameterSets as ParameterSet-Array or null if no parameterSets exist.
	 */
	public IParameterSet[] getParameterSets()
	{
	    ArrayList<IParameterSet> allParameterSets = new ArrayList<IParameterSet>();

		// first add all instance-parameters to the new parameter-list
		for (int i=0; i < parameterSetList.size(); i++)
		{
			allParameterSets.add(parameterSetList.get(i));
		}

		// then, add the type-parameters. Parameters, whose values are overwritten
		// by the instance's parameter-values are not added, instead only their variables
		// are copied to the instance's parameter.

		System.err.println("AgentInstance.getParameterSets: FixMe !!! instance and type-parameters have to be merged.");

		ParameterSet[] typeParameterSets = type.getParameterSets();
		for (int i=0; i < typeParameterSets.length; i++)
		{
			allParameterSets.add(typeParameterSets[i]);
		}

		/*Parameter[] typeParameters = type.getParameters();
		for (int i=0; i < typeParameters.length; i++)
		{
			for (int j=0; j < allParameters.size(); j++)
			{
				if (typeParameters[i].getName().equals(allParameters.get(j).getName()))
				{
					// copy the type's parameter into
				}
			}
		}
		*/

		ParameterSet[] returnArray = new ParameterSet[allParameterSets.size()];
		allParameterSets.toArray(returnArray);
		return returnArray;
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
	/*public IAgentParameterSet getParameterSetClone(String name) throws ModelException
	{
		IAgentParameterSet parameterSetClone = (IAgentParameterSet)((AgentType)type).getParameterSet(name).clone();
		if (parameterSetClone == null)
			throw new ModelException("<html>Sorry, it seems the parameterSet <i>"+name+"</i> of agent <i>"+getName()+"</i> doesn't support cloning !<br>This parameter-set cannot be passed on to the running instance.</html>");
		parameterSetClone.setValues(this.getParameterSetValues(name));
		return parameterSetClone;
	}
	*/

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
	/*public IAgentParameterSet[] getParameterSetClones() throws ModelException
	{
        IAgentParameterSet[] parameterSets = ((AgentType)type).getParameterSets();
		IAgentParameterSet[] returnParameterSets = new IAgentParameterSet[parameterSets.length];

		for (int i=0; i < returnParameterSets.length; i++)
		{
			returnParameterSets[i] = getParameterSetClone(parameterSets[i].getName());
		}

		return returnParameterSets;
	}
	*/

	// ------------------------------------------------------------------------------------------------

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed.
	 * If there are many events thrown in very short time-intervals these events will not be
	 * dispatched, if they are of the same kind. Instead a timer counts down and then dispatches
	 * one event.
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode)
	{
		throwModelChangedEvent(eventCode, null);
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed.
	 * If there are many events thrown in very short time-intervals these events will not be
	 * dispatched, if they are of the same kind. Instead a timer counts down and then dispatches
	 * one event.
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 * @param userObject  This object is stored in the ModelChangedEvent. For example: When runnables
	 * are added or removed, these RunnableModels are stored as userObjects to access them later on.
	 */
	public void throwModelChangedEvent(String eventCode, Object userObject)
	{
		ModelChangedEvent event = new ModelChangedEvent(this, eventCode, userObject);
		Vector modelChangedListener = getType().getModelChangedListener();
		for (int i=0; i < modelChangedListener.size(); i++)
		{
			((ModelChangedListener)modelChangedListener.elementAt(i)).modelChanged(event);
		}

		/*if (eventThrownTimeStamp + EVENT_INTERVAL > System.currentTimeMillis() || (!lastThrownEventCode.equals(eventCode)))
		{
			// the event may be thrown, because the time-interval is not violated
			reallyThrowModelChangedEvent(eventCode, userObject);
		}
		else
		{
			if (!timerHasBeenStarted)
			{
				System.err.println("SocietyInstanceModel.throwModelChanged: start timer");

				// the time-interval is violated, so check if the timer has been started and if not, start it.
				LongTimeActionStartEvent startEvent = new LongTimeActionStartEvent(LongTimeActionStartEvent.ACTION_WAIT_FOR_MODELCHANGE_POSTING);
				startEvent.addParameter("callback", this);
				startEvent.addParameter("waitfor", new Long(EVENT_INTERVAL));
				startEvent.addParameter("eventcode", eventCode);
				startEvent.addParameter("userobject", userObject);

				Vector timer = getParentSocietyType().getLongTimeActionStartListener();
				LongTimeActionStartListener[] listenerArray = new LongTimeActionStartListener[timer.size()];
				timer.toArray(listenerArray);

				for (int i=0; i < listenerArray.length; i++)
				{
					listenerArray[i].startLongTimeAction(startEvent);
				}
			}
			if (!timerHasBeenStarted)
			{
				System.err.println("SocietyInstanceModel.throwModelChanged: timer already started, throw event away");
			}
		}
         */

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

		
		for (int i=0; i < parameterList.size(); i++)
		{
			str += "    parameter: " + parameterList.get(i);
		}

		for (int i=0; i < parameterSetList.size(); i++)
		{
			str += "    parameterSet: " + parameterSetList.get(i);
		}

		IToolOption[] toolsOptions = getToolOptions();
		for (int i=0; i < toolsOptions.length; i++)
		{
			str += "    toolOption: "+toolsOptions[i]+"\n";
		}

		return str;
	}
}