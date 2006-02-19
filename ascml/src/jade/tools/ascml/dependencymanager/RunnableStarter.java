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
import jade.util.Logger;

public class RunnableStarter implements ModelChangedListener {
	
	private AgentLauncher launcher;
	private FunctionalStateController myFunctionalController;

	public RunnableStarter(AgentLauncher launcher,FunctionalStateController myFunctionalController) {
		this.myFunctionalController=myFunctionalController;
		this.launcher=launcher;
	}
	
	public void modelChanged(ModelChangedEvent evt) {
		if (evt.getEventCode() == ModelChangedEvent.STATUS_CHANGED) {
			if (evt.getModel() instanceof IAbstractRunnable) {			
				try {
					IAbstractRunnable absRunnable = (IAbstractRunnable)evt.getModel();
					if (launcher.myLogger.isLoggable(Logger.INFO)) {
						launcher.myLogger.info("Received starting for "+absRunnable.getFullyQualifiedName());
					}					
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
	}

	private void stopThisModel(IAbstractRunnable absRunnable) {
		if (absRunnable instanceof IRunnableAgentInstance) {
			IRunnableAgentInstance agentInstance = (IRunnableAgentInstance)absRunnable;
            AgentKillThread akt = new AgentKillThread(agentInstance, launcher);
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
		//FIXME: Now, let's stop the remote references
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
			AgentLauncherThread new_thread = new AgentLauncherThread(agentInstance, launcher, null);		
		} else if (absRunnable instanceof IRunnableSocietyInstance) {
			IRunnableSocietyInstance societyInstance = (IRunnableSocietyInstance)absRunnable;
			myFunctionalController.addModel(societyInstance);
			IRunnableAgentInstance[] agentInstances = societyInstance.getRunnableAgentInstances();
			for (int i=0;i<agentInstances.length;i++) {
				try {
					launcher.getDependencyManager().startThisAgent(agentInstances[i]);
				}
				catch (ModelActionException e) {
					agentInstances[i].setStatus(new Error());
					agentInstances[i].setDetailedStatus(e.getMessage());
				}
			}
			IRunnableRemoteSocietyInstanceReference[] remoteSocieties = societyInstance.getRemoteRunnableSocietyInstanceReferences();
			for (int i=0;i<remoteSocieties.length;i++) {
				//TODO: Add some mechanism to watch the status of remote societies
				launcher.inquirerAndStartRemoteSociety(remoteSocieties[i]);
			}
		}	
	}

}
