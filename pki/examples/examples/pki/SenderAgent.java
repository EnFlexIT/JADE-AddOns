/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.pki;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.security.pki.messaging.behaviours.SendCertificateBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.security.pki.messaging.PKIAgentMessagingHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import java.util.logging.Level;

/**
 *
 * @author aidecoe
 */
public class SenderAgent extends Agent {

    transient private PKIAgentMessagingHelper pki;
    transient private static final Logger logger =
            Logger.getMyLogger(SenderAgent.class.getName());

    class PingBehaviour extends SimpleBehaviour {

        private final MessageTemplate matchInform;
        private boolean ok;
        private AID to;

        public PingBehaviour(Agent a, AID to) {
            super(a);
            matchInform = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ok = false;
            this.to = to;
        }

        @Override
        public void action() {
            logger.log(Level.INFO, "Pinging {0}", to.getName());
            ACLMessage m = new ACLMessage(ACLMessage.INFORM);
            m.addReceiver(to);
            m.setContent("ping");
            send(m);
            m = receive(matchInform);
            if (m != null) {
                logger.log(Level.INFO, "I've got the MESSAGE! JUPI!");
                ok = true;
            }
            block(2000);
        }

        @Override
        public boolean done() {
            return ok;
        }
    }

    @Override
    protected void setup() {
        try {
            pki = (PKIAgentMessagingHelper)
                getHelper("jade.security.pki.messaging.PKIAgentMessaging");
        } catch (ServiceException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        addBehaviour(new SendCertificateBehaviour(this));
        //AID aid = new AID("recv@mgr", AID.ISGUID);
        //aid.addAddresses("http://ittemni.oaza-spokoju.lan:7778/acc");
        AID aid = new AID("recv", AID.ISLOCALNAME);
        addBehaviour(new PingBehaviour(this, aid));
    }
}
