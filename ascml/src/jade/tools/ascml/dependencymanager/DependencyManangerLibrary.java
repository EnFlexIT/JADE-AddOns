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

package jade.tools.ascml.dependencymanager;

import java.util.HashMap;

import jade.core.AID;
import jade.core.NotFoundException;
import jade.tools.ascml.absmodel.*;

public class DependencyManangerLibrary {
	
	private HashMap<String, IRunnableAgentInstance>		nameToAgentTypeMap;
	private HashMap<String, IRunnableSocietyInstance>	nameToSocietyMap;
	
	public DependencyManangerLibrary () {
		nameToAgentTypeMap = new HashMap<String, IRunnableAgentInstance>();
		nameToSocietyMap = new HashMap<String, IRunnableSocietyInstance>();
	}
	
	public void resetLibrary () {
		nameToAgentTypeMap.clear();
		nameToSocietyMap.clear();
	}
	
	public boolean addAgent(AID agentAID, IRunnableAgentInstance agentInstanceModel) {
		return addAgent(agentAID.getLocalName(),agentInstanceModel);
	}

	public boolean addAgent(String name, IRunnableAgentInstance agentInstanceModel) {
		if (nameToAgentTypeMap.containsKey(name)) {
			return false;
		} else {
			nameToAgentTypeMap.put(name,agentInstanceModel);
			return true;
		}		
	}	
	
	public boolean addSociety(String name, IRunnableSocietyInstance societyInstanceModel) {
		if (nameToSocietyMap.containsKey(name)) {
			return false;
		} else {
			nameToSocietyMap.put(name,societyInstanceModel);
			return true;
		}
	}
	
	public boolean delAgent(AID agentAID) {	
		return delAgent(agentAID.getLocalName());
	}
	
	public boolean delAgent(String name) {
		if (nameToAgentTypeMap.containsKey(name)) {
			nameToAgentTypeMap.remove(name);
			return true;
		} else {
			return false;	
		}
	}
	
	public boolean delSociety(String name){
		if (nameToSocietyMap.containsKey(name)) {
			nameToSocietyMap.remove(name);
			return true;
		} else {
			return false;
		}
	}
	
	public String lookupAgentType (AID agentAID) throws NotFoundException {
		return lookupAgentType(agentAID.getLocalName());
	}
	
	public String lookupAgentType (String name) throws NotFoundException {
		if (nameToAgentTypeMap.containsKey(name)) {
			return nameToAgentTypeMap.get(name).getType().getFullyQualifiedName();
		} else {
			throw new NotFoundException();
		}
	}	
	
	public IRunnableSocietyInstance lookupSocietyName (String name) throws NotFoundException {
		if (nameToSocietyMap.containsKey(name)) {
			return nameToSocietyMap.get(name);
		} else {
			throw new NotFoundException();
		}
	}

	public boolean hasAgent(AID agentAID) {
		return hasAgent(agentAID.getLocalName());
	}

	public boolean hasAgent(String name) {
		return nameToAgentTypeMap.containsKey(name);
	}	

	public boolean hasSociety(String name) {
		return nameToSocietyMap.containsKey(name);
	}

	public IRunnableAgentInstance getAgent(AID agentAID) {
		return nameToAgentTypeMap.get(agentAID.getLocalName());
	}
}
