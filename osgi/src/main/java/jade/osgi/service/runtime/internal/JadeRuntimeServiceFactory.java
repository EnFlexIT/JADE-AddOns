package jade.osgi.service.runtime.internal;

import jade.osgi.AgentManager;
import jade.wrapper.ContainerController;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class JadeRuntimeServiceFactory implements ServiceFactory {

	private ContainerController container;
	private Map<Long,JadeRuntimeServiceImpl> usedJadeServices=new HashMap<Long, JadeRuntimeServiceImpl>();
	private AgentManager agentManager;
	
	public JadeRuntimeServiceFactory(ContainerController container, AgentManager agentManager) {
		this.container = container;
		this.agentManager = agentManager;
	}
	
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		//System.out.println("JadeRuntimeServiceFactory#getService "+bundle.getSymbolicName());
		JadeRuntimeServiceImpl jadeService = new JadeRuntimeServiceImpl(container, agentManager, bundle);
		usedJadeServices.put(bundle.getBundleId(),jadeService);
		return jadeService;
	}

	public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
		//System.out.println("JadeRuntimeServiceFactory#ungetService called for bundle " +bundle.getSymbolicName());
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

}