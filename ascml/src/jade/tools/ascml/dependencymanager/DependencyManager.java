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

import java.util.Vector;

import jade.core.AID;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.events.*;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.launcher.*;
import jade.tools.ascml.onto.*;
import jade.tools.ascml.onto.Error;
import jade.tools.ascml.repository.Repository;

public class DependencyManager implements ModelChangedListener {

	private AgentLauncher myAgentLauncher;
	private Repository myRepository;
	private DependencyManangerLibrary myLibrary;
	private FunctionalStateController myFunctionalController;
	private RunnableStarter myRunnableStarter;
	private DependencyStateController myDependencyStateController;

	public DependencyManager(AgentLauncher launcher) {
		myAgentLauncher = launcher;
		myRepository = launcher.getRepository();
		myLibrary = new DependencyManangerLibrary();
		myFunctionalController = new FunctionalStateController(launcher);
		myRunnableStarter = new RunnableStarter(launcher, myFunctionalController);
		myDependencyStateController = new DependencyStateController(launcher);
	}

	public synchronized void modelChanged(ModelChangedEvent evt) {
		if (evt.getEventCode() == ModelChangedEvent.STATUS_CHANGED) {                            
			try {
				IAbstractRunnable absRunnable = (IAbstractRunnable)evt.getModel();                
				if (absRunnable instanceof IRunnableSocietyInstance) {
					IRunnableSocietyInstance societyInstanceModel = (IRunnableSocietyInstance) absRunnable;
					myFunctionalController.updateSociety(societyInstanceModel);
				} else if (absRunnable instanceof IRunnableAgentInstance) {
					IRunnableAgentInstance agentInstanceModel = (IRunnableAgentInstance) absRunnable;
					myFunctionalController.updateAgent(agentInstanceModel);
				}
			} catch (ClassCastException cce) {
				System.err.println("DependencyManager: ClassCastException:");
				System.err.println(cce.toString());
			}
		}
	}

	public void agentBorn(AID agentAID) {
		if (myLibrary.hasAgent(agentAID)) {
			IRunnableAgentInstance agent = myLibrary.getAgent(agentAID);
			agent.setStatus(new Running());
		}
		myFunctionalController.agentBorn(agentAID);
	}

	public void agentDied(AID agentAID) {
		if (myLibrary.hasAgent(agentAID)) {
			IRunnableAgentInstance agent = myLibrary.getAgent(agentAID);
			if (agent.getStatus().equals(new Stopping())) {
				agent.setStatus(new Dead());
			} else {
				agent.setStatus(new Error());
			}
			myLibrary.delAgent(agentAID);
		}
		myFunctionalController.agentDied(agentAID);
	}

	public void startThisAgent(IRunnableAgentInstance agentInstanceModel) throws ModelActionException {
		if (myLibrary.hasAgent(agentInstanceModel.getName())) {
			throw new ModelActionException("Agent is already in library", agentInstanceModel);
		}
		myLibrary.addAgent(agentInstanceModel.getName(), agentInstanceModel);
		myDependencyStateController.addModel(agentInstanceModel);
	}

	public void startThisSociety(IRunnableSocietyInstance societyInstanceModel) throws ModelActionException {
		if (myLibrary.hasSociety(societyInstanceModel.getName())) {
			throw new ModelActionException("Found society in library", societyInstanceModel);
		}
		myLibrary.addSociety(societyInstanceModel.getName(), societyInstanceModel);
		myDependencyStateController.addModel(societyInstanceModel);
	}

	public void stopThisAgent(IRunnableAgentInstance agentInstance) {
		agentInstance.setStatus(new Stopping());
	}
	
	public void stopThisSociety(IRunnableSocietyInstance societyInstanceModel) {
		societyInstanceModel.setStatus(new Stopping());
	}
}
