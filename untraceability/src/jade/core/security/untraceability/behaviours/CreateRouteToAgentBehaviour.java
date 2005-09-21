package jade.core.security.untraceability.behaviours;

import java.util.*;

import jade.content.onto.basic.*;
import jade.core.*;
import jade.core.security.untraceability.*;
import jade.domain.*;
import jade.domain.JADEAgentManagement.*;
import jade.domain.mobility.*;
import jade.lang.acl.*;
import jade.proto.*;

/**
 * This behaviour aims at providing the running it untraceable agent, a migration path
 * to another agent which AID (agent identifier) is explicitly given.
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
public class CreateRouteToAgentBehaviour
    extends SimpleAchieveREInitiator {

  public static String conversationID = "agent_locating";

  private ACLMessage request;
  private UntraceableAgent myAgent;
  private boolean myFinished;

  public CreateRouteToAgentBehaviour(UntraceableAgent a, AID aid) {
    super(a, new ACLMessage(ACLMessage.REQUEST));
    myFinished = false;
    myAgent = a;
    myAgent.myIsWaitingForRoute = true;
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
      WhereIsAgentAction whereIsAgentAction = new WhereIsAgentAction();
      whereIsAgentAction.setAgentIdentifier(aid);
      action.setAction(whereIsAgentAction);
      a.getContentManager().fillContent(request, action);
    }
    catch (Exception fe) {
      fe.printStackTrace();
    }
    reset(request);
  }

  public boolean done() {
    return (myFinished);
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
    if (conversationID.compareTo(inform.getConversationId()) != 0) {
      return;
    }
    String content = inform.getContent();
    myFinished = true;
    try {
      Result results = (Result) myAgent.getContentManager().extractContent(
          inform);
      Iterator iterator = results.getItems().iterator();
      if (iterator.hasNext()) {
        Location location = (Location) iterator.next();
        myAgent.addBehaviour(new CreateRouteToDestinationBehaviour( (
            UntraceableAgent) myAgent, location));
      }
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
