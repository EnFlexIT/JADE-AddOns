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

import java.io.*;

import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.exceptions.ResourceNotFoundException;
import jade.tools.ascml.model.jibx.AgentType;
import jade.tools.ascml.model.jibx.SocietyType;
import jade.tools.ascml.model.Document;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.ISocietyType;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.IUnmarshallingContext;

/**
 *  The default model loader creates the default model elements.
 *  This class is instantiated via reflection by the ModelConfiguration
 */
public class JiBXModelFactory extends AbstractModelFactory
{
	/**
	 *  Load an AgentType-model.
	 *  @param xmlFile  The agent-description file.
	 *  @param repository  The Repository-object
	 *  @return The AgentType-model.
	 */
	public AgentType createAgentTypeModel(String xmlFile, Repository repository) throws ModelException, ResourceNotFoundException
	{
		String jarFile = null;
		if(xmlFile.indexOf(".jar::")!=-1)
		{
			jarFile = xmlFile.substring(0, xmlFile.indexOf(".jar::")+4);
			xmlFile = xmlFile.substring(xmlFile.indexOf(".jar::")+6, xmlFile.length());
		}
		else if(xmlFile.indexOf(".zip::")!=-1)
		{
			jarFile = xmlFile.substring(0, xmlFile.indexOf(".zip::")+4);
			xmlFile = xmlFile.substring(xmlFile.indexOf(".zip::")+6, xmlFile.length());
		}

		InputStream modelInputStream = null;
		if(jarFile!=null)
			modelInputStream = getModelInputStreamFromJar(xmlFile, jarFile);
		else
			modelInputStream = getModelInputStream(xmlFile);

		try
		{
			IBindingFactory bfact = BindingDirectory.getFactory(AgentType.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();

			AgentType model = (AgentType)uctx.unmarshalDocument(modelInputStream, null);
			model.setDocument(new Document(sourceName));
			model.setModelChangedListener(repository.getListenerManager().getModelChangedListener());
			model.setStatus(IAgentType.STATUS_OK);

			return model;
		}
		catch (JiBXException e)
		{
			throw new ModelException("Failed to load agenttype-model from '"+xmlFile+"'", e);
		}
	}

	/**
	 *  Load a SocietyType-model.
	 *  @param xmlFile  The society-description file.
	 *  @param repository  The Repository-object
	 *  @return The SocietyType-model.
	 */
	public SocietyType createSocietyTypeModel(String xmlFile, Repository repository) throws ModelException, ResourceNotFoundException
	{
		String jarFile = null;
		if(xmlFile.indexOf(".jar::")!=-1)
		{
			jarFile = xmlFile.substring(0, xmlFile.indexOf(".jar::")+4);
			xmlFile = xmlFile.substring(xmlFile.indexOf(".jar::")+6, xmlFile.length());
		}
		else if(xmlFile.indexOf(".zip::")!=-1)
		{
			jarFile = xmlFile.substring(0, xmlFile.indexOf(".zip::")+4);
			xmlFile = xmlFile.substring(xmlFile.indexOf(".zip::")+6, xmlFile.length());
		}

		InputStream modelInputStream = null;
		if(jarFile!=null)
			modelInputStream = getModelInputStreamFromJar(xmlFile, jarFile);
		else
			modelInputStream = getModelInputStream(xmlFile);

		try
		{
			IBindingFactory bfact = BindingDirectory.getFactory(SocietyType.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();

			SocietyType model = (SocietyType)uctx.unmarshalDocument(modelInputStream, null);
			model.setDocument(new Document(sourceName));
			model.setModelChangedListener(repository.getListenerManager().getModelChangedListener());
			model.setStatus(ISocietyType.STATUS_OK);

			return model;
		}
		catch (JiBXException e)
		{
			throw new ModelException("Failed to load societytype-model from '"+xmlFile+"'", e);
		}
	}
}
