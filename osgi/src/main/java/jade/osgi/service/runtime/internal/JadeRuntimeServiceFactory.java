package jade.osgi.service.runtime.internal;

import java.util.HashMap;
import java.util.Map;

import jade.wrapper.ContainerController;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;

public class JadeRuntimeServiceFactory implements ServiceFactory, ServiceListener, BundleListener {

	private ContainerController container;
	private Map<Long,JadeRuntimeServiceImpl> usedJadeServices=new HashMap<Long, JadeRuntimeServiceImpl>();
	
	public JadeRuntimeServiceFactory(ContainerController container) {
		this.container = container;
	}
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		
		System.out.println("JadeRuntimeServiceFactory#getService "+bundle.getSymbolicName());
		JadeRuntimeServiceImpl jadeService = new JadeRuntimeServiceImpl(container,bundle);
		usedJadeServices.put(bundle.getBundleId(),jadeService);
		return jadeService;
	}

	public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
		System.out.println("JadeRuntimeServiceFactory#ungetService called: removing agent of bundle " +bundle.getSymbolicName());
		//usedJadeServices.remove(bundle.getBundleId());
//		if(service instanceof JadeRuntimeServiceImpl) {
//			JadeRuntimeServiceImpl jadeService = (JadeRuntimeServiceImpl) service;
//			try {
//				jadeService.removeAgents();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	public void serviceChanged(ServiceEvent serviceEvent) {
		System.out.println("JadeRuntimeServiceFactory#serviceChanged called: "+serviceEvent.getType());
		new ServiceEventHandler(serviceEvent, usedJadeServices.get(serviceEvent.getServiceReference().getBundle().getBundleId()));
	}
	public void bundleChanged(BundleEvent bundleEvent) {
		System.out.println("JadeRuntimeServiceFactory#bundleChanged called: " +bundleEvent.getBundle().getBundleId());
		System.out.println("usedJadeServices count "+usedJadeServices.size());
		if(usedJadeServices.containsKey(bundleEvent.getBundle().getBundleId())) {
			new BundleEventHandler(bundleEvent,usedJadeServices.get(bundleEvent.getBundle().getBundleId()));
			
		}
	}

}