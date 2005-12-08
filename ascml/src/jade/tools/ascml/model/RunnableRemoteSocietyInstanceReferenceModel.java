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
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.onto.Status;
import jade.tools.ascml.onto.Starting;

/**
 *  This class describes the properties of a runnable societyInstance-reference.
 */
public class RunnableRemoteSocietyInstanceReferenceModel extends AbstractRunnable implements IRunnableRemoteSocietyInstanceReference
{							   
	//-------- attributes --------
	
	/** The name of the references SocietyType. */
	protected String typeName;

	/** The name of the references SocietyInstance. */
	protected String instanceName;

	/** The name of the launcher, who shall launch this SocietyInstance. */
	protected String launcherName;
	
	/** The addresses of the launcher. */
	protected String[] launcherAddresses;

	//-------- constructors --------

	/**
	 *  Create a new parameter.
	 */
	public RunnableRemoteSocietyInstanceReferenceModel(String name, IRunnableSocietyInstance parentModel,
												 IDependency[] dependencies, Vector runnableModelListener,
												 String typeName, String instanceName,
	                                             String launcherName, String[] launcherAddresses)
	{
		super(name, parentModel, dependencies, runnableModelListener, parentModel);
		this.typeName = typeName;
		this.instanceName = instanceName;
		this.launcherName = launcherName;
		this.launcherAddresses = launcherAddresses;

		this.status = new Starting();
		this.detailedStatus = "Runnable remote societyinstance-reference has been created";
	}

	//-------- methods --------

	/**
	 * Returns the name of this reference.
	 * @return the name of this reference.
	 */
	public String getName()
	{
		if (name == null)
			return "";
		return name;
	}

	/**
	 * Returns the FQ- name of this reference.
	 * @return the FQ-name of this reference.
	 */
	public String getFullyQualifiedName()
	{
		String fqn= getTypeName()+"."+getInstanceName()+"."+getName();
		return fqn;
	}
	
	/**
	 * Returns the name of the referenced SocietyType.
	 * @return  The name of the referenced SocietyType.
	 */
	public String getTypeName()
	{
		if (typeName == null)
			return "";
		return typeName;
	}

	/**
	 * Returns the name of the referenced SocietyInstance.
	 * @return the name of the referenced SocietyInstance.
	 */
	public String getInstanceName()
	{
		if (instanceName == null)
			return "";
		return instanceName;
	}

	/**
	 * Returns the name of the launcher, responsible for launching this reference.
	 * @return  the name of the launcher.
	 */
	public String getLauncherName()
	{
		if (launcherName == null)
			return "";
		return launcherName;
	}

	/**
	 * Returns the addresses (as String-Vector) of the launcher, responsible 
	 * for launching this reference.
	 * @return  the addresses of the launcher (as String-Vector)
	 */
	public String[] getLauncherAddresses()
	{
		return launcherAddresses;
	}

	public String toString()
	{
		String str = "";
		str += getName() + " (" + getTypeName() + "." + getInstanceName() +")";
		return str;
	}
}
