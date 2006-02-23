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

import java.util.Vector;
import java.util.HashMap;

/**
 * 
 */
public interface IServiceDependency extends IDependency
{
	/**
	 * This constant is only used in case there has been no name specified for this service-dependency.
	 * Since the name is a mandatory attribute, it is nevertheless set.
	 */
	public String NAME_UNKNOWN = "Unknown";

	/**
	 * This constant is only used in case there has been no type specified for this service-dependency.
	 * Since the type is a mandatory attribute, it is nevertheless set.
	 */
	public String TYPE_UNKNOWN = "Unknown";

	/**
	 * This constant is only used in case there has been no ownership specified for this service-dependency.
	 */
	public String OWNERSHIP_UNKNOWN = "Unknown";

	public String getName();

	public void setName(String name);

	public String getServiceType();

	public void setServiceType(String type);

	public String getOwnership();

	public void setOwnership(String ownership);

	public IProvider getProvider();

	public void setProvider(IProvider provider);

	public Vector getProtocols();

	public void setProtocols(Vector protocols);

	public void addProtocol(String protocol);

	public Vector getOntologies();

	public void setOntologies(Vector ontologies);

	public void addOntology(String ontology);

	public Vector getLanguages();

	public void setLanguages(Vector languages);

	public void addLanguage(String language);

	public HashMap getProperties();

	public String getProperty(String name);

	public void setProperties(HashMap properties);

	public void addProperty(String name, String value);
}
