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


package jade.tools.ascml.repository;

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.exceptions.ResourceNotFoundException;
import jade.tools.ascml.events.ProjectListener;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.model.AgentTypeModel;

import java.util.*;
import java.io.File;
import java.io.IOException;

public class Project
{
	public final static String BASIC_VIEW	= "basic";
	public final static String EXPERT_VIEW	= "expert";

	public final static String DEFAULT_PROJECT_NAME = "Default Project";

	private String name;
	private String view;
	private String workingDirectory;

    /** These two variables are only used at ASCML startup, while the model-locations
	 * are read out of the property-file. Because the ModelManager is not ready at this
	 * stage, the model-locations have to be cached in order to set the read model-object
	 * later on. When the ASCML-startup has been finished these two Vector should be empty.
	 */
	private HashMap temporarySocietyTypesCache;			/** Key = resourceLocation, Value = viewable */
	private HashMap temporaryAgentTypesCache;			/** Key = resourceLocation, Value = viewable */

	private HashMap societyTypesNameMap;				/** Key = qualif. model-name, Value = model-object */

	private HashMap agentTypesNameMap;				/** Key = qualif. model-name, Value = model-object */

	private Repository repository;
	private ModelManager modelManager;

	/**
	 *  Instantiate a new Project and initialize some variables
	 */
	public Project(String name, Repository repository)
	{
		if ((name == null) || name.equals(""))
			this.name = DEFAULT_PROJECT_NAME;
		else
			this.name = name;

		modelManager				= null; // set in init-method
		this.repository				= repository;
		view						= BASIC_VIEW;
		workingDirectory			= System.getProperty("user.dir");
		temporarySocietyTypesCache	= new HashMap();
		temporaryAgentTypesCache	= new HashMap();
		societyTypesNameMap			= new HashMap();
		agentTypesNameMap			= new HashMap();
	}
	
	
	/**
	 *  Get a societyType-model specified by it's fully qualified name.
	 *  @param fullyQualifiedName  The name of the societyType.
	 *  @return the societyType-model-object.
	 */
	public ISocietyType getSocietyType(String fullyQualifiedName)
	{
		return (ISocietyType)societyTypesNameMap.get(fullyQualifiedName);
	}
	
	/**
	 *  Returns all the societyTypes specified in this project.
	 *  @return  An array of SocietyTypes.
	 */
	public ISocietyType[] getSocietyTypes()
	{
		Collection models = societyTypesNameMap.values();

		ISocietyType[] returnArray = new ISocietyType[models.size()];
		models.toArray(returnArray);
		return returnArray;
	}
	
	/**
	 *  Returns all the societyType-locations specified in this project.
	 *  @return  A Vector containing all societyType-locations.
	 */
	public Vector getSocietyTypeLocations()
	{
		return (Vector)temporarySocietyTypesCache.keySet();
	}
	
	/**
	 * Add a societyType-location to the project.
	 * This location has to be resolved later on, in order to get the model-objects
	 * (use the addSocietyTypeMapping-method once the societyType-model object has been created).
	 * @param societyTypeLocation  The source-object of the societyType (used as key for internal map)
	 */
	public void addTemporarySocietyType(Object societyTypeLocation)
	{
		temporarySocietyTypesCache.put(societyTypeLocation, "");
	}

	public ISocietyType[] addSocietyTypes(Vector<String> societyLocations) throws ModelException
	{
		ModelException me = new ModelException("Error while adding one or more societytypes.", "Write me !!!");
		ISocietyType[] returnArray = new ISocietyType[societyLocations.size()];

		HashMap<ISocietyType, ModelException> erroneousSocietyTypes = new HashMap();

		for (int i=0; i < societyLocations.size(); i++)
		{
			try
			{
				// System.err.println("Project.addSocietyTypes: try to add " + societyLocations.elementAt(i));
				returnArray[i] = addSocietyType(societyLocations.elementAt(i));
			}
			catch(ModelException exc)
			{
				if ((exc.getUserObject() != null) && (exc.getUserObject() instanceof ISocietyType))
					returnArray[i] = (ISocietyType)exc.getUserObject();
				erroneousSocietyTypes.put(returnArray[i], exc);
			}
		}

		// now retry to resolve the references, this may lead to success, because
		// the models, that were added above are now present in the repository,
		// so resolving the reference should find the referenced models.
		boolean resolvingSuccessful = true;
		while (resolvingSuccessful)
		{
			resolvingSuccessful = false;
			Iterator<ISocietyType> modelIterator = erroneousSocietyTypes.keySet().iterator();

			while (modelIterator.hasNext())
			{
				ISocietyType socTypeToResolve = modelIterator.next();
				// System.err.println("Project.addSocTypes: SocType " + socTypeToResolve + " needs resolving");
				resolvingSuccessful = resolveSocietyReferences(socTypeToResolve);
				String status = socTypeToResolve.getStatus();

				if ((status != ISocietyType.STATUS_ERROR) && (status != ISocietyType.STATUS_REFERENCE_ERROR))
					erroneousSocietyTypes.remove(socTypeToResolve);
			}
		}

		// iterate through all erroneous societyTypes again and add the corresponding exception
		// to the root-exception.
		Iterator<ModelException> exceptionIterator = erroneousSocietyTypes.values().iterator();
		while(exceptionIterator.hasNext())
		{
			me.addNestedException(exceptionIterator.next());
		}

		if (me.hasNestedExceptions())
			throw me;

		return returnArray;
	}

	/**
	 * Add a societyType to the project.
	 * @param societyLocation  The source-location of the societyType
	 */
	public ISocietyType addSocietyType(String societyLocation) throws ModelException
	{
		ISocietyType model = null;
		ModelException me = new ModelException("Error while loading a societytype from '"+societyLocation, "There has been an error while loading the societytype. The societytype has nevertheless been loaded and added to the repository, but it is marked as errorneous and some (or all) societyinstances may not be started until the error is corrected.");
		try
		{
			model = (ISocietyType)modelManager.getModel(societyLocation);
		}
		catch(ResourceNotFoundException exc)
		{
			System.err.println("Project.addSocietyType: Resource not found: " + societyLocation);
			ResourceNotFoundException exception = new ResourceNotFoundException("Following models could not be found: " + exc.getUserObject(), "The ASCML wasn't able to load one or more models specified within the given society. Because of this, the society could not be initialized properly and is not loaded into the repository. Please have a look into your source-files !", exc);
			me.addNestedException(exception);
		}
		catch(ModelException exc)
		{
			System.err.println("Project.addSocietyType: Loaded with errors: " + societyLocation);
			me.addNestedException(exc);
			model = (ISocietyType)exc.getUserObject();
		}

		if (model != null)
		{
			// try to resolve the references to subsocieties and agenttypes within the society
			resolveSocietyReferences(model);
			try
			{
				// System.err.println("Project.addSocietyType(String): try to add model " + model);
				addSocietyType(model);
			}
			catch(ModelException exc)
			{
				me.addNestedException(exc);
			}
		}

		if (me.hasNestedExceptions())
		{
			me.setUserObject(model);
			throw me;
		}

		return model;
	}

	/**
	 * Add a societyType-mapping to the project, this mapping resolves the relation 
	 * between location and model-object
	 * @param societyType  The model-object to add
	 */
	public void addSocietyType(ISocietyType societyType) throws ModelException
	{
		String societyTypeName = societyType.getFullyQualifiedName();

		societyTypesNameMap.put(societyTypeName, societyType);

		// update the status of all societytypes
		ISocietyType[] societyTypes = repository.getModelIndex().getSocietyTypeObjects();
		for (int i=0; i < societyTypes.length; i++)
		{
			societyTypes[i].updateStatus();
		}

		// System.err.println("Project.addSocietyType: socType=" + societyType);
		throwProjectChangedEvent(new ProjectChangedEvent(ProjectChangedEvent.SOCIETYTYPE_ADDED, societyType, this));
		
		if ((societyType.getStatus() == ISocietyType.STATUS_ERROR) ||
			(societyType.getStatus() == ISocietyType.STATUS_REFERENCE_ERROR))
			throw societyType.getStatusException();
	}

	/**
	 * Remove a societyType from the repository. The societyType is only removed, if it is not
	 * referenced by any other society. If the society is referenced, it is only be set to be
	 * non-viewable in the repository-tree. Also all agentTypes referenced by the society, which
	 * are not referenced by any other society and are already non-viewable are removed.
	 * The society is also not removed, if there are still some runnableModels present.
	 * @param societyTypeName The fully qualified societyType-name to remove
	 * @return  true, if the society has been removed, false otherwise.
	 */
	public boolean removeSocietyType(String societyTypeName)
	{
		boolean modelHasBeenRemoved;
		ISocietyType societyType = (ISocietyType)societyTypesNameMap.get(societyTypeName);

		boolean runnablesLeft = (repository.getRunnableManager().getRunnables(societyType).length > 0);

		ISocietyInstance[] societyInstances = societyType.getSocietyInstances();
		for (int i=0; i < societyInstances.length;i++)
		{
			if (repository.getRunnableManager().getRunnables(societyInstances[i]).length > 0)
				runnablesLeft = true;
		}

		if (runnablesLeft)
		{
			repository.throwExceptionEvent(new ModelException("Error removing/reloading '" + societyType + "'. Runnables are still present.", "The model couldn't be removed/reloaded because there are still runnable-instances of this model left. Please make sure, all runnable instances have been stopped, before removing/reloading this model."));
			modelHasBeenRemoved = false;
		}
		else
		{
			this.societyTypesNameMap.remove(societyTypeName);
			modelHasBeenRemoved = true;

			/*// check if the society is referenced somewhere
			Vector references = getReferences(societyType);
			if (references.size() == 0)
			{
				// if it is not referenced it may now completly be removed
				this.societyTypesNameMap.remove(societyTypeName);

				// now test, if the agentTypes belonging to the society that should be removed,
				// are elsewhere in use (means referenced by any other society or explicitly loaded by the user)
				// and if NOT, than also remove them.

				IAgentType[] agentTypes = societyType.getAgentTypes();

				for (int i=0; i < agentTypes.length; i++)
				{
					boolean agentIsReferenced = (getReferences(agentTypes[i]).size() > 0);

					// completly remove the agentTypes from the repository
					if (!agentIsReferenced)
					{
						this.agentTypesNameMap.remove(agentTypes[i].getFullyQualifiedName());
					}
				}
			}
			else // there are references to this society
			{
				setSocietyTypeViewable(societyTypeName, false);
			}
			modelHasBeenRemoved = true;
			*/
		}

		updateSocietyTypeStatus();

		if (modelHasBeenRemoved)
			throwProjectChangedEvent(new ProjectChangedEvent(ProjectChangedEvent.SOCIETYTYPE_REMOVED, societyType, this));

		return modelHasBeenRemoved;
	}

	/**
	 * Reload a societyType from it's source.
	 * @param societyTypeName  The fully qualified name of the societyType to reload
	 */
	public void reloadSocietyType(String societyTypeName)
	{
		ISocietyType societyType = ((ISocietyType)societyTypesNameMap.get(societyTypeName));
		String source = (String)societyType.getDocument().getSource();
		try
		{
			boolean modelHasBeenRemoved = removeSocietyType(societyTypeName);
			if (modelHasBeenRemoved)
			{
				modelManager.getModelIndex().removeModel(source);
				addSocietyType(source);
			}
		}
		catch(ModelException exc)
		{
			repository.throwExceptionEvent(new ModelException("Error reloading the societytype '" + societyTypeName + "'.", "The societytype you tried to reload contains errors, therefore it is removed from the repository, but not added again. Please have a look at the other exception-messages for further details.", exc));
		}
	}

	/**
	 * This method tries to resolve the references to agenttypes and subsocieties contained
	 * within the societyType.
	 * @param model  The model-object which references needs to be resolved into real models.
	 */
	private boolean resolveSocietyReferences(ISocietyType model)
	{
		boolean atLeastOneReferenceResolved = true;

		// The loop is iterated as long one reference could be resolved.
		// If no reference could be resolved any longer, we're finished or stuck.
		// Nevertheless resolving quits.
		while (atLeastOneReferenceResolved)
		{
			atLeastOneReferenceResolved = false;
			// 1. Resolve referenced agenttype-Strings into AgentTypeModels
			//
			// a) Iterate through all referenced agentTypeNames.
			// b) Check if model of a name-model pair is null
			// c) if so, try to get model from modelIndex (if not found try using fq-package-name or imports)
			// d) if model has been found set it in the SocietyTypeModel

			String[] agentTypeNames = model.getAgentTypeNames();
			for (int i=0; i < agentTypeNames.length; i++) // a
			{
				IAgentType oneAgentType = model.getAgentType(agentTypeNames[i]);
				if (oneAgentType == null) // b)
				{
					// System.err.println("Project: 1 Try to resolve AgentType " + agentTypeNames[i]);
					Object possibleModel = repository.getModelIndex().getModel(agentTypeNames[i]);

					if (possibleModel == null) // c
					{
						// try using package-name
						possibleModel = repository.getModelIndex().getModel(model.getDocument().getPackageName() + "." + agentTypeNames[i]);
						if (possibleModel != null)
						{
							// model found with fq-name, remove the old reference-identifier (cause it's not fully qualified)
							// and set the new fully-qualified name
							model.removeAgentType(agentTypeNames[i]);
							agentTypeNames[i] = model.getDocument().getPackageName() + "." + agentTypeNames[i];
						}
						else
						{
                            // try using imports
							String[] imports = model.getDocument().getImports();
							for (int j=0; j < imports.length; j++)
							{
								possibleModel = repository.getModelIndex().getModel(imports[j] + "." + agentTypeNames[i]);
								if (possibleModel != null)
								{
									// model found with import-prefix, remove the old reference-identifier
									// and set the new fully-qualified name with import
									model.removeAgentType(agentTypeNames[i]);
									agentTypeNames[i] = imports[j] + "." + agentTypeNames[i];
									j = Integer.MAX_VALUE - 1; // break out of for-loop
								}
							}
						}
					} // end of c)

					if (possibleModel != null) // d
					{
						// System.err.println("Project: 1 Successfully resolved " + possibleModel);
						model.addAgentType(agentTypeNames[i], (IAgentType)possibleModel);
						atLeastOneReferenceResolved = true;
					}
				}
			} // end of agenttype-reference resolving

			// 2. Resolve referenced societytype-Strings into SocietyTypeModels
			//
			// a) Iterate through all referenced agentTypeNames.
			// b) Check if model of a name-model pair is null
			// c) if so, try to get model from modelIndex (if not found try using fq-package-name or imports)
			// d) if model has been found set it in the SocietyTypeModel

			String[] societyTypeNames = model.getSocietyTypeNames();
			for (int i=0; i < societyTypeNames.length; i++) // a
			{
				// System.err.println("Project: 2 Try to resolve SocietyType " + societyTypeNames[i]);
				ISocietyType oneSocietyType = model.getSocietyType(societyTypeNames[i]);
				if (oneSocietyType == null) // b)
				{
					Object possibleModel = repository.getModelIndex().getModel(societyTypeNames[i]);

					if (possibleModel == null) // c
					{
						// try using package-name
						possibleModel = repository.getModelIndex().getModel(model.getDocument().getPackageName() + "." + societyTypeNames[i]);
						if (possibleModel != null)
						{
							// model found with fq-name, remove the old reference-identifier (cause it's not fully qualified)
							// and set the new fully-qualified name
							model.removeSocietyType(societyTypeNames[i]);
							societyTypeNames[i] = model.getDocument().getPackageName() + "." + societyTypeNames[i];
						}
						else
						{
                            // try using imports
							String[] imports = model.getDocument().getImports();
							for (int j=0; j < imports.length; j++)
							{
								possibleModel = repository.getModelIndex().getModel(imports[j] + "." + societyTypeNames[i]);
								if (possibleModel != null)
								{
									// model found with import-prefix, remove the old reference-identifier
									// and set the new fully-qualified name with import
									model.removeSocietyType(societyTypeNames[i]);
									societyTypeNames[i] = imports[j] + "." + societyTypeNames[i];
									j = Integer.MAX_VALUE - 1; // break out of for-loop
								}
							}
						}
					} // end of c)

					if (possibleModel != null) // d
					{
						// System.err.println("Project: 2 Successfully resolved " + possibleModel);
						model.addSocietyType(societyTypeNames[i], (ISocietyType)possibleModel);
						atLeastOneReferenceResolved = true;
					}
				}
			} // end of societytype-reference resolving

			// 3. Iterate through all SocietyInstances and resolve referenced agenttype-Strings into AgentTypeModels
			//
			// a) Iterate through all referenced agentInstances.
			// b) check if typeName != null (means type could not be resolved)
			// c) if type could not be resolved try to get the type from societyType.getAgentType(name)
			// d) if type could not be resolved try to get the type from modelIndex
			// e) if type could not be resolved try to get the type from modelIndex with package-name
			// f) if type could not be resolved try to get the type from modelIndex with imports
			// g) if model has been found set it in the AgentInstanceModel

			ISocietyInstance[] societyInstances = model.getSocietyInstances();
			for (int i=0; i < societyInstances.length; i++)
			{
				IAgentInstance[] agentInstances = societyInstances[i].getAgentInstanceModels();
				for (int j=0; j < agentInstances.length; j++) // a
				{
					String typeName = agentInstances[j].getTypeName();
					if (typeName != null) // b
					{
						// System.err.println("Project: 3 Try to resolve " + typeName + " in " + societyInstances[i]);
						Object possibleModel = model.getAgentType(typeName); // c

						if (possibleModel == null) // d
						{
							// try using package-name
							possibleModel = repository.getModelIndex().getModel(model.getDocument().getPackageName() + "." + typeName);
							if (possibleModel == null) // e
							{
								// try using imports
								String[] imports = model.getDocument().getImports();
								for (int k=0; k < imports.length; k++)
								{
									if (possibleModel == null) // f
										possibleModel = repository.getModelIndex().getModel(imports[k] + "." + typeName);
								}
							}
						}

						// g)
						if (possibleModel != null)
						{
							// System.err.println("Project: 3 Successfully resolved " + possibleModel);
							agentInstances[j].setType((IAgentType)possibleModel);
						}
						else
						{
							// Set a dummy AgentType
							IAgentType newAgentType = new AgentTypeModel(repository.getListenerManager().getModelChangedListener());
							newAgentType.updateStatus();
							agentInstances[j].setType(newAgentType);
						}
					}
				}
			} // end of 3.

			// 4. Iterate through all SocietyInstances and resolve referenced agenttype-Strings into AgentTypeModels
			//
			// a) Iterate through all referenced agentInstances.
			// b) check if no type has been set before and if reference is local (remote reference do not have to be resolved)
			// c) if type could not be resolved try to get the type from societyType.getSocietyType(name)
			// d) if type could not be resolved try to get the type from modelIndex
			// e) if type could not be resolved try to get the type from modelIndex with package-name
			// f) if type could not be resolved try to get the type from modelIndex with imports
			// g) if model has been found set it in the SocietyInstanceReferenceModel as 'locallyReferencedModel'

			for (int i=0; i < societyInstances.length; i++)
			{
				ISocietyInstanceReference[] instanceReferences = societyInstances[i].getSocietyInstanceReferences();
				for (int j=0; j < instanceReferences.length; j++) // a
				{
					String typeName = instanceReferences[j].getTypeName();
					String instanceName = instanceReferences[j].getInstanceName();

					if (!instanceReferences[j].isRemoteReference() || (instanceReferences[j].getLocallyReferencedModel() == null)) // b
					{
						// System.err.println("Project: 4 Try to resolve reference " + typeName + "." + instanceName);
						Object possibleModel = model.getSocietyType(typeName); // c

						if (possibleModel == null) // d
						{
							// try getting model from model-index with fully qualified name as given in description-file
							possibleModel = repository.getModelIndex().getModel(typeName);

							// try getting model from model-index with packagename of parent-model
							if (possibleModel == null)
								possibleModel = repository.getModelIndex().getModel(model.getDocument().getPackageName() + "." + typeName);

							if (possibleModel == null) // e
							{
								// try using imports
								String[] imports = model.getDocument().getImports();
								for (int k=0; k < imports.length; k++)
								{
									if (possibleModel == null) // f
										possibleModel = repository.getModelIndex().getModel(imports[k] + "." + typeName);
								}
							}
						}

						// g)
						if (possibleModel != null)
						{
							// System.err.println("Project: 4 Successfully resolved " + possibleModel);
							ISocietyType typeModel = (ISocietyType)possibleModel;
							instanceReferences[j].setLocallyReferencedModel(typeModel.getSocietyInstance(instanceName));
						}
					}
				}
			} // end of 4.
		}

		model.updateStatus();
		if (model.getStatus() == ISocietyType.STATUS_REFERENCE_ERROR)
			return false;
		else
			return true;
	}

	private Vector getReferences(Object model)
	{
		Vector referenceVector = new Vector();

		// get all societies contained in the repository
		ISocietyType[] societies = getSocietyTypes();

		// iterate through all societies and their agentTypes.
		for (int i=0; i < societies.length; i++)
		{
			ISocietyType oneSociety = societies[i];

			IAgentType[] oneSocietiesAgents = oneSociety.getAgentTypes();
			for (int j=0; j < oneSocietiesAgents.length; j++)
			{
				if (model == oneSocietiesAgents[j])
					referenceVector.add(oneSociety);
			}

			ISocietyType[] oneSocietiesSubSocieties = oneSociety.getSocietyTypes();
			for (int j=0; j < oneSocietiesSubSocieties.length; j++)
			{
				if (model == oneSocietiesSubSocieties[j])
				{
					if (!referenceVector.contains(oneSociety))
						referenceVector.add(oneSociety);
				}
			}
		}
        return referenceVector;
	}

	/**
	 *  Get a agentType-model specified by it's fully qualified name.
	 *  @param fullyQualifiedName  The name of the agentType.
	 *  @return the agentType-model-object.
	 */
	public IAgentType getAgentType(String fullyQualifiedName)
	{
		return (IAgentType)agentTypesNameMap.get(fullyQualifiedName);
	}
	
	/**
	 *  Returns all the agentTypes specified in this project.
	 *  @return  An array of agentTypes.
	 */
	public IAgentType[] getAgentTypes()
	{
		Collection models = agentTypesNameMap.values();
			
		IAgentType[] returnArray = new IAgentType[models.size()];
		models.toArray(returnArray);
		return returnArray;
	}
	
	/**
	 *  Returns all the agentType-locations specified in this project.
	 *  @return  A Vector containing all agentType-locations.
	 */
	public Vector getAgentTypeLocations()
	{
		return (Vector)temporaryAgentTypesCache.keySet();
	}
	
	/**
	 * Add an agentType-location to the project.
	 * This location has to be resolved later on, in order to get the model-objects
	 * (use the addAgentTypeMapping-method once the agentType-model object has been created).
	 * @param agentTypeLocation  The source-object of the agentType (used as key for internal map)
	 */
	public void addTemporaryAgentType(Object agentTypeLocation)
	{
		temporaryAgentTypesCache.put(agentTypeLocation,"");
	}

	public IAgentType[] addAgentTypes(Vector<String> agentLocations) throws ModelException
	{
		ModelException me = new ModelException("Error while adding one or more agenttypes.", "Write me !!!");
		IAgentType[] returnArray = new IAgentType[agentLocations.size()];

		for (int i=0; i < agentLocations.size(); i++)
		{
			try
			{
				returnArray[i] = addAgentType(agentLocations.elementAt(i));
			}
			catch(ModelException exc)
			{
				me.addNestedException(exc);
			}
		}

		if (me.hasNestedExceptions())
			throw me;

		return returnArray;
	}

	/**
	 * Add an agentType-mapping to the project.
	 * @param agentLocation  The source-location of the agentType
	 */
	public IAgentType addAgentType(String agentLocation) throws ModelException
	{
		IAgentType model = null;
		try
		{
			model = (IAgentType)modelManager.getModel(agentLocation);
		}
		catch(ResourceNotFoundException exc)
		{
			System.err.println("Project.addAgentType: Resource not found: " + agentLocation);
			exc.setErrorCode(ResourceNotFoundException.AGENTTYPE_MODEL_NOT_FOUND);
			repository.throwExceptionEvent(exc);

		}
		catch(ModelException exc)
		{
			System.err.println("Project.addAgentType: Loaded with errors: " + agentLocation);
			repository.throwExceptionEvent(exc);
			model = (IAgentType)exc.getUserObject();
		}
		if (model != null)
			addAgentType(model); // may throw ModelException
		return model;
	}

	/**
	 * Add an agentType-mapping to the project
	 * @param agentType  The model-object
	 */
	public void addAgentType(IAgentType agentType) throws ModelException
	{
		String agentTypeName = agentType.getFullyQualifiedName();

		agentTypesNameMap.put(agentTypeName, agentType);

		// update the status of the agent and all societytypes
		agentType.updateStatus();

		updateSocietyTypeStatus();

		throwProjectChangedEvent(new ProjectChangedEvent(ProjectChangedEvent.AGENTTYPE_ADDED, agentType, this));

        // if (agentType.getStatus() == IAgentType.STATUS_ERROR)
		//	throw agentType.getStatusException();
	}

	private void updateSocietyTypeStatus()
	{
		ISocietyType[] societyTypes = repository.getModelIndex().getSocietyTypeObjects();
		for (int i=0; i < societyTypes.length; i++)
		{
			String societyStatus = societyTypes[i].getStatus();
			if (societyStatus == ISocietyType.STATUS_REFERENCE_ERROR)
			{
				resolveSocietyReferences(societyTypes[i]);
			}
			societyTypes[i].updateStatus();
		}
	}

	/**
	 * Remove an agentType from the project.
	 * An agentType may only be removed, if no associated runnableModels are present.
	 * @param agentTypeName The fully qualified name of the agentType to remove
	 * @return  true, if agentType has been removed, false otherwise
	 */
	public boolean removeAgentType(String agentTypeName)
	{
		boolean modelHasBeenRemoved;

		IAgentType agentType = ((IAgentType)agentTypesNameMap.get(agentTypeName));

		// check if there are still runnablemodels of this agenttype and if so, throw an exception
		if (repository.getRunnableManager().getRunnables(agentType).length > 0)
		{
			repository.throwExceptionEvent(new ModelException("Error removing/reloading '" + agentTypeName + "'. Runnables are still present.", "The model couldn't be removed/reloaded because there are still runnable-instances of this model left. Please make sure, all runnable instances have been stopped, before removing/reloading this model."));
			modelHasBeenRemoved = false;
		}
		else
		{
			// check if the agentType is referenced somewhere

			this.agentTypesNameMap.remove(agentTypeName);
			modelHasBeenRemoved = true;
			/*Vector references = getReferences(agentType);

			if (references.size() == 0)
			{
				this.agentTypesNameMap.remove(agentTypeName);
				this.agentTypesViewableMap.remove(agentTypeName);
			}
			else
			{
				// setAgentTypeViewable(agentTypeName, false);
				for (int i=0; i < references.size(); i++)
				{
					ISocietyType societyType = (ISocietyType)references.elementAt(i);
					// societyType.setStatus(ISocietyType.STATUS_REFERENCE_ERROR)
					societyType.updateStatus();
				}
			}
			modelHasBeenRemoved = true;*/
		}

		// updateSocietyTypeStatus();

		if (modelHasBeenRemoved)
			throwProjectChangedEvent(new ProjectChangedEvent(ProjectChangedEvent.AGENTTYPE_REMOVED, agentType, this));

		return modelHasBeenRemoved;
	}

	/**
	 * Reload an agentType from it's source.
	 * @param agentTypeName The fully qualified name of the agentType to reload
	 */
	public void reloadAgentType(String agentTypeName)
	{
		IAgentType agentType = ((IAgentType)agentTypesNameMap.get(agentTypeName));
		String source = (String)agentType.getDocument().getSource();
		try
		{
			boolean modelHasBeenRemoved = removeAgentType(agentTypeName);
			if (modelHasBeenRemoved)
			{
				modelManager.getModelIndex().removeModel(source);
				addAgentType(source);
			}
		}
		catch(ModelException exc)
		{
			repository.throwExceptionEvent(new ModelException("Error reloading the agenttype '"+agentTypeName+"'.", "The agenttype you tried to reload contains errors, therefore it is removed from the repository, but not added again. Please have a look at the other exception-messages for further details.", exc));
		}
	}

	/**
	 * Set the kind of view, in which this project should be shown.
	 * @param view The new view, possible values are definded as constants
     * BASIC_VIEW, EXPERT_VIEW in class Project.
	 */
	public void setView(String view)
	{
        // This needs to be checked, cause through the whole ASCML
        // the view is checked against the constants with == and not with equals (performance reasons)
		if (view.equalsIgnoreCase(Project.BASIC_VIEW))
            this.view = Project.BASIC_VIEW;
        else if (view.equalsIgnoreCase(Project.EXPERT_VIEW))
            this.view = Project.EXPERT_VIEW;

		throwProjectChangedEvent(new ProjectChangedEvent(ProjectChangedEvent.VIEW_CHANGED, this));
	}
	
	/**
	 * Removes a remoteRepository from the local repository.
	 */
	public String getView()
	{
		return this.view;
	}
	
	/**
	 * Removes a remoteRepository from the local repository.
	 * @param dir  The new working-directory
	 */
	public void setWorkingDirectory(String dir)
	{
		// dir might be a path name or a fileName (for convenience)
		// in case dir is a fileName, it's path has to be extracted
		try
		{
			File newPath = new File(dir);
			if (newPath.isFile())
				this.workingDirectory =	newPath.getCanonicalPath().substring(0, newPath.getCanonicalPath().lastIndexOf(File.separatorChar));
			else
				this.workingDirectory =	newPath.getCanonicalPath();
			throwProjectChangedEvent(new ProjectChangedEvent(ProjectChangedEvent.WORKING_DIRECTORY_CHANGED, this));
		}
		catch(IOException exc)
		{
			// nothing to do
		}
	}
	
	/**
	 * Removes a remoteRepository from the local repository.
	 * @return
	 */
	public String getWorkingDirectory()
	{
		return this.workingDirectory;
	}
	
	/**
	 * Set the name of this project.
	 * @param name  The name of this project
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Get the name of this project
	 * @return Name of this project as String
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Initialize this project. Initializing means, remove the model-locations
	 * from the temporaryModelLocation-Vectors and load the real model-objects.
	 */
	public void init(ModelManager modelManager) throws ModelException
	{
		// System.err.println("Project.init: initializing Project, loading models ...");

		// this exception is only thrown when some exceptions occured (exceptionsOccured == true)
		// In this case, all exceptions are attached to this mainException-object.
		ModelException mainException = new ModelException("Errors occured while loading the Project named '"+getName()+"'", "The Project named '"+getName()+"' has been initialized, but not all elements (agent- and societytypes) contained in this project could be loaded. For example: there may have been an agent- or societytype, which description-file (e.g. XML-file) misses some mandatory description-elements and without these the agent- or societytype cannot be loaded into the repository. So have a look in your description-files and correct the problem.");

		this.modelManager = modelManager;

		Iterator iter = temporaryAgentTypesCache.keySet().iterator();
		while (iter.hasNext())
		{
			String oneLocation = (String)iter.next();
			// System.err.println("Project.init(): Load agent from " + oneLocation);
			try
			{
				addAgentType((String)oneLocation);
			}
			catch(ModelException me)
			{
                mainException.addNestedException(me);
			}
		}
        temporaryAgentTypesCache.clear();

        iter = temporarySocietyTypesCache.keySet().iterator();
		while (iter.hasNext())
		{
			String oneLocation = (String)iter.next();
			// System.err.println("Project.init(): Load society from " + oneLocation);
			try
			{
				addSocietyType((String)oneLocation);
			}
			catch(Exception exc)
			{
                mainException.addNestedException(exc);
			}
		}
		temporarySocietyTypesCache.clear();
		if (mainException.hasNestedExceptions())
			throw mainException;
	}
	
	/**
	 * Has this project already been initialized ? A project is initialized when
	 * all model-mappings have been done
	 * If a project is not initialized, not all mappings from model-objects to 
	 * source-locations are resolved.
	 */
	public boolean isInitialized()
	{
		return (this.temporarySocietyTypesCache.isEmpty() && temporaryAgentTypesCache.isEmpty() && (modelManager != null));
	}

	private void throwProjectChangedEvent(ProjectChangedEvent event)
	{
		// System.err.println("Project.throwProjectChangedEvent: event=" + event.getEventCode());

        Vector projectListener = repository.getListenerManager().getProjectListener();
		for (int i=0; i < projectListener.size(); i++)
		{
			((ProjectListener)projectListener.elementAt(i)).projectChanged(event);
		}
	}

	public String toString()
	{
		return getName();
	}
}
