package jade.core.security.untraceability.behaviours;

import jade.content.onto.basic.*;
import jade.domain.*;
import jade.domain.JADEAgentManagement.*;
import jade.domain.mobility.*;
import jade.lang.acl.*;
import jade.proto.*;
import java.util.Iterator;
import java.util.Vector;
import java.util.Collection;
import jade.core.security.untraceability.UntraceableAgent;
import jade.core.Location;

/**
 * This behaviour aims at providing the running it untraceable agent, a migration path
 * to a location which description is explicitly given.
 *
 * The behaviour is dedicated to agents instantiating the
 * <code>jade.core.security.untraceability.UntraceableAgent</code> class or its
 * subclass.
 *
 * The path is stored in the agent's state
 * (in the <code>jade.core.security.untraceability.UntraceableAgent.myUnprotectedRoute</code>).
 *
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 * @see jade.core.security.untraceability
 * @see jade.core.security.untraceability.behaviours.GoAheadbehaviour
 * @see jade.core.security.untraceability.behaviours.UntraceableAgent
 */
public class CreateRouteToDestinationBehaviour
    extends SimpleAchieveREInitiator {

  private ACLMessage request;
  private UntraceableAgent myAgent;
  private Location myDestination;
  public static String conversationID = "route_to_destination_creation";

  public CreateRouteToDestinationBehaviour(UntraceableAgent a, Location destination) {
    super(a, new ACLMessage(ACLMessage.REQUEST));

    myAgent = a;
    myAgent.myIsWaitingForRoute = true;
    myDestination = destination;

    request = (ACLMessage) getDataStore().get(REQUEST_KEY);
    request.clearAllReceiver();
    request.addReceiver(a.getAMS());
    request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
    request.setOntology(MobilityOntology.NAME);
    request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    request.setConversationId(conversationID);
    try {
      Action action = new Action();
      action.setActor(a.getAMS());
      action.setAction(new QueryPlatformLocationsAction());
      a.getContentManager().fillContent(request, action);
    }
    catch (Exception fe) {
      fe.printStackTrace();
    }
    reset(request);
  }

  protected void handleNotUnderstood(ACLMessage reply) {
    System.out.println(myAgent.getLocalName() + " handleNotUnderstood : " +
                       reply.toString());
  }

  protected void handleRefuse(ACLMessage reply) {
    System.out.println(myAgent.getLocalName() + " handleRefuse : " +
                       reply.toString());
  }

  protected void handleFailure(ACLMessage reply) {
    System.out.println(myAgent.getLocalName() + " handleFailure : " +
                       reply.toString());
  }

  protected void handleAgree(ACLMessage reply) {
  }

  protected void handleInform(ACLMessage inform) {
    if (conversationID.compareTo(inform.getConversationId()) != 0) return;
    String content = inform.getContent();
    try {
      Result results = (Result) myAgent.getContentManager().extractContent(
          inform);
      Iterator iterator = results.getItems().iterator();
      String currentContainerName = myAgent.getContainerController().getContainerName();
      myAgent.myUnprotectedRoute.push(myDestination);
      while (iterator.hasNext()) {
        Object next = iterator.next();
        //Assuring that the base container won't be put to the LIFO
        if (next.toString().startsWith(currentContainerName) == false)
          if (next.toString().startsWith(myDestination.getName()) == false)
            myAgent.myUnprotectedRoute.push(next);
      }
      myAgent.myIsWaitingForRoute = false;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  void printRoute(){
    System.out.println("myAgent.myUnprotectedRoute:");
    for (int k=0; k<myAgent.myUnprotectedRoute.size(); k++)
      System.out.println(myAgent.myUnprotectedRoute.elementAt(k));
  }
}
