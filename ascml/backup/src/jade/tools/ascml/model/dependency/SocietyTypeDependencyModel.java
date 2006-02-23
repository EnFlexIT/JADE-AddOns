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

import jade.tools.ascml.absmodel.ISocietyTypeDependency;

/**
 * 
 */
public class SocietyTypeDependencyModel extends AbstractDependencyModel implements ISocietyTypeDependency
{
	private String quantity;
	private String name;

	public SocietyTypeDependencyModel(String name, String quantity)
	{
		super(SOCIETYTYPE_DEPENDENCY);
		setName(name);
		setQuantity(quantity);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String societyTypeName)
	{
		if ((societyTypeName == null) || (societyTypeName.equals("")))
			societyTypeName = NAME_UNKNOWN;
		this.name = societyTypeName;
	}

	public String getQuantity()
	{
		return quantity;
	}

	public void setQuantity(String quantity)
	{
		if ((quantity != null) && (!quantity.trim().equals("")))
		{
			if (quantity.equalsIgnoreCase(ANY))
				quantity = ANY;
			else if (quantity.equalsIgnoreCase(ALL))
				quantity = ALL;
			this.quantity = quantity;
		}
		else
			this.quantity = ANY;
	}	
}
