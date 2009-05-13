package jade.osgi.service.runtime.internal;

import jade.wrapper.ContainerController;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class JadeRuntimeServiceFactory implements ServiceFactory {

	private ContainerController container;
	
	public JadeRuntimeServiceFactory(ContainerController container) {
		this.container = container;
	}
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		return new JadeRuntimeServiceImpl(container,bundle);
	}

	public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
		System.out.println("JadeRuntimeServiceFactory#ungetService called: removing agent of bundle " +bundle.getSymbolicName());
		if(service instanceof JadeRuntimeServiceImpl) {
			JadeRuntimeServiceImpl jadeService = (JadeRuntimeServiceImpl) service;
			try {
				jadeService.removeAgents();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}