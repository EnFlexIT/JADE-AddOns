package jade.core.security.untraceability;

import java.util.*;

import jade.content.lang.sl.*;
import jade.core.*;
import jade.core.security.*;
import jade.domain.*;
import jade.domain.mobility.*;

/**
 * The base class for untraceable agents.
 *
 * <p>To achieve full functionality, using this class should be acocmpanied with
 * employing untraceable migration enabled behaviours such as
 * <code>GoAheadBehaviour</code> and <code>ComeBackBehaviour</code>.</p>
 *
 * <p>This class implements the specification of <i>Untraceability Protocol I</i>
 * published for the first time in:
 * R.&nbsp;Leszczyna and J.&nbsp;G&#243;rski. Untraceability of mobile agents.
 * <em>Proceedings of the 4th International Joint Conference on
 * Autonomous Agents and Multiagent Systems (AAMAS '05)</em>, 3:1233-1234, July
 * 2005.</p>
 *
 * <p>For protocol's details refer to tha manual of this service.</p>
 *
 * @see jade.core.security.untraceability.behaviours
 *
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 */

public class UntraceableAgent
    extends Agent {

  /**
   *
   * The placeholder for storing the agent's list of locations to pass when
   * heading the destination. This list (the route) should be prepared before
   * the agent is launched for its migration. In fact, when using default
   * predetermined migration behaviours -
   * <code>GoAheadBehaviour</code> and <code>ComeBackBehaviour</code> available
   * in the <code>behaviours</code> package - the agent is unable to move
   * untill the route is ready.
   * The location identifiers in the list are evident (not obfuscated).
   *
  */
  public Stack myUnprotectedRoute;

  /**
   *
   * The placeholder for storing the agent's list of locations to pass when
   * coming back to the base station. This route is composed automatically
   * during the agent's migration towards its destination, according to
   * <i>Untraceability Protocol I</i> specification. The location identifiers
   * in the list are encrypted.
   *
  */
  public Stack myRoute;

  /**
   *
   * Indicates whether the agent's route is already prepared (and stored into the
   * <code>myUnprotectedRoute</code> field).
   *
  */
  public boolean myIsWaitingForRoute;

  /*
   *
   * @todo Providing a secure way for informing the current location about the
   *   location from which an agent came. With the storage of the preeceding
   *   location identifier in the agent state, namely in the
   *   <code>myPredecessorLoc</code>, the undetectable attack is
   *   possible: an attacker can modify the identifier to cause the agent to
   *   visit a malicious location on the agent's route back.
   */
  public Location myPredecessorLoc;

  public transient UntraceabilityHelper myUntraceabilityHelper;

  /**
   *
   * Initializes the agent's state.
   *
  */
  protected void initializeFields() {
    myUnprotectedRoute = new Stack();
    myRoute = new Stack();
    myPredecessorLoc = here();
  }

  /**
   *
   * Initializes services, including the untraceability service.
   *
   * @see jade.core.security.UntraceabilityHelper
  */
  protected void initializeServices() {
    getContentManager().registerLanguage(new SLCodec(),
                                         FIPANames.ContentLanguage.FIPA_SL0);
    getContentManager().registerOntology(MobilityOntology.getInstance());
    try {
      myUntraceabilityHelper = (UntraceabilityHelper) getHelper(
          "jade.core.security.Untraceability");
    }
    catch (ServiceException ex) {
      ex.printStackTrace();
      doDelete();
    }
  }

  /**
   *
   * The agent's startup code.
   *
   * @see jade.core.Agent
  */
  protected void setup() {
    initializeFields();
    initializeServices();
  }

  /**
   * This method is executed every time after the agent is moved to another
   * location.
   */
  protected void afterMove() {
    initializeServices();
  }

  /**
   * This method is executed when the agent reaches its destination.
   */
  public void atDestination() {
  }

  /**
   * This method is executed when the agent is back to its source location.
   */
  public void afterComeBack() {
    initializeFields();
  }
}
