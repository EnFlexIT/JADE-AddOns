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
import jade.tools.ascml.absmodel.ISocietyInstanceDependency;

/**
 * 
 */
public class SocietyInstanceDependencyModel extends AbstractDependencyModel implements ISocietyInstanceDependency
{

	private String societyTypeName;
	private String societyInstanceName;
	private String status;
	private IProvider provider;

	public SocietyInstanceDependencyModel(String societyInstanceName, String societyTypeName, String status, IProvider provider)
	{
		super(SOCIETYINSTANCE_DEPENDENCY);
		setSocietyInstanceName(societyInstanceName);
		setSocietyTypeName(societyTypeName);
		setStatus(status);
		setProvider(provider);
	}

	public String getSocietyTypeName()
	{
		return societyTypeName;
	}

	public void setSocietyTypeName(String societyTypeName)
	{
		if ((societyTypeName == null) || (societyTypeName.equals("")))
			societyTypeName = NAME_UNKNOWN;
		this.societyTypeName = societyTypeName;
	}

	public String getSocietyInstanceName()
	{
		return societyInstanceName;
	}

	public void setSocietyInstanceName(String societyInstanceName)
	{
		if ((societyInstanceName == null) || (societyInstanceName.equals("")))
			societyInstanceName = NAME_UNKNOWN;
		this.societyInstanceName = societyInstanceName;
	}

	public String getFullyQualifiedName()
	{
		return societyTypeName + "." + societyInstanceName;
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
			status = status.trim();

			if (status.equalsIgnoreCase(STATUS_CREATED))
				this.status = STATUS_CREATED;
			else if (status.equalsIgnoreCase(STATUS_STARTING))
				this.status = STATUS_STARTING;
			else if (status.equalsIgnoreCase(STATUS_RUNNING))
				this.status = STATUS_RUNNING;
			else if (status.equalsIgnoreCase(STATUS_PARTLY_RUNNING))
				this.status = STATUS_PARTLY_RUNNING;
			else if (status.equalsIgnoreCase(STATUS_STOPPING))
				this.status = STATUS_STOPPING;
			else if (status.equalsIgnoreCase(STATUS_NOT_RUNNING))
				this.status = STATUS_NOT_RUNNING;
			else if (status.equalsIgnoreCase(STATUS_ERROR))
				this.status = STATUS_ERROR;
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
