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

import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;

import java.util.*;

/**
 *  This interface describes the properties of an societyInstance-reference.
 */
public interface ISocietyInstanceReference
{							   	
	/**
	 * This constant is used to indicate, that this model has successfully been loaded.
	 */
	public final static String STATUS_OK				= "successfully loaded";

	/**
	 * This constant is used to indicate, that at least one error occurred while loading the model.
	 */
	public final static String STATUS_ERROR				= "loading error";

	/**
	 * This constant is used to indicate, that at least one referenced element (subsociety or agenttype)
	 * has NOT been loaded successfully
	 */
	public final static String STATUS_REFERENCE_ERROR	= "erroneous reference";

	/**
	 * This constant is used to indicate, that the status of a reference is unknown, this constant
	 * is usually used for remote references
	 */
	public final static String STATUS_REFERENCE_UNKNOWN	= "unknown reference status";

	/**
	 * This constant is used when no name has been given to this societyinstance-reference.
	 * Since the name is mandatory, it is set to NAME_UNKNOWN.
	 */
	public final static String NAME_UNKNOWN				= "Unnamed Reference";

	/**
	 * This constant is used when no name has been given to the referenced societytype.
	 * Since the societytype-name is mandatory, it is set to TYPE_UNKNOWN.
	 */
	public final static String TYPE_UNKNOWN				= "Unknown Reference-Type";

	/**
	 * This constant is used when no name has been given to referenced societyinstance.
	 * Since the societyinstance-name is mandatory, it is set to INSTANCE_UNKNOWN.
	 */
	public final static String INSTANCE_UNKNOWN			= "Unknown Reference-Instance";

	/**
	 * Returns the name of this reference.
	 * @return the name of this reference.
	 */
	public String getName();

	/**
	 * Sets the reference's name.
	 * @param name The name of this reference.
	 */
	public void setName(String name);
	
	/**
	 * Returns the name of the referenced SocietyType.
	 * @return  The name of the referenced SocietyType.
	 */
	public String getTypeName();

	/**
	 * Set the name of the referenced SocietyType.
	 * @param typeName  the name of the referenced SocietyType.
	 */
	public void setTypeName(String typeName);

	/**
	 * Returns the name of the referenced SocietyInstance.
	 * @return the name of the referenced SocietyInstance.
	 */
	public String getInstanceName();

	/**
	 * Sets the name of the referenced SocietyInstance.
	 * @param instanceName  the name of the referenced SocietyInstance.
	 */
	public void setInstanceName(String instanceName);

	/**
	 * Returns the name of the launcher, responsible for launching this reference.
	 * @return  the name of the launcher.
	 */
	public String getLauncherName();

	/**
	 * Sets the name of the launcher, responsible for launching this reference.
	 * @param launcherName  the name of the launcher.
	 */
	public void setLauncherName(String launcherName);

	/**
	 * Returns the addresses (as String-Vector) of the launcher, responsible 
	 * for launching this reference.
	 * @return  the addresses of the launcher (as String-Vector)
	 */
	public String[] getLauncherAddresses();

	/**
	 * Adds an address, where the launcher can be contacted.
	 * @param launcherAddress One address of the launcher.
	 */
	public void addLauncherAddress(String launcherAddress);

	/**
	 * Returns wheter this reference points to a local or a remote reference.
	 * @return  true, if remote-reference, false if reference is local.
	 */
	public boolean isRemoteReference();

	/**
	 * Set the local SocietyInstanceModel-object referenced by this ReferenceModel.
	 * In case the reference points to a remote SocietyInstance nothing has to be set.
	 * @param locallyReferencedModel  The local SocietyInstanceModel, referenced by this ReferenceModel
	 */
	public void setLocallyReferencedModel(ISocietyInstance locallyReferencedModel);

	/**
	 * Get the local SocietyInstanceModel-object referenced by this ReferenceModel.
	 * In case the reference points to a remote SocietyInstance, null is returned.
	 * @return  The local SocietyInstanceModel, referenced by this ReferenceModel or null,
	 * if reference points to a remote SocietyInstance.
	 */
	public ISocietyInstance getLocallyReferencedModel();

	/**
	 * Update the status of this model.
	 * The status indicates, whether this model is ready to run or nor.
	 */
	public void updateStatus();

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_WARNING
	 * STATUS_REFERENCE_ERROR, STATUS_REFERENCE_UNKNOWN.
	 */
	public String getStatus();

	/**
	 * Get the status-exception, which describe errors or warnings in detail.
	 * @return  A ModelException containing detailed status-messages as Strings.
	 */
	public ModelException getStatusException();

	/**
	 * Returns the dependencies (as IDependency-Vector) that must be fulfilled
	 * in order to start this SocietyInstance-reference.
	 * @return  the dependencies for this reference (as IDependency-Vector)
	 */
	public IDependency[] getDependencies();

	/**
	 * Adds an dependency, that must be fulfilled in order to start this reference.
	 * @param dependency One DependencyModel for this dependency.
	 */
	public void addDependency(IDependency dependency);

	/**
	 * Returns the number of references to be started.
	 * @return Number of references to start.
	 */
	public long getQuantity();

	/**
	 * Sets the number of refereces to be started.
	 * @param quantity  Number of references to be started.
	 */
	public void setQuantity(String quantity);

	/**
	 * Returns the naming-scheme to be used for naming the references if quantitiy is > 1.
	 * @return The naming-scheme.
	 */
	public String getNamingScheme();

	/**
	 * Sets the naming-scheme to be used for naming the references if quantitiy is > 1.
	 * @param namingScheme The naming-scheme.
	 */
	public void setNamingScheme(String namingScheme);

	/**
	 * Get all the ModelChangedListener.
	 * @return  Vector containing ModelChangedListener
	 */
	public Vector getModelChangedListener();

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode);
}
