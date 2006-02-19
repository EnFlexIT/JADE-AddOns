package jade.tools.ascml.dependencymanager;

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.launcher.AgentKillThread;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.launcher.AgentLauncherThread;
import jade.tools.ascml.onto.*;
import jade.tools.ascml.onto.Error;

public class RunnableStarter implements ModelChangedListener {
	
	private AgentLauncher al;
	private FunctionalStateController myFunctionalController;

	public RunnableStarter(AgentLauncher al,FunctionalStateController myFunctionalController) {
		this.myFunctionalController=myFunctionalController;
		this.al=al;
	}
	
	public void modelChanged(ModelChangedEvent evt) {
		if (evt.getEventCode() == ModelChangedEvent.STATUS_CHANGED) {
			try {
				IAbstractRunnable absRunnable = (IAbstractRunnable)evt.getModel();
				if (absRunnable.getStatus().equals(new Starting())) {
					startThisModel(absRunnable);
				} else if (absRunnable.getStatus().equals(new Stopping())) {
					stopThisModel(absRunnable);
				}
			} catch (ClassCastException cce) {
				System.err.println("DependencyManager: ClassCastException:");
				System.err.println(cce.toString());
			}
		}
	}

	private void stopThisModel(IAbstractRunnable absRunnable) {
		if (absRunnable instanceof IRunnableAgentInstance) {
			IRunnableAgentInstance agentInstance = (IRunnableAgentInstance)absRunnable;
            AgentKillThread akt = new AgentKillThread(agentInstance, al);
            try {
                akt.getResult();
            } catch (ModelActionException mae) {
				agentInstance.setStatus(new Error());
				agentInstance.setDetailedStatus(mae.getMessage());
            }
		} else if (absRunnable instanceof IRunnableSocietyInstance) {
			stopThisSociety((IRunnableSocietyInstance)absRunnable);
		}
	}

	private void stopThisSociety(IRunnableSocietyInstance societyInstance) {
		IRunnableAgentInstance[] agentInstances = societyInstance.getRunnableAgentInstances();
		for (int i = 0; i < agentInstances.length; i++) {
			agentInstances[i].setStatus(new Stopping());
		}
		IRunnableRemoteSocietyInstanceReference[] remoteSocieties = societyInstance.getRemoteRunnableSocietyInstanceReferences();
		//FIXME: Now, let's stop the remote ones
		/*Vector<RemoteStopperThread> rstVector = new Vector<RemoteStopperThread>();
		IRunnableRemoteSocietyInstanceReference[] rsocs = instance.getRemoteRunnableSocietyInstanceReferences();
		for (int i = 0; i < rsocs.length; i++) {
			RemoteStopperThread rst = new RemoteStopperThread(rsocs[i], al, 60000);
			rstVector.add(rst);
		}*/		
	}

	private void startThisModel(IAbstractRunnable absRunnable) {
		if (absRunnable instanceof IRunnableAgentInstance) {
			IRunnableAgentInstance agentInstance = (IRunnableAgentInstance)absRunnable;
			AgentLauncherThread new_thread = new AgentLauncherThread(agentInstance, al, null);
			try {
				new_thread.getResult();
			}
			catch (ModelActionException e) {
				absRunnable.setStatus(new Error());
				absRunnable.setDetailedStatus(e.getMessage());
			}			
		} else if (absRunnable instanceof IRunnableSocietyInstance) {
			IRunnableSocietyInstance societyInstance = (IRunnableSocietyInstance)absRunnable;
			myFunctionalController.addModel(societyInstance);
			IRunnableAgentInstance[] agentInstances = societyInstance.getRunnableAgentInstances();
			for (int i=0;i<agentInstances.length;i++) {
				try {
					al.getDependencyManager().startThisAgent(agentInstances[i]);
				}
				catch (ModelActionException e) {
					agentInstances[i].setStatus(new Error());
					agentInstances[i].setDetailedStatus(e.getMessage());
				}
			}
			IRunnableRemoteSocietyInstanceReference[] remoteSocieties = societyInstance.getRemoteRunnableSocietyInstanceReferences();
			for (int i=0;i<remoteSocieties.length;i++) {
				//FIXME: Now let's start the remote ones			
			}
		}	
	}

}
