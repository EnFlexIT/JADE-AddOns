package jade.tools.ascml.dependencymanager;

public abstract class AbstractDependencyWatcher implements Runnable {

	protected AbstractDependencyRecord parentDeps;
	protected boolean takeDown;
	protected Thread t;
	
	public AbstractDependencyWatcher (AbstractDependencyRecord parentDeps) {
		this.parentDeps = parentDeps;
		takeDown = false;	
		t = new Thread(this);
	}
	
	
	public synchronized void takeDown() {
		takeDown = true;
		t.interrupt();
	}
	
	protected void changed() {
		//synchronized (parentDeps) {
			parentDeps.dependencyChanged(this);
		//}
	}
	
}
