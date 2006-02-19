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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jade.core.AID;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.*;
import jade.util.Logger;

public abstract class AbstractDependencyController {
	
	//I heavily rely on Garbage Collection
	//If the key of a HashMap entry is freed, the entry should be deleted (hopefully)
	
	protected HashMap <String, HashSet<AbstractDependencyRecord>> agentNameMap;
	protected HashMap <String, MutableInteger> runningAgentTypeCountMap;
	protected HashMap <String, HashSet<AbstractDependencyRecord>> societyNameMap;
	protected HashMap <String, MutableInteger> runningSocietyTypeCountMap;	
	protected HashMap <IAbstractRunnable,AbstractDependencyRecord>	model2DependecyMap;//Only needed because of Garbage Collection
	protected AgentLauncher launcher;
	
	public AbstractDependencyController(AgentLauncher al) {
		this.launcher=al;
		model2DependecyMap = new HashMap<IAbstractRunnable,AbstractDependencyRecord>();
		runningAgentTypeCountMap = new HashMap <String, MutableInteger>();
		agentNameMap = new HashMap <String, HashSet<AbstractDependencyRecord>>();
		runningSocietyTypeCountMap = new HashMap <String, MutableInteger>();
		societyNameMap = new HashMap <String, HashSet<AbstractDependencyRecord>>();
	}

	public void updateSociety(IRunnableSocietyInstance societyInstanceModel) {
		Status socStatus = societyInstanceModel.getStatus();
		if (socStatus.equals(new Starting())) {
			//I don't care about starting societies
			//If it will run, we get another update message
			return;
		}
		String societyName = societyInstanceModel.getFullyQualifiedName();
		String societyType = ((ISocietyInstance)societyInstanceModel.getParentModel()).getFullyQualifiedName();
		if (runningSocietyTypeCountMap.containsKey(societyType)) {
			MutableInteger socCount = runningSocietyTypeCountMap.get(societyInstanceModel.getFullyQualifiedName());
			socCount.value++;
			socCount.notifyAll();
		} else if (societyNameMap.containsKey(societyName)) {
			HashSet<AbstractDependencyRecord> socNameDependencySet = societyNameMap.get(societyName);
			Iterator<AbstractDependencyRecord> sdrIt = socNameDependencySet.iterator();
			while (sdrIt.hasNext()) {
				AbstractDependencyRecord oneSdr = sdrIt.next();
				oneSdr.updateModel(societyName,socStatus);
			}
		}
	}
	
	public void updateAgent(IRunnableAgentInstance agentInstanceModel) {
		Status agentStatus = agentInstanceModel.getStatus();
		if (agentStatus.equals(new Starting())) {
			//I don't care about starting agents
			//If it will run, we get another update message
			return;
		}
		String agentName = agentInstanceModel.getFullyQualifiedName();
		String agentType = agentInstanceModel.getType().getFullyQualifiedName();
		if (runningAgentTypeCountMap.containsKey(agentType)) {
			MutableInteger agentCount = runningAgentTypeCountMap.get(agentInstanceModel.getFullyQualifiedName());
			agentCount.value++;
			agentCount.notifyAll();
		} else if (agentNameMap.containsKey(agentName)) {
			HashSet<AbstractDependencyRecord> agentNameDependencySet = agentNameMap.get(agentName);
			Iterator<AbstractDependencyRecord> sdrIt = agentNameDependencySet.iterator();
			while (sdrIt.hasNext()) {
				AbstractDependencyRecord oneSdr = sdrIt.next();
				oneSdr.updateModel(agentName,agentStatus);
			}
		}		
	}

	public void agentBorn(String agentType) {
		MutableInteger count;
		if (runningAgentTypeCountMap.containsKey(agentType)) {
			count = runningAgentTypeCountMap.get(agentType);
		} else {
			count = new MutableInteger(0);
		}
		count.value++;
		count.notifyAll();
	}
	
	public void agentBorn(AID agentAID) {
		//TODO: check agentAID.getLocalName() against IAgentInstanceDependency.getName();
		// also see: agentDied(AID agentAID) (same thing there)
		String agentName = agentAID.getLocalName();
		if (agentNameMap.containsKey(agentName)) {
			HashSet<AbstractDependencyRecord> nameDependencySet = agentNameMap.get(agentName);
			//TODO: What about already running agents?
			//at the moment, only new agents are taken into account when we have
			//agentInstance dependencies
			Iterator<AbstractDependencyRecord> sdrIt = nameDependencySet.iterator();
			while (sdrIt.hasNext()) {
				AbstractDependencyRecord oneSdr = sdrIt.next();
				oneSdr.agentBorn(agentName);		
			}			
		}
	}

	public void agentDied(String agentType) {
		if (runningAgentTypeCountMap.containsKey(agentType)) {
			MutableInteger count = runningAgentTypeCountMap.get(agentType);
			count.value--;
			count.notifyAll();
		}
	}

	public void agentDied(AID agentAID) {
		String agentName = agentAID.getLocalName();
		if (agentNameMap.containsKey(agentName)) {
			HashSet<AbstractDependencyRecord> nameDependencySet = agentNameMap.get(agentName);
			Iterator<AbstractDependencyRecord> sdrIt = nameDependencySet.iterator();
			while (sdrIt.hasNext()) {
				AbstractDependencyRecord oneSdr = sdrIt.next();
				oneSdr.agentDied(agentName);
			}			
		}
	}
	
	protected abstract Vector<IDependency> getDependenciesFromModel(IAbstractRunnable societyInstanceModel);
	protected abstract AbstractDependencyRecord getNewRecord(IAbstractRunnable absRunnable);
	protected abstract void noDependencies(IAbstractRunnable absRunnable);
	protected abstract void handleActiveDependency(IDependency oneDep);
	
	public void addModel(IAbstractRunnable absRunnable) {
		Vector<IDependency> deps;
		try {
			deps = getDependenciesFromModel(absRunnable);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Iterator<IDependency> depIterator = deps.iterator();
		if (depIterator.hasNext()) {
			while (depIterator.hasNext()) {
				IDependency oneDep = depIterator.next();
				AbstractDependencyRecord societyDepRecord;
				if (model2DependecyMap.containsKey(absRunnable)) {
					societyDepRecord = model2DependecyMap.get(absRunnable);
				} else {
					societyDepRecord  = getNewRecord(absRunnable);
					model2DependecyMap.put(absRunnable,societyDepRecord);
				}
				String depType = oneDep.getType();
				if (depType.equals(IDependency.AGENTINSTANCE_DEPENDENCY)) {
					IAgentInstanceDependency instDep = (IAgentInstanceDependency) oneDep;
					if (instDep.getProvider()==null) {
						String agentName = instDep.getName();
						societyDepRecord.addAgentDependency(agentName);
						HashSet<AbstractDependencyRecord> nameDependencySet;
						if (agentNameMap.containsKey(agentName)) {
							nameDependencySet = agentNameMap.get(agentName);
						} else {
							nameDependencySet = new HashSet<AbstractDependencyRecord>();
							agentNameMap.put(agentName,nameDependencySet);
						}
						nameDependencySet.add(societyDepRecord);
					} else {
						//We got a remote dependency
						RemoteInstanceWatcher remoteWatcher = new RemoteInstanceWatcher(societyDepRecord,launcher,instDep);
						societyDepRecord.addWatcherDependency(remoteWatcher);
					}
				} else if (depType.equals(IDependency.AGENTTYPE_DEPENDENCY)) {
					IAgentTypeDependency typeDep = (IAgentTypeDependency) oneDep;
					String agentType = typeDep.getName();
					MutableInteger runningCount;
					if (runningAgentTypeCountMap.containsKey(agentType)) {
						runningCount = runningAgentTypeCountMap.get(agentType);
					} else {
						runningCount = new MutableInteger(0);
					}
					String strCount =  typeDep.getQuantity();
					int intCount = Integer.parseInt(strCount);
					TypeCountWatcher tcw = new TypeCountWatcher(societyDepRecord,agentType, absRunnable, intCount, runningCount);
					societyDepRecord.addWatcherDependency(tcw);
				} else if (depType.equals(IDependency.SOCIETYINSTANCE_DEPENDENCY)) {
					//FIXME: fill me
				} else if (depType.equals(IDependency.SOCIETYTYPE_DEPENDENCY)) {
					//FIXME: fill me
				} else if (depType.equals(IDependency.DELAY_DEPENDENCY)) {
					//FIXME: fill me
				} else {
					continue;
				};
				if (oneDep.isActive()) {
					handleActiveDependency(oneDep);
				}
			}
		} else {
			//We don't have any Dependency
			if (launcher.myLogger.isLoggable(Logger.INFO)) {
				launcher.myLogger.info("This one ("+absRunnable.getFullyQualifiedName()+ ") has no dependencies");
			}
			noDependencies(absRunnable);
		}
	}

}
