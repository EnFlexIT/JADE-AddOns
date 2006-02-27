package jade.tools.ascml.dependencymanager;

import jade.tools.ascml.absmodel.IAbstractRunnable;

public class TypeCountWatcher extends AbstractDependencyWatcher{
	
	private int needed;
	private MutableInteger running;
	private String agentType;
	private IAbstractRunnable absRunnable;
	private boolean currentStatus;;
	
	public TypeCountWatcher(AbstractDependencyRecord parentDeps, String agentType,IAbstractRunnable absRunnable,int needed, MutableInteger running) {
		super(parentDeps);
		this.agentType = agentType;
		this.absRunnable = absRunnable;
		this.needed = needed;
		this.running = running;
		currentStatus = false;
		t.start();
	}
	
	private boolean isFulfilled() {
		//synchronized (running) {
			return (running.value>=needed);
		//}
	}
	
	public void run() {
		while (!takeDown) {
			try {
				synchronized (running) {
					System.err.println(agentType+" is now waiting on running");
					running.wait();
					System.err.println(agentType+" finished waiting on running");
				}
			}
			catch (InterruptedException e) {
			}
			if (isFulfilled()!=currentStatus) {
				changed();
			}
		}
	}
}
