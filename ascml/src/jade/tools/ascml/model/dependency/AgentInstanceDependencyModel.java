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

import jade.tools.ascml.absmodel.*;

/**
 * 
 */
public class AgentInstanceDependencyModel extends AbstractDependencyModel implements IAgentInstanceDependency
{
    /**
     * These constants are used to check whether the status, that is set in the setStatus-method
     * is a valid status.
     */
	private static String STATUS_BORN       = "born";
	private static String STATUS_RUNNING    = "running";
	private static String STATUS_ERROR      = "error";
	private static String STATUS_DEAD       = "dead";

	private String name;
	private String status;
	private IProvider provider;

	public AgentInstanceDependencyModel(String name, String status, IProvider provider)
	{
		super(AGENTINSTANCE_DEPENDENCY);
		setName(name);
		setStatus(status);
		setProvider(provider);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		if ((status == null) || (status.trim().equals("")))
			this.status = STATUS_RUNNING;
		else
		{
			if (status.equalsIgnoreCase(STATUS_BORN))
				this.status = STATUS_BORN;
			else if (status.equalsIgnoreCase(STATUS_RUNNING))
				this.status = STATUS_RUNNING;
			else if (status.equalsIgnoreCase(STATUS_ERROR))
				this.status = STATUS_ERROR;
			else if (status.equalsIgnoreCase(STATUS_DEAD))
				this.status = STATUS_DEAD;
			else
			{
				System.err.println("AgentInstanceDependencyModel.setStatus: Unknown status ("+status+")");
				this.status = STATUS_RUNNING;
			}
		}
	}

	public IProvider getProvider()
	{
		return provider;
	}

	public void setProvider(IProvider provider)
	{
		this.provider = provider;
	}
}
