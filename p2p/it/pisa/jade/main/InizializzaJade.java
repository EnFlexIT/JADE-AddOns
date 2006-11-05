/**
 * 
 */
package it.pisa.jade.main;



import it.pisa.jade.util.AgentName;
import it.pisa.jade.util.FactoryAgent;
import it.pisa.jade.util.FactoryUtil;
import it.pisa.jade.util.WrapperErrori;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.management.AgentManagementService;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * @author Domenico Trimboli
 * 
 */
public class InizializzaJade {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();

		// Exit the JVM when there are no more containers around
		rt.setCloseVM(true);
		// Launch a complete platform on the 1099 port
		// create a default Profile
		Profile pMain = new ProfileImpl(null, 1099, null);
		
		pMain.setParameter(AgentManagementService.AGENTS_PATH,FactoryUtil.getApplicationPath());
		String service=pMain.getParameter("services","")+";jade.core.migration.InterPlatformMobilityService";
		pMain.setParameter("services",service);
		//pMain.setParameter("services","jade.core.mobility.AgentMobilityService");
		System.out.println("Sto per lanciare la piattaforma..." + pMain);
		AgentContainer mc = rt.createMainContainer(pMain);
		
		lanciaMulticastManager(mc);
		lanciaFederator(mc);
		lanciaGuiAgent(mc);
	}
	private static void lanciaGuiAgent(AgentContainer mc) {
		System.out.println("Sto per lanciare il gui Agent");
		AgentController subDF;
		try {
			subDF = mc.createNewAgent(AgentName.guiAgent.name(),
					FactoryAgent.getAgent(AgentName.guiAgent).getName(), new Object[0]);
			subDF.start();
		} catch (StaleProxyException e) {
			
		}
		System.out.println("Agente gui Agent lanciato");
	}

	private static void lanciaMulticastManager(AgentContainer mc) {
		System.out.println("Sto per lanciare il multicast manager");
		AgentController multicastManager;
		try {
			multicastManager = mc.createNewAgent(AgentName.multicastManagerAgent
					.name(), FactoryAgent.getAgent(
					AgentName.multicastManagerAgent).getName(), new Object[0]);
			multicastManager.start();
		} catch (StaleProxyException e) {
			WrapperErrori.wrap("", e);
		}
		System.out.println("Agente multicast manager lanciato");
	}

	private static void lanciaFederator(AgentContainer mc) {
		System.out.println("Sto per lanciare l'agente federator");
		AgentController federator;
		try {
			federator = mc.createNewAgent(AgentName.federatorAgent.name(),
					FactoryAgent.getAgent(AgentName.federatorAgent).getName(),
					new Object[0]);
			federator.start();
		} catch (StaleProxyException e) {
			WrapperErrori.wrap("", e);
		}
		System.out.println("Agente federator lanciato");
	}


}
