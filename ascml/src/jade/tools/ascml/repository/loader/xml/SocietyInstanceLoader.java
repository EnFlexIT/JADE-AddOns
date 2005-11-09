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

import jade.tools.ascml.model.*;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.repository.Repository;
import org.w3c.dom.*;

import java.util.Vector;

/**
 *  The class for loading a scenario from xml-dom.
 */
public class SocietyInstanceLoader
{
	public final static String TAG_AGENTS				= "agentinstances";
	public final static String TAG_AGENT				= "agentinstance";
	public final static String TAG_SOCIETYINSTANCEREF	= "societyinstanceref";
	public final static String TAG_LAUNCHER				= "launcher";
	public final static String TAG_DEPENDENCY			= "dependency";
	public final static String TAG_ADDRESS				= "address";
	public final static String TAG_FUNCTIONAL			= "functional";
	public final static String TAG_INVARIANT			= "invariant";

	public final static String ATTRIBUTE_NAME				= "name";
	public final static String ATTRIBUTE_DESCRIPTION		= "description";
	public final static String ATTRIBUTE_QUANTITY			= "quantity";
	public final static String ATTRIBUTE_NAMINGSCHEME		= "namingscheme";
	public final static String ATTRIBUTE_SOCIETYTYPE		= "societytype";
	public final static String ATTRIBUTE_SOCIETYINSTANCE	= "societyinstance";

	public static String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	public static String SCHEMA_SOURCE = "http://intensivstation.informatik.rwth-aachen.de:8080/schema/society.xsd";

	private Vector modelChangedListener;

	public SocietyInstanceLoader(Vector modelChangedListener)
	{
		this.modelChangedListener = modelChangedListener;
	}

	public ISocietyInstance getModel(Element root, ISocietyType societyModel, Repository repository, ModelException rootException)
	{
		String modelName = null;
        String description = null;
		String quantity = null;
		String namingScheme = null;

		// ModelException me = new ModelException("Missing mandatory societyinstance-attributes", "A societyinstance has to be provided with special attributes (e.g. a name, ...) in order to be loaded into the ASCML's repository.", ModelException.ERRORCODE_ATTRIBUTES_MISSING);
		// me.setUserObject(model);

		NamedNodeMap societyInstanceAttributeNodes = root.getAttributes();
		for (int i=0; i < societyInstanceAttributeNodes.getLength(); i++)
		{
			Node oneAttributeNode = societyInstanceAttributeNodes.item(i);

			String attributeName = oneAttributeNode.getNodeName().toLowerCase();
			String attributeValue = oneAttributeNode.getNodeValue().trim();

			if (attributeName.equals(ATTRIBUTE_NAME))
				modelName = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_DESCRIPTION))
		        description = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_QUANTITY))
		        quantity = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_NAMINGSCHEME))
		        namingScheme = attributeValue;
		}

		if (modelName == null)
			rootException.addExceptionDetails("Name of societyinstance is missing.", "Write me!");

		SocietyInstanceModel model = new SocietyInstanceModel(modelName, description);
		model.setQuantity(quantity);
		model.setNamingScheme(namingScheme);

		NodeList nodes = root.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node oneNode = nodes.item(i);

			String nodeName = oneNode.getNodeName().toLowerCase();
			if(nodeName.equals(TAG_AGENTS))
			{
				NodeList agentNodes = oneNode.getChildNodes();
				for(int j = 0; j < agentNodes.getLength(); j++)
				{
					Node oneAgentNode = agentNodes.item(j);

					if(oneAgentNode.getNodeName().equals(TAG_AGENT))
					{
						IAgentInstance agentModel = AgentInstanceLoader.getModel((Element)oneAgentNode, societyModel, modelChangedListener, rootException);
						model.addAgentInstanceModel(agentModel);
					}
					else if (!(oneAgentNode instanceof Text))
						rootException.addExceptionDetails("Suspicious Tag found in agentinstance-declaration section (node-name:'"+oneAgentNode.getNodeName()+"').", "The declaration-section for agentinstances within this societyinstance contains a tag which could not be identified. Please check the spelling of your agentinstance-tags.");
				}
			}
			else if(nodeName.equals(TAG_SOCIETYINSTANCEREF))
			{
				SocietyInstanceReferenceModel oneReference = initSocietyInstanceReference(oneNode, rootException);
				model.addSocietyInstanceReference(oneReference);
			}
			else if(nodeName.equals(TAG_FUNCTIONAL))
			{
				IFunctional functionalModel = new FunctionalModel();

				NodeList subNodes = oneNode.getChildNodes();
				for(int j = 0; j < subNodes.getLength(); j++)
				{
					Node oneSubNode = subNodes.item(j);
					String subNodeName = oneSubNode.getNodeName().toLowerCase();
					if (subNodeName.equals(TAG_DEPENDENCY))
					{
						IDependency dependencyModel = DependencyTypeLoader.getModel((Element)oneSubNode, model.getName(), rootException);
						functionalModel.addDependency(dependencyModel);
					}
					else if (subNodeName.equals(TAG_INVARIANT))
					{
						String invariant = oneSubNode.getTextContent().trim();
						functionalModel.addInvariant(invariant);
					}
				}
				model.setFunctionalModel(functionalModel);
			}
		}

		return model;
	}

	private SocietyInstanceReferenceModel initSocietyInstanceReference(Node oneNode, ModelException rootException)
	{
		String referenceName = null;
		String typeName = null;
		String instanceName = null;
		String refQuantity = null;
		String refNamingScheme = null;

		NamedNodeMap societyInstanceReferenceAttributes = oneNode.getAttributes();
		for (int j=0; j < societyInstanceReferenceAttributes.getLength(); j++)
		{
			Node oneAttributeNode = societyInstanceReferenceAttributes.item(j);

			String attributeName = oneAttributeNode.getNodeName().toLowerCase();
			String attributeValue = oneAttributeNode.getNodeValue().trim();

			if (attributeName.equals(ATTRIBUTE_NAME))
				referenceName = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_SOCIETYTYPE))
				typeName = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_SOCIETYINSTANCE))
				instanceName = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_QUANTITY))
				refQuantity = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_NAMINGSCHEME))
				refNamingScheme = attributeValue;
		}

		if (referenceName == null)
			rootException.addExceptionDetails("Name of societyinstance-reference missing.", "Write me!");
		if (typeName == null)
		{
			if (referenceName == null)
				rootException.addExceptionDetails("Societytype-name of societyinstance-reference missing.", "Write me!");
			else
				rootException.addExceptionDetails("Societytype-name of societyinstance-reference '"+referenceName+"' missing.", "Write me!");
		}
		if (instanceName == null)
		{
			if (referenceName == null)
				rootException.addExceptionDetails("Societyinstance-name of societyinstance-reference missing.", "Write me!");
			else
				rootException.addExceptionDetails("Societyinstance-name of societyinstance-reference '"+referenceName+"' missing.", "Write me!");
		}

		SocietyInstanceReferenceModel oneReference = new SocietyInstanceReferenceModel(referenceName, typeName, instanceName, modelChangedListener);
		oneReference.setQuantity(refQuantity);
		oneReference.setNamingScheme(refNamingScheme);

		NodeList subNodes = oneNode.getChildNodes();
		for(int j = 0; j < subNodes.getLength(); j++)
		{
			Node oneSubNode = subNodes.item(j);
			String nodeName = oneSubNode.getNodeName().toLowerCase();

			if(nodeName.equals(TAG_LAUNCHER))
			{
				NamedNodeMap launcherAttributes = oneSubNode.getAttributes();
				if ((launcherAttributes != null) &&
					(launcherAttributes.getNamedItem(ATTRIBUTE_NAME) != null) &&
					(!launcherAttributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim().equals("")))
				{
					String launcherName = launcherAttributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue();
					oneReference.setLauncherName(launcherName);
				}
				else
				{
					rootException.addExceptionDetails("The 'name'-attribute of an launcher-tag in the is missing.", "The societyinstance-reference named '"+oneReference.getInstanceName()+"' specifies a launcher and this launcher has to be provided with the name of the agent, which is capable of launching the referenced societyinstance. But there has been no 'name'-attribute within the <launcher...>-tag, so please check your societyinstanceref-specifications in your Society-description-files.");
				}

				// process addressNodes (if any)
				NodeList addressNodes = oneSubNode.getChildNodes();
				for(int k = 0; k < addressNodes.getLength(); k++)
				{
					Node oneAddressNode = addressNodes.item(k);
					if(oneAddressNode.getNodeName().equals(TAG_ADDRESS))
					{
						Node oneAddressTextNode = oneAddressNode.getFirstChild();
						oneReference.addLauncherAddress(oneAddressTextNode.getNodeValue());
					}
				} // end of for...
			}
			else if(nodeName.equals(TAG_DEPENDENCY))
			{
				IDependency dependencyModel = DependencyTypeLoader.getModel((Element)oneSubNode, referenceName, rootException);
				// System.err.println("SocietyInstanceLoader.getModel: add socInstRef-dependency: " + dependencyModel);
				oneReference.addDependency(dependencyModel);
			}
		}

		return oneReference;
	}
}
