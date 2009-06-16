package jade.osgi.service.runtime.internal;

import jade.osgi.AgentManager;
import java.util.EventObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;

public class OsgiEventHandler {
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> task;
	private AgentManager agentManager;
	private static final int shift = 10;
	private static final int SERVICE_REGISTERED = ServiceEvent.REGISTERED+shift;
	private static final int SERVICE_UNREGISTERING = ServiceEvent.UNREGISTERING+shift;
	

	public OsgiEventHandler(AgentManager am) {
		this.agentManager = am;
	}

	public  void handleEvent(EventObject event) {
		String symbolicName = null;
		int eventType = -99;
		if(event instanceof BundleEvent) {
			symbolicName = ((BundleEvent) event).getBundle().getSymbolicName();
			eventType = ((BundleEvent) event).getType();
		}
		else if (event instanceof ServiceEvent) {
			symbolicName = ((ServiceEvent) event).getServiceReference().getBundle().getSymbolicName();
			eventType = ((ServiceEvent) event).getType()+shift;
		}
		switch(eventType) {
			case BundleEvent.INSTALLED:
				System.out.println("Bundle " + symbolicName + " INSTALLED");
				break;
				
			case BundleEvent.STARTED:
				System.out.println("Bundle " + symbolicName + " STARTED");
				break;

			case BundleEvent.STOPPED:
				System.out.println("Bundle " + symbolicName + " STOPPED");
				try {
					if(agentManager.killAgents(symbolicName)) {
						task = scheduler.schedule(new AgentRemover(symbolicName), 10000, TimeUnit.MILLISECONDS);
						System.out.println("Task remover scheduled for bundle  " + symbolicName);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;

			case BundleEvent.UPDATED:
				System.out.println("Bundle " + symbolicName + " UPDATED");
				System.out.println("task scheduled " + task);
				if(task != null) {
					System.out.println("task remover to be canceled for bundle: " + symbolicName);
					task.cancel(true);
					System.out.println("task remover cancelled for bundle "+symbolicName);
				}
				break;
				
			case SERVICE_REGISTERED:
				System.out.println(symbolicName+") REGISTERED");
				System.out.println("JADE_OSGI: restarting agents");
				try {
					agentManager.restartAgents(symbolicName);
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;

			case SERVICE_UNREGISTERING:
				System.out.println(symbolicName+") UNREGISTERED");
				break;

			default:
				System.out.println("Bundle " + symbolicName + " EVENT " + eventType);
				break;
		}

	}
	
	class AgentRemover implements Runnable {
		
		private String symbolicName;
		
		public AgentRemover(String symbolicName) {
			this.symbolicName = symbolicName;
		}

		public void run() {
			System.out.println("agent remover  task  for bundle "+symbolicName);
			try {
				agentManager.removeAgents(symbolicName);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}
}
