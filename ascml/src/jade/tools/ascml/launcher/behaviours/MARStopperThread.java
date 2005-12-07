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


package jade.tools.ascml.launcher.behaviours;

import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;
import jade.tools.ascml.absmodel.IRunnableAgentInstance;
import jade.tools.ascml.absmodel.IRunnableSocietyInstance;
import jade.tools.ascml.events.ModelActionEvent;
import jade.tools.ascml.launcher.AgentKillThread;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.launcher.abstracts.AbstractMARThread;

import jade.tools.ascml.onto.*;
import jade.tools.ascml.exceptions.ModelActionException;


import java.util.Iterator;
import java.util.Vector;

public class MARStopperThread  extends AbstractMARThread {

	public MARStopperThread(AgentLauncher al, Iterator it, ACLMessage message, Action a, AbsModel m) {
		super(al, it, message, a, m);
	}

	@Override
	public void doAction() throws Exception {
		if (toResolve == RESOLVE_SOCIETY) {
			while (it.hasNext()) {
                SocietyInstance sc = (SocietyInstance) it.next();
				String societyName = sc.getFullQuallifiedName();

				IRunnableSocietyInstance instance = (IRunnableSocietyInstance) al.getRepository().getRunnableManager().getRunnable(societyName);
				try
				{
					ModelActionEvent event = new ModelActionEvent(ModelActionEvent.CMD_STOP_SOCIETYINSTANCE, instance);
					al.li.modelActionPerformed(event);
				}
				catch(ModelActionException exc)
				{
					System.err.println("MARStopperThread: An error occured while stopping runnable-instance " + instance);
				}
			}
		} else if (toResolve == RESOLVE_AGENTINSTANCE) {	
			Vector<AgentKillThread> aktVector = new Vector<AgentKillThread>();
			while (it.hasNext()) {					
				AgentInstance ai = (AgentInstance) it.next();
				String instanceName = ai.getFullQuallifiedName();
				IRunnableAgentInstance rai = (IRunnableAgentInstance) al.getRepository().getRunnableManager().getRunnable(instanceName);
				if (!al.isAgentStarted(rai.getName())) {
					AgentKillThread akt = new AgentKillThread(rai,al);
					aktVector.add(akt);
				}
			}
			for (int i=0;i<aktVector.size();i++) {
				AgentKillThread akt =  aktVector.get(i);
				akt.getResult();
			}			
		}
	}


}
