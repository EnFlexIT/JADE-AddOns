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
 * 
 */
public interface IDependency
{
	/** These constants are used to indicate the type of dependency that is represented
		by this dependency-model. */
	String AGENTTYPE_DEPENDENCY			= "agenttype";
	String AGENTINSTANCE_DEPENDENCY		= "agentinstance";
	String SERVICE_DEPENDENCY			= "service";
	String SOCIETYINSTANCE_DEPENDENCY	= "societyinstance";
	String SOCIETYTYPE_DEPENDENCY		= "societytype";
	String DELAY_DEPENDENCY				= "delay";

	String getType();

	boolean isActive();

	void setActive(boolean active);
}
