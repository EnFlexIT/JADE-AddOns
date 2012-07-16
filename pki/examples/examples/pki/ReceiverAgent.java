/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.pki;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.security.pki.messaging.behaviours.SendCertificateBehaviour;
import jade.security.pki.messaging.PKIAgentMessagingHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.security.pki.core.spec.KeySizeAlgorithmParameterSpec;
import jade.util.Logger;
import java.util.logging.Level;

/**
 *
 * @author aidecoe
 */
public class ReceiverAgent extends Agent {

    transient private PKIAgentMessagingHelper pki;
    transient private static final Logger logger =
            Logger.getMyLogger(ReceiverAgent.class.getName());

    class PongBehaviour extends CyclicBehaviour {

        private final MessageTemplate matchInform;

        public PongBehaviour(Agent a) {
            super(a);
            matchInform = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        }

        @Override
        public void action() {
            try {
                ACLMessage msg = receive(matchInform);
                if (msg == null) {
                    block(5000);
                    return;
                }
                String content = msg.getContent();
                if ("ping".equalsIgnoreCase(content)) {
                    logger.log(Level.INFO, "Received ping message");
                    AID replyTo = msg.getSender();
                    msg = msg.createReply();
                    msg.setContent("PONG");
                    pki.markForSignature(msg);
                    if (pki.requestReceiversCertificate(myAgent, replyTo,
                            1000)) {
                        logger.log(Level.INFO, "Encrypting");
                        pki.markForEncryption(msg, "AES",
                                new KeySizeAlgorithmParameterSpec(128));
                    }
                    send(msg);
                } else {
                    logger.log(Level.INFO, "Received unexpected message");
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected void setup() {
        try {
            pki = (PKIAgentMessagingHelper) getHelper(
                    "jade.security.pki.messaging.PKIAgentMessaging");
        } catch (ServiceException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        addBehaviour(new SendCertificateBehaviour(this));
        addBehaviour(new PongBehaviour(this));
    }
}
