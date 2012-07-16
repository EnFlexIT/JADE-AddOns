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
package jade.security.pki.messaging;

import jade.lang.acl.UnreadableException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.PKICrypt;
import jade.domain.FIPAAgentManagement.PKIObject;
import jade.domain.FIPAAgentManagement.PKIProperty;
import jade.domain.FIPAAgentManagement.PKISign;
import jade.domain.FIPAException;
import jade.domain.FIPAService;
import jade.lang.acl.ACLMessage;
import jade.security.pki.PKIServiceHelper;
import jade.util.Logger;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;

/**
 * This class is a service helper for {@link PKIAgentMessagingService}.  It
 * exposes methods for an agent to control whether to apply encryption or
 * signature or not and helper methods for certificates exchange.
 *
 * @author Amadeusz Żołnowski
 */
public class PKIAgentMessagingHelper extends PKIServiceHelper {

    /**
     * Creates a {@code PKIAgentMessagingHelper} object.
     * 
     * @param logger the logger which should be passed by the service
     *      initializing the helper.
     * @param ac the agent container on which the service runs.
     */
    public PKIAgentMessagingHelper(Logger logger, AgentContainer ac) {
        super(logger, ac);
    }

    /**
     * Adds the specified mark (which is instance of {@link PKIObject} to the
     * message envelope.  If envelope is not present yet, it creates empty one
     * before.  If the message already has the specified mark, it skip addition.
     * 
     * @param message the message to mark.
     * @param po the mark, instance of {@link PKIObject}.
     */
    protected void markFor(ACLMessage message, PKIObject po) {
        Envelope env = message.getEnvelope();
        if (env == null) {
            env = new Envelope();
            message.setEnvelope(env);
        }

        if ((PKISign.NAME.equals(po.getName()) && PKISign.findIn(env) != null)
                && (PKICrypt.NAME.equals(po.getName())
                && PKICrypt.findIn(env) != null)) {
            myLogger.log(Level.WARNING, "Message is already marked.");
            return;
        }
        env.addProperties(new PKIProperty(po));
    }

    /**
     * Marks the message to be encrypted by the {@link PKIAgentMessagingService}
     * (i.e. by its filter).  It also sets parameters for the symmetric cipher.
     * 
     * @param message the message to mark for encryption.
     * @param cipherTransformation the name of the transformation, e.g.,
     *      AES/CBC/PKCS7Padding.  See <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html#AppA">Appendix
     *      A in the Java Cryptography Extension Reference Guide</a> for
     *      information about standard transformation names. 
     * @param symKeyParams the parameter set for the session key generator.
     */
    public void markForEncryption(ACLMessage message,
            String cipherTransformation, AlgorithmParameterSpec symKeyParams) {
        markFor(message, new PKICrypt(cipherTransformation, symKeyParams));
    }

    /**
     * Marks the message to be signed by the {@link PKIAgentMessagingService}
     * (i.e. by its filter).
     * 
     * @param message the message to mark for signing.
     */
    public void markForSignature(ACLMessage message) {
        markFor(message, new PKISign());
    }

    /**
     * Sends the ACL message to the receiver with the request of the container
     * certificate which it resides on.
     * 
     * The message is of {@link ACLMessage#REQUEST} performative and its content
     * is {@link PKIObject#REQUEST_CERTIFICATE}.  The method wait for
     * {@code timeout} milliseconds for the response from the {@code receiver}.
     * 
     * @param sender the sender agent.
     * @param receiver the receiver agent which is requested for its container
     *      certificate.
     * @param timeout milliseconds to wait for the response.
     * @return {@code true} if {@code receiver} has responded correctly to the
     *      request or {@code false} if the response is incorrect or
     *      {@code timeout} has passed.
     */
    public boolean requestReceiversCertificate(Agent sender, AID receiver,
            long timeout) {
        X509Certificate recvCert = cPKI.getCertificateManager().get(receiver);
        /*if (recvCert != null) {
        return true;
        }*/

        ACLMessage reqReply = null;
        ACLMessage reqMsg = new ACLMessage(ACLMessage.REQUEST);

        reqMsg.setEnvelope(new Envelope());
        reqMsg.addReceiver(receiver);
        reqMsg.setContent(PKIObject.REQUEST_CERTIFICATE);
        reqMsg.setSender(sender.getAID());

        try {
            reqReply = FIPAService.doFipaRequestClient(sender, reqMsg, timeout);
        } catch (FIPAException ex) {
            myLogger.log(Level.WARNING, null, ex);
        }

        if (reqReply == null
                || reqReply.getPerformative() != ACLMessage.INFORM) {
            myLogger.log(Level.WARNING, "Agent {0} hasn''t responded to "
                    + "certificate request from {1}",
                    new Object[]{receiver.getName(), sender.getName()});
            return false;
        }

        try {
            Object o = reqReply.getContentObject();
            if (o instanceof X509Certificate) {
                cPKI.getCertificateManager().add(receiver, (X509Certificate) o);
                myLogger.log(Level.INFO, "Added new certificate (from: {0})",
                        receiver.getName());
                return true;
            }
        } catch (UnreadableException ex) {
            myLogger.log(Level.WARNING, null, ex);
            return false;
        }

        return false;
    }

    /**
     * If the {@code reqMsg} is the ACL message of {@link ACLMessage#REQUEST}
     * performative and {@link PKIObject#REQUEST_CERTIFICATE} content than it
     * prepares and sends the response containing the certificate of the
     * container on which {@code agent} resides on.
     * 
     * @param agent the agent which has received the request of the container
     *      certificate.
     * @param reqMsg the ACL message with the request.
     * @return {@code true} if the response has been sent or {@code false} if
     *      the {@code reqMsg} is incorrect.
     * @throws IOException 
     */
    public boolean replyForRequestReceiversCertificate(Agent agent,
            ACLMessage reqMsg) throws IOException {
        if (!(reqMsg.getPerformative() == ACLMessage.REQUEST
                && PKIObject.REQUEST_CERTIFICATE.equals(reqMsg.getContent()))) {
            return false;
        }

        AID replyTo = reqMsg.getSender();
        ACLMessage reqReply = reqMsg.createReply();
        reqReply.setPerformative(ACLMessage.INFORM);

        reqReply.setContentObject(cPKI.getMyContainerCertificate());
        markForSignature(reqReply);

        agent.send(reqReply);
        myLogger.log(Level.INFO, "Sent certificate from {0} to {1}",
                new Object[]{reqReply.getSender().getName(),
                    replyTo.getName()});

        return true;
    }
}
