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

import jade.tools.ascml.onto.Status;

/**
 * 
 */
public interface IAbstractRunnable
{
	public static final String STATUS_NOT_CREATED	= "not_created";
	public static final String STATUS_CREATED		= "created";
	public static final String STATUS_STARTING		= "starting";
	public static final String STATUS_RUNNING		= "running";
	public static final String STATUS_PARTLY_RUNNING = "partly_running";
	public static final String STATUS_STOPPING		= "stopping";
	public static final String STATUS_NOT_RUNNING	= "not_running";
	public static final String STATUS_ERROR			= "error";

	public void setName(String name);

	public String getName();

	/**
	 *  Get the fully qualified name of this runnable
	 *  @return  runnable's fully qualified name.
	 */
	public String getFullyQualifiedName();

	public Object getParentModel();

	/**
	 * Set the parent runnable-model of this AbstractRunnable.
	 * @param parentRunnable  runnable-model to which this runnable-model belongs
	 */
	public void setParentRunnable(IAbstractRunnable parentRunnable);

	public IDependency[] getDependencies();

	public void addDependencies(IDependency[] dependencies);

	public void setStatus(Status status);

	public Status getStatus();

	/**
	 * Runnables may inform their parent-Runnables (e.g. the RunnableSociety to which
	 * a RunnableSociety or -AgentInstance belongs) about a status-change of their own.
	 * @param runnableName The name of the runnable-model, whose status changed (only used for debugging)
	 * @param newStatus  New status of the agent
	 * @param oldStatus  Old status of the agent
	 */
	public void informStatus(String runnableName, Status newStatus, Status oldStatus);

	public String getDetailedStatus();

	public void setDetailedStatus(String detailedStatus);

	public String toString();

	public String toFormattedString();
}
