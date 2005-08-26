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


package jade.tools.ascml.launcher;

import java.util.Vector;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.events.ModelActionListener;
import jade.tools.ascml.events.ModelActionEvent;
import jade.tools.ascml.events.ProgressUpdateEvent;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.onto.*;

import jade.tools.ascml.onto.Stopping;

public class LauncherInterface implements ModelActionListener {
    private AgentLauncher al;

    public LauncherInterface(AgentLauncher al) {
        this.al = al;
    }

public void modelActionPerformed(ModelActionEvent event) throws ModelActionException {
        if (event.getActionCommand().equals(ModelActionEvent.CMD_START_AGENTINSTANCE)) {
            IRunnableAgentInstance instance = (IRunnableAgentInstance) event.getModel();

			instance.setDetailedStatus("The agentinstance is starting.");
            instance.setStatus(new Starting());
            AgentLauncherThread new_thread = new AgentLauncherThread(instance, al, null);

            // System.out.println("Starting single agent :
            // "+instance.getName()+"\nof class: "+event.getModel().getClass());

            new_thread.getResult();

			instance.setDetailedStatus("The agent-instance successfully started and is now running.");
            instance.setStatus(new Functional());

        } else if (event.getActionCommand().equals(ModelActionEvent.CMD_STOP_AGENTINSTANCE)) {
            IRunnableAgentInstance instance = (IRunnableAgentInstance) event.getModel();
            if (al.isAgentStarted(instance.getName())) {
                al.removeLaunchedAgent(instance);

				System.err.println("LauncherInterface: stop agentinstance, name=" + instance.getName() + " status=" + instance.getStatus());
                instance.setStatus(new Stopping());
                instance.setDetailedStatus("The agent-instance is being stopped");

                AgentKillThread akt = new AgentKillThread(instance, al);

                try {
                    akt.getResult();
                } catch (ModelActionException mae) {
                    instance.setStatus(new jade.tools.ascml.onto.Error());
                    instance.setDetailedStatus(mae.getShortMessage());
                    throw (mae);
                }
		// After we stop an Instance it's Available, we can then remove it from the Repo et al.
                instance.setStatus(new Known());
                instance.setDetailedStatus("The agent-instance has been stopped and is not running anymore.");
            }
        } else if (event.getActionCommand().equals(ModelActionEvent.CMD_START_SOCIETYINSTANCE)) {
            IRunnableSocietyInstance instance = (IRunnableSocietyInstance) event.getModel();
            instance.setStatus(new Starting());
            instance.setDetailedStatus("The SocietyInstance is starting. All local agent- and society-instances contained within this society have been created and are starting as well. In case remote society-references have been specified, a request will soon be send to the appropiate launcher in order to force their startup.");

            // retrieve list of local dependencies and resolve them
            IRunnableSocietyInstance[] lsocs = instance.getLocalRunnableSocietyInstanceReferences();
            for (int i = 0; i < lsocs.length; i++) {
                instance.setDetailedStatus("The local SocietyInstance-reference is going to start soon.");
                // System.out.println("Local Reference ("+instance.getName()+"):
                // "+lsocs[i].getName()+"\n");
                IRunnableSocietyInstance sm = lsocs[i];
                ModelActionEvent ae = new ModelActionEvent(ModelActionEvent.CMD_START_SOCIETYINSTANCE, lsocs[i]);
                modelActionPerformed(ae);
            }

            // Now handled it over to the thread to do the remote ones
            DependencyDispatcherThread dd = new DependencyDispatcherThread(al, instance, true);
        } else if (event.getActionCommand().equals(ModelActionEvent.CMD_STOP_SOCIETYINSTANCE)) {
            IRunnableSocietyInstance instance = (IRunnableSocietyInstance) event.getModel();
            al.getRepository().throwProgressUpdateEvent(new ProgressUpdateEvent("Stopping " + instance, ProgressUpdateEvent.PROGRESS_ADVANCE));
            instance.setStatus(new Stopping());
            instance.setDetailedStatus("The society-instance is going to stop soon. All local society-instance-references contained within this society-instance are now being stopped.");
            ModelActionException concatMAE = null;
            IRunnableSocietyInstance[] lsocs = instance.getLocalRunnableSocietyInstanceReferences();
            for (int i = 0; i < lsocs.length; i++) {
                instance.setDetailedStatus("The local society-instance-reference is going to stop soon.");
                // System.out.println("Local Reference ("+instance.getName()+"):
                // "+lsocs[i].getName()+"\n");
                IRunnableSocietyInstance sm = lsocs[i];
		// STATUS FIXME: how do I transcribe this situation to the new Status model?
                if (! (al.getRepository().getRunnableStatus(sm.getFullyQualifiedName()) instanceof Functional)) {
                    ModelActionEvent ae = new ModelActionEvent(ModelActionEvent.CMD_STOP_SOCIETYINSTANCE, lsocs[i]);
                    modelActionPerformed(ae);
                }
            }

            Vector<RemoteStopperThread> rstVector = new Vector<RemoteStopperThread>();
            IRunnableRemoteSocietyInstanceReference[] rsocs = instance.getRemoteRunnableSocietyInstanceReferences();
            for (int i = 0; i < rsocs.length; i++) {
                RemoteStopperThread rst = new RemoteStopperThread(rsocs[i], al, 60000);
                rstVector.add(rst);
            }

            instance.setDetailedStatus("The society-instance is going to stop soon. All local agent-instances contained within this society-instance are now being stopped.");
            // stop all agent-instances defined by this societyinstance
            IRunnableAgentInstance[] rai = instance.getRunnableAgentInstances();
            for (int i = 0; i < rai.length; i++) {
                System.err.println("LauncherInterface: stopping agentinstance " + rai);
				al.getRepository().throwProgressUpdateEvent(new ProgressUpdateEvent("Stopping " + rai[i].getName(), ProgressUpdateEvent.PROGRESS_ADVANCE));
                instance.setDetailedStatus("The society-instance is going to stop soon. All local agent-instances contained within this society-instance are now being stopped. Sending 'stop-signal' to agent: " + rai[i].getName());
                try {
                    ModelActionEvent ae = new ModelActionEvent(ModelActionEvent.CMD_STOP_AGENTINSTANCE, rai[i]);
                    modelActionPerformed(ae);
                } catch (ModelActionException mae) {
                    if (concatMAE == null) {
                        concatMAE = mae;
                    } else {
                        concatMAE = new ModelActionException(concatMAE.getShortMessage() + "\n" + mae.getShortMessage(), instance);
                    }
                }
                // System.out.println("Killed " + rai[i].getName());
            }

            RemoteStopperThread runningOne;
            do {
                runningOne = null;
                for (int i = 0; i < rstVector.size(); i++) {
                    RemoteStopperThread rst = rstVector.get(i);
                    if (rst.getResult() == DispatcherThread.STATUS_RUNNING) {
                        runningOne = rst;
                    } else if (rst.getResult() != DispatcherThread.STATUS_SUCCESSFULL) {
                        if (concatMAE == null) {
                            concatMAE = rst.getError();
                        } else {
                            concatMAE = new ModelActionException(concatMAE.getShortMessage() + "\n" + rst.getError().getShortMessage(), instance);
                        }
                    }
                }
                if (runningOne != null) {
                    try {
                        runningOne.join();
                    } catch (InterruptedException e) {
                    }
                }
            } while (runningOne != null);

            if (concatMAE != null) {
                // instance.setStatus(IAbstractRunnable.STATUS_ERROR);
                throw (concatMAE);
            }
            // instance.setStatus(IAbstractRunnable.STATUS_NOT_RUNNING);
            instance.setDetailedStatus("The society-instance is not running. All agent- and society-instances contained within this society-instance have been stopped.");
        }
    }}
