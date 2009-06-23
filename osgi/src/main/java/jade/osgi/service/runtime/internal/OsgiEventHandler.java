package jade.osgi.service.runtime.internal;

import jade.osgi.internal.AgentManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.osgi.framework.BundleEvent;

public class OsgiEventHandler {

	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private String bundleIdentifier;
	private AgentManager agentManager;
	private boolean restartAgents;
	private long restartAgentsTimeout;
	private ScheduledFuture<?> task;

	public OsgiEventHandler(String bundleIdentifier, AgentManager am, boolean restartAgents, long restartAgentsTimeout) {
		this.bundleIdentifier = bundleIdentifier;
		this.agentManager = am;
		this.restartAgents = restartAgents;
		this.restartAgentsTimeout = restartAgentsTimeout;
	}

	public void handleEvent(BundleEvent event) {
		int eventType = event.getType();

		switch(eventType) {
			case BundleEvent.INSTALLED:
				System.out.println(bundleIdentifier + " INSTALLED");
				break;

			case BundleEvent.STARTED:
				System.out.println(System.currentTimeMillis()+ " " +bundleIdentifier + " STARTED");
				try {
					if(restartAgents && task != null) { // FIXME task null ? add comment
						System.out.println("AgentRemover for " + bundleIdentifier + " CANCELED");
						task.cancel(true);
						agentManager.restartAgents(bundleIdentifier);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;

			case BundleEvent.STOPPED:
				System.out.println(System.currentTimeMillis()+ " " +bundleIdentifier + " STOPPED");
				try {
					if(agentManager.killAgents(bundleIdentifier)) {
						if(restartAgents) {
							task = scheduler.schedule(new AgentRemover(), restartAgentsTimeout, TimeUnit.MILLISECONDS);
							System.out.println("AgentRemover for " + bundleIdentifier + " SCHEDULED");
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;

			case BundleEvent.UPDATED:
				System.out.println(System.currentTimeMillis()+ " " +bundleIdentifier + " UPDATED");
				if(restartAgents && task != null) { // FIXME task null ? add comment
					agentManager.bundleUpdated(bundleIdentifier);
				}
				break;

			default:
				System.out.println("Bundle " + bundleIdentifier + " EVENT " + eventType);
				break;
		}

	}

	class AgentRemover implements Runnable {

		public void run() {
			System.out.println("AgentRemover for " + bundleIdentifier + " EXECUTED");
			try {
				agentManager.removeAgents(bundleIdentifier);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void stop() {
		if(task != null) {
			task.cancel(true);
		}
	}
}
