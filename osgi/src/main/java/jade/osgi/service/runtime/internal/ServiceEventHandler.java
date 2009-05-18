package jade.osgi.service.runtime.internal;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.ServiceEvent;

public class ServiceEventHandler {
	private ScheduledExecutorService scheduler =Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> task;private ServiceEvent serviceEvent;
	final JadeRuntimeServiceImpl jadeService;
 
	public ServiceEventHandler(ServiceEvent serviceEvent,
			JadeRuntimeServiceImpl jadeService) {
		this.serviceEvent = serviceEvent;
		this.jadeService = jadeService;
		
		handleEvent();
	}
 
	private void handleEvent() {
		System.out.println(serviceEvent.getSource().getClass());
		System.out.println(serviceEvent.getServiceReference().getClass().getName());
		System.out.println(serviceEvent.getServiceReference().getBundle().getSymbolicName());
		switch (serviceEvent.getType()) {
		case ServiceEvent.REGISTERED:
			
			System.out.println("check and eventually cancel agent remover");
			if(task != null) {
				task.cancel(true);
				System.out.println("task remover cancelled");
			}
			System.out.println("restarting agents");
			try {
					if(jadeService!= null)
						jadeService.restartAgents();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			break;

		case ServiceEvent.UNREGISTERING:
			System.out.println("UNREGISTERING");
			task = scheduler.schedule(new AgentRemover(), 10000, TimeUnit.MILLISECONDS);
			System.out.println("task remover scheduled:  is completed: "+task.isDone());

			break;
		default:
			break;
		}

	}
	class AgentRemover implements Runnable {

		public void run() {
			System.out.println("agentRemover#run for bundle "+serviceEvent.getServiceReference().getBundle().getBundleId());
			try {
				jadeService.removeAgents();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
