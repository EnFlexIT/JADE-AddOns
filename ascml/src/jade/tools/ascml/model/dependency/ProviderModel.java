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

import jade.tools.ascml.absmodel.IProvider;

import java.util.Vector;

/**
 * 
 */
public class ProviderModel implements IProvider
{
	private String name;
	private Vector addresses;

	public ProviderModel(String name, Vector addresses)
	{
		this.addresses = new Vector();
		setName(name);
		setAddresses(addresses);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Vector getAddresses()
	{
		return addresses;
	}

	public void setAddresses(Vector addresses)
	{
		this.addresses = addresses;
	}

	public void addAddress(String address)
	{
		this.addresses.add(address);
	}
}
