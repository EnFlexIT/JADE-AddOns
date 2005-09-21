package jade.core.security.untraceability.behaviours;

import jade.core.*;
import jade.core.behaviours.*;
import jade.core.security.untraceability.*;

/**
 * A behaviour of untraceable migration towards an agent's destination
 * dedicated to agents instantiating the
 * <code>jade.core.security.untraceability.UntraceableAgent</code> class or its
 * subclass.
 *
 * <p>An agent running this behaviour, waits until its list of locations to pass
 * (<code>UntraceableAgent.myUnprotectedRoute</code>) is set.</p>
 * <p>Then, after the route is given, it passes it up.
 * <p>In each passed location the <i>encrypted location identifier</i> is
 * created using the Untraceability JADE kernel-level service
 * (see <code>jade.core.security.untraceability</code>) and stored in
 * the agent's state, in the <code>jade.core.security.untraceability.UntraceableAgent.myRoute</code></p>
 * field.
 * <p>This is performed until the agent's route is empty.</p>
 *
 * <p>This behaviour implements the specification of <i>Untraceability Protocol I</i>
 * published for the first time in:
 * R.&nbsp;Leszczyna and J.&nbsp;G&#243;rski. Untraceability of mobile agents.
 * <em>Proceedings of the 4th International Joint Conference on
 * Autonomous Agents and Multiagent Systems (AAMAS '05)</em>, 3:1233-1234, July
 * 2005.</p>
 *
 * <p>For protocol's details refer to the manual of this service.</p>
 *
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 * @see jade.core.security.untraceability
 */
public class GoAheadBehaviour
    extends Behaviour {

  /**
   * Indicates whether the migration is completed (the agent passed all locations
   * on its list (<code>UntraceableAgent.myUnprotectedRoute</code>).
   */
  private boolean finished;

  /**
   * A reference to the owner agent.
   */
  private UntraceableAgent myAgent;

  /**
   * A class default constructor.
   *
   * @param untraceableAgent a reference to the agent owning the behavior
   */
  public GoAheadBehaviour(UntraceableAgent untraceableAgent) {
    myAgent = untraceableAgent;
    finished = false;
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
   * <p>Firstly, the migration is suspended as long as the agent's list of locations to pass
   * (<code>UntraceableAgent.myUnprotectedRoute</code>) is empty.</p>
   * <p>After the route is given, the agent removes from its top (the list is implemented as
   * a stack) a location identifier and moves to the location.
   * <p>In each passed location the <i>encrypted location identifier</i> is
   * created using the Untraceability JADE kernel-level service
   * (see <code>jade.core.security.untraceability</code>) and stored in
   * the agent's state, in the <code>jade.core.security.untraceability.UntraceableAgent.myRoute</code></p>
   * field.
   * <p>The action is performed until the agent's route is empty.</p>
   *
   * @see jade.core.security.untraceability
   */
  public void action() {
    Location location;
    if (myAgent.myIsWaitingForRoute) return;
    if (myAgent.myUnprotectedRoute.empty()) {
      if (myAgent.myRoute.empty()) return; //Agent has not obtained a route yet.
      finished = true; //Agent has reached the target container.
      myAgent.atDestination();
      return;
    }
    else {       //Agent is heading the target container.
      location = (Location) myAgent.myUnprotectedRoute.pop();
      myAgent.myRoute.push(myAgent.myUntraceabilityHelper.getAESEncryptedID(
          location));
    }
    myAgent.myPredecessorLoc = myAgent.here();
    myAgent.doMove(location);
  }
}
