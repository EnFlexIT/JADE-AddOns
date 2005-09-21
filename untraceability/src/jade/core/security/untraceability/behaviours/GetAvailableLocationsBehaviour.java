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
import jade.core.Agent;

/**
 * A behaviour of obtaining a list of containers set up in the agent platform.
 *
 * The behaviour should be provided with an empty instantion of <code>ResultWrapper</code>
 * (see jade.core.security.untraceability.behaviours.ResultWrapper).
 *
 * When the behaviour is successfully finished, the list of containers
 * is stored as a <code>jade.content.onto.basic.Result</code> wrapped into
 * the <code>ResultWrapper</code> class.
 *
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 * @see jade.core.security.untraceability.behaviours.ResultWrapper
 */
public class GetAvailableLocationsBehaviour
    extends SimpleAchieveREInitiator {

  private ACLMessage request;
  private ResultWrapper myRW;

  public GetAvailableLocationsBehaviour(Agent agent, ResultWrapper rw) {
    super(agent, new ACLMessage(ACLMessage.REQUEST));
    myRW = rw;
    request = (ACLMessage) getDataStore().get(REQUEST_KEY);
    request.clearAllReceiver();
    request.addReceiver(myAgent.getAMS());
    request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
    request.setOntology(MobilityOntology.NAME);
    request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    try {
      Action action = new Action();
      action.setActor(myAgent.getAMS());
      action.setAction(new QueryPlatformLocationsAction());
      myAgent.getContentManager().fillContent(request, action);
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
    String content = inform.getContent();
    try {
      myRW.isEmpty = false;
      myRW.results = (Result) myAgent.getContentManager().extractContent(
          inform);
    }
    catch (Exception e) {
      myRW.isEmpty = true;
      e.printStackTrace();
    }
  }
}
