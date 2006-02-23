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


package jade.tools.ascml.repository.loader;

import jade.tools.ascml.gui.components.StatusBar;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.ISocietyType;
import jade.tools.ascml.exceptions.ResourceNotFoundException;

import java.util.zip.*;
import java.util.jar.*;
import java.util.*;
import java.io.*;

/**
 *  This class holds information about all models available.
 *  This means if a model is loaded it's source path is stored here or if an autoSearch
 *  is started all model-sources, that are found are also stored here.
 *  By convention files are named 'MyAgent.agent.xml' and 'MySociety.society.xml'.
 *  todo: fix the model table?! what kind of mapping is done? filename->jarfilename?
 */
public class ModelIndex
{
	/** this Map contains the sourceNames(e.g. xml-fileName) as keys and the modelObjects as values (or null, if model not yet loaded */
	private HashMap models;

	/** This is the time in milliSecs when the last index-refresh has been done */
	private long lastIndexRefresh;

	/**
	 *  Create a new file handler.
	 */
	public ModelIndex()
	{
		this.models				= new HashMap();
		this.lastIndexRefresh	= 0;
	}

	/**
	 * Add a model-source and its model-object to the index.
	 * @param sourceName  The source-name of the model.
	 * @param modelObject  The model-object loaded from the source.
	 */
	public void addModel(String sourceName, Object modelObject)
	{
		models.put(sourceName, modelObject);
		lastIndexRefresh = System.currentTimeMillis();
	}

    /**
	 * Add a model-source to the index (this method is used for example by the 'autosearch').
	 * @param sourceName  The source-name of the model.
	 */
	public void addModel(String sourceName)
	{
		addModel(sourceName, null);
	}

	/**
	 * Get the sources of all models (agent- & societytype), that were loaded by the ASCML during runtime.
	 * @return  An Array containing all source-identifiers of the loaded models.
	 */
	public Object[] getModelSources()
	{
		Object[] returnArray = new Object[models.size()];
		models.keySet().toArray(returnArray);
		return returnArray;
	}

	/**
	 * Get all models (agent- & societytype), that were loaded by the ASCML during runtime.
	 * @return  An Array containing all loaded models.
	 */
	public Object[] getModelObjects()
	{
		Vector dummyVector = new Vector();

		Object[] modelSources = getModelSources();
		for (int i=0; i < modelSources.length; i++)
		{
			Object modelObject = models.get(modelSources[i]);
			if (modelObject != null)
				dummyVector.add(modelObject);
		}

		Object[] returnArray = new Object[dummyVector.size()];
		dummyVector.toArray(returnArray);
		return returnArray;
	}

	/**
	 * Get all societytypes, that were loaded by the ASCML during runtime.
	 * @return  An Array containing all loaded SocietyType-models
	 */
	public ISocietyType[] getSocietyTypeObjects()
	{
		Vector dummyVector = new Vector();

		Object[] modelSources = getModelSources();
		for (int i=0; i < modelSources.length; i++)
		{
			Object modelObject = models.get(modelSources[i]);
			if ((modelObject != null) && (modelObject instanceof ISocietyType))
				dummyVector.add(modelObject);
		}

		ISocietyType[] returnArray = new ISocietyType[dummyVector.size()];
		dummyVector.toArray(returnArray);
		return returnArray;
	}

	/**
	 * Get all agenttypes, that were loaded by the ASCML during runtime.
	 * @return  An Array containing all loaded AgentType-models
	 */
	public IAgentType[] getAgentTypeObjects()
	{
		Vector dummyVector = new Vector();

		Object[] modelSources = getModelSources();
		for (int i=0; i < modelSources.length; i++)
		{
			Object modelObject = models.get(modelSources[i]);
			if ((modelObject != null) && (modelObject instanceof IAgentType))
				dummyVector.add(modelObject);
		}

		IAgentType[] returnArray = new IAgentType[dummyVector.size()];
		dummyVector.toArray(returnArray);
		return returnArray;
	}

	/**
	 * Get a specific model.
	 * @param modelName  Either fully qualified name or sourceName
	 * @return  The model-object with the given name
	 */
	public Object getModel(String modelName)
	{
		Object[] modelObjects = getModelObjects();
		for (int i=0; i < modelObjects.length; i++)
		{
			if ((modelObjects[i] instanceof IAgentType) &&
				(((IAgentType)modelObjects[i]).getFullyQualifiedName().equals(modelName)))
				return modelObjects[i];

			else if ((modelObjects[i] instanceof ISocietyType) &&
				(((ISocietyType)modelObjects[i]).getFullyQualifiedName().equals(modelName)))
				return modelObjects[i];
		}

		if (models.containsKey(modelName) && (models.get(modelName) != null))
			return models.get(modelName);

		return null;
	}

	/**
	 * Get all models (agent- & societytype), that were loaded by the ASCML during runtime.
	 * @return  A HashMap containing all loaded models, key=sourceName value=model-object
	 */
	public HashMap getModels()
	{
		return models;
	}

	/**
	 *  Get the timestamp, when the index has been refreshed.
	 *  @return  timestamp of the last index-refresh in milliseconds.
	 */
	public long getLastIndexRefreshTime()
	{
		return lastIndexRefresh;
	}

	/**
	 * Remove a model-source and its model-object from the index.
	 * @param sourceName  The source-name of the model.
	 */
	public void removeModel(String sourceName)
	{
		if (models.containsKey(sourceName))
			models.remove(sourceName);
		lastIndexRefresh = System.currentTimeMillis();
	}

	public String toString()
	{
		Object[] models = getModelObjects();
		String str = "";
		str += "|--- ModelIndex  --------------------\n";
		if (models.length == 0)
			str += "| empty\n";	
		for (int i=0; i < models.length; i++)
		{
			if (models[i] instanceof ISocietyType)
				str += "| " + ((ISocietyType)models[i]).getFullyQualifiedName() + " ("+((ISocietyType)models[i]).getStatus()+")\n";
			else if (models[i] instanceof IAgentType)
				str += "| " + ((IAgentType)models[i]).getFullyQualifiedName() + " ("+((IAgentType)models[i]).getStatus()+")\n";
		}
		str += "|------------------------------------\n";
		return str;
	}
}
