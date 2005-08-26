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
import jade.tools.ascml.absmodel.IAgentDescription;

/**
 *  Model-object containing all required information about a FIPA AgentDescription
 */
public class AgentDescriptionModel implements IAgentDescription
{
	//-------- attributes --------

	/** The name. */
	private String name;

	/** The addresses. */
	private ArrayList addresses;

	/** The services. */
	private ArrayList services;

	/** The protocols. */
	private ArrayList protocols;

	/** The ontologies. */
	private ArrayList ontologies;

	/** The langauges. */
	private ArrayList languages;

	//-------- constructors --------

	/**
	 *  Instantiate a new model and initialise some variables
	 */
	public AgentDescriptionModel()
	{
		name		= "";
		addresses	= new ArrayList();
		services	= new ArrayList();
		protocols	= new ArrayList();
		ontologies	= new ArrayList();
		languages	= new ArrayList();
	}
	
	//-------- methods --------

	/**
	 *  Gets the name of this AgentDescription.
	 *  @return The agent's name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Sets the name of this AgentDescription.
	 *  @param name  The agent's name.
	 */
	public void setName(String name)
	{
		if(name == null)
			name = "";
		this.name = name.trim();
	}

	/**
	 *  Gets the addresses, where the agent can be found.
	 *  @return  A ArrayList containing the agent's addresses as Strings.
	 */
	public String[] getAddresses()
	{
		return (String[])addresses.toArray(new String[addresses.size()]);
	}

	/**
	 *  Sets the addresses where the agent can be found
	 */
	public void addAddress(String address)
	{
		this.addresses.add(address);
	}

	/**
	 *  Gets the services the agent offers.
	 *  @return  A ArrayList containing ServiceDescriptionModels.
	 */
	public Object[] getServices()
	{
		return (String[])services.toArray(new String[services.size()]);
	}

	/**
	 *  Add a service.
	 */
	public void addService(String service)
	{
		this.services.add(service);
	}

	/**
	 *  Gets the protocols the agent supports.
	 *  @return  A ArrayList containing the supported protocols as Strings.
	 */
	public String[] getProtocols()
	{
		return (String[])protocols.toArray(new String[protocols.size()]);
	}

	/**
	 *  Add a protocol.
	 */
	public void addProtocol(String protocol)
	{
		this.protocols.add(protocol);
	}

	/**
	 *  Gets the ontologies the agent supports.
	 *  @return  A ArrayList containing the supported ontologies as Strings.
	 */
	public String[] getOntologies()
	{
		return (String[])ontologies.toArray(new String[ontologies.size()]);
	}

	/**
	 *  Add an ontology.
	 */
	public void addOntology(String ontology)
	{
		this.ontologies.add(ontology);
	}

	/**
	 *  Gets the languages this agent supports.
	 *  @return  A ArrayList containing the supported languages as Strings.
	 */
	public String[] getLanguages()
	{
		return (String[])languages.toArray(new String[languages.size()]);
	}

	/**
	 *  Add a language.
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
		str += name + "\n";
		str += "addresses: " + getAddresses() + "\n";
		str += "services: " + getServices() + "\n";
		str += "protocols: " + getProtocols() + "\n";
		str += "ontologies: " + getOntologies() + "\n";
		str += "languages:" + getLanguages() + "\n";
		return str;
	}
}
