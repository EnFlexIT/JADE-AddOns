package jade.osgi.service.runtime.internal;

import jade.osgi.AgentManager;
import java.util.EventObject;
import java.util.concurrent.TimeUnit;
import org.osgi.framework.BundleEvent;

public class BundleEventHandler extends EventHandler {

	public BundleEventHandler(EventObject event, AgentManager am) {
		super(event, am);
	}

	protected void handleEvent() {
		BundleEvent bundleEvent = (BundleEvent) event;
		String symbolicName = bundleEvent.getBundle().getSymbolicName();

		switch(bundleEvent.getType()) {
			case BundleEvent.INSTALLED:
				System.out.println("Bundle " + symbolicName + " INSTALLED");
				break;
				
			case BundleEvent.STARTED:
				System.out.println("Bundle " + symbolicName + " STARTED");
				break;

			case BundleEvent.STOPPED:
				System.out.println("Bundle " + symbolicName + " STOPPED");
				try {
					agentManager.killAgents(symbolicName);
				} catch(Exception e) {
					e.printStackTrace();
				}
				task = scheduler.schedule(new AgentRemover(symbolicName), 10000, TimeUnit.MILLISECONDS);
				System.out.println("Task remover scheduled: is completed: " + task.isDone());
				break;

			case BundleEvent.UPDATED:
				System.out.println("Bundle " + symbolicName + " UPDATED");
				if(task != null) {
					System.out.println("task remover to be canceled:  is completed: " + task.isDone());
					task.cancel(true);
					System.out.println("task remover cancelled");
				}
				break;
				
			default:
				System.out.println("Bundle " + symbolicName + " EVENT " + bundleEvent.getType());
				break;
		}

	}

}
