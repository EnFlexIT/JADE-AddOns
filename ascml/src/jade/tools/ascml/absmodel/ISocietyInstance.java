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
import jade.tools.ascml.exceptions.ResourceNotFoundException;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.events.ModelChangedEvent;

import java.util.Vector;

/**
 *  The scenario interface that corresponds directly to the xml schema.
 *  todo: create scenarioref model element
 */
public interface ISocietyInstance
{
	/**
	 * This constant is used to indicate, that this model has successfully been loaded.
	 */
	public final static String STATUS_OK				= "successfully loaded";

	/**
	 * This constant is used to indicate, that at least one error occurred while loading the model.
	 */
	public final static String STATUS_ERROR				= "loading error";

	/**
	 * This constant is used to indicate, that at least one referenced element (subsociety or agenttype)
	 * has NOT been loaded successfully
	 */
	public final static String STATUS_REFERENCE_ERROR	= "erroneous reference";

	/**
	 * This constant is used when no name has been given to this societyinstance.
	 * Since the name is mandatory, it is set to NAME_UNKNOWN.
	 */
	public final static String NAME_UNKNOWN				= "Unnamed Society-Instance";

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
	 * Update the status of this model.
	 * The status indicates, whether this model is ready to run or not.
	 */
	public void updateStatus();

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_WARNING
	 * STATUS_REFERENCE_ERROR.
	 */
	public String getStatus();

	/**
	 * Set the model on which the functional-status relies.
	 * @param functionalModel  The model-object containing the funtional-dependencies and invariants.
	 */
	public void setFunctionalModel(IFunctional functionalModel);

	/**
	 * Get the model on which the functional-status relies.
	 * @return  The model-object containing the funtional-dependencies and invariants.
	 */
	public IFunctional getFunctionalModel();

	/**
	 * Get the status-exception, which describe errors or warnings in detail.
	 * @return  A ModelException containing detailed status-messages as Strings.
	 */
	public ModelException getStatusException();
	
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
	 *  This method returns a formatted String showing the model.
	 *  @return  formatted String showing ALL information about this model.
	 */
	public String toFormattedString();

	/**
	 *  Add a new AgentInstanceModel to the scenario.
	 *  @param model  The agentInstance's model-object
	 */
	public void addAgentInstanceModel(IAgentInstance model);

	/**
	 *  Get an AgentInstance-model
	 *  @return  The agentInstance's model-object
	 */
	public IAgentInstance getAgentInstanceModel(String name);

	/**
	 *  Get all AgentInstance-models
	 *  @return  The agentInstance model-objects as a HashMap with agent-name
	 *           as key and the instance-model as value.
	 */
	public IAgentInstance[] getAgentInstanceModels();

	/**
	 *  Add a new societyInstance-reference to this society.
	 *  @param socInstRef  The reference-model for this societyInstance-reference
	 */
	public void addSocietyInstanceReference(ISocietyInstanceReference socInstRef);
	
	/**
	 *  Get the reference-model of a societyInstance-reference.
	 *  @param  referenceName  The reference's name
	 *  @return  The reference-model for this societyInstance-reference
	 */
	 public ISocietyInstanceReference getSocietyInstanceReference(String referenceName);
	
	/**
	 *  Get all societyInstance-references.
	 *  @return  An array containing all the societyInstance-reference-models used by
	 *           
	 */
	public ISocietyInstanceReference[] getSocietyInstanceReferences();

	/**
	 * Get the amount of local agentinstances, that are definded by this model
	 * and all local subsocieties.
	 * @return  agentinstance-count of local agentinstances.
	 */
	public int getLocalAgentInstanceCount();

	/**
	 *  Get the SocietyTypeModel of the society, in which this scenario is declared
	 *  @return SocietyTypeModel in which this scenario is declared.
	 */
	public ISocietyType getParentSocietyType();

	/**
	 *  Set the SocietyTypeModel of the society, in which this scenario is declared
	 *  @param parentSociety SocietyTypeModel in which this scenario is declared.
	 */
	public void setParentSocietyType(ISocietyType parentSociety);
	
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

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed.
	 * If there are many events thrown in very short time-intervals these events will not be
	 * dispatched, if they are of the same kind. Instead a timer counts down and then dispatches
	 * one event.
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode);

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed.
	 * If there are many events thrown in very short time-intervals these events will not be
	 * dispatched, if they are of the same kind. Instead a timer counts down and then dispatches
	 * one event.
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 * @param userObject  This object is stored in the ModelChangedEvent. For example: When runnables
	 * are added or removed, these RunnableModels are stored as userObjects to access them later on.
	 */
	public void throwModelChangedEvent(String eventCode, Object userObject);
}
