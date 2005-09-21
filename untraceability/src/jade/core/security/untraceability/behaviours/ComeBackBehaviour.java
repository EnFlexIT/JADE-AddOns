package jade.core.security.untraceability.behaviours;

import jade.core.*;
import jade.core.behaviours.*;
import jade.core.security.untraceability.*;

/**
 * A behaviour of untraceable returning to the agent's source location
 * dedicated to agents instantiating the <code>jade.core.security.untraceability.UntraceableAgent</code> class or its
 * subclass.
 *
 * <p>The behaviour should be utilized only after using a "going-towards" behaviour
 * responsible for the agent's arrival to its destination and setting up
 * the <code>jade.core.security.untraceability.UntraceableAgent.myRoute</code>
 * field. An example of such "going-towards" behaviour" is
 * <code>jade.core.security.untraceability.behaviours.GoAheadbehaviour</code>
 *
 * <p>The return is possible only if the
 * <code>jade.core.security.untraceability.UntraceableAgent.myRoute</code>
 * agent's field is set with <i>encrypted location identifiers</i>, so any use of
 * the behaviour should be preceded with a utilisation of the "going-towards"
 * behaviour.</p>
 *
 * <p>An agent running this behaviour, moves subsequently to the locations
 * stored on its
 * <code>jade.core.security.untraceability.UntraceableAgent.myRoute</code> stack
 * until it arrives at the source location.</p>
 *
 * <p>This behaviour implements the specification of <i>Untraceability Protocol I</i>
 * published for the first time in:
 * R.&nbsp;Leszczyna and J.&nbsp;G&#243;rski. Untraceability of mobile agents.
 * <em>Proceedings of the 4th International Joint Conference on
 * Autonomous Agents and Multiagent Systems (AAMAS '05)</em>, 3:1233-1234, July
 * 2005.</p>
 *
 * <p>For protocol's details refer to tha manual of this service.</p>
 *
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 * @see jade.core.security.untraceability
 * @see jade.core.security.untraceability.behaviours.GoAheadbehaviour
 * @see jade.core.security.untraceability.behaviours.UntraceableAgent
 */
public class ComeBackBehaviour
    extends Behaviour {

  /**
   * Indicates whether the migration is completed (the agent returned to its base
   * location).
   */
  private boolean finished;

  /**
   * A reference to the owner agent.
   */
  private UntraceableAgent myAgent;

  /**
   * A class default constructor. Apart of standard initialization of class
   * fields an action is performed aiming at moving the running the behaviour
   * back to the preceeding location of its route. Another words: the agent leaves it
   * destination and steps back.
   *
   * @param untraceableAgent a reference to the owner agent
   */
  public ComeBackBehaviour(UntraceableAgent untraceableAgent) {
    myAgent = untraceableAgent;
    finished = false;
    Location location = myAgent.myPredecessorLoc;
    myAgent.myPredecessorLoc = myAgent.here();
    myAgent.doMove(location);
  }

  /**
   * A standard-way overridden base class method.
   *
   * The behaviour is completed when the value of the <code>finished</code>
   * behaviour's private field is set to <code>true</code>.
   * @return a boolean value indicating if the behaviour's task is completed
   * @see jade.core.behaviours.Behaviour
   */
  public boolean done() {
    return finished;
  }

  /**
   * An action performed during migration.
   *
   * <p>An agent removes from the top of its
   * <code>jade.core.security.untraceability.UntraceableAgent.myRoute</code> stack
   * an <i>encrypted location identifier</i> and exctracts the next location on
   * its way back with the aid of the Untraceability JADE kernel-level service
   * (see <code>jade.core.security.untraceability</code>). After having the location
   * moves to it.
   *
   * <p>The action is performed until the agent's current location matches
   * the next one, which according to the specification of the indicates that
   * the agent returned to the source location.</p>
   *
   * @see jade.core.security.untraceability
   * @see jade.core.security.untraceability.UntraceableAgent
   */
  public void action() {
    Location location; //Agent is returning from the target container.
    byte[] aesEncryptedID = (byte[]) myAgent.myRoute.pop();
    location = myAgent.myUntraceabilityHelper.getLocation(aesEncryptedID);
    if (location.getAddress().compareTo(myAgent.here().getAddress()) == 0) {
      if (location.getName().compareTo(myAgent.here().getName()) == 0) { //Agent has returned to the base container.
        finished = true;
        myAgent.afterComeBack();
        return;
      }
    }
    myAgent.myPredecessorLoc = myAgent.here();
    myAgent.doMove(location);
  }
}
