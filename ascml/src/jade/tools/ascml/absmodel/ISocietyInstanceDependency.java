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

import jade.tools.ascml.absmodel.IProvider;
import jade.tools.ascml.absmodel.IAbstractRunnable;

/**
 * 
 */
public interface ISocietyInstanceDependency extends IDependency
{
	/**
	 * This constant is only used in case there has been no name specified for this agenttype-dependency.
	 * Since the name is a mandatory attribute, it is nevertheless set.
	 */
	public String NAME_UNKNOWN = "Unknown";

	public String STATUS_CREATED			= IAbstractRunnable.STATUS_CREATED;
	public String STATUS_STARTING			= IAbstractRunnable.STATUS_STARTING;
	public String STATUS_RUNNING			= IAbstractRunnable.STATUS_RUNNING;
	public String STATUS_PARTLY_RUNNING		= IAbstractRunnable.STATUS_PARTLY_RUNNING;
	public String STATUS_STOPPING			= IAbstractRunnable.STATUS_STOPPING;
	public String STATUS_NOT_RUNNING		= IAbstractRunnable.STATUS_NOT_RUNNING;
	public String STATUS_ERROR				= IAbstractRunnable.STATUS_ERROR;

	public void setSocietyTypeName(String societyTypeName);

	public String getSocietyInstanceName();

	public void setSocietyInstanceName(String societyInstanceName);

	public String getSocietyTypeName();

	public String getFullyQualifiedName();

	public String getStatus();

	public void setStatus(String status);

	public IProvider getProvider();

	public void setProvider(IProvider provider);
}
