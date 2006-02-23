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
import jade.tools.ascml.model.*;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import org.w3c.dom.*;

/**
 *  Responsible for loading an agent type.
 */
public class AgentTypeLoader
{
	public final static String TAG_AGENT        = "agent";
	public final static String TAG_PARAMETERS   = "parameters";
	public final static String TAG_PARAMETER    = "parameter";
	public final static String TAG_PARAMETERSET = "parameterset";
	public final static String TAG_VALUE        = "value";
	public final static String TAG_CONSTRAINT   = "constraint";
	
	public final static String ATTRIBUTE_NAME        = "name";
	public final static String ATTRIBUTE_PACKAGE     = "package";
	public final static String ATTRIBUTE_CLASS       = "class";
	public final static String ATTRIBUTE_DESCRIPTION = "description";
	public final static String ATTRIBUTE_TYPE        = "type";
	public final static String ATTRIBUTE_OPTIONAL    = "optional";
	public final static String ATTRIBUTE_ICON        = "icon";

	public static synchronized IAgentType getModel(Element rootNode, Object sourceName, Vector modelChangedListener) throws ModelException
	{
		AgentTypeModel model = new AgentTypeModel(modelChangedListener);

		DocumentModel doc = new DocumentModel();
		doc.setSource(sourceName);
		model.setDocument(doc);

		try
		{
			ModelException me = new ModelException("Some mandatory agenttype-attributes are missing", "An agenttype has to be provided with special attributes (e.g. a name, a package-name, implementation-class, ...) in order to be loaded into the ASCML's repository. Some of these attributes are missing, please have a look the other exception-messages to get more information about missing elements.", ModelException.ERRORCODE_ATTRIBUTES_MISSING);
			boolean throwException = false;

			NamedNodeMap attributes = rootNode.getAttributes();
			if (attributes.getNamedItem(ATTRIBUTE_NAME) == null)
			{
				me.addExceptionDetails("The name-attribute is missing.", "Each agenttype has to be provided with a name to uniquely identify this agent throughout the ASCML. For example: in an XML-file, the agent-tag has to have an attribute 'name=\"MyAgentName\".");
				throwException = true;
			}
			else
				model.setName(attributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue());

			if (attributes.getNamedItem(ATTRIBUTE_PACKAGE) == null)
			{
				me.addExceptionDetails("The package-attribute is missing.", "Each agenttype has to be provided with a package-name to uniquely identify this agent throughout the ASCML. For example: in an XML-file, the agent-tag has to have an attribute 'package=\"jade.tools.MyPackageName\".");
				throwException = true;
			}
			else
				doc.setPackageName(attributes.getNamedItem(ATTRIBUTE_PACKAGE).getNodeValue());

			if (attributes.getNamedItem(ATTRIBUTE_CLASS) == null)
			{
				me.addExceptionDetails("The class-attribute is missing.", "Each agenttype has to be provided with a class-name. The ASCML instantiates this class once an agent shall be started. For example: in an XML-file, the agent-tag has to have an attribute 'class=\"MyAgentClass\".");
				throwException = true;
			}
			else
				model.setClassName(attributes.getNamedItem(ATTRIBUTE_CLASS).getNodeValue());

			if (attributes.getNamedItem(ATTRIBUTE_TYPE) == null)
			{
				me.addExceptionDetails("The (platform)type-attribute is missing. 'JADE' is set as default-type.", "Each agenttype has to be provided with the name of the platform (e.g. 'JADE' or 'Jadex') it is designed for. For example: in an XML-file, the agent-tag has to have an attribute 'type=\"jade\". In case no value has been supplied for the type-attribute the model is initialized with a default value.");
				throwException = true;
			}
			else
				model.setPlatformType(attributes.getNamedItem(ATTRIBUTE_TYPE).getNodeValue());

			if (attributes.getNamedItem(ATTRIBUTE_DESCRIPTION) == null)
				model.setDescription("No description given.");
			else
				model.setDescription(attributes.getNamedItem(ATTRIBUTE_DESCRIPTION).getNodeValue());

			if (attributes.getNamedItem(ATTRIBUTE_ICON) != null)
			{
				String iconPath = attributes.getNamedItem(ATTRIBUTE_ICON).getNodeValue().trim();
				if ((iconPath != null) && !iconPath.trim().equals(""))
				{
					model.setIcon(ImageIconLoader.createImageIcon(iconPath));
				}
			}

			NodeList nodes = rootNode.getChildNodes();
			for (int i=0; i < nodes.getLength(); i++)
			{
				Node parametersNode = nodes.item(i);

				if(parametersNode.getNodeName().equals(TAG_PARAMETERS))
				{
					NodeList parameterNodes = parametersNode.getChildNodes();
					for (int j=0; j < parameterNodes.getLength(); j++)
					{
						Node oneParameterNode = parameterNodes.item(j);
                        try
						{
							if(oneParameterNode.getNodeName().equals(TAG_PARAMETER))
							{
								initParameter(oneParameterNode, model);
							}
							if(oneParameterNode.getNodeName().equals(TAG_PARAMETERSET))
							{
								initParameterSet(oneParameterNode, model);
							}
						}
						catch(ModelException exc)
						{
							me.addNestedException(exc);
							// note: Exception not necessarily needs to be thrown
						}
					}
				}
			}

			// if flag is set throw the Exception (Exception is caught below and wrapped in a general exception)
			if (throwException)
				throw me;

		} catch (Exception e)
		{
			ModelException f = new ModelException("Error while parsing an agenttype-description from '"+sourceName+"'", "There occured an error while parsing an agenttype-description. This possibly means, that one or more data-items (like tags or attributes in XML-files) are missing. Please check the description-source ("+sourceName+").", e);
			f.setUserObject(model);
			throw f;
		}
		return model;
	}
	
	private static void initParameter(Node oneParameterNode, IAgentType model) throws ModelException
	{
		try
		{
			ModelException me = new ModelException("Missing mandatory agenttype-attributes", "An agenttype has to be provided with special attributes (e.g. a name, a package-name, implementation-class, ...) in order to be loaded into the ASCML's repository.", ModelException.ERRORCODE_ATTRIBUTES_MISSING);
			boolean throwException = false;

			NamedNodeMap paramAttribs = oneParameterNode.getAttributes();

			AgentParameter param = new AgentParameter();

			if (paramAttribs.getNamedItem(ATTRIBUTE_NAME) == null)
			{
				me.addExceptionDetails("Please provide a name for the parameter !", "Each parameter has to have a name in order to identify this parameter within the model as well as within the running agent. For example: in an XML-file, the parameter-tag has to have an attribute 'name=\"MyParameterName\".");
				throwException = true;
			}
			else
				param.setName(paramAttribs.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim());

            if (paramAttribs.getNamedItem(ATTRIBUTE_TYPE) == null)
			{
				me.addExceptionDetails("Please provide a type for the parameter !", "Each parameter has to have a type. This type specifies the kind of value (e.g. Integer, String, etc.). For example: in an XML-file, the parameter-tag has to have an attribute 'type=\"Integer\".");
				throwException = true;
			}
			else
				param.setType(paramAttribs.getNamedItem(ATTRIBUTE_TYPE).getNodeValue().trim());

			if (paramAttribs.getNamedItem(ATTRIBUTE_OPTIONAL) != null)
				param.setOptional(paramAttribs.getNamedItem(ATTRIBUTE_OPTIONAL).getNodeValue().trim());

			if (paramAttribs.getNamedItem(ATTRIBUTE_DESCRIPTION) != null)
				param.setDescription(paramAttribs.getNamedItem(ATTRIBUTE_DESCRIPTION).getNodeValue().trim());

			String paramValue = "";
			NodeList valueNodes = oneParameterNode.getChildNodes();
			for(int k = 0; k < valueNodes.getLength(); k++)
			{
				Node oneValueNode = valueNodes.item(k);

				if (oneValueNode.getNodeName().equals(TAG_VALUE))
				{
					Node oneValue = oneValueNode.getFirstChild();
					paramValue = oneValue.getNodeValue();
				}
			}
			param.setValue(paramValue);

			NodeList constraintNodes = oneParameterNode.getChildNodes();
			for(int k = 0; k < constraintNodes.getLength(); k++)
			{
				Node oneValueNode = constraintNodes.item(k);

				if (oneValueNode.getNodeName().equals(TAG_CONSTRAINT))
				{
					Node oneValue = oneValueNode.getFirstChild();
					String value = oneValue.getNodeValue();
					param.addConstraint(value);
				}
			}
			model.addParameter(param);

			// if flag is set throw the Exception (Exception is caught below and wrapped in a general exception)
			if (throwException)
				throw me;

		} catch (Exception e)
		{
			ModelException f = new ModelException("Errors in parameter-specifications", "The parameter(set)s given for this model are not specified correctly. This possibly means, that one or more parameter are lacking mandatory attributes. Please check the description-source.", e);
			f.setUserObject(model);
			throw f;
		}
	}
	
	private static void initParameterSet(Node oneParameterNode, IAgentType model) throws ModelException
	{
		try
		{
			ModelException me = new ModelException("Missing mandatory agenttype-attributes", "An agenttype has to be provided with special attributes (e.g. a name, a package-name, implementation-class, ...) in order to be loaded into the ASCML's repository.", ModelException.ERRORCODE_ATTRIBUTES_MISSING);
			boolean throwException = false;

			NamedNodeMap paramAttribs = oneParameterNode.getAttributes();

			AgentParameterSet param = new AgentParameterSet();

			if (paramAttribs.getNamedItem(ATTRIBUTE_NAME) == null)
			{
				me.addExceptionDetails("Please provide a name for the parameterset !", "Each parameterset has to have a name in order to identify this parameter within the model as well as within the running agent. For example: in an XML-file, the parameterset-tag has to have an attribute 'name=\"MyParameterName\".");
				throwException = true;
			}
			else
				param.setName(paramAttribs.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim());

            if (paramAttribs.getNamedItem(ATTRIBUTE_TYPE) == null)
			{
				me.addExceptionDetails("Please provide a type for the parameterset !", "Each parameterset has to have a type. This type specifies the kind of value (e.g. Integer, String, etc.). For example: in an XML-file, the parameterset-tag has to have an attribute 'type=\"Integer\".");
				throwException = true;
			}
			else
				param.setType(paramAttribs.getNamedItem(ATTRIBUTE_TYPE).getNodeValue().trim());

			if (paramAttribs.getNamedItem(ATTRIBUTE_OPTIONAL) != null)
				param.setOptional(paramAttribs.getNamedItem(ATTRIBUTE_OPTIONAL).getNodeValue().trim());

			if (paramAttribs.getNamedItem(ATTRIBUTE_DESCRIPTION) != null)
				param.setDescription(paramAttribs.getNamedItem(ATTRIBUTE_DESCRIPTION).getNodeValue().trim());

			NodeList valueNodes = oneParameterNode.getChildNodes();
			for(int k = 0; k < valueNodes.getLength(); k++)
			{
				Node oneValueNode = valueNodes.item(k);

				if (oneValueNode.getNodeName().equals(TAG_VALUE))
				{
					Node oneValue = oneValueNode.getFirstChild();
					String value = oneValue.getNodeValue();
					param.addValue(value);
				}
			}

			NodeList constraintNodes = oneParameterNode.getChildNodes();
			for(int k = 0; k < constraintNodes.getLength(); k++)
			{
				Node oneValueNode = constraintNodes.item(k);

				if (oneValueNode.getNodeName().equals(TAG_CONSTRAINT))
				{
					Node oneValue = oneValueNode.getFirstChild();
					String value = oneValue.getNodeValue();
					param.addConstraint(value);
				}
			}

			model.addParameterSet(param);

			// if flag is set throw the Exception (Exception is caught below and wrapped in a general exception)
			if (throwException)
				throw me;

		} catch (Exception e)
		{
			ModelException f = new ModelException("Errors in parameterset-specifications", "The parameter(set)s given for this model are not specified correctly. This possibly means, that one or more parameter are lacking mandatory attributes. Please check the description-source.", e);
			throw f;
		}
	}
}