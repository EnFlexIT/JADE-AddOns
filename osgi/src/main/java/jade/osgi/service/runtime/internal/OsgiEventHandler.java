package jade.osgi.service.runtime.internal;

import jade.osgi.AgentManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.osgi.framework.BundleEvent;

public class OsgiEventHandler {

	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private String symbolicName;
	private AgentManager agentManager;
	private boolean restartAgents;
	private long restartAgentsTimeout;
	private ScheduledFuture<?> task;

	public OsgiEventHandler(String symbolicName, AgentManager am, boolean restartAgents, long restartAgentsTimeout) {
		this.symbolicName = symbolicName;
		this.agentManager = am;
		this.restartAgents = restartAgents;
		this.restartAgentsTimeout = restartAgentsTimeout;
	}

	public void handleEvent(BundleEvent event) {
		int eventType = event.getType();

		switch(eventType) {
			case BundleEvent.INSTALLED:
				System.out.println(symbolicName + " INSTALLED");
				break;

			case BundleEvent.STARTED:
				System.out.println(System.currentTimeMillis()+ " " +symbolicName + " STARTED");
				try {
					if(restartAgents && task != null) { // FIXME task null ? add comment
						System.out.println("AgentRemover for " + symbolicName + " CANCELED");
						task.cancel(true);
						agentManager.restartAgents(symbolicName);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;

			case BundleEvent.STOPPED:
				System.out.println(System.currentTimeMillis()+ " " +symbolicName + " STOPPED");
				try {
					if(agentManager.killAgents(symbolicName)) {
						if(restartAgents) {
							task = scheduler.schedule(new AgentRemover(), restartAgentsTimeout, TimeUnit.MILLISECONDS);
							System.out.println("AgentRemover for " + symbolicName + " SCHEDULED");
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;

			case BundleEvent.UPDATED:
				System.out.println(System.currentTimeMillis()+ " " +symbolicName + " UPDATED");
				if(restartAgents && task != null) { // FIXME task null ? add comment
					agentManager.bundleUpdated(symbolicName);
				}
				break;

			default:
				System.out.println("Bundle " + symbolicName + " EVENT " + eventType);
				break;
		}

	}

	class AgentRemover implements Runnable {

		public void run() {
			System.out.println("AgentRemover for " + symbolicName + " EXECUTED");
			try {
				agentManager.removeAgents(symbolicName);
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
