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
import jade.tools.ascml.events.ModelChangedListener;

/**
 *  Model-object containing all required information about a scenario. 
 */
public class SocietyInstanceModel implements ISocietyInstance
{
	/**
	 * The status indicates if this model has successfully been loaded.
	 * Possible stati are STATUS_OK, STATUS_ERROR, STATUS_WARNING, STATUS_REFERENCE_ERROR.
	 */
	private String status;

	/**
	 * This Exception may contain a set of detailed String-messages in case the status is != STATUS_OK
	 */
	protected ModelException statusException;

	private String name;
	private String description;
	private HashMap agents;
	private HashMap societyInstanceRefs;
	private ISocietyType parentSociety;
	private long quantity;
	private String namingScheme;

	/**
	 *  Instantiate a new model and initialize some variables 
	 */
	public SocietyInstanceModel(String name, String description)
	{
		setName(name);
		setDescription(description);
		agents = new HashMap();
		societyInstanceRefs = new HashMap();
		quantity = 1;
		namingScheme = "";
	}

	/**
	 *  Get the SocietyTypeModel of the society, in which this scenario is declared
	 *  @return SocietyTypeModel in which this scenario is declared.
	 */
	public ISocietyType getParentSocietyType()
	{
		return parentSociety;
	}
	
	/**
	 *  Set the SocietyTypeModel of the society, in which this scenario is declared
	 *  @param parentSociety SocietyTypeModel in which this scenario is declared.
	 */
	public void setParentSocietyType(ISocietyType parentSociety)
	{
		this.parentSociety = parentSociety;
	}
	
	/**
	 *  Set the scenario's name.
	 *  @param name  name of the scenario.
	 */
	public void setName(String name)
	{
		if ((name == null) || (name.trim().equals("")))
			name = NAME_UNKNOWN;
		this.name = name;
	}
	
	/**
	 *  Returns the scenario's name
	 *  @return name of the scenario.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Update the status of this model.
	 * The status indicates, whether this model is ready to run or not.
	 */
	public void updateStatus()
	{
		String newStatus = STATUS_OK;
		statusException = new ModelException("The SocietyInstance '"+getName()+"' is errorneous.", "The SocietyInstance contains errors, and therefore may not be started.");

		IAgentInstance[] agentInstances = getAgentInstanceModels();
		ISocietyInstanceReference[] societyInstanceReferences = getSocietyInstanceReferences();

		boolean referenceError = false;
		for (int i=0; i < agentInstances.length; i++)
		{
			agentInstances[i].updateStatus();

			if ((agentInstances[i].getStatus() == IAgentInstance.STATUS_ERROR) ||
				(agentInstances[i].getStatus() == IAgentInstance.STATUS_REFERENCE_ERROR))
			{
				statusException.addNestedException(agentInstances[i].getStatusException());
				referenceError = true;
			}
		}
		for (int i=0; i < societyInstanceReferences.length; i++)
		{
			societyInstanceReferences[i].updateStatus();
			if ((societyInstanceReferences[i].getStatus() == ISocietyInstanceReference.STATUS_ERROR) ||
			    (societyInstanceReferences[i].getStatus() == ISocietyInstanceReference.STATUS_REFERENCE_ERROR))
			{
				statusException.addNestedException(societyInstanceReferences[i].getStatusException());
				referenceError = true;
			}
		}

		if (referenceError)
		{
			// statusException.addExceptionDetails("At least one referenced agent- or societyinstance has error-status", "write me!");
			newStatus = STATUS_REFERENCE_ERROR;
		}
		if (getParentSocietyType().getStatus() == ISocietyType.STATUS_ERROR)
		{
			statusException.addExceptionDetails("The parent-societytype of this societyinstance is in error-status", "write me!");
			newStatus = STATUS_REFERENCE_ERROR;
		}

		if ((getName() == null) || getName().equals("") || (getName() == NAME_UNKNOWN))
		{
			statusException.addExceptionDetails("A name for this societyinstance is missing", "write me!");
			newStatus = STATUS_ERROR;
		}
		if (getParentSocietyType() == null)
		{
			statusException.addExceptionDetails("There is no parent-societytype set for this societyinstance", "write me!");
			newStatus = STATUS_ERROR;
		}
		if ((getFullyQualifiedName() == null) || getFullyQualifiedName().equals(""))
		{
			statusException.addExceptionDetails("A fully qualified name for this societinstance is missing", "write me!");
			newStatus = STATUS_ERROR;
		}
		if ((agentInstances.length == 0) && (societyInstanceReferences.length == 0))
		{
			statusException.addExceptionDetails("There are neither agentinstances nor subsocieties specified", "write me!");
			newStatus = STATUS_ERROR;
		}

		if (getStatus() != newStatus)
		{
			status = newStatus;
			throwModelChangedEvent(ModelChangedEvent.STATUS_CHANGED);
		}
	}

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_WARNING
	 * STATUS_REFERENCE_ERROR.
	 */
	public String getStatus()
	{
		return this.status;
	}

	/**
	 * Get the status-exception, which describe errors or warnings in detail.
	 * @return  A ModelException containing detailed status-messages as Strings.
	 */
	public ModelException getStatusException()
	{
		return statusException;
	}

	/**
	 *  Returns the fully qualified scenario name. The name is composed of 
	 *  the societies-packagename, the societyname itself and the scenarioName,
	 *  for example my.societyName.scenarioName would be a correct 'fully qualified'
	 *  scenarioName.
	 *  @return name of the scenario.
	 */
	public String getFullyQualifiedName()
	{
		return ((ISocietyType)parentSociety).getFullyQualifiedName() + "." + getName();
	}
	
	/**
	 *  Set the scenario's description.
	 *  @param description  description of the scenario.
	 */
	public void setDescription(String description)
	{
		if (description == null)
			description = "";
		this.description = description;
	}
	
	/**
	 *  Returns the scenario's description
	 *  @return description-String of the scenario.
	 */
	public String getDescription()
	{
		return description;
	}	
	
	/**
	 *  Add a new AgentInstanceModel to the societyinstance.
	 *  @param model  The agentInstance's model-object
	 */
	public void addAgentInstanceModel(IAgentInstance model)
	{
		agents.put(model.getName(), model);
	}

	/**
	 *  Get an AgentInstance-model
	 *  @return  The agentInstance's model-object
	 */
	public IAgentInstance getAgentInstanceModel(String name)
	{
		return (IAgentInstance)agents.get(name);
	}
	
	/**
	 *  Get all AgentInstance-models
	 *  @return  An array containing the agentInstance model-objects
	 *           definded directly by this societyinstance.
	 */
	public IAgentInstance[] getAgentInstanceModels()
	{
		return (IAgentInstance[])agents.values().toArray(new IAgentInstance[agents.size()]);
	}
	
	/**
	 *  Add a new societyInstance-reference to this society.
	 *  @param socInstRef  The reference-model for this societyInstance-reference
	 */
	public void addSocietyInstanceReference(ISocietyInstanceReference socInstRef)
	{		
		societyInstanceRefs.put(socInstRef.getName(), socInstRef);
	}

	/**
	 *  Get the reference-model of a societyInstance-reference.
	 *  @param  referenceName  The reference's name
	 *  @return  The reference-model for this societyInstance-reference
	 */
	 public ISocietyInstanceReference getSocietyInstanceReference(String referenceName)
	{
		return (ISocietyInstanceReference)societyInstanceRefs.get(referenceName);
	}
	
	/**
	 *  Get all societyInstance-references.
	 *  @return  An array containing all the societyInstance-reference-models used by
	 *           
	 */
	public ISocietyInstanceReference[] getSocietyInstanceReferences()
	{
		ISocietyInstanceReference[] returnArray = new ISocietyInstanceReference[societyInstanceRefs.values().size()];
		societyInstanceRefs.values().toArray(returnArray);
		return returnArray;
	}

	/**
	 * Get the amount of local agentinstances, that are defined by this model
	 * and all local subsocieties. The 'quantity' of each instance is hereby considered.
	 * @return  agentinstance-count of local agentinstances.
	 */
	public int getLocalAgentInstanceCount()
	{
        int agentCount = 0;

		// count agentinstances, defined by this model.
		IAgentInstance[] agentInstances = getAgentInstanceModels();
		for (int i=0; i < agentInstances.length; i++)
		{
			agentCount += agentInstances[i].getQuantity();
		}

		// count instances, defined by references societyModels.
		ISocietyInstanceReference[] societyInstances = getSocietyInstanceReferences();
		for (int i=0; i < societyInstances.length; i++)
		{

			if (!societyInstances[i].isRemoteReference())
			{
				ISocietyInstance oneInstance = societyInstances[i].getLocallyReferencedModel();
				agentCount += (oneInstance.getLocalAgentInstanceCount() * societyInstances[i].getQuantity());
			}
		}

		// return agentCount multiplied with the 'quantity' of this model.
		return (int)(agentCount * this.getQuantity());
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
		Vector modelChangedListener = getParentSocietyType().getModelChangedListener();
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
	 *  This method returns a short String with the scenario-name.
	 *  It is used by the RepositoryTree for example, to name the nodes.
	 *  @return  String containing the name of this scenario. 
	 */
	public String toString()
	{		
		String str = getName();
		if (getQuantity() > 1)
			str += " (" + getQuantity() + ")";
		return str;
	}
	
	/**
	 *  This method returns a formatted String showing the scenario-model.
	 *  @return  formatted String showing ALL information about this scenario.
	 */
	public String toFormattedString()
	{
		String str = "";
		
		str += "SocietyInstance : name = " + getName() + "\n";
		ISocietyInstanceReference[] socInstArray = getSocietyInstanceReferences();
		for (int i = 0; i < socInstArray.length; i++)
		{
			
			ISocietyInstanceReference oneReference = socInstArray[i];
			str += "    Reference: " + oneReference + "\n";
		}

		str +=  "     AgentInst.: \n";
		Iterator keys = agents.keySet().iterator();		
		while (keys.hasNext())
		{
			String oneKey = (String)keys.next();
			str += "        " + agents.get(oneKey) + "\n";
		}
		return str;		
	}
}
