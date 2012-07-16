/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.pki;

import jade.core.Agent;
import jade.core.Location;
import jade.core.ServiceException;
import jade.security.pki.messaging.PKIAgentMessagingHelper;
import jade.util.Logger;
import java.util.logging.Level;

/**
 *
 * @author aidecoe
 */
public class TestAgent extends Agent {

    transient private static final Logger logger =
            Logger.getMyLogger(ReceiverAgent.class.getName());
    transient private PKIAgentMessagingHelper pki;

    protected void init() {
        logger.log(Level.INFO, "init transients");
        try {
            pki = (PKIAgentMessagingHelper) getHelper("jade.core.security.pki.PKI");
        } catch (ServiceException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void setup() {
        super.setup();
        init();
    }

    @Override
    protected void beforeMove() {
        super.beforeMove();
    }

    @Override
    protected void afterMove() {
        super.afterMove();
        init();
    }

    @Override
    public void doMove(Location destination) {
        logger.log(Level.INFO, "before move?");
        super.doMove(destination);
        logger.log(Level.INFO, "after move?");
    }
}
