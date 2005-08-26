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

/**
 *  Model-object containing exactly one condition on which the startup-process of an agent depends.
 */
public class DependencyModel implements IDependency
{
	/** These constants are used to indicate the type of dependency that is represented
	    by this dependency-model. */
	public static final String AGENTTYPE_DEPENDENCY			= "agenttype";
	public static final String AGENTINSTANCE_DEPENDENCY		= "agentinstance";
	public static final String SERVICE_DEPENDENCY			= "service";
	public static final String SOCIETYINSTANCE_DEPENDENCY	= "societyinstance";
	public static final String SOCIETYTYPE_DEPENDENCY		= "societytype";
	public static final String DELAY_ONLY_DEPENDENCY		= "delay";
	public static final String UNSPECIFIED_DEPENDENCY		= "unspecified";
	
	// /** The name of this dependency */
	// private String name;
	
	/** This HashMap may contain a set of attributes specified in the xml-description file. */ 
	private HashMap attributes;
	
	/** An 'active' dependency tries to fulfill it's conditions trying to start the agents,
	    scenarios or services it depends on, in contrast a 'passive' dependency waits passivly
		as long as all dependencies are fulfilled */
	private boolean isActive;
	
	/** The amount of milliseconds to wait after the last dependency-condition is fulfilled */
	private int delay;
	
	/** This String indicates what type of dependency (agent, scenario, service, agentType) is
	    represented by this dependency-model */
	private String dependencyType;
	
	/**
	 *  Instantiate a new dependency model and initialise some variables with their default-values 
	 */
	public DependencyModel()
	{
		isActive = false;
		delay = 0;
		dependencyType = UNSPECIFIED_DEPENDENCY;
	}
	
	/**
	 *  Set whether this is an active or passive dependency.
	 *  @param isActive  'true' if this is an active dependency (default), 'false' otherwise.
	 */
	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}

	/**
	 *  Returns whether this is an active or passive dependency.
	 *  @return  'true' if this dependency is active, 'false' otherwise.
	 */	
	public boolean isActive()
	{
		return isActive;
	}
	
	/**
	 *  Set the delay for this dependency-model. If the dependency itself is fulfilled
	 *  this delay determines the time-to-wait before an agent may be started. 
	 *  @param delay  delay in milliseconds.
	 */
	public void setDelay(int delay)
	{
		this.delay = delay;
	}

	/**
	 *  Get the delay for this dependency-model. If the dependency itself is fulfilled
	 *  this delay determines the time-to-wait before an agent may be started. 
	 *  @return  delay in milliseconds.
	 */
	public int getDelay()
	{
		return delay;
	}
	
	/**
	 *  Get the type of the dependency, that is represented by this dependency-model.
	 *  The type may be one of the following constants:
	 *  AGENTTYPE_DEPENDENCY ('agenttype'), AGENTINSTANCE_DEPENDENCY ('agent'), SERVICE_DEPENDENCY ('service'),
	 *  SOCIETYINSTANCE_DEPENDENCY ('societyinstance'), SOCIETYTYPE_DEPENDENCY ('societytype'),
	 *  UNSPECIFIED_DEPENDENCY ('unspecified').
	 *  @return  String representing the type of this dependency-model.
	 */
	public String getType()
	{
		if ((dependencyType == UNSPECIFIED_DEPENDENCY) && (getDelay() > 0))
			return DELAY_ONLY_DEPENDENCY;
		
		return dependencyType;
	}
	
	/**
	 *  Set the type and attributes of this dependency-Model.
	 *  @param name  The name of the agentType the agentInstance depends on.
	 *  @param quantity The amount of agents of the given agentType that have to
	 *                  be running, before the agentInstance may start.
	 *                  quantity may be an integer-value as well as "ALL" or "ANY"
	 */
	public void setAgentTypeDependency(String name, String quantity)
	{
		this.dependencyType = AGENTTYPE_DEPENDENCY;
		
		if (name == null)
			name = "unspecified";
		if (quantity == null)
			quantity = "ANY";
		
		this.attributes = new HashMap();
		this.attributes.put("name", name.trim());
		this.attributes.put("quantity", quantity.trim().toUpperCase());
	}
	
	/**
	 *  Returns true, if this dependency represents an agentType-dependency.
	 *  @return 'true' if agentType-dependend, 'false' otherwise
	 */
	public boolean isAgentTypeDependend()
	{
		return (this.dependencyType == AGENTTYPE_DEPENDENCY);
	}
	
	/**
	 *  Returns a HashMap with all the attributes provided by the agentType-dependency.
	 *  The keys of this HashMap are "name" and "quantity".
	 *  @return HashMap with all the dependency-attributes as values.
	 */
	public Map getAgentTypeDependency()
	{
		if (this.dependencyType == AGENTTYPE_DEPENDENCY)
			return attributes;
		else
			return null;
	}
	
	/**
	 *  Set the type and attributes of this dependency-Model.
	 *  @param name  The name of the agent the agentInstance depends on.
	 *  @param status  The status of the agent the agentInstance depends on.
	 *  @param providerName  A provider for querying the availability of an 
	 *                       agentInstance (e.g. agent, AMS, launcher).
	 *  @param providerAddresses  One or more addresses of the provider.
	 */
	public void setAgentInstanceDependency(String name, String status, String providerName, Collection providerAddresses)
	{
		this.dependencyType = AGENTINSTANCE_DEPENDENCY;
				
		if (name == null)
			name = "unspecified";
		if (status == null)
			status = "started";
		if (providerName == null)
			providerName = "unspecified";
		if (providerAddresses == null)
			providerAddresses = new Vector();

		this.attributes = new HashMap();
		this.attributes.put("name", name.trim());
		this.attributes.put("status", status.trim());
		this.attributes.put("providername", providerName.trim());
		this.attributes.put("provideraddresses", providerAddresses);
	}
	
	/**
	 *  Returns true, if this dependency represents an agent-dependency.
	 *  @return 'true' if agent-dependend, 'false' otherwise
	 */
	public boolean isAgentInstanceDependend()
	{
		return (this.dependencyType == AGENTINSTANCE_DEPENDENCY);
	}
	
	/**
	 *  Returns a HashMap with all the attributes provided by the agent-dependency.
	 *  The keys of this HashMap are "name", "status", "providername" and "provideraddresses".
	 *  @return HashMap with all the dependency-attributes as values.
	 */
	public Map getAgentInstanceDependency()
	{
		if (this.dependencyType == AGENTINSTANCE_DEPENDENCY)
			return attributes;
		else
			return null;
	}

	/**
	 *  Set the type and attributes of this dependency-Model.
	 *  @param ServiceDescriptionModel  The model describing the service-dependency.
	 */
	public void setServiceDependency(IServiceDescription serviceModel)
	{
		this.dependencyType = SERVICE_DEPENDENCY;
				
		this.attributes = new HashMap();
		this.attributes.put("service", serviceModel);
	}
	
	/**
	 *  Returns true, if this dependency represents a service-dependency.
	 *  @return 'true' if service-dependend, 'false' otherwise
	 */
	public boolean isServiceDependend()
	{
		return (this.dependencyType == SERVICE_DEPENDENCY);
	}
	
	/**
	 *  Returns a ServiceDescriptionModel containing all the attributes provided 
	 *  by the service-dependency.
	 *  @return ServiceDescriptionModel describing the service-dependency.
	 */
	public IServiceDescription getServiceDependency()
	{
		if (this.dependencyType == SERVICE_DEPENDENCY)
			return (IServiceDescription)attributes.get("service");
		else
			return null;
	}

	/**
	 *  Set the type and attributes of this dependency-Model.
	 *  @param name  The name of the societyinstance on which this instance is depending on.
	 *  @param launcherName  A launcher for querying the availability of a scenario.
	 *  @param launcherAddresses  The address(es) of the launcher. 
	 */
	public void setSocietyInstanceDependency(String name, String launcherName, Collection launcherAddresses)
	{
		this.dependencyType = SOCIETYINSTANCE_DEPENDENCY;
				
		if (name == null)
			name = "unspecified";
		if (launcherName == null)
			launcherName = "unspecified";
		if (launcherAddresses == null)
			launcherAddresses = new Vector();

		this.attributes = new HashMap();
		this.attributes.put("name", name.trim());
		this.attributes.put("launchername", launcherName.trim());
		this.attributes.put("launcheraddresses", launcherAddresses);
	}
	
	/**
	 *  Returns true, if this dependency represents a societyinstance-dependency.
	 *  @return 'true' if scenario-dependend, 'false' otherwise
	 */
	public boolean isSocietyInstanceDependend()
	{
		return (this.dependencyType == SOCIETYINSTANCE_DEPENDENCY);
	}	

	
	/**
	 *  Returns a HashMap with all the attributes provided by the societyinstance-dependency.
	 *  The keys of this HashMap are "name", "launchername", "launcheraddresses".
	 *  @return HashMap with all the dependency-attributes as values.
	 */
	public Map getSocietyInstanceDependency()
	{
		if (this.dependencyType == SOCIETYINSTANCE_DEPENDENCY)
			return attributes;
		else
			return null;
	}
	
	/**
	 *  Set the type and attributes of this dependency-Model.
	 *  @param name  The name of the societyType the agentInstance depends on.
	 *  @param quantity The amount of societyinstance of the given societyType that have to
	 *                  be started, before this societyInstance may start.
	 *                  quantity may be an integer-value as well as "ALL" or "ANY"
	 */
	public void setSocietyTypeDependency(String name, String quantity)
	{
		this.dependencyType = SOCIETYTYPE_DEPENDENCY;
		
		if (name == null)
			name = "unspecified";
		if (quantity == null)
			quantity = "ANY";
		
		this.attributes = new HashMap();
		this.attributes.put("name", name.trim());
		this.attributes.put("quantity", quantity.trim().toUpperCase());
	}
	
	/**
	 *  Returns true, if this dependency represents a societyType-dependency.
	 *  @return 'true' if societyType-dependend, 'false' otherwise
	 */
	public boolean isSocietyTypeDependend()
	{
		return (this.dependencyType == SOCIETYTYPE_DEPENDENCY);
	}
	
	/**
	 *  Returns a HashMap with all the attributes provided by the societyType-dependency.
	 *  The keys of this HashMap are "name" and "quantity".
	 *  @return HashMap with all the dependency-attributes as values.
	 */
	public Map getSocietyTypeDependency()
	{
		if (this.dependencyType == SOCIETYTYPE_DEPENDENCY)
			return attributes;
		else
			return null;
	}
	
	/**
	 *  This method returns a formatted String showing the dependency-model.
	 *  @return  formatted String showing ALL information about this dependency.
	 */
	public String toFormattedString()
	{		
		String str = "";
		
		str += "Dependency: type = " + getType() + " active = " + isActive() + " delay = " + getDelay() + "\n";
		if (isAgentInstanceDependend())
		{
			str += "     name = " + attributes.get("name") + "\n";
			str += "     status = " + attributes.get("status") + "\n";
			str += "     providername = " + attributes.get("providername") + "\n";
			str += "     provideraddresses = " + attributes.get("provideraddresses") + "\n";
		}
		else if (isAgentTypeDependend())
		{
			str += "     name = " + attributes.get("name") + "\n";
			str += "     quantity = " + attributes.get("quantity") + "\n";
		}
		return str;
	}
	
	/**
	 *  This method returns a formatted String showing the dependency-model.
	 *  @return  formatted String showing ALL information about this dependency.
	 */
	public String toString()
	{
		return toFormattedString();
	}
}
