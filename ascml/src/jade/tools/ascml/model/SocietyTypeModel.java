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
import javax.swing.ImageIcon;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.repository.loader.ImageIconLoader;

/**
 * Model-object containing all required information about a society including all
 * neccessary Scenario-, AgentType-, AgentInstance- and other referenced SocietyTypeModels.
 */
public class SocietyTypeModel implements ISocietyType
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
	private HashMap agentTypes;
	private HashMap societyTypes;
	private HashMap societyInstances;
	private String defaultSocietyInstance;
	private Vector imports;
	private IDocument document;
	private ImageIcon icon;
	private Vector modelChangedListener;
	private Vector longTimeActionStartListener;

	/**
	 * Instantiate a new model and initialize some variables
	 */
	public SocietyTypeModel(Vector modelChangedListener, Vector longTimeActionStartListener)
	{
		agentTypes = new HashMap();
		societyTypes = new HashMap();
		societyInstances = new HashMap();
		imports = new Vector();
		icon = null;
		this.modelChangedListener = modelChangedListener;
		this.longTimeActionStartListener = longTimeActionStartListener;
	}

	/**
	 * Set the society-name.
	 *
	 * @param name name of the society.
	 */
	public void setName(String name)
	{
		if ((name == null) || (name.trim().equals("")))
			this.name = NAME_UNKNOWN;
		else
			this.name = name;
	}

	/**
	 * Get the society's name.
	 *
	 * @return Name of the society.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Update the status of this model.
	 * The status indicates, whether the model (or societyinstance-models) is/are ready to run or not (loading was successfully)
	 */
	public void updateStatus()
	{
		// at first update the status of all societyinstances
		ISocietyInstance[] societyInstances = getSocietyInstances();
		for (int i=0; i < societyInstances.length; i++)
		{
			societyInstances[i].updateStatus();
		}

		String newStatus = STATUS_OK;
		statusException = new ModelException("The SocietyType '"+getName()+"' is errorneous.", "The SocietyType contains errors.");

		societyInstances = getSocietyInstances();
		boolean referenceError = false;
		for (int i=0; i < societyInstances.length; i++)
		{
			if ((societyInstances[i].getStatus() == ISocietyInstance.STATUS_ERROR) ||
			    (societyInstances[i].getStatus() == ISocietyInstance.STATUS_REFERENCE_ERROR))
			{
				// if (!referenceError) // only add the header onece
					// statusException.addExceptionDetails("At least one referenced societyinstance is errorneous", "Societyinstances may contain agentinstances and references to other societyinstances. In this case either an agentinstance or a societyinstance has reference-errors. This means, that either one of the agentinstances point to an agenttype, which has not been loaded into the repository, or one of the societyinstance-references could not be resolved, because the referenced societyinstance is not contained within the repository. Please check if all referenced elements are loaded into the repository and if the spelling of these references is correct.");
				statusException.addNestedException(societyInstances[i].getStatusException());
				referenceError = true;
			}
		}
		if (referenceError)
		{
			newStatus = STATUS_REFERENCE_ERROR;
		}

		if ((getName() == null) || getName().equals(""))
		{
			statusException.addExceptionDetails("A name for this societytype is missing", "write me!");
			newStatus = STATUS_ERROR;
		}
		if ((getFullyQualifiedName() == null) || getFullyQualifiedName().equals(""))
		{
			statusException.addExceptionDetails("A fully qualified name for this societytype is missing", "write me!");
			newStatus = STATUS_ERROR;
		}
		if (getDocument() == null)
		{
			statusException.addExceptionDetails("The source-document for this societytype is missing", "write me!");
			newStatus = STATUS_ERROR;
		}
		if (getSocietyInstances().length == 0)
		{
			statusException.addExceptionDetails("There are no societyinstances specified for this societytype", "write me!");
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
	 *  Get the fully qualified name.
	 *  @return The fully qualified name.
	 */
	public String getFullyQualifiedName()
	{
		if(getDocument().getPackageName() == null || getDocument().getPackageName().length()==0)
			return name;
		else
			return getDocument().getPackageName()+"."+name;
	}

	/**
	 *  Set the document.
	 *  @param document  The document.
	 */
	public void setDocument(IDocument document)
	{
		this.document = document;
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
	 *  Set the image-icon.
	 *  @param icon  The image-icon.
	 */
	public void setIcon(ImageIcon icon)
	{
		this.icon = icon;
	}

	/**
	 *  Get the image-icon.
	 *  @return The image-icon.
	 */
	public ImageIcon getIcon()
	{
		if (icon == null)
			icon = ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYTYPE); // get default-icon
		return icon;
	}

	/**
	 * Set the society's description.
	 *
	 * @param description of the society.
	 */
	public void setDescription(String description)
	{
		if(description == null)
			description = "";
		this.description = description;
	}

	/**
	 * Get the society's description.
	 *
	 * @return description of the society.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Add an import for this society.
	 *
	 * @param oneImport String representation of an import.
	 */
	public void addImport(String oneImport)
	{
		imports.add(oneImport.trim());
	}

	/**
	 * Get all imports for this society.
	 *
	 * @return  String-array containing all the imports defindes for this societyType
	 */
	public String[] getImports()
	{
		String[] returnArray = new String[imports.size()];
		imports.toArray(returnArray);
		return returnArray;
	}
	
	/**
	 * Add a new AgentTypeModel to the society.
	 * @param name The agentType's fully qualified name (i.e. 'jade.examples.PingPong.agent.xml')
	 * @param type The corresponding agentType's model-object or null, if no appropiate model
	 * could be found in the repository
	 */
	public void addAgentType(String name, IAgentType type)
	{
		agentTypes.put(name, type);
	}

	/**
	 * Remove an agentType from the society. This method is called for example,
	 * when a reference is not fully qualified and should be replaced with a new one.
	 * @param name The agentType's name
	 */
	public void removeAgentType(String name)
	{
		agentTypes.remove(name);
	}

	/**
	 * Get an AgentTypeModel.
	 *
	 * @param name The agentType's fully qualified name (i.e. 'jade.examples.PingPong.agent.xml')
	 *             If the name is not fully qualified the agentTypes declared in this society
	 *             are searched for a best fit model.
	 * @return The corresponding agentType's model-object
	 */
	public IAgentType getAgentType(String name)
	{
		// System.err.println("SocietyTypeModel.GETAgentType: name=" + name);
		IAgentType returnModel = (IAgentType)agentTypes.get(name);
		if(returnModel==null)
		{
			// maybe the name is not fully qualified, so look for a machting name
			// in all declared agentTypes.
			Iterator modelIterator = agentTypes.keySet().iterator();
			while(modelIterator.hasNext())
			{
				String oneModelName = (String)modelIterator.next();
				// System.err.println("SocietyTypeModel.GETAgentType: available Model =" + oneModelName);
				if(oneModelName.endsWith(name))
				{
					return (IAgentType)agentTypes.get(oneModelName);
				}
			}
			// no model found at all
			return null;
		}
		else
		{
			return returnModel;
		}
	}

	/**
	 * Get all AgentTypeModels.
	 * Note: There may have been defined more agenttypes in this society than actually returned,
	 * but in case these agenttypes couldn't be resolved into models (by Project.resolveSocietyReferences)
	 * there are still null values in the agenttype-map, which are of course not returned.
	 * Use getAgentTypeNames()-method in case you want to have all defined models.
	 * @return All agentType model-objects in an Array
	 */
	public IAgentType[] getAgentTypes()
	{
		Vector dummyVector = new Vector();
		Iterator iter = agentTypes.values().iterator();
		while (iter.hasNext())
		{
			Object oneAgentType = iter.next();
			if (oneAgentType != null)
				dummyVector.add(oneAgentType);
		}

		return (IAgentType[])dummyVector.toArray(new IAgentType[dummyVector.size()]);
	}

	/**
	 * Get the names of all AgentTypeModels.
	 * @return  All agentType model-names as String-Array
	 */
	public String[] getAgentTypeNames()
	{
		return (String[])agentTypes.keySet().toArray(new String[agentTypes.size()]);
	}

	/**
	 * Add a new SocietyTypeModel to the society.
	 * @param name The societyType's name.
	 * @param type The corresponding societyType's model-object or null, if no appropiate
	 * model could be found in the repository
	 */
	public void addSocietyType(String name, ISocietyType type)
	{
		societyTypes.put(name.trim(), type);
	}

	/**
	 * Remove a (sub-)societyType from the society. This method is called for example,
	 * when a reference is not fully qualified and should be replaced with a new one.
	 * @param name The societyType's name
	 */
	public void removeSocietyType(String name)
	{
		societyTypes.remove(name);
	}

	/**
	 * Get a SocietyTypeModel.
	 *
	 * @param name The societyType's name.
	 * @return The corresponding societyType's model-object
	 */
	public ISocietyType getSocietyType(String name)
	{
		return (ISocietyType)societyTypes.get(name);
	}

	/**
	 * Get all referenced SocietyTypeModels.
	 * @return  All societyType model-objects in an Array
	 */
	public ISocietyType[] getSocietyTypes()
	{
		return (ISocietyType[])societyTypes.values().toArray(new ISocietyType[societyTypes.size()]);
	}

	/**
	 * Get the names of all SocietyTypeModels.
	 * @return  All societyType model-names as String-Array
	 */
	public String[] getSocietyTypeNames()
	{
		return (String[])societyTypes.keySet().toArray(new String[societyTypes.size()]);
	}

	/**
	 * Add a new SocietyInstanceModel to the society.
	 *
	 * @param model The societyInstance's model-object
	 */
	public void addSocietyInstance(ISocietyInstance model)
	{
		String name = model.getName();
		societyInstances.put(name, model);
	}

	/**
	 * Get a SocietyInstanceModel.
	 *
	 * @param name The societyInstance's name.
	 * @return The corresponding societyInstance's model-object (SocietyInstanceModel)
	 */
	public ISocietyInstance getSocietyInstance(String name)
	{
		return (ISocietyInstance)societyInstances.get(name);
	}

	/**
	 * Get all SocietyInstanceModels.
	 *
	 * @return Every societyInstance this society contains as an HashMap with
	 *         key='somename' and value='someSocietyInstanceModel'.
	 */
	public ISocietyInstance[] getSocietyInstances()
	{
		return (ISocietyInstance[])societyInstances.values().toArray(new ISocietyInstance[societyInstances.size()]);
	}

	/**
	 * Set the the name of the default-societyInstance.
	 *
	 * @param defaultScenario Name of the societyInstance used as default-societyInstance.
	 */
	public void setDefaultSocietyInstance(String defaultScenario)
	{
		if(defaultScenario==null)
			defaultScenario = "";
		this.defaultSocietyInstance = defaultScenario;
	}

	/**
	 * Get the default SocietyInstanceModel.
	 *
	 * @return The default scenario's model-object
	 */
	public ISocietyInstance getDefaultSocietyInstance()
	{
		ISocietyInstance societyInstance = (ISocietyInstance)societyInstances.get(defaultSocietyInstance);
		if (societyInstance == null)
			societyInstance = getSocietyInstances()[0];
		return societyInstance;
	}

	/**
	 * Get all the ModelChangedListener.
	 * @return  A Vector containing ModelChangedListener
	 */
	public Vector getModelChangedListener()
	{
		return modelChangedListener;
	}

	/**
	 * Get all the LongTimeActionStartListener.
	 * @return  A Vector containing LongTimeActionStartListener
	 */
	public Vector getLongTimeActionStartListener()
	{
		return longTimeActionStartListener;
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode)
	{
		ModelChangedEvent event = new ModelChangedEvent(this, eventCode);
		for (int i=0; i < modelChangedListener.size(); i++)
		{
			((ModelChangedListener)modelChangedListener.elementAt(i)).modelChanged(event);
		}
	}

	/**
	 * This method returns a short String with the society-name.
	 * It is used by the RepositoryTree for example, to name the nodes.
	 *
	 * @return String containing the name of this society.
	 */
	public String toString()
	{
		return getName();// + " " + super.toString();
	}

	/**
	 * This method returns a formatted String showing the societyType-model.
	 *
	 * @return formatted String showing ALL information about this society.
	 */
	public String toFormattedString()
	{
		Iterator keys;
		String str = "";

		str += "Society : name = "+name+"\n";

		keys = agentTypes.keySet().iterator();
		while(keys.hasNext())
		{
			String oneKey = (String)keys.next();
			str += "          agentTypes :  name = "+oneKey+"\n";
			str += "-- Type ------------------- \n";
			str += agentTypes.get(oneKey)+"\n";
			str += "--------------------------- \n";
		}

		keys = societyTypes.keySet().iterator();
		while(keys.hasNext())
		{
			String oneKey = (String)keys.next();
			str += "          societyTypes: "+societyTypes.get(oneKey)+"\n";
		}

		str += "          default-societyInstance: name = "+defaultSocietyInstance+"\n";

		keys = societyInstances.keySet().iterator();
		while(keys.hasNext())
		{
			String oneKey = (String)keys.next();
			str += societyInstances.get(oneKey);
		}
		return str;
	}
}