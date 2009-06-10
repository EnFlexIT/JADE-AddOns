package jade.osgi.service.runtime.internal;

import jade.osgi.AgentManager;
import java.util.EventObject;
import java.util.concurrent.TimeUnit;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceEvent;

public class ServiceEventHandler extends EventHandler {

	public ServiceEventHandler(EventObject event, AgentManager am) {
		super(event, am);
	}
	
	protected void handleEvent() {
		ServiceEvent serviceEvent = (ServiceEvent) event;
		String symbolicName = getSymbolicName(serviceEvent);
		switch(serviceEvent.getType()) {
			case ServiceEvent.REGISTERED:
				System.out.println(serviceEvent.getServiceReference().getClass().getName()+"("+symbolicName+") REGISTERED");
				System.out.println("JADE_OSGI: check and eventually cancel agent remover");
				if(task != null) {
					task.cancel(true);
					System.out.println("JADE_OSGI: task remover cancelled");
				}
				System.out.println("JADE_OSGI: restarting agents");
				try {
					agentManager.restartAgents(symbolicName);
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;

			case ServiceEvent.UNREGISTERING:
				System.out.println(serviceEvent.getServiceReference().getClass().getName()+"("+symbolicName+") UNREGISTERED");
				if(symbolicName != null) {
    				task = scheduler.schedule(new AgentRemover(symbolicName), 10000, TimeUnit.MILLISECONDS);
    				System.out.println("task remover scheduled: is completed: " + task.isDone());
				}
				break;
			default:
				break;
		}

	}
	
	private String getSymbolicName(ServiceEvent serviceEvent) {
		String symbolicName = null;
		Bundle bundle = serviceEvent.getServiceReference().getBundle();
		if(bundle != null) {
			symbolicName = bundle.getSymbolicName();
		}
		return symbolicName;
	}


}
