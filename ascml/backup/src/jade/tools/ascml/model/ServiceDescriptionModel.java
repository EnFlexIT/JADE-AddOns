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
import jade.tools.ascml.absmodel.IServiceDescription;

/**
 *  Model-object containing all required information about a ServiceDescription
 */
public class ServiceDescriptionModel implements IServiceDescription
{

	private String name;
	private String type;
	private String ownership;
	private String providerName;
	private List providerAddresses;
	private List properties;
	private List protocols;
	private List ontologies;
	private List languages;

	/**
	 *  Instantiate a new model and initialise some variables
	 */
	public ServiceDescriptionModel()
	{
		name				= "";
		type				= "";
		ownership			= "";
		providerName 		= "";
		providerAddresses	= new ArrayList();
		properties			= new ArrayList();
		protocols			= new ArrayList();
		ontologies			= new ArrayList();
		languages			= new ArrayList();
	}

	/**
	 *  Gets the name of this ServiceDescription.
	 *  @return The service's name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Sets the name of this ServiceDescription.
	 *  @param name  The services's name.
	 */
	public void setName(String name)
	{
		if(name == null)
			name = "";
		this.name = name.trim();
	}

	/**
	 *  Gets the type of this ServiceDescription.
	 *  @return The service's type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Sets the type of this ServiceDescription.
	 *  @param type  The services's type.
	 */
	public void setType(String type)
	{
		if (type == null)
			type = "";
		this.type = type.trim();
	}

	/**
	 *  Gets the ownership of this ServiceDescription.
	 *  @return The service's ownership.
	 */
	public String getOwnership()
	{
		return ownership;
	}

	/**
	 *  Sets the ownership of this ServiceDescription.
	 *  @param ownership  The services's ownership.
	 */
	public void setOwnership(String ownership)
	{
		if (ownership == null)
			ownership = "";
		this.ownership = ownership.trim();
	}

	/**
	 *  Gets the name of the provider for the service.
	 *  @return  The name of the provider.
	 */
	public String getProviderName()
	{
		return providerName;
	}

	/**
	 *  Sets the name of the provider for the service.
	 *  @param providerName  The name of the provider.
	 */
	public void setProviderName(String providerName)
	{
		if (providerName == null)
			providerName = "";
		this.providerName = providerName;
	}

	/**
	 *  Gets the addresses of the service-provider.
	 *  @return  A Vector containing the service-provider addresses as Strings.
	 */
	public String[] getProviderAddresses()
	{
		return (String[])providerAddresses.toArray(new String[providerAddresses.size()]);
	}
	
	/**
	 *  Sets the addresses of the service-provider.
	 *  @param addresses  A Vector containing the service-provider addresses as Strings.
	 * /
	public void setProviderAddresses(String[] addresses)
	{
		if(addresses == null)
			addresses = new Vector();
		this.providerAddresses = addresses;
	}*/
	/**
	 *  Add an address.
	 *  @param address The address.
	 */
	public void addProviderAddress(String address)
	{
		this.providerAddresses.add(address);
	}

	/**
	 *  Gets the properties of this service.
	 *  @return  A Vector containing properties as Strings.
	 */
	public String[] getProperties()
	{
		return (String[])properties.toArray(new String[properties.size()]);
	}
	
	/**
	 *  Sets the properties of this service.
	 *  @param properties  A Vector containing properties as Strings.
	 * /
	public void setProperties(String[] properties)
	{
		if (properties == null)
			properties = new Vector();
		this.properties = properties;
	}*/
	/**
	 *  Add a property.
	 *  @param property The property.
	 */
	public void addProperty(String property)
	{
		this.properties.add(property);
	}

	/**
	 *  Gets the protocols the service supports.
	 *  @return  A Vector containing the supported protocols as Strings.
	 */
	public String[] getProtocols()
	{
		return (String[])protocols.toArray(new String[protocols.size()]);
	}
	
	/**
	 *  Sets the protocols the service supports.
	 *  @param protocols  A Vector containing the supported protocols as Strings.
	 * /
	public void setProtocols(String[] protocols)
	{
		if (protocols == null)
			protocols = new Vector();
		this.protocols = protocols;
	}*/
	/**
	 *  Add a protocol.
	 *  @param protocol The protocol.
	 */
	public void addProtocol(String protocol)
	{
		this.protocols.add(protocol);
	}

	/**
	 *  Gets the ontologies the service supports.
	 *  @return  A Vector containing the supported ontologies as Strings.
	 */
	public String[] getOntologies()
	{
		return (String[])ontologies.toArray(new String[ontologies.size()]);
	}
	
	/**
	 *  Sets the ontologies the service supports.
	 *  @param ontologies  A Vector containing the supported ontologies as Strings.
	 * /
	public void setOntologies(String[] ontologies)
	{
		if (ontologies == null)
			ontologies = new Vector();
		this.ontologies = ontologies;
	}*/
	/**
	 *  Add an ontology.
	 *  @param ontology The ontology.
	 */
	public void addOntology(String ontology)
	{
		this.ontologies.add(ontology);
	}

	/**
	 *  Gets the languages this service supports.
	 *  @return  A Vector containing the supported languages as Strings.
	 */
	public String[] getLanguages()
	{
		return (String[])languages.toArray(new String[languages.size()]);
	}
	
	/**
	 *  Sets the languages this service supports.
	 *  @param languages  A Vector containing the supported languages as Strings.
	 * /
	public void setLanguages(String[] languages)
	{
		this.languages = languages;
	}*/

	/**
	 *  Add a language.
	 *  @param language The language.
	 */
	public void addLanguage(String language)
	{
		this.languages.add(language);
	}

	/**
	 *  This methods returns a simple String-representation of this model.
	 */
	public String toString()
	{
		String str = "";
		str += name;
		return str;
	}

	/**
	 *  This method returns a formatted String showing the model.
	 *  @return  formatted String showing ALL information about this model.
	 */
	public String toFormattedString()
	{
		String str = "";
		str += getName() + " type: " + getType() + " ownership: " + getOwnership() + "\n";
		str += "providerName: " + getProviderName() + "\n";
		str += "providerAddresses: " + getProviderAddresses() + "\n";
		str += "properties: " + getProperties() + "\n";
		str += "protocols: " + getProtocols() + "\n";
		str += "ontologies: " + getOntologies() + "\n";
		str += "languages:" + getLanguages() + "\n";
		return str;
	}
}
