package jade.osgi.service.runtime.internal;

import jade.osgi.AgentManager;
import java.util.EventObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public abstract class EventHandler {
	protected ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	protected ScheduledFuture<?> task;
	protected EventObject event;
	AgentManager agentManager;

	public EventHandler(EventObject event, AgentManager am) {
		this.event = event;
		this.agentManager = am;
		handleEvent();
	}

	protected abstract void handleEvent();
	
	class AgentRemover implements Runnable {
		
		private String symbolicName;
		
		public AgentRemover(String symbolicName) {
			this.symbolicName = symbolicName;
		}

		public void run() {
			try {
				agentManager.removeAgents(symbolicName);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}
}
