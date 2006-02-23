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
 *  interface containing all required information about a runnable SocietyInstance. 
 */
public interface IRunnableRemoteSocietyInstanceReference extends IAbstractRunnable
{

	/**
	 * Returns the name of this reference.
	 * @return the name of this reference.
	 */
	public String getName();
	/**
	 * Returns the fully qualified name of this reference.
	 * @return the name of this reference.
	 */
	public String getFullyQualifiedName();
	
	/**
	 * Returns the name of the referenced SocietyType.
	 * @return  The name of the referenced SocietyType.
	 */
	public String getTypeName();

	/**
	 * Returns the name of the referenced SocietyInstance.
	 * @return the name of the referenced SocietyInstance.
	 */
	public String getInstanceName();

	/**
	 * Returns the name of the launcher, responsible for launching this reference.
	 * @return  the name of the launcher.
	 */
	public String getLauncherName();

	/**
	 * Returns the addresses (as String-Vector) of the launcher, responsible 
	 * for launching this reference.
	 * @return  the addresses of the launcher (as String-Vector)
	 */
	public String[] getLauncherAddresses();
}
