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

/**
 *  This interface describes the properties of an agent startup parameter set. 
 */
public interface IAgentParameterSet extends Cloneable
{
	/**
	 *  Set the name.
	 *  @param name The name.
	 */
	public void setName(String name);

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName();

	/**
	 *  Set the type.
	 *  @param type The type.
	 */
	public void setType(String type);

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType();

	/**
	 *  Set the description.
	 *  @param description The description.
	 */
	public void setDescription(String description);

	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription();

	/**
	 *  Set the value.
	 *  @param value The value.
	 */
	public void addValue(Object value);

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public Object[] getValues();

	/**
	 *  Set the values of this parameterSet (and clear all old ones)
	 *  @param value The value.
	 */
	public void setValues(Object[] newValues);
	
	/**
	 *  Set optional.
	 *  @param optional The optional value.
	 */
	public void setOptional(String optional);

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public boolean isOptional();

	/**
	 *  Add a constraint.
	 *  @param constraint The constraint.
	 */
	public void addConstraint(String constraint);

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public String[] getConstraints();

	/**
	 *  Get a clone of this object.
	 *  @return clone of this parameter-object.
	 */
	 public Object clone();
}
