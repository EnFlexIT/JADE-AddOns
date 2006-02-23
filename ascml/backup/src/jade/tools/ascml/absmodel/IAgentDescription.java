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
 *  The agent description interface that corresponds directly to the xml schema.
 */
public interface IAgentDescription
{
	/**
	 *  Gets the name of this AgentDescription.
	 *  @return The agent's name.
	 */
	public String getName();

	/**
	 *  Sets the name of this AgentDescription.
	 *  @param name  The agent's name.
	 *  todo: introduce IAgentId to combine name and addresses?
	 */
	public void setName(String name);

	/**
	 *  Gets the addresses, where the agent can be found.
	 *  @return  A Vector containing the agent's addresses as Strings.
	 */
	public String[] getAddresses();

	/**
	 *  Add an address.
	 *  @param address An address.
	 */
	public void addAddress(String address);

	/**
	 *  Gets the services the agent offers.
	 *  @return  A Vector containing ServiceDescriptionModels.
	 */
	public Object[] getServices();

	/**
	 *  Add a service.
	 *  @param service The new service.
	 */
	public void addService(String service);

	/**
	 *  Gets the protocols the agent supports.
	 *  @return  A Vector containing the supported protocols as Strings.
	 */
	public String[] getProtocols();

	/**
	 *  Add a protocol.
	 *  @param protocol Add a protocol.
	 */
	public void addProtocol(String protocol);

	/**
	 *  Gets the ontologies the agent supports.
	 *  @return  A Vector containing the supported ontologies as Strings.
	 */
	public String[] getOntologies();

	/**
	 *  Add an ontology.
	 *  @param ontology Add an ontology.
	 */
	public void addOntology(String ontology);

	/**
	 *  Gets the languages this agent supports.
	 *  @return  A Vector containing the supported languages as Strings.
	 */
	public String[] getLanguages();

	/**
	 *  Add a new language.
	 *  @param language Add a language.
	 */
	public void addLanguage(String language);

	/**
	 *  This method returns a formatted String showing the model.
	 *  @return  formatted String showing ALL information about this model.
	 */
	public String toFormattedString();
}
