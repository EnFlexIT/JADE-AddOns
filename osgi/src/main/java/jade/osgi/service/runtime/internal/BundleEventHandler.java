package jade.osgi.service.runtime.internal;

import jade.security.JADESecurityException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleEvent;

public class BundleEventHandler {

	ScheduledExecutorService scheduler =Executors.newScheduledThreadPool(1);
	ScheduledFuture<?> task;
	final BundleEvent bundleEvent;
	final JadeRuntimeServiceImpl jadeService;
	public BundleEventHandler(BundleEvent bundleEvent,
			JadeRuntimeServiceImpl jadeService) {
		this.bundleEvent = bundleEvent;
		this.jadeService = jadeService;
		
		handleEvent();
	}
	private void handleEvent() {
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
			System.out.println("task remover to be canceled:  is completed: "+task.isDone());
			if(task != null) {
				task.cancel(true);
				System.out.println("task remover cancelled");
			}
			break;
		default:
			System.out.println("Bundle "+bundleEvent.getBundle().getSymbolicName()+" EVENT "+bundleEvent.getType());

			break;
		}
	
	}
	class AgentRemover implements Runnable {

		public void run() {
			System.out.println("agentRemover#run for bundle "+bundleEvent.getBundle().getBundleId());
			try {
				jadeService.removeAgents();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
