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
import jade.tools.ascml.model.dependency.*;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.exceptions.ModelException;
import org.w3c.dom.*;

/**
 *  Load dependecy elements from xml-dom.
 */
public class DependencyTypeLoader
{
	public final static String TAG_AGENTTYPE 		= "agenttype";
	public final static String TAG_AGENTINSTANCE	= "agentinstance";
	public final static String TAG_SERVICE   		= "service";
	public final static String TAG_SOCIETYINSTANCE  = "societyinstance";
	public final static String TAG_SOCIETYTYPE  	= "societytype";
	public final static String TAG_DELAY   			= "delay";
	public final static String TAG_PROVIDER  		= "provider";
	public final static String TAG_ADDRESS   		= "address";
	public final static String TAG_PROTOCOL   		= "protocol";
	public final static String TAG_ONTOLOGY   		= "ontology";
	public final static String TAG_LANGUAGE   		= "language";
	public final static String TAG_PROPERTY   		= "property";

	public final static String ATTRIBUTE_ACTIVE				= "active";
	public final static String ATTRIBUTE_NAME				= "name";
	public final static String ATTRIBUTE_QUANTITY			= "quantity";
	public final static String ATTRIBUTE_STATUS				= "status";
	public final static String ATTRIBUTE_TYPE				= "type";
	public final static String ATTRIBUTE_OWNERSHIP			= "ownership";
	public final static String ATTRIBUTE_SOCIETYINSTANCE	= "societyinstance";
	public final static String ATTRIBUTE_SOCIETYTYPE		= "societytype";

	private DependencyTypeLoader()
	{
	}

	/**
	 * 
	 */
	public synchronized static AbstractDependencyModel getModel(Element root, String parentModel, ModelException rootException)
	{
        boolean active = false;

		// check if this is an active dependency
		NamedNodeMap attributes = root.getAttributes();
		if (attributes != null)
		{
			if (attributes.getNamedItem(ATTRIBUTE_ACTIVE) != null)
				active = Boolean.valueOf(attributes.getNamedItem(ATTRIBUTE_ACTIVE).getNodeValue().trim()).booleanValue();
		}

		AbstractDependencyModel returnModel = null;

		// now process the subnode, depending on the node-name, a different dependency-model is created and returned
		NodeList nodes = root.getChildNodes();
		for (int i=0; (i < nodes.getLength()) && (returnModel == null); i++)
		{
		    Node oneNode = nodes.item(i);
			if(oneNode.getNodeName().equals(TAG_AGENTTYPE))
			{
				returnModel = initAgentType(oneNode, rootException);
			}
			else if(oneNode.getNodeName().equals(TAG_AGENTINSTANCE))
			{
				returnModel = initAgentInstance(oneNode, rootException);
			}
			else if(oneNode.getNodeName().equals(TAG_SERVICE))
			{
				returnModel = initService(oneNode, rootException);
			}
			else if(oneNode.getNodeName().equals(TAG_SOCIETYINSTANCE))
			{
				returnModel = initSocietyInstance(oneNode, rootException);
			}
			else if(oneNode.getNodeName().equals(TAG_SOCIETYTYPE))
			{
				returnModel = initSocietyType(oneNode, rootException);
			}
			else if(oneNode.getNodeName().equals(TAG_DELAY))
			{
				returnModel = initDelay(oneNode, rootException);
			}
		} // end of for

		if (returnModel != null)
			returnModel.setActive(active);
		else
			rootException.addExceptionDetails("Unknown dependencyType defined by '"+parentModel+"'.", "At least one dependency-type is unknown. Please check the spelling in the appropiate description file. Possible dependency-types are: 'agentinstance',, 'agenttype, 'societyinstance', 'societytype', 'service' and 'delay'.");
		return returnModel;
	}
	
	/**
	 *  Process an <agentinstance>-dependency-Node.
	 */
	private static AgentInstanceDependencyModel initAgentInstance(Node oneNode, ModelException rootException)
	{
		String name = null;
		String status = null;
		ProviderModel provider = null;

		// ModelException me = new ModelException("Error while parsing an agentinstance-dependency.", "One agentinstance-dependency could not be parsed, because some mandatory attributes are missing. Please have a look at the other exception-messages for further details.");
		NamedNodeMap agentNodeAttributes = oneNode.getAttributes();
		for (int i=0; i < agentNodeAttributes.getLength(); i++)
		{
			Node oneAttributeNode = agentNodeAttributes.item(i);

			String attributeName = oneAttributeNode.getNodeName().toLowerCase();
			String attributeValue = oneAttributeNode.getNodeValue().trim();

            if (attributeName.equals(ATTRIBUTE_NAME))
				name = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_STATUS))
				status = attributeValue;
		}

		if (name == null)
			rootException.addExceptionDetails("The name-attribute of an agentinstance-dependency is missing.", "Each agentinstance-dependency has to be provided with the name of the agentinstance, that has to be present in order for this dependency to hold.");

		// process the provider (if one exits)
		provider = initProvider(oneNode, rootException);

		return new AgentInstanceDependencyModel(name, status, provider);
	}

	private static AgentTypeDependencyModel initAgentType(Node oneNode, ModelException rootException)
	{
		// ModelException me = new ModelException("Error while parsing an agenttype-dependency.", "One agenttype-dependency could not be parsed, because some mandatory attributes are missing. Please have a look at the other exception-messages for further details.");
        String name = null;
		String quantity = null;

		NamedNodeMap agentNodeAttributes = oneNode.getAttributes();
		for (int i=0; i < agentNodeAttributes.getLength(); i++)
		{
			Node oneAttributeNode = agentNodeAttributes.item(i);

			String attributeName = oneAttributeNode.getNodeName().toLowerCase();
			String attributeValue = oneAttributeNode.getNodeValue().trim();

            if (attributeName.equals(ATTRIBUTE_NAME))
				name = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_QUANTITY))
				quantity = attributeValue;
		}

		if (name == null)
			rootException.addExceptionDetails("The name-attribute is missing.", "Each agenttype-dependency has to be provided with the name of the agenttype, which has to be present in order for this dependency to hold.");

		return new AgentTypeDependencyModel(name, quantity);
	}

	private static ServiceDependencyModel initService(Node oneNode, ModelException rootException)
	{
		String name				= null;
		String type				= null;
		String ownership		= null;
		ProviderModel provider	= null;

		// ModelException me = new ModelException("Error while parsing a societyinstance-dependency.", "One societyinstance-dependency could not be parsed, because some mandatory attributes are missing. Please have a look at the other exception-messages for further details.");
		NamedNodeMap serviceNodeAttributes = oneNode.getAttributes();
		for (int i=0; i < serviceNodeAttributes.getLength(); i++)
		{
			Node oneAttributeNode = serviceNodeAttributes.item(i);

			String attributeName = oneAttributeNode.getNodeName().toLowerCase();
			String attributeValue = oneAttributeNode.getNodeValue().trim();

			if (attributeName.equals(ATTRIBUTE_NAME))
				name = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_TYPE))
				type = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_OWNERSHIP))
				ownership = attributeValue;
		}

		if (name == null)
        	rootException.addExceptionDetails("The service-dependency misses the 'name'-attribute.", "Each service-dependency has to be provided with the name of the service, which has to be present in order for this dependency to hold. Please specify an attribute 'name=\"MyServiceName\"' within the <service...>-tag of the dependency !");
		if (type == null)
			rootException.addExceptionDetails("The service-dependency misses the 'type'-attribute.", "Each service-dependency has to be provided with the type of service, which has to be present in order for this dependency to hold. Please specify an attribute 'type=\"MyServiceType\"' within the <service...>-tag of the dependency !");

		// process the provider
		provider = initProvider(oneNode, rootException);

		ServiceDependencyModel serviceDependencyModel = new ServiceDependencyModel(name, type, ownership, provider);

		// process optional protocols, languages, ontologies and properties
		NodeList subNodes = oneNode.getChildNodes();
		for(int i = 0; i < subNodes.getLength(); i++)
		{
			Node oneSubNode = subNodes.item(i);
			Node oneValueNode = oneSubNode.getFirstChild();
			String subNodeName = oneSubNode.getNodeName().toLowerCase();

			if (subNodeName.equals(TAG_PROTOCOL))
				serviceDependencyModel.addProtocol(oneValueNode.getNodeValue());
			else if (subNodeName.equals(TAG_ONTOLOGY))
				serviceDependencyModel.addOntology(oneValueNode.getNodeValue());
			else if (subNodeName.equals(TAG_LANGUAGE))
				serviceDependencyModel.addLanguage(oneValueNode.getNodeValue());
			else if (subNodeName.equals(TAG_PROPERTY))
			{
				String propertyName = null;
				String propertyValue = oneValueNode.getNodeValue();

				// process the name-attribute of the properties
				NamedNodeMap propertyNodeAttributes = oneSubNode.getAttributes();
				for (int j=0; j < propertyNodeAttributes.getLength(); j++)
				{
					Node oneAttributeNode = propertyNodeAttributes.item(j);

					String attributeName = oneAttributeNode.getNodeName().toLowerCase();
					if (attributeName.equals(ATTRIBUTE_NAME))
						propertyName = oneAttributeNode.getNodeValue().trim();
				}

				if (propertyName == null)
					rootException.addExceptionDetails("The service-dependency misses the 'name'-attribute of a property-tag.", "Each property of a service-dependency has to be provided with a property-name. Please specify an attribute 'name=\"MyPropertyName\"' within the <property...>-tag of the service-dependency !");

				serviceDependencyModel.addProperty(propertyName, propertyValue);
			}
		}

		return serviceDependencyModel;
	}
	
	private static SocietyInstanceDependencyModel initSocietyInstance(Node oneNode, ModelException rootException)
	{
		String societyInstanceName	= null;
		String societyTypeName		= null;
		String status				= null;
		ProviderModel provider		= null;

		// ModelException me = new ModelException("Error while parsing a societyinstance-dependency.", "One societyinstance-dependency could not be parsed, because some mandatory attributes are missing. Please have a look at the other exception-messages for further details.");
		NamedNodeMap societyNodeAttributes = oneNode.getAttributes();
		for (int i=0; i < societyNodeAttributes.getLength(); i++)
		{
			Node oneAttributeNode = societyNodeAttributes.item(i);

			String attributeName = oneAttributeNode.getNodeName().toLowerCase();
			String attributeValue = oneAttributeNode.getNodeValue().trim();

			if (attributeName.equals(ATTRIBUTE_SOCIETYINSTANCE))
				societyInstanceName = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_SOCIETYTYPE))
				societyTypeName = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_STATUS))
				status = attributeValue;
		}

		if (societyInstanceName == null)
        	rootException.addExceptionDetails("The 'societyinstance'-attribute is missing.", "Each societyinstance-dependency has to be provided with the name of the societyinstance, which has to be present in order for this dependency to hold. Please specify an attribute 'societyinstance=\"MyInstanceName\"' within the <societyinstance...>-tag of the dependency !");
		if (societyTypeName == null)
			rootException.addExceptionDetails("The 'societytype'-attribute is missing.", "Each societytype-dependency has to be provided with the name of the societytype, that contains the societyinstance, which has to be present in order for this dependency to hold. Please specify an attribute 'societytype=\"MyPackageName.MyTypeName\"' within the <societyinstance...>-tag of the dependency !");

		// process the provider
		provider = initProvider(oneNode, rootException);

		return new SocietyInstanceDependencyModel(societyInstanceName, societyTypeName, status, provider);
	}
	
	private static SocietyTypeDependencyModel initSocietyType(Node oneNode, ModelException rootException)
	{
		String name = null;
		String quantity = null;
		// ModelException me = new ModelException("Error while parsing a societytype-dependency.", "One societytype-dependency could not be parsed, because some mandatory attributes are missing. Please have a look at the other exception-messages for further details.");

		NamedNodeMap societyNodeAttributes = oneNode.getAttributes();
		for (int i=0; i < societyNodeAttributes.getLength(); i++)
		{
			Node oneAttributeNode = societyNodeAttributes.item(i);

			String attributeName = oneAttributeNode.getNodeName().toLowerCase();
			String attributeValue = oneAttributeNode.getNodeValue().trim();

            if (attributeName.equals(ATTRIBUTE_NAME))
				name = attributeValue;
			else if (attributeName.equals(ATTRIBUTE_QUANTITY))
				quantity = attributeValue;
		}

		if (name == null)
			rootException.addExceptionDetails("Societytype-dependency misses the name-attribute.", "Each societytype-dependency has to be provided with the name of the societytype, which has to be present in order for this dependency to hold.");

		return new SocietyTypeDependencyModel(name, quantity);
	}

	private static DelayDependencyModel initDelay(Node oneNode, ModelException rootException)
	{
		String quantity = null;
		// ModelException me = new ModelException("Error while parsing a delay-dependency.", "One delay-dependency could not be parsed, because some mandatory attributes are missing. Please have a look at the other exception-messages for further details.");

		NamedNodeMap delayNodeAttributes = oneNode.getAttributes();
        for (int i=0; i < delayNodeAttributes.getLength(); i++)
		{
			Node oneAttributeNode = delayNodeAttributes.item(i);

			String attributeName = oneAttributeNode.getNodeName().toLowerCase();
			String attributeValue = oneAttributeNode.getNodeValue().trim();

            if (attributeName.equals(ATTRIBUTE_QUANTITY))
				quantity = attributeValue;
		}

		if (quantity == null)
			rootException.addExceptionDetails("Delay-dependency misses the quantity-attribute.", "Each delay-dependency has to be provided with the amount of milliseconds to wait before starting a model after all other specified dependencies hold. Please specify an attribute 'quantity=\"1234\"' within the <delay ...>-tag.");

		return new DelayDependencyModel(quantity);
	}

	private static ProviderModel initProvider(Node oneNode, ModelException rootException)
	{
		String providerName = null;
		Vector providerAddresses = new Vector();

		// look for provider-node
		NodeList possibleProviderNodes = oneNode.getChildNodes();
		for (int i=0; i < possibleProviderNodes.getLength(); i++)
		{
			Node onePossibleProviderNode = possibleProviderNodes.item(i);
			String nodeName = onePossibleProviderNode.getNodeName().toLowerCase();

			if (nodeName.equals(TAG_PROVIDER))
			{
				// provider tag found, now look for name-attribute
				NamedNodeMap providerNodeAttributes = onePossibleProviderNode.getAttributes();
				for (int j=0; j < providerNodeAttributes.getLength(); j++)
				{
					Node oneAttributeNode = providerNodeAttributes.item(j);
					String attributeName = oneAttributeNode.getNodeName().toLowerCase();

					if (attributeName.equals(ATTRIBUTE_NAME))
						providerName = oneAttributeNode.getNodeValue().trim();;
				}

				if (providerName == null)
					rootException.addExceptionDetails("The provider-name is missing.", "A provider has been specified for this dependency, but no name has been given to the provider. This name is needed by the ASCML to send requests to this provider.");

				// process the provider-addresses
				NodeList addressNodes = onePossibleProviderNode.getChildNodes();
				for(int j = 0; j < addressNodes.getLength(); j++)
				{
					Node oneAddressNode = addressNodes.item(j);
					String adressNodeName = oneAddressNode.getNodeName().toLowerCase();

					if (adressNodeName.equals(TAG_ADDRESS))
					{
						Node oneAddressValueNode = oneAddressNode.getFirstChild();
						providerAddresses.add(oneAddressValueNode.getNodeValue());
					}
				}

				if ((providerName != null) && (!providerName.equals("")) && (providerAddresses.size() == 0))
					rootException.addExceptionDetails("The provider-addresses are missing.", "A provider has to have at least one address in order to send messages to it. Please specify the addresses as subtags of the <provider...>-tag in the form of <address>http://127.0.0.1:7789/ascml</address> for example.");

				return new ProviderModel(providerName, providerAddresses);
			}
		} // end of for

		// no provider has been specified
		return null;
	}

}
