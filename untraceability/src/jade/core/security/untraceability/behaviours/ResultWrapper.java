package jade.core.security.untraceability.behaviours;

import jade.content.onto.basic.Result;

/**
 * A wrapper class for the <code>jade.content.onto.basic.Result</code> class.
 *
 * The wrapper was introduced for the sake of the <code>GetAvailableLocationsBehaviour</code>
 * class (see jade.core.security.untraceability.behaviours.GetAvailableLocationsBehaviour)
 * which aims at providing a list of containers set up in the agent platform.
 *
 * This wrapper allows transparent use of the behaviour. In the sense that any
 * agent may run it. No matter which agent class it instantiates.
 *
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 * @see jade.core.security.untraceability.behaviours.GetAvailableLocationsBehaviour
 */

public class ResultWrapper {

  public Result results;
  public boolean isEmpty;

  public ResultWrapper() {
    results = new Result();
    isEmpty = true;
  }

}
