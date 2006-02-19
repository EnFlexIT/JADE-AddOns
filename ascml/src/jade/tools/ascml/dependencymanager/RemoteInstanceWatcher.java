package jade.tools.ascml.dependencymanager;

import jade.tools.ascml.absmodel.IAgentInstanceDependency;
import jade.tools.ascml.launcher.AgentLauncher;

public class RemoteInstanceWatcher extends AbstractDependencyWatcher{
	
	private AgentLauncher launcher;

	public RemoteInstanceWatcher(AbstractDependencyRecord parentDeps, AgentLauncher launcher, IAgentInstanceDependency instDep) {
		super(parentDeps);
		this.launcher=launcher;
		t.run();
	}

	public void run() {
		//FIXME: fill me
	}

}
