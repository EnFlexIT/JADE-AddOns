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
		t.run();
	}
	
	private boolean isFulfilled() {
		//synchronized (running) {
			return (running.value>=needed);
		//}
	}
	
	public void run() {
		while (!takeDown) {
			try {
				running.wait();
			}
			catch (InterruptedException e) {
			}
			if (isFulfilled()!=currentStatus) {
				changed();
			}
		}
	}
}
