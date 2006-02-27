package jade.tools.ascml.dependencymanager;

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.onto.*;

import java.util.HashSet;

public abstract class AbstractDependencyRecord {
	protected HashSet<String> neededAgentNames;
	protected HashSet<String> runningAgentNames;
	protected HashSet<String> runningSocietyNames;
	protected HashSet<String> neededSocietyNames;
	protected HashSet<AbstractDependencyWatcher> runningWatcherTypes;
	protected HashSet<AbstractDependencyWatcher> neededWatcherTypes;	
	protected IAbstractRunnable absRunnable;

	public AbstractDependencyRecord(IAbstractRunnable absRunnable) {
		this.absRunnable = absRunnable;
		neededAgentNames = new HashSet<String>();
		runningAgentNames = new HashSet<String>();
		neededSocietyNames = new HashSet<String>();
		runningSocietyNames = new HashSet<String>();
		neededWatcherTypes = new HashSet<AbstractDependencyWatcher>();		
		runningWatcherTypes = new HashSet<AbstractDependencyWatcher>();
	}

	protected abstract void checkStatus();

	public void addWatcherDependency(AbstractDependencyWatcher tcw) {
		runningWatcherTypes.add(tcw);
		if (absRunnable.getStatus().equals(new Functional())) {
			absRunnable.setStatus(new NonFunctional());
		}
	}

	public void addAgentDependency(String name) {
		neededAgentNames.add(name);
		if (absRunnable.getStatus().equals(new Functional())) {
			absRunnable.setStatus(new NonFunctional());
		}
	}

	public void addSocietyDependency(String name) {
		neededSocietyNames.add(name);
		if (absRunnable.getStatus().equals(new Functional())) {
			absRunnable.setStatus(new NonFunctional());
		}
	}	

	protected void watcherDependencyFulfilled(AbstractDependencyWatcher watcher) {
		runningWatcherTypes.add(watcher);
		neededWatcherTypes.remove(watcher);
		checkStatus();
	}

	protected void watcherDependencyFailed(AbstractDependencyWatcher watcher) {
		neededWatcherTypes.add(watcher);
		runningWatcherTypes.remove(watcher);
		checkStatus();
	}

	public void dependencyChanged(AbstractDependencyWatcher watcher) {
		if (runningWatcherTypes.contains(watcher)) {
			watcherDependencyFulfilled(watcher);
		} else {
			watcherDependencyFailed(watcher);
		}

	}

	public void agentBorn(String agentName) {
		if (neededAgentNames.remove(agentName)) {
			runningAgentNames.add(agentName);
			checkStatus();
		}
	}

	public void agentDied(String agentName) {
		if (runningAgentNames.remove(agentName)) {
			neededAgentNames.add(agentName);
			checkStatus();
		}
	}

	public void updateModel(String societyName, Status socStatus) {
		if (socStatus.equals(new Functional())) {
			neededSocietyNames.remove(societyName);
			runningSocietyNames.add(societyName);
			checkStatus();
		} else if (runningSocietyNames.contains(societyName)) {
			runningSocietyNames.remove(societyName);
			neededSocietyNames.add(societyName);
			checkStatus();
		}
	}
}
