package it.pisa.jade.main;

import it.pisa.jade.util.AgentName;
import it.pisa.jade.util.FactoryAgent;
import it.pisa.jade.util.FactoryUtil;
import it.pisa.jade.util.Parameter;
import it.pisa.jade.util.Values;
import it.pisa.jade.util.WrapperErrori;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.management.AgentManagementService;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class ApplicationMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();

		// Exit the JVM when there are no more containers around
		rt.setCloseVM(true);
		// Launch a complete platform on the Parameter port
		// create a default Profile
		Profile pMain = new ProfileImpl(null, Parameter
				.getInt(Values.jadePortNumber), null);
		if (Parameter.get(Values.mobileInterplatformService)!=null&&Parameter.get(Values.mobileInterplatformService).equals(Parameter.YES)) {
			pMain.setParameter(AgentManagementService.AGENTS_PATH, Parameter
					.get(Values.mobileInterplatformPath) != null ? Parameter
					.get(Values.mobileInterplatformPath) : FactoryUtil
					.getApplicationPath());
			String service = pMain.getParameter("services", "")
					+ ";jade.core.migration.InterPlatformMobilityService";
			pMain.setParameter("services", service);
		}
		System.out.println("Launch platform..." + pMain);
		AgentContainer mc = rt.createMainContainer(pMain);

		lanciaMulticastManager(mc);
		lanciaFederator(mc);
		lanciaGuiAgent(mc);
	}

	private static void lanciaGuiAgent(AgentContainer mc) {
		System.out.println("Launch guiAgent");
		AgentController subDF;
		try {
			subDF = mc.createNewAgent(AgentName.guiAgent.name(), FactoryAgent
					.getAgent(AgentName.guiAgent).getName(), new Object[0]);
			subDF.start();
		} catch (StaleProxyException e) {

		}
		System.out.println("guiAgent launched");
	}

	private static void lanciaMulticastManager(AgentContainer mc) {
		System.out.println("Launch multicastManagerAgent");
		AgentController multicastManager;
		try {
			multicastManager = mc.createNewAgent(AgentName.multicastManagerAgent
					.name(), FactoryAgent.getAgent(AgentName.multicastManagerAgent)
					.getName(), new Object[0]);
			multicastManager.start();
		} catch (StaleProxyException e) {
			WrapperErrori.wrap("", e);
		}
		System.out.println("multicastManagerAgent launched");
	}

	private static void lanciaFederator(AgentContainer mc) {
		System.out.println("Launch federatorAgent");
		AgentController federator;
		try {
			federator = mc.createNewAgent(AgentName.federatorAgent.name(),
					FactoryAgent.getAgent(AgentName.federatorAgent).getName(),
					new Object[0]);
			federator.start();
		} catch (StaleProxyException e) {
			WrapperErrori.wrap("", e);
		}
		System.out.println("agentFederator launched");
	}

}
