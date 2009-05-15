package jade.osgi.service.agentFactory;

import jade.core.AgentContainer;
import jade.core.BaseService;
import jade.core.IMTPException;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.ServiceException;
import jade.core.management.AgentManagementService;
import jade.osgi.JadeActivator;

public class AgentFactoryService extends BaseService {
	public static final String NAME = AgentFactoryService.class.getName();

	private AgentContainer myContainer;

	public void init(AgentContainer ac, Profile p) throws ProfileException {
		super.init(ac, p);
		myContainer = ac;
	}

	@Override
	public void boot(Profile p) throws ServiceException {
		try {
			AgentManagementService ams = (AgentManagementService) myContainer.getServiceFinder().findService(AgentManagementService.NAME);
			ams.addAgentLoader(new OSGIAgentLoader(JadeActivator.getBundleContext()));
			System.out.println("AgentFactoryService started!");
		} catch(IMTPException e) {
			throw new ServiceException("Cannot retrieve AgentManagementService.", e);
		}
	}

	public String getName() {
		return NAME;
	}

}
