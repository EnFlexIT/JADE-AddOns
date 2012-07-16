/**
 * 
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * 
 */
package jade.security.pki.messaging.behaviours;

import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.security.pki.messaging.PKIAgentMessagingHelper;
import jade.domain.FIPAAgentManagement.PKIObject;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.security.pki.messaging.PKIAgentMessagingService;
import jade.util.Logger;
import java.io.IOException;
import java.util.logging.Level;

/**
 * This class implements behaviour of replying for a certificate request for
 * an agent.
 *
 * @author Amadeusz Żołnowski
 */
public class SendCertificateBehaviour extends CyclicBehaviour {

    private static final MessageTemplate CERT_REQ_MATCH = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchContent(PKIObject.REQUEST_CERTIFICATE));
    private transient PKIAgentMessagingHelper pki;

    /**
	 * Constructor with owner agent.
     * 
	 * @param a the agent owning this behaviour.
     */
    public SendCertificateBehaviour(Agent a) {
        super(a);
    }

    /**
     * It listens for an ACL message with {@link PKIObject#REQUEST_CERTIFICATE}
     * content and {@link ACLMessage#REQUEST} performative and replies for any
     * matched message with the certificate of the container it resides on.
     */
    @Override
    public void action() {
        ACLMessage msg = myAgent.blockingReceive(CERT_REQ_MATCH, 100);
        if (msg == null) {
            return;
        }

        try {
            if (!getPKIAgentMessagingHelper().replyForRequestReceiversCertificate(myAgent, msg)) {
                Logger.getMyLogger(myAgent.getClass().getName()).log(
                        Level.WARNING, "Failed to reply to certificate request "
                        + "from {0}", msg.getSender());
            }
        } catch (IOException ex) {
            Logger.getMyLogger(myAgent.getClass().getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    /**
     * Retrieves the helper of the {@link PKIAgentMessagingService}.
     * 
     * @return the service helper.
     */
    protected PKIAgentMessagingHelper getPKIAgentMessagingHelper() {
        if (pki == null) {
            try {
                pki = (PKIAgentMessagingHelper) myAgent.getHelper(
                        PKIAgentMessagingService.NAME);
            } catch (ServiceException ex) {
                Logger.getMyLogger(myAgent.getAID().toString()).log(
                        Level.SEVERE, null, ex);
            }
        }
        return pki;
    }
}
