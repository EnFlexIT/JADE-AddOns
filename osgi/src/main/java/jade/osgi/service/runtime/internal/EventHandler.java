package jade.osgi.service.runtime.internal;


import java.util.EventObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;


public abstract class EventHandler {
	protected ScheduledExecutorService scheduler =Executors.newScheduledThreadPool(1);
	protected ScheduledFuture<?> task;
	protected EventObject event;
	JadeRuntimeServiceImpl jadeService;
 
	public EventHandler(EventObject event,
			JadeRuntimeServiceImpl jadeService) {
		this.event = event;
		this.jadeService = jadeService;
		
		handleEvent();
	}
 
	protected abstract void handleEvent();

	class AgentRemover implements Runnable {

		public void run() {
		System.out.println("agentRemover#run ");
			try {
				jadeService.removeAgents();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
