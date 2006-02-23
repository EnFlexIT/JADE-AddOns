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
import jade.tools.ascml.absmodel.IAgentParameter;

/**
 *  This interface describes the properties of an agent startup parameter.
 */
public class AgentParameter implements IAgentParameter
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The type. */
	protected String type;

	/** The description */
	protected String description;

	/** The value. */
	protected Object value;

	/** The optional flag. */
	protected boolean optional;

	/** The constraints. */
	protected ArrayList constraints;

	//-------- constructors --------

	/**
	 *  Create a new parameter.
	 */
	public AgentParameter()
	{
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
	 *  Set the value.
	 *  @param value The value.
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public Object getValue()
	{
		return value;
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
	 *  Is this parameter optional ???
	 *  @return 'true' if optional, 'false' otherwise.
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
	 *  Get the value.
	 *  @return The value.
	 */
	public String[] getConstraints()
	{
		return (String[])constraints.toArray(new String[constraints.size()]);
	}

	/**
	 *  Get a clone of this object.
	 *  @return clone of this parameter-object.
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
	 
	public String toString()
	{
		String str = "";
		str += "Parameter : name = "+getName()+"\n";
		str += "     type = "+getType()+"\n";
		str += "     optional = "+isOptional()+"\n";
		str += "     decription = "+getDescription()+"\n";
		str += "     value = "+getValue()+"\n";
		str += "     constraints = "+getConstraints()+"\n";
		
		return str;
	}
}
