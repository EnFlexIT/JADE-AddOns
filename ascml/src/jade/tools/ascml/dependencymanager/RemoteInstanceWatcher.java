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
		//FIXME: Remote Dependecies
		// 1. Test if the remote ASCML has the model
		// 2. Check the status of the model
		// 3. If it isn't running start it
		// 4. Wait for it to be started and update the dependecy
	}

}
