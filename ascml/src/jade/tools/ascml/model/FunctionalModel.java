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

import jade.tools.ascml.absmodel.IDependency;
import jade.tools.ascml.absmodel.IFunctional;

import java.util.Vector;

public class FunctionalModel implements IFunctional
{
	private Vector<String> invariants;
	private Vector<IDependency> dependencies;

	/**
	 * Creates a new FunctionalModel.
	 */
	public FunctionalModel()
	{
		this.invariants = new Vector<String>();
		this.dependencies = new Vector<IDependency>();
	}

	/**
	 * Get all the invariants defined for the functional-state.
	 * @return a String-Vector containing the functional-invariants.
	 */
	public Vector<String> getInvariants()
	{
		return invariants;
	}

	/**
	 * Add an invariant for the functional-state.
	 * @param invariant  A String representing the invariant.
	 */
	public void addInvariant(String invariant)
	{
		this.invariants.add(invariant);
	}

	/**
	 * Get all the dependencies on which the functional-state relies.
	 * @return  A Vector containing Dependency-Objects.
	 */
	public Vector<IDependency> getDependencies()
	{
		return dependencies;
	}

	/**
	 * Add a dependency on which the functional-state relies.
	 * @param dependency  A Dependency-model.
	 */
	public void addDependency(IDependency dependency)
	{
		this.dependencies.add(dependency);
	}

	public String toString()
	{
		String str = "";
		str += "FUNCTIONAL (invariants=" + getInvariants() + ") depending on:\n" + getDependencies();
		return str;
	}
}
