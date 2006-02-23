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


package jade.tools.ascml.repository.loader.xml;

import java.util.Vector;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.model.*;
import jade.tools.ascml.repository.*;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.exceptions.ResourceNotFoundException;
import jade.tools.ascml.exceptions.ASCMLException;
import org.w3c.dom.*;

/**
 *  The class for loading a scenario from xml-dom.
 */
public class SocietyTypeLoader
{
	public final static String TAG_SOCIETY      	= "society";
	public final static String TAG_IMPORTS      	= "imports";
	public final static String TAG_IMPORT       	= "import";
	public final static String TAG_AGENTTYPES   	= "agenttypes";
	public final static String TAG_AGENTTYPE    	= "agenttype";
	public final static String TAG_SOCIETYTYPES 	= "societytypes";
	public final static String TAG_SOCIETYTYPE  	= "societytype";
	public final static String TAG_SOCIETYINSTANCES	= "societyinstances";
	public final static String TAG_SOCIETYINSTANCE	= "societyinstance";
	
	public final static String ATTRIBUTE_NAME        = "name";
	public final static String ATTRIBUTE_DESCRIPTION = "description";
	public final static String ATTRIBUTE_PACKAGE     = "package";
	public final static String ATTRIBUTE_DEFAULT     = "default";
	public final static String ATTRIBUTE_ICON        = "icon";

	private static Object sourceName;
	private static Repository repository;

	/**
	 * 
	 */
	public static synchronized ISocietyType getModel(Element root, Object srcName, Repository rep) throws ModelException, ResourceNotFoundException
	{
		repository = rep;
		sourceName = srcName;

		ISocietyType model = new SocietyTypeModel(repository.getListenerManager().getModelChangedListener(), repository.getListenerManager().getLongTimeActionStartListener());
		IDocument doc = new DocumentModel();
		doc.setSource(sourceName);
		model.setDocument(doc);

		ModelException me = new ModelException("Some mandatory attributes are missing.", "The societytype description could not be correctly parsed, because some mandatory attributes are missing. Please have a look at the other exception-messages for further details.", ModelException.ERRORCODE_ATTRIBUTES_MISSING);
		me.setUserObject(model);

		String societyTypeName = null;
		String packageName = null;
		String description = null;
		String iconName = null;

		NamedNodeMap societyNodeAttributes = root.getAttributes();
		for (int i=0; i < societyNodeAttributes.getLength(); i++)
		{
			Node oneAttributeNode = societyNodeAttributes.item(i);

			String attributeName = oneAttributeNode.getNodeName().toLowerCase();
			String attributeValue = oneAttributeNode.getNodeValue().trim();

            if (attributeName.equals(ATTRIBUTE_NAME))
				societyTypeName = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_PACKAGE))
				packageName = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_DESCRIPTION))
				description = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_ICON))
				iconName = attributeValue;
		}

		if (societyTypeName == null)
			me.addExceptionDetails("The name-attribute of the societytype is missing.", "Each societytype has to be provided with a name to uniquely identify this society throughout the ASCML. For example: in an XML-file, the societytype-tag has to have an attribute 'name=\"MySocietyTypeName\".");
		if (packageName == null)
		{
		    if (societyTypeName == null)
				me.addExceptionDetails("The package-attribute of a societytype is missing.", "Each societytype has to be provided with a package-name to uniquely identify this society throughout the ASCML. For example: in an XML-file, the societytype-tag has to have an attribute 'package=\"my.package\".");
			else
				me.addExceptionDetails("The package-attribute of societytype '"+societyTypeName+"' is missing.", "Each societytype has to be provided with a package-name to uniquely identify this society throughout the ASCML. For example: in an XML-file, the societytype-tag has to have an attribute 'package=\"my.package\".");
		}

		model.setName(societyTypeName);
		doc.setPackageName(packageName);
		model.setDescription(description);
		model.setIcon(ImageIconLoader.createImageIcon(iconName));

		// search for agenttypes, societytypes and societyinstances
		NodeList nodes = root.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node oneNode = nodes.item(i);
			String nodeName = oneNode.getNodeName().toLowerCase();

			if(nodeName.equals(TAG_IMPORTS))
			{
				initImports(oneNode, model, me);
			}
			else if(nodeName.equals(TAG_AGENTTYPES))
			{
				initAgentTypes(oneNode, model, me);
			}
			else if(nodeName.equals(TAG_SOCIETYTYPES))
			{
				initSocietyTypes(oneNode, model, me);
			}
			else if(nodeName.equals(TAG_SOCIETYINSTANCES))
			{
				initSocietyInstances(oneNode, model, me);
			}
		}

		// if flag is set throw the Exception
		if (me.hasExceptionDetails() || me.hasNestedExceptions())
			throw me;

		return model;
	}
	
	private static void initImports(Node oneNode, ISocietyType model, ModelException me)
	{
		NodeList nodes = oneNode.getChildNodes();

		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node oneImport = nodes.item(i);

			if(oneImport.getNodeName().equals(TAG_IMPORT))
			{
				Node importNode = oneImport.getFirstChild();
				model.getDocument().addImport(importNode.getNodeValue());
			}
		}
	}

	private static void initAgentTypes(Node oneNode, ISocietyType model, ModelException me)
	{
		NodeList nodes = oneNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node oneAgentType = nodes.item(i);

			if(oneAgentType.getNodeName().equals(TAG_AGENTTYPE))
			{
				NamedNodeMap attribs = oneAgentType.getAttributes();
				
				String name = attribs.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim();

				// cut off xml-ending, it's not neccessary
				if (name.endsWith(".agent.xml"))
				{
					name = name.substring(0, name.indexOf(".agent.xml"));
				}

				// set the models name, but not the model-object. This is set later on.
				model.addAgentType(name, null);
			}
		}
	}
	
	private static void initSocietyTypes(Node oneNode, ISocietyType model, ModelException me)
	{
		NodeList nodes = oneNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node oneSocietyType = nodes.item(i);

			if(oneSocietyType.getNodeName().equals(TAG_SOCIETYTYPE))
			{
				NamedNodeMap attribs = oneSocietyType.getAttributes();

				String name = attribs.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim();

				/* SocietyTypes declared in a society must not have a '.society.xml'-suffix
				   so this is checked here and in case the qualified model-name has in fact no
				   such suffix, it is added */

				// cut off xml-ending, it's not neccessary
				if (name.endsWith(".society.xml"))
				{
					name = name.substring(0, name.indexOf(".society.xml"));
				}

				ISocietyType societyModel = null;
                // toDo: check if it's necessary to load models from repository in this stage
				try
				{
					// try to get the model by it's name written in the xml-file
					societyModel = repository.getSocietyType(name, false);
					if (societyModel == null)
					{
						// try to get the model by it's name with the society-package as prefix
						societyModel = repository.getSocietyType(model.getDocument().getPackageName() + "." + name, false);

						if (societyModel == null)
						{
							// try to get the model with one of the import-statements as prefix
							String[] imports = model.getDocument().getImports();
							for (int j=0; j < imports.length; j++)
							{
								societyModel = repository.getSocietyType(imports[j] + "." + name, false);
								if (societyModel != null)
									break; // ... out of for-loop
							}

							if (societyModel == null)
							{
								me.addNestedException(new ModelException("Error while parsing '"+sourceName+"'. The SocietyModel named '"+name+"' could not be found !", "The societyModel named '"+name+"' could not be found."));
							}
						}
					}
				}
				catch(ModelException exc)
				{
					// this ModelException may be thrown when loading the SocietyTypeModels from the repository.
					me.addNestedException(exc);
				}

				model.addSocietyType(name, societyModel);
			}
		}
	}
	
	private static void initSocietyInstances(Node oneNode, ISocietyType model, ModelException me)
	{
		String defaultSocietyInstance = null;

		NamedNodeMap societyInstanceAttributes = oneNode.getAttributes();
		for (int i=0; i < societyInstanceAttributes.getLength(); i++)
		{
			Node oneAttributeNode = societyInstanceAttributes.item(i);

			String attributeName = oneAttributeNode.getNodeName().toLowerCase();
			String attributeValue = oneAttributeNode.getNodeValue().trim();

            if (attributeName.equals(ATTRIBUTE_NAME))
				defaultSocietyInstance = attributeValue;

		}

		model.setDefaultSocietyInstance(defaultSocietyInstance);

		NodeList socInstNodes = oneNode.getChildNodes();

		// process societyinstance-nodes
		for(int j = 0; j < socInstNodes.getLength(); j++)
		{
			Node oneSocInstNode = socInstNodes.item(j);

			if(oneSocInstNode.getNodeName().equals(TAG_SOCIETYINSTANCE))
			{
				// let the societyinstance-model be loaded by the SocietyInstanceLoader
				SocietyInstanceLoader socInstLoader = new SocietyInstanceLoader(repository.getListenerManager().getModelChangedListener());
				ISocietyInstance socInstModel = null;

				socInstModel = socInstLoader.getModel((Element)oneSocInstNode, model, repository, me);

				socInstModel.setParentSocietyType(model);
				model.addSocietyInstance(socInstModel);
			}
		}
	}
}
