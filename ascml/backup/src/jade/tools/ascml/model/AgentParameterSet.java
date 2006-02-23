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

import java.util.ArrayList;
import jade.tools.ascml.absmodel.IAgentParameterSet;

/**
 *  This interface describes the properties of an agent startup parameter set.
 */
public class AgentParameterSet implements IAgentParameterSet
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The type. */
	protected String type;

	/** The description */
	protected String description;

	/** The values. */
	protected ArrayList values;

	/** The optional flag. */
	protected boolean optional;

	/** The constraints. */
	protected ArrayList constraints;

	//-------- constructors --------

	/**
	 *  Create a new parameter.
	 */
	public AgentParameterSet()
	{
		this.values = new ArrayList();
		this.constraints = new ArrayList();
	}

	//-------- methods --------

	/**
	 *  Set the name.
	 *  @param name The name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the type.
	 *  @param type The type.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the description.
	 *  @param description The description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *  Add a value to this parameterSet
	 *  @param value The value.
	 */
	public void addValue(Object value)
	{
		values.add(value);
	}

	/**
	 *  Set the values of this parameterSet (and clear all old ones)
	 *  @param newValues The new values.
	 */
	public void setValues(Object[] newValues)
	{
		values.clear();
		for (int i=0; i < newValues.length; i++)
		{
			values.add(newValues[i]);
		}
	}
	
	/**
	 *  Get all values.
	 *  @return The values.
	 */
	public Object[] getValues()
	{
		if (values.size() > 0)
			return values.toArray();
		else
			return null;
	}

	/**
	 *  Set optional.
	 *  @param optional The optional value.
	 */
	public void setOptional(String optional)
	{
		this.optional = new Boolean(optional.trim()).booleanValue();
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public boolean isOptional()
	{
		return optional;
	}

	/**
	 *  Add a constraint.
	 *  @param constraint The constraint.
	 */
	public void addConstraint(String constraint)
	{
		// todo
		this.constraints.add(constraint);
	}

	/**
	 *  Get a clone of this object.
	 *  @return clone of this parameterset-object.
	 */
	 public Object clone()
	 {
		 try
		 {
			 return super.clone();
		 }
		 catch(CloneNotSupportedException exc)
		 {
			 exc.printStackTrace();
			 return null;
		 }
	 }
	 
	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public String[] getConstraints()
	{
		return (String[])constraints.toArray(new String[constraints.size()]);
	}

	public String toString()
	{
		String str = "";
		str += "ParameterSet : name = "+getName()+"\n";
		str += "     type = "+getType()+"\n";
		str += "     optional = "+isOptional()+"\n";
		str += "     decription = "+getDescription()+"\n";

		str += "     values = [";
		Object[] values = getValues();
		for (int i=0; i < values.length; i++)
		{
			str += values[i]+",";
		}
		str += "]\n";
		
		str += "     constraints = "+getConstraints()+"\n";
		
		return str;
	}
}
