package examples.untraceability.roaming;

import jade.*;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;
import jade.wrapper.AgentContainer;

/**
 * The startup class of the example of implementing an untraceable agent.
 * (see the description of the
 * <code>examples.untraceability.roaming</code> package)
 *
 * <p>In the example an agent obtains a list of locations (containers) available
 * in the agent platform, chooses one of the locations as a destination of its
 * migration and moves towards it passing intermediate locations. After
 * reaching the destination it comes back to the source location.</p>
 *
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 * @see examples.untraceability.roaming
 */
public class UntraceableRoaming {

  /**
   * The number of containers to set up in the agent platform.
   */
  static int CONTAINERS_NUMBER = 8;

  /**
   *
   * Sets a number of containers in the agent platform. Creates and runs an
   * untraceable agent.
   * @see examples.untraceability.roaming.UntraceableRoamingAgent
   * @param args String[]
   */
  public static void main(String[] args) {
    String[] defaultArgs = {"-services",
       "jade.core.security.UntraceabilityService;"+
       "jade.core.mobility.AgentMobilityService;"+
       "jade.core.event.NotificationService"};
    Boot.main(defaultArgs);
    try {

      Runtime rt = Runtime.instance();
      Profile p;
      AgentContainer ac;

      rt.setCloseVM(true);

      for (int k = 0; k < CONTAINERS_NUMBER-1; k++) {
        p = new ProfileImpl();
        p.setParameter(p.SERVICES, "jade.core.security.UntraceabilityService;jade.core.mobility.AgentMobilityService;jade.core.event.NotificationService");
        ac = rt.createAgentContainer(p);
      }

      p = new ProfileImpl();
      p.setParameter(p.SERVICES, "jade.core.security.UntraceabilityService;jade.core.mobility.AgentMobilityService;jade.core.event.NotificationService");
      ac = rt.createAgentContainer(p);


      Object reference = new Object();
      Object agentArgs[] = new Object[1];
      agentArgs[0] = reference;

      AgentController uA = ac.createNewAgent("URA",
          "examples.untraceability.roaming.UntraceableRoamingAgent", agentArgs);
      uA.start();

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }
}
