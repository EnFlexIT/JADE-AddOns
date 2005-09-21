package examples.untraceability.roaming;

import java.util.*;

import jade.core.*;
import jade.core.behaviours.*;
import jade.core.security.untraceability.*;
import jade.core.security.untraceability.behaviours.*;
import jade.wrapper.*;

/**
 * This class provides an exemplar implementation of an untraceable agent.
 *
 * <p>The agent obtains a list of locations (containers) available
 * in the agent platform, chooses one of the locations as a destination of its
 * migration and moves towards it passing intermediate locations. After
 * reaching the destination it comes back to the source location.</p>
 *
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 * @see jade.core.security.untraceability
 */
public class UntraceableRoamingAgent
    extends UntraceableAgent {

 /**
  *
  * The agent's startup code. Two behaviours are added.
  *
  *
  */
  protected void setup() {
    super.setup();
    addBehaviour(new CreateRouteBehaviour(this));
    addBehaviour(new GoAheadBehaviour(this));
  }

  protected void afterMove() {
    super.afterMove();
    try {
      System.out.print("I am on the container ");
      System.out.println(getContainerController().getContainerName()+".");
    }
    catch (ControllerException ex) {
    }
  }

  public void atDestination() {

    System.out.println("I have arrived at the destination.");
    addBehaviour(new ComeBackBehaviour(this));

  }

  public void afterComeBack() {
    super.afterComeBack();
    System.out.println("I have returned to my base station.");
  }

  private class CreateRouteBehaviour extends Behaviour {

    private ResultWrapper rw;
    private Location myAgentDestination;

    CreateRouteBehaviour(Agent agent) {
      myAgent = agent;
      rw = new ResultWrapper();
      myAgent.addBehaviour(new GetAvailableLocationsBehaviour(myAgent, rw));
    }

    public void action() {
    }

    public boolean done() {
      if (rw.isEmpty == false) {
        Iterator iterator = rw.results.getItems().iterator();
        Object[] objects = rw.results.getItems().toArray();

        myAgentDestination = (Location) objects[new Random().nextInt(objects.length)];

        String currentContainerName = null;
        try {
          currentContainerName = myAgent.getContainerController().
              getContainerName();
        }
        catch (ControllerException ex) {
        }

        while (iterator.hasNext()) {
          Object next = iterator.next();
          //Assuring that the base container won't be put to the LIFO
          if (next.toString().startsWith(currentContainerName) == false)
            if (next.toString().startsWith(myAgentDestination.getName()) == false)
              ((UntraceableAgent) myAgent).myUnprotectedRoute.push(next);
        }

       ((UntraceableAgent) myAgent).myIsWaitingForRoute = false;

        return true;
      }
      return false;
    }

    void printRoute(){
      for (int k=0; k<((UntraceableAgent) myAgent).myUnprotectedRoute.size(); k++)
        System.out.println("unprotectedRoute = "+((UntraceableAgent) myAgent).myUnprotectedRoute.elementAt(k));
    }

  }
}
