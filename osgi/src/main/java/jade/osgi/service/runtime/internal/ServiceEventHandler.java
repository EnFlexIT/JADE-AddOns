package jade.osgi.service.runtime.internal;


import java.util.EventObject;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.ServiceEvent;

public class ServiceEventHandler extends EventHandler {
 
 
	public ServiceEventHandler(EventObject event,
			JadeRuntimeServiceImpl jadeService) {
		super(event, jadeService);
	}
	protected void handleEvent() {
		ServiceEvent serviceEvent = (ServiceEvent) event;
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

}
