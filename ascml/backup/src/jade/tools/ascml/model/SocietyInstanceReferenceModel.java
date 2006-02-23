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
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;

/**
 *  This class describes the properties of an societyInstance-reference.
 */
public class SocietyInstanceReferenceModel implements ISocietyInstanceReference
{							   
	//-------- attributes --------

	/** The name. */
	protected String name;
	
	/** The name of the references SocietyType. */
	protected String typeName;

	/** The name of the references SocietyInstance. */
	protected String instanceName;

	/**
	 * This object may be null, in case this referenceModel points to a remote reference
	 * or to a local reference, which cannot be found in the local repository.
	 */
	protected ISocietyInstance locallyReferencedModel;

	/** The name of the launcher, who shall launch this SocietyInstance. */
	protected String launcherName;
	
	/** The addresses of the launcher. */
	protected Vector launcherAddresses;

	/** The reference's status. */
	protected String status;

	/**
	 * This Exception may contain a set of detailed String-messages in case the status is != STATUS_OK
	 */
	protected ModelException statusException;

	/** The dependencies, that must be fulfilled in order to launch the SocietyInstance */
	protected Vector<IDependency> dependencies;
	
	/** The number of instances that should be launched. */
	protected long quantity;
	
	/** The naming-scheme used to name the instances in case quantity is > 1. */
	protected String namingScheme;

	/** The Listeners to be informed when this model changes. */
	protected Vector modelChangedListener;

	//-------- constructors --------

	/**
	 *  Create a new parameter.
	 */
	public SocietyInstanceReferenceModel(String name, String typeName, String instanceName, Vector modelChangedListener)
	{
		setName(name);
		setTypeName(typeName);
		setInstanceName(instanceName);
		
		this.quantity = 1;
		this.locallyReferencedModel = null;
		this.modelChangedListener = modelChangedListener;
		this.launcherAddresses = new Vector();
		this.dependencies = new Vector<IDependency>();
		this.namingScheme = "";
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
	 * Returns the fullyqualified name of this reference.
	 * @return the name of this reference.
	 */
	public String getFullyQualifiedName()
	{
		if (name == null)
			return "";
		return getTypeName()+name;
	}

	/**
	 * Sets the reference's name.
	 * @param name The name of this reference.
	 */
	public void setName(String name)
	{
		if ((name == null) || (name.trim().equals("")))
			name = NAME_UNKNOWN;
		this.name = name;
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
	 * Set the name of the referenced SocietyType.
	 * @param typeName  the name of the referenced SocietyType.
	 */
	public void setTypeName(String typeName)
	{
		if ((typeName == null) || (typeName.trim().equals("")))
			typeName = TYPE_UNKNOWN;
		this.typeName = typeName;
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
	 * Sets the name of the referenced SocietyInstance.
	 * @param instanceName  the name of the referenced SocietyInstance.
	 */
	public void setInstanceName(String instanceName)
	{
		if ((instanceName == null) || (instanceName.trim().equals("")))
			instanceName = INSTANCE_UNKNOWN;
		this.instanceName = instanceName;
	}

	/**
	 * Set the local SocietyInstanceModel-object referenced by this ReferenceModel.
	 * In case the reference points to a remote SocietyInstance nothing has to be set.
	 * @param locallyReferencedModel  The local SocietyInstanceModel, referenced by this ReferenceModel
	 */
	public void setLocallyReferencedModel(ISocietyInstance locallyReferencedModel)
	{
		this.locallyReferencedModel = locallyReferencedModel;
	}

	/**
	 * Get the local SocietyInstanceModel-object referenced by this ReferenceModel.
	 * In case the reference points to a remote SocietyInstance, null is returned.
	 * @return  The local SocietyInstanceModel, referenced by this ReferenceModel or null,
	 * if reference points to a remote SocietyInstance.
	 */
	public ISocietyInstance getLocallyReferencedModel()
	{
		return locallyReferencedModel;
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
	 * Sets the name of the launcher, responsible for launching this reference.
	 * @param launcherName  the name of the launcher.
	 */
	public void setLauncherName(String launcherName)
	{
		this.launcherName = launcherName.trim();
	}

	/**
	 * Returns the addresses (as String-Vector) of the launcher, responsible 
	 * for launching this reference.
	 * @return  the addresses of the launcher (as String-Vector)
	 */
	public String[] getLauncherAddresses()
	{
		String[] returnArray = new String[launcherAddresses.size()];
		launcherAddresses.toArray(returnArray);
		return returnArray;
	}

	/**
	 * Adds an address, where the launcher can be contacted.
	 * @param launcherAddress One address of the launcher.
	 */
	public void addLauncherAddress(String launcherAddress)
	{
		if ((launcherAddress != null) && (!launcherAddress.trim().equals("")))
			this.launcherAddresses.addElement(launcherAddress.trim());
	}

	/**
	 * Returns wheter this reference points to a local or a remote reference.
	 * @return  true, if remote-reference, false if reference is local.
	 */
	public boolean isRemoteReference()
	{
		return ((launcherName != null) && (!launcherName.equals("")));
	}

	/**
	 * Set the status of this model.
	 * The status indicates, whether this model is ready to run or nor.
	 */
	public void updateStatus()
	{
		String newStatus = STATUS_OK;
		statusException = new ModelException("The SocietyInstanceReference '"+getName()+"' is errorneous.", "The SocietyInstanceReference contains errors, and therefore may not be started.");

		String referenceName;
		if (getLocallyReferencedModel() == null)
			referenceName = getTypeName() + "." + getInstanceName();
		else
			referenceName = getLocallyReferencedModel().getFullyQualifiedName();

		if (!isRemoteReference() && (getLocallyReferencedModel() == null))
		{
			statusException.addExceptionDetails("The referenced societyinstance-model-object '"+referenceName+"' is not set (maybe could not be resolved, i.e. is no contained within the repository).", "write me!");
			newStatus = STATUS_REFERENCE_ERROR;
		}
		if (!isRemoteReference() && ( (getLocallyReferencedModel() == null) || (getLocallyReferencedModel().getStatus() == ISocietyInstance.STATUS_ERROR) || (getLocallyReferencedModel().getStatus() == ISocietyInstance.STATUS_REFERENCE_ERROR)))
		{
			statusException.addExceptionDetails("The referenced societyinstance-model-object '"+referenceName+"' has error-status", "write me!");
			newStatus = STATUS_REFERENCE_ERROR;
		}

		if ((getName() == null) || getName().equals(""))
		{
			statusException.addExceptionDetails("The name for this societyinstance-reference is missing", "write me!");
			newStatus = STATUS_ERROR;
		}
		if ((getFullyQualifiedName() == null) || getFullyQualifiedName().equals(""))
		{
			statusException.addExceptionDetails("The fully qualified name for this societinstance-reference is missing", "write me!");
			newStatus = STATUS_ERROR;
		}
		if ((getTypeName() == null) || getTypeName().equals(""))
		{
			statusException.addExceptionDetails("The referenced societtype-name is missing", "write me!");
			newStatus = STATUS_ERROR;
		}
		if ((getInstanceName() == null) || getInstanceName().equals(""))
		{
			statusException.addExceptionDetails("The referenced societinstance-name is missing", "write me!");
			newStatus = STATUS_ERROR;
		}
		if (isRemoteReference() && (getLauncherAddresses().length == 0))
		{
			statusException.addExceptionDetails("There are no launcher-addresses given for for launcher '"+getLauncherName()+"'", "write me!");
			newStatus = STATUS_ERROR;
		}

		if (getStatus() != newStatus)
		{
			status = newStatus;
			throwModelChangedEvent(ModelChangedEvent.STATUS_CHANGED);
		}
	}

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * Get the status-exception, which describe errors or warnings in detail.
	 * @return  A ModelException containing detailed status-messages as Strings.
	 */
	public ModelException getStatusException()
	{
		return statusException;
	}

	/**
	 * Returns the dependencies (as IDependency-Vector) that must be fulfilled
	 * in order to start this SocietyInstance-reference.
	 * @return  the dependencies for this reference (as IDependency-Vector)
	 */
	public Vector<IDependency> getDependencies()
	{
		return dependencies;
	}

	/**
	 * Adds an dependency, that must be fulfilled in order to start this reference.
	 * @param dependency One DependencyModel for this dependency.
	 */
	public void addDependency(IDependency dependency)
	{
		if (dependency != null)
			this.dependencies.add(dependency);
	}

	/**
	 * Returns the number of references to be started.
	 * @return Number of references to start.
	 */
	public long getQuantity()
	{
		return quantity;
	}

	/**
	 * Sets the number of refereces to be started.
	 * @param quantity  Number of references to be started.
	 */
	public void setQuantity(String quantity)
	{
		if ((quantity == null) || (quantity.trim().equals("")))
			this.quantity = 1;
		else
			this.quantity = new Long(quantity.trim());
	}

	/**
	 * Returns the naming-scheme to be used for naming the references if quantitiy is > 1.
	 * @return The naming-scheme.
	 */
	public String getNamingScheme()
	{
		if ((getQuantity() > 1) && (namingScheme.equals("")))
			return "%N"; // default scheme
		else
			return this.namingScheme; // user-scheme
	}

	/**
	 * Sets the naming-scheme to be used for naming the references if quantitiy is > 1.
	 * @param namingScheme The naming-scheme.
	 */
	public void setNamingScheme(String namingScheme)
	{
		if (namingScheme == null)
			this.namingScheme = "";
		else
			this.namingScheme = namingScheme.trim();
	}

	/**
	 * Get all the ModelChangedListener.
	 * @return  A Vector containing ModelChangedListener
	 */
	public Vector getModelChangedListener()
	{
		return modelChangedListener;
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode)
	{
		ModelChangedEvent event = new ModelChangedEvent(this, eventCode);
		for (int i=0; i < modelChangedListener.size(); i++)
		{
			((ModelChangedListener)modelChangedListener.elementAt(i)).modelChanged(event);
		}
	}

	public String toString()
	{
		String str;

		String typeName = getTypeName();
		if (isRemoteReference())
		{
			str = getName() + " -> " + getInstanceName() + "@" + getLauncherName();
		}
		else
		{
			if (typeName.equals(this.getLocallyReferencedModel().getParentSocietyType().getName()))
				str = getName() + " -> " + getInstanceName();
			else
				str = getName() + " -> " + typeName + "." + getInstanceName();
		}

		if (getQuantity() > 1)
			str += " (" + getQuantity() + ")";
		return str;
	}
}
