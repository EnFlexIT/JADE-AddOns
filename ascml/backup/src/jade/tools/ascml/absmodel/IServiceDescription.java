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


package jade.tools.ascml.absmodel;

/**
 *  The FIPA service description.
 */
public interface IServiceDescription
{
	/**
	 *  Gets the name of this ServiceDescription.
	 *  @return The service's name.
	 */
	public String getName();

	/**
	 *  Sets the name of this ServiceDescription.
	 *  @param name  The services's name.
	 */
	public void setName(String name);

	/**
	 *  Gets the type of this ServiceDescription.
	 *  @return The service's type.
	 */
	public String getType();

	/**
	 *  Sets the type of this ServiceDescription.
	 *  @param type  The services's type.
	 */
	public void setType(String type);

	/**
	 *  Gets the ownership of this ServiceDescription.
	 *  @return The service's ownership.
	 */
	public String getOwnership();

	/**
	 *  Sets the ownership of this ServiceDescription.
	 *  @param ownership  The services's ownership.
	 */
	public void setOwnership(String ownership);

	/**
	 *  Gets the name of the provider for the service.
	 *  @return  The name of the provider.
	 */
	//public String getProviderName();

	/**
	 *  Sets the name of the provider for the service.
	 *  @param providerName  The name of the provider.
	 */
	//public void setProviderName(String providerName);

	/**
	 *  Gets the addresses of the service-provider.
	 *  @return  A Vector containing the service-provider addresses as Strings.
	 */
	//public String[] getProviderAddresses();

	/**
	 *  Sets the addresses of the service-provider.
	 *  @param addresses  A Vector containing the service-provider addresses as Strings.
	 * /
	public void setProviderAddresses(String[] addresses);*/

	/**
	 *  Sets the addresses of the service-provider.
	 *  @param address  A Vector containing the service-provider addresses as Strings.
	 */
	//public void addProviderAddress(String address);

	/**
	 *  Gets the properties of this service.
	 *  @return  A Vector containing properties as Strings.
	 */
	public String[] getProperties();

	/**
	 *  Sets the properties of this service.
	 *  @param properties  A Vector containing properties as Strings.
	 */
	//public void setProperties(String[] properties);
	public void addProperty(String property);

	/**
	 *  Gets the protocols the service supports.
	 *  @return  A Vector containing the supported protocols as Strings.
	 */
	public String[] getProtocols();

	/**
	 *  Sets the protocols the service supports.
	 *  @param protocols  A Vector containing the supported protocols as Strings.
	 */
	//public void setProtocols(String[] protocols);
	public void addProtocol(String protocol);

	/**
	 *  Gets the ontologies the service supports.
	 *  @return  A Vector containing the supported ontologies as Strings.
	 */
	public String[] getOntologies();

	/**
	 *  Sets the ontologies the service supports.
	 *  @param ontologies  A Vector containing the supported ontologies as Strings.
	 */
	//public void setOntologies(String[] ontologies);
	public void addOntology(String ontology);

	/**
	 *  Gets the languages this service supports.
	 *  @return  A Vector containing the supported languages as Strings.
	 */
	public String[] getLanguages();

	/**
	 *  Sets the languages this service supports.
	 *  @param languages  A Vector containing the supported languages as Strings.
	 */
	//public void setLanguages(String[] languages);
	public void addLanguage(String language);

	/**
	 *  This method returns a formatted String showing the model.
	 *  @return  formatted String showing ALL information about this model.
	 */
	public String toFormattedString();
}
