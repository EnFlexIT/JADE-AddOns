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

import javax.swing.ImageIcon;
import java.util.Vector;

/**
 *  The society interface that corresponds directly to the xml schema.
 */
public interface ISocietyType
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
	 * This constant is used to indicate, that at least one warning occurred while loading the model,
	 * but the model has nevertheless been loaded.
	 */
	public final static String STATUS_WARNING			= "loading warning";

	/**
	 * This constant is used when no name has been given to this societytype.
	 * Since the name is mandatory, it is set to NAME_UNKNOWN.
	 */
	public final static String NAME_UNKNOWN				= "Unnamed Society-Type";

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
	 * The status indicates, whether the model (or societyinstance-models) is/are ready to run or not (loading was successfully)
	 */
	public void updateStatus();

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_WARNING
	 * STATUS_REFERENCE_ERROR.
	 */
	public String getStatus();

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
	 *  Set the document.
	 *  @param document  The document.
	 */
	public void setDocument(IDocument document);

	/**
	 *  Get the document.
	 *  @return The document.
	 */
	public IDocument getDocument();

	/**
	 *  Set the image-icon.
	 *  @param icon  The image-icon.
	 */
	public void setIcon(ImageIcon icon);

	/**
	 * Add an import for this society.
	 *
	 * @param oneImport String representation of an import.
	 */
	public void addImport(String oneImport);

	/**
	 * Get all imports for this society.
	 *
	 * @return  String-array containing all the imports defindes for this societyType
	 */
	public String[] getImports();
	
	/**
	 *  Get the image-icon.
	 *  @return The image-icon.
	 */
	public ImageIcon getIcon();
	
	/**
	 *  Add a new AgentTypeModel to the society.
	 *  @param name  The agentType's fully qualified name (i.e. 'jade.examples.PingPong.agent.xml')
	 *  @param type  The corresponding agentType's model-object
	 */
	public void addAgentType(String name, IAgentType type);

	/**
	 * Remove an agentType from the society. This method is called for example,
	 * when a reference is not fully qualified and should be replaced with a new one.
	 * @param name The agentType's name
	 */
	public void removeAgentType(String name);

	/**
	 *  Get an AgentTypeModel.
	 *  @param name  The agentType's fully qualified name (i.e. 'jade.examples.PingPong.agent.xml')
	 *               If the name is not fully qualified the agentTypes declared in this society
	 *               are searched for a best fit model.
	 *  @return  The corresponding agentType's model-object
	 */
	public IAgentType getAgentType(String name);

	/**
	 * Get all AgentTypeModels.
	 * @return All agentType model-objects in an Array
	 */
	public IAgentType[] getAgentTypes();

	/**
	 * Get the names of all AgentTypeModels.
	 * @return  All agentType model-names as String-Array
	 */
	public String[] getAgentTypeNames();

	/**
	 *  Add a new SocietyTypeModel to the society.
	 *  @param name  The societyType's name.
	 *  @param type  The corresponding societyType's model-object
	 */
	public void addSocietyType(String name, ISocietyType type);

	/**
	 * Remove a (sub-)societyType from the society. This method is called for example,
	 * when a reference is not fully qualified and should be replaced with a new one.
	 * @param name The societyType's name
	 */
	public void removeSocietyType(String name);

	/**
	 *  Get a SocietyTypeModel.
	 *  @param name  The societyType's name.
	 *  @return  The corresponding societyType's model-object
	 */
	public ISocietyType getSocietyType(String name);

	/**
	 *  Get all referenced SocietyTypeModels.
	 *  @return  All societyType model-objects in an Array.
	 */
	public ISocietyType[] getSocietyTypes();

	/**
	 * Get the names of all SocietyTypeModels.
	 * @return  All societyType model-names as String-Array
	 */
	public String[] getSocietyTypeNames();

	/**
	 * Add a new SocietyInstanceModel to the society.
	 *
	 * @param model The societyInstance's model-object
	 */
	public void addSocietyInstance(ISocietyInstance model);

	/**
	 * Get a SocietyInstanceModel.
	 *
	 * @param name The societyInstance's name.
	 * @return The corresponding societyInstance's model-object (SocietyInstanceModel)
	 */
	public ISocietyInstance getSocietyInstance(String name);

	/**
	 * Get all SocietyInstanceModels.
	 *
	 * @return Every societyInstance this society contains as an HashMap with
	 *         key='somename' and value='someSocietyInstanceModel'.
	 */
	public ISocietyInstance[] getSocietyInstances();

	/**
	 * Set the the name of the default-societyInstance.
	 *
	 * @param defaultScenario Name of the societyInstance used as default-societyInstance.
	 */
	public void setDefaultSocietyInstance(String defaultScenario);

	/**
	 * Get the default SocietyInstanceModel.
	 *
	 * @return The default scenario's model-object
	 */
	public ISocietyInstance getDefaultSocietyInstance();

	/**
	 * Get all the ModelChangedListener.
	 * @return  A Vector containing ModelChangedListener
	 */
	public Vector getModelChangedListener();

	/**
	 * Get all the LongTimeActionStartListener.
	 * @return  A Vector containing LongTimeActionStartListener
	 */
	public Vector getLongTimeActionStartListener();

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode);
}
