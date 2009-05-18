package jade.osgi.service.runtime.internal;


import java.util.EventObject;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleEvent;

public class BundleEventHandler extends EventHandler {

	public BundleEventHandler(EventObject event,
			JadeRuntimeServiceImpl jadeService) {
		super(event,jadeService);
	}
	protected void handleEvent() {
		BundleEvent bundleEvent = (BundleEvent) event;
		System.out.println("Event received from: "+bundleEvent.getBundle());

		switch (bundleEvent.getType()) {
		case BundleEvent.INSTALLED:
			System.out.println("Bundle "+bundleEvent.getBundle().getSymbolicName()+" INSTALLED");
			break;
		case BundleEvent.STARTED:
			System.out.println("Bundle "+bundleEvent.getBundle().getSymbolicName()+" STARTED");
			break;

		case BundleEvent.STOPPED:
			System.out.println("Bundle "+bundleEvent.getBundle().getSymbolicName()+" STOPPED");
			try {
					jadeService.killAgents();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			task = scheduler.schedule(new AgentRemover(), 10000, TimeUnit.MILLISECONDS);
			System.out.println("task remover scheduled:  is completed: "+task.isDone());
			break;

		case BundleEvent.UPDATED:
			System.out.println("Bundle "+bundleEvent.getBundle().getSymbolicName()+" UPDATED");
			if(task != null) {
				System.out.println("task remover to be canceled:  is completed: "+task.isDone());
				task.cancel(true);
				System.out.println("task remover cancelled");
			}
			break;
		default:
			System.out.println("Bundle "+bundleEvent.getBundle().getSymbolicName()+" EVENT "+bundleEvent.getType());

			break;
		}
	
	}

}
