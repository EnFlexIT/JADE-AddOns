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
 * 
 */
public interface IAgentTypeDependency extends IDependency
{
	/**
	 * This constant is only used in case there has been no name specified for this agenttype-dependency.
	 * Since the name is a mandatory attribute, it is nevertheless set.
	 */
	public String NAME_UNKNOWN = "Unknown";

	/**
	 * This constant is used to indicate that the number of required agenttypes doesn't matter
	 */
	public String ANY = "any";

	/**
	 * Write me: What's the meaning of this constant ?
	 */
	public String ALL = "all";

	public String getName();

	public void setName(String name);

	public String getQuantity();

	public void setQuantity(String quantity);
}
