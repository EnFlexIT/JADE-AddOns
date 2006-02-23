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


package jade.tools.ascml.model.dependency;

import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.absmodel.IProvider;
import jade.tools.ascml.absmodel.IServiceDependency;

import java.util.Vector;
import java.util.HashMap;

/**
 * 
 */
public class ServiceDependencyModel extends AbstractDependencyModel implements IServiceDependency
{
	private String name;
	private String type;
	private String ownership;
	private IProvider provider;

	private Vector protocols;
	private Vector ontologies;
	private Vector languages;
	private HashMap properties;

	public ServiceDependencyModel(String name, String type, String ownership, IProvider provider)
	{
		super(SERVICE_DEPENDENCY);
		setName(name);
		setServiceType(type);
		setOwnership(ownership);
		setProvider(provider);

		protocols	= new Vector();
		ontologies	= new Vector();
		languages	= new Vector();
		properties	= new HashMap();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if ((name == null) || (name.equals("")))
			name = NAME_UNKNOWN;
		this.name = name;
	}

	public String getServiceType()
	{
		return type;
	}

	public void setServiceType(String type)
	{
		if ((type == null) || (type.equals("")))
			type = TYPE_UNKNOWN;
		this.type = type;
	}

	public String getOwnership()
	{
		return ownership;
	}

	public void setOwnership(String ownership)
	{
		if ((ownership == null) || (ownership.equals("")))
			ownership = OWNERSHIP_UNKNOWN;
		this.ownership = ownership;
	}

	public IProvider getProvider()
	{
		return provider;
	}

	public void setProvider(IProvider provider)
	{
		this.provider = provider;
	}

	public Vector getProtocols()
	{
		return protocols;
	}

	public void setProtocols(Vector protocols)
	{
		this.protocols = protocols;
	}

	public void addProtocol(String protocol)
	{
		this.protocols.add(protocol);
	}

	public Vector getOntologies()
	{
		return ontologies;
	}

	public void setOntologies(Vector ontologies)
	{
		this.ontologies = ontologies;
	}

	public void addOntology(String ontology)
	{
		this.ontologies.add(ontology);
	}

	public Vector getLanguages()
	{
		return languages;
	}

	public void setLanguages(Vector languages)
	{
		this.languages = languages;
	}

	public void addLanguage(String language)
	{
		this.languages.add(language);
	}

	public HashMap getProperties()
	{
		return properties;
	}

	public String getProperty(String name)
	{
		return (String)properties.get(name);
	}

	public void setProperties(HashMap properties)
	{
		this.properties = properties;
	}

	public void addProperty(String name, String value)
	{
		if ((name == null) || (name.equals("")))
			name = NAME_UNKNOWN;
		this.properties.put(name, value);
	}
}
