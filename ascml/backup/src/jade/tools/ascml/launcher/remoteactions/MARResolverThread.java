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


/*
 * Created on 20.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jade.tools.ascml.launcher.remoteactions;

import java.util.Iterator;
import java.util.Vector;

import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.launcher.*;
import jade.tools.ascml.onto.*;

/**
 * @author SLilienthal
 * 
 */
public class MARResolverThread extends AbstractMARThread {

	public MARResolverThread(AgentLauncher al, Iterator it, ACLMessage message, Action a, AbsModel m) {
		super(al, it, message, a, m);
	}

	@Override
	public void doAction() throws Exception {
		Vector<AgentLauncherThread> altVector = new Vector<AgentLauncherThread>();
			if (toResolve == RESOLVE_SOCIETY) {
				while (it.hasNext()) {
                    SocietyInstance sc = (SocietyInstance) it.next();
					String societyName = sc.getFullQuallifiedName();				
					IRunnableSocietyInstance rsoc = null;
					try {
						rsoc = al.getRepository().createRunnableSocietyInstance(societyName);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (rsoc == null) {
						continue;
					}
					Iterator it = sc.getAllToolOptions();
					while (it.hasNext()) {
						ToolOption socTO = (ToolOption) it.next();
						String strTO = socTO.toString();
						//TODO: ToolOptions for Societies
					}
                    
                    Iterator recIt = message.getAllIntendedReceiver();
                    while (recIt.hasNext()) {
                        AID oneAID = (AID)recIt.next();
                    }
                    
					al.getDependencyManager().startThisSociety(rsoc);
				}
			} else if (toResolve == RESOLVE_AGENTINSTANCE) {
				while (it.hasNext()) {					
					AgentInstance ai = (AgentInstance) it.next();
					String instanceName = ai.getFullQuallifiedName();					

					IRunnableAgentInstance[] rai = al.getRepository().createRunnableAgentInstance(instanceName, 1);
					for (int i=0; i < rai.length; i++)
					{
						//FIXME: rework due to dependecyManager
						//if (!al.isAgentStarted(rai[i].getName())) {
							AgentLauncherThread alt = new AgentLauncherThread(rai[i],al,null);
							altVector.add(alt);
						//}
					}
                    Iterator recIt = message.getAllIntendedReceiver();
                    while (recIt.hasNext()) {
                        AID oneAID = (AID)recIt.next();
                    }
				}
				for (int i=0;i<altVector.size();i++) {
					AgentLauncherThread alt = altVector.get(i);
					alt.getResult();
				}
			} else if (toResolve == RESOLVE_AGENTTYPE) {
				while (it.hasNext()) {
					AgentType at = (AgentType) it.next();
					String typeName = at.getFullQuallifiedName();

					IRunnableAgentInstance rai = (IRunnableAgentInstance)al.getRepository().getRunnableManager().getRunnable(message.getSender().getLocalName()+typeName);
					// FIXME: rework due to dependecyManager
					//if (!al.isAgentStarted(rai.getName())) {
						AgentLauncherThread alt = new AgentLauncherThread(rai,al,null);
						altVector.add(alt);
					//}
                    
                    Iterator recIt = message.getAllIntendedReceiver();
                    while (recIt.hasNext()) {
                        AID oneAID = (AID)recIt.next();
                    }                    
				}
				for (int i=0;i<altVector.size();i++) {
					AgentLauncherThread alt = altVector.get(i);
					alt.getResult();
				}
			}
	}

	/**
	 * @param doWait
	 *            Wait until everything is resolved or quit after sending command to start
	 * @param instance
	 *            The IRunnableSocietyInstance to start
	 * @return The DependencyDispatcher to Wait for;
	 * @throws ModelActionException
	 *//*
	private void startSociety(IRunnableSocietyInstance instance, boolean doWait)
			throws ModelActionException {
		// retrieve list of local dependencies and resolve them
		IRunnableSocietyInstance[] lsocs = instance.getLocalRunnableSocietyInstanceReferences();
		for (int i = 0; i < lsocs.length; i++) {
			System.out.println("Local Reference (" + instance.getName() + "): " + lsocs[i].getName() + "\n");
			IRunnableSocietyInstance sm = lsocs[i];
			startSociety(sm, doWait);
		}
		// Now handled it over to the thread to do the remote ones
		//DependencyDispatcherThread dd = new DependencyDispatcherThread(al, instance, !doWait);
		if (doWait) {
			dd.join();
			//return null;
		} else {
			//return dd;
		}
	}
*/
}
