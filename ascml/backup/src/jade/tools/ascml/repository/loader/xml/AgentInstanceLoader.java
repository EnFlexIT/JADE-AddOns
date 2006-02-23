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
import java.util.HashMap;

import jade.tools.ascml.model.*;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.exceptions.ModelException;
import org.w3c.dom.*;

/**
 *  Class for loading agent instances from xml.
 */
public class AgentInstanceLoader
{
	public final static String TAG_PARAMETERVALUE = "parametervalue";
	public final static String TAG_PARAMETERSET   = "parameterset";
	public final static String TAG_TOOLOPTION     = "tooloption";
	public final static String TAG_PROPERTY       = "property";
	public final static String TAG_DEPENDENCY     = "dependency";
	public final static String TAG_VALUE          = "value";
	
	public final static String ATTRIBUTE_NAME			= "name";
	public final static String ATTRIBUTE_TYPE			= "type";
	public final static String ATTRIBUTE_ENABLED		= "enabled";
	public final static String ATTRIBUTE_QUANTITY		= "quantity";
	public final static String ATTRIBUTE_NAMINGSCHEME	= "namingscheme";

	private final static boolean debug = false;

	/**
	 * 
	 */
	public static synchronized IAgentInstance getModel(Element root, ISocietyType societyModel, Vector modelChangedListener, ModelException rootException)
	{
		AgentInstanceModel model = new AgentInstanceModel(modelChangedListener);
		NamedNodeMap attributes = root.getAttributes();

		// ModelException me = new ModelException("Missing mandatory agentinstance-attributes", "An agentinstance has to be provided with special attributes (e.g. a name and a type, ...) in order to be loaded into the ASCML's repository.", ModelException.ERRORCODE_ATTRIBUTES_MISSING);

		if ((attributes.getNamedItem(ATTRIBUTE_NAME) == null) || (attributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim().equals("")))
		{
			rootException.addExceptionDetails("Please provide a name for all agentinstances !", "Each agentinstance has to be provided with a name to uniquely identify this instance throughout the ASCML. For example: in an XML-file, the agentinstance has to have an attribute 'name=\"MyAgentInstanceName\".");
		}
		else
			model.setName(attributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim());

		// note: this type-attribut may not be a fully qualified name
		if ((attributes.getNamedItem(ATTRIBUTE_TYPE) == null) || (attributes.getNamedItem(ATTRIBUTE_TYPE).getNodeValue().trim().equals("")))
		{
			if (model.getName() != null)
				rootException.addExceptionDetails("Please provide a type for the agentinstance named '"+model.getName()+"' !", "Each agentinstance has to be provided with an agenttype, on which this instance relies. For example: in an XML-file, the agentinstance has to have an attribute 'type=\"AgentTypeName\".");
			else
				rootException.addExceptionDetails("Please provide a type for all agentinstances !", "Each agentinstance has to be provided with an agenttype, on which this instance relies. For example: in an XML-file, the agentinstance has to have an attribute 'type=\"AgentTypeName\".");
		}
		else
			model.setTypeName(attributes.getNamedItem(ATTRIBUTE_TYPE).getNodeValue().trim());

		if (attributes.getNamedItem(ATTRIBUTE_QUANTITY) != null)
			model.setQuantity(attributes.getNamedItem(ATTRIBUTE_QUANTITY).getNodeValue().trim());

		if (attributes.getNamedItem(ATTRIBUTE_NAMINGSCHEME) != null)
			model.setNamingScheme(attributes.getNamedItem(ATTRIBUTE_NAMINGSCHEME).getNodeValue().trim());

		NodeList nodes = root.getChildNodes();

		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node oneNode = nodes.item(i);
			if(oneNode.getNodeName().equals(TAG_PARAMETERVALUE))
			{
				initParameter(oneNode, model, rootException);
			}
			else if(oneNode.getNodeName().equals(TAG_PARAMETERSET))
			{
				initParameterSet(oneNode, model, rootException);
			}
			else if(oneNode.getNodeName().equals(TAG_TOOLOPTION))
			{
				initToolOption(oneNode, model, rootException);
			}
			else if(oneNode.getNodeName().equals(TAG_DEPENDENCY))
			{
				initDependency(oneNode, model, rootException);
			}
		}

		return model;
	}
		
	private static void initParameter(Node oneNode, IAgentInstance model, ModelException rootException)
	{
		NamedNodeMap paramNodeAttributes = oneNode.getAttributes();

		String paramName;

		if ((paramNodeAttributes.getNamedItem(ATTRIBUTE_NAME) == null) || (paramNodeAttributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim().equals("")))
		{
			rootException.addExceptionDetails("Please provide a name for the parameterset !", "Each parameterset has to be provided with a name. For example: in an XML-file, the parameter-tag has to have an attribute 'name=\"MyParameterName\".");
			paramName = IAgentInstance.NAME_UNKNOWN;
		}
		else
			paramName = paramNodeAttributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim();
		
		Node oneValueNode = oneNode.getFirstChild();
		if (oneValueNode == null)
			rootException.addExceptionDetails("Please provide a value for the parameter named '" + paramName + "' !", "Each parameter has to have a value (otherwise the parameter will not be passed on to the agent at startup). For example: in an XML-file, the parameter-tag has to have one child-tag like '<value>OneParameterSetValue</value>'.");

		model.setParameterValue(paramName, oneValueNode.getNodeValue());
	}

	private static void initParameterSet(Node oneNode, IAgentInstance model, ModelException rootException)
	{
		NamedNodeMap paramNodeAttributes = oneNode.getAttributes();

		String paramName;
		if ((paramNodeAttributes.getNamedItem(ATTRIBUTE_NAME) == null) || (paramNodeAttributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim().equals("")))
		{
			rootException.addExceptionDetails("Please provide a name for the parameterset !", "Each parameterset has to be provided with a name. For example: in an XML-file, the parameter-tag has to have an attribute 'name=\"MyParameterName\".");
			paramName = IAgentInstance.NAME_UNKNOWN;
		}
		else
			paramName = paramNodeAttributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim();

		NodeList valueNodes = oneNode.getChildNodes();
		for(int j = 0; j < valueNodes.getLength(); j++)
		{
			Node oneValueNode = valueNodes.item(j);

			if (oneValueNode.getNodeName().equals(TAG_VALUE))
			{
				Node oneValue = oneValueNode.getFirstChild();
				model.addParameterSetValue(paramName, oneValue.getNodeValue());
			}
		}

		if (valueNodes.getLength() == 0)
			rootException.addExceptionDetails("Please provide a set of values for the parameterset !", "Each parameterset has to have a set of values (otherwise the parameter-set will not be passed on to the agent at startup). For example: in an XML-file, the parameterset-tag has to have child tags like '<value>OneParameterSetValue</value>'.");
	}

	private static void initToolOption(Node oneNode, IAgentInstance model, ModelException rootException)
	{
		NamedNodeMap toolOptionAttributes = oneNode.getAttributes();
		String toolOptionType = null;

		// check if the tooloption is enabled. If enabled == false, don't process this tooloption any further and return
		if ((toolOptionAttributes.getNamedItem(ATTRIBUTE_ENABLED) != null) && (!toolOptionAttributes.getNamedItem(ATTRIBUTE_ENABLED).getNodeValue().trim().equals("")))
		{
			String enabledString = toolOptionAttributes.getNamedItem(ATTRIBUTE_ENABLED).getNodeValue().trim();
			if (new Boolean(enabledString).booleanValue() == false)
				return;
		}

		if ((toolOptionAttributes.getNamedItem(ATTRIBUTE_TYPE) == null) || (toolOptionAttributes.getNamedItem(ATTRIBUTE_TYPE).getNodeValue().trim().equals("")))
		{
			rootException.addExceptionDetails("A tooloption misses a type-attribute.", "A tooloption misses a type-attribute. The type-attribute is used for identifying the tool, that should be started together with this agentinstance. Please provide the tool-option tag with an attribute 'type=\"someType\', where someType is one of the following types: 'benchmark', 'sniffer', 'debug', 'log'.");
		}
		else
			toolOptionType = toolOptionAttributes.getNamedItem(ATTRIBUTE_TYPE).getNodeValue().trim();

		if (oneNode.hasChildNodes())
		{
			HashMap<String,Vector<String>> propertyMap = new HashMap<String,Vector<String>>();

			NodeList propertyNodes = oneNode.getChildNodes();
			for(int j = 0; j < propertyNodes.getLength(); j++)
			{
				Node onePropertyNode = propertyNodes.item(j);

				if (onePropertyNode.getNodeName().equalsIgnoreCase(TAG_PROPERTY))
				{
					NamedNodeMap toolOptionPropertyAttributes = onePropertyNode.getAttributes();
					String propertyName = "";
					String propertyValue = "";
					if ((toolOptionPropertyAttributes.getNamedItem(ATTRIBUTE_NAME) == null) || (toolOptionPropertyAttributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim().equals("")))
					{
						rootException.addExceptionDetails("A tooloption-property misses a name.", "Each tooloption-property has to have a name to access this property later on. Please provide an attribute 'name=\"myPropertyName\"' in the tooloption-tag.");
					}
					else
					{
						propertyName = toolOptionPropertyAttributes.getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim();
						propertyValue = onePropertyNode.getFirstChild().getNodeValue();

						if (! propertyMap.containsKey(propertyName))
							propertyMap.put(propertyName, new Vector<String>());

						propertyMap.get(propertyName).add(propertyValue);
					}
				}
			}
			model.addToolOption(toolOptionType, propertyMap);
		}
		else
		{
		    model.addToolOption(toolOptionType, null);
		}
	}

	private static void initDependency(Node oneNode, IAgentInstance model, ModelException rootException)
	{
		IDependency dependencyModel = DependencyTypeLoader.getModel((Element)oneNode, model.getName(), rootException);
		// System.err.println("AgentInstanceModel.initDependency: " + dependencyModel.toFormattedString());
		model.addDependency(dependencyModel);
	}
}