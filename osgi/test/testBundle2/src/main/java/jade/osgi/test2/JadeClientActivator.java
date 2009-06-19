package jade.osgi.test2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public class JadeClientActivator implements BundleActivator {

//	ServiceReference jadeRef;
	private MyBundleAgentFactory agentFactory;
	private ScheduledFuture<?> updateTask;
	private BundleContext context;
	

	public void start(BundleContext context) throws Exception {
		this.context = context;
//		jadeRef = context.getServiceReference(JadeRuntimeService.class.getName());
//		if(jadeRef != null) {
//			JadeRuntimeService jade = (JadeRuntimeService) context.getService(jadeRef);
//			AgentController ac = jade.createAgent("createbyTestBundle2", "jade.osgi.test.MyBundleAgent", "testBundle.testBundle", null);
//			ac.start();
//		}

		agentFactory = new MyBundleAgentFactory();
		agentFactory.init(context.getBundle());

		try {
			System.out.println("Searching for PackageAdmin service");
			ServiceReference[] srs = context.getServiceReferences(PackageAdmin.class.getName(), null);
			if(srs != null) {
				PackageAdmin pa = (PackageAdmin) context.getService(srs[0]);
				System.out.println("PackageAdmin Service FOUND");
				Bundle[] bundles = pa.getBundles("testBundle.testBundle", null);
				System.out.println("Bundles founded "+ bundles);
				if(bundles != null) {
					Bundle testBundle = bundles[0];
					ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
					BundleUpdater bu = new BundleUpdater(testBundle);
					updateTask = scheduler.scheduleAtFixedRate(bu, 5000, 5000, TimeUnit.MILLISECONDS);
				}
				context.ungetService(srs[0]);
			} else {
				System.out.println("Cannot retrieve PackageAdmin service");
			}
		} catch(InvalidSyntaxException e) {
			System.out.println("Cannot retrieve PackageAdmin service");
			e.printStackTrace();
		}
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println(context.getBundle().getSymbolicName()+ " STOPPING");
//		context.ungetService(jadeRef);
		agentFactory.stop();
		updateTask.cancel(true);
	}
	
	private class BundleUpdater implements Runnable {
		private Bundle b;

		public BundleUpdater(Bundle b) {
			this.b = b;
		}

		public void run() {
			try {
				System.out.println("Updating "+b.getSymbolicName() + "........... my state is "+context.getBundle().getState());
				b.update();
			} catch(BundleException e) {
				System.out.println("Cannot update "+b.getSymbolicName());
				e.printStackTrace();
			}
		}
	}

}
