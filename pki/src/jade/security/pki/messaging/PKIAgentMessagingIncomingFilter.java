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

import jade.security.pki.messaging.util.MessagingUtil;
import jade.core.AID;
import jade.core.AgentContainer;
import jade.core.VerticalCommand;
import jade.core.messaging.GenericMessage;
import jade.core.messaging.MessagingSlice;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.PKICrypt;
import jade.domain.FIPAAgentManagement.PKISign;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.security.pki.PKIFilter;
import jade.security.pki.core.util.ContainerPKI;
import jade.security.pki.core.util.SymmetricCrypto;
import jade.util.Logger;
import java.security.cert.X509Certificate;
import java.util.logging.Level;

/**
 * This filter is responsible for handling secured incoming messages.  If a
 * message is encrypted, the filter decrypts it with the private key of the
 * container and if the message is signed, the filter verifies it against an
 * attached signature and a public key from an attached certificate.
 * A certificate is validated before use.
 *
 * @author Amadeusz Żołnowski
 */
public class PKIAgentMessagingIncomingFilter extends PKIFilter {

    private AgentContainer ac;

    /**
     * Creates a {@code PKIAgentMessagingIncomingFilter} object and sets its
     * position to 51 - which is very important.  It is before other services
     * might make some changes to the message and after the message is decoded
     * to the format in which it was signed.
     * 
     * @param logger the logger used by the service which creates an instance of
     *      a filter.
     * @param ac the agent container on which the service which creates an
     *      instance of a filter is activated on.
     */
    public PKIAgentMessagingIncomingFilter(Logger logger, AgentContainer ac) {
        super(logger, ac);
        this.ac = ac;
        this.setPreferredPosition(51);
    }

    /**
     * Processes the {@link MessagingSlice#SEND_MESSAGE} command.
     * 
     * The command comes with {@code sender}, {@code message} and
     * {@code receiver}.  If the message contains {@link PKICrypt} or
     * {@link PKISign} mark, it is handled by one of or by all of methods {@link
     * #handleDecryption(jade.domain.FIPAAgentManagement.PKICrypt,
     * jade.lang.acl.ACLMessage)} and {@link
     * #handleSigVerification(jade.domain.FIPAAgentManagement.PKISign,
     * jade.lang.acl.ACLMessage)}.  If any of these methods returns
     * {@code false} then the command {@link MessagingSlice#SEND_MESSAGE} is
     * blocked, the message delivery is abandoned and the failure notification
     * is send to the sender by AMS agent.
     * 
     * @param cmd the command to process.  The only one is in interest of this
     *      filter is {@link MessagingSlice#SEND_MESSAGE}.
     * @return {@code true} if {@code cmd} is out of interest of the filter or
     *      if message doesn't contain any JADE-PKI marks.  If command is in the
     *      interest of the filter and has been processed successfully it
     *      returns {@code true}, too.  It returns {@code false} only if an
     *      error has occurred when processing message containing JADE-PKI marks
     *      or verification was unsuccessful.
     */
    @Override
    protected boolean accept(VerticalCommand cmd) {
        try {
            if (cmd.getName().equals(MessagingSlice.SEND_MESSAGE)) {
                /* SEND_MESSAGE(AID sender, GenericMessage message,
                 *              AID receiver)
                 */
                AID sender = (AID) cmd.getParam(0);
                GenericMessage message = (GenericMessage) cmd.getParam(1);
                ACLMessage acl = message.getACLMessage();
                AID receiver = (AID) cmd.getParam(2);
                if (myLogger.isLoggable(Level.FINE)) {
                    myLogger.log(Level.FINE,
                            "[In<] Processing message from {0} to {1} [{2}]...",
                            new Object[]{sender.getName(), receiver.getName(),
                                MessagingUtil.msgToString(message)});
                    myLogger.log(Level.FINEST, message.toString());
                }
                boolean altered = false;
                if (acl != null) {
                    Envelope env = message.getEnvelope() != null
                            ? message.getEnvelope() : acl.getEnvelope();
                    if (env != null) {
                        PKISign pkiSign = PKISign.findIn(env);
                        if (pkiSign != null) {
                            if (!handleSigVerification(pkiSign, acl)) {
                                MessagingUtil.notifyFailureToSender(myLogger,
                                        ac, acl, "verification failed");
                                return false;
                            }
                            altered = true;
                            pkiSign.clear(false);
                        }
                        PKICrypt pkiCrypt = PKICrypt.findIn(env);
                        if (pkiCrypt != null) {
                            if (!handleDecryption(pkiCrypt, acl)) {
                                MessagingUtil.notifyFailureToSender(myLogger,
                                        ac, acl, "decryption failed");
                                return false;
                            }
                            altered = true;
                            pkiCrypt.clear(false);
                        }
                        if (altered) {
                            //acl.setEnvelope(env);
                            message.update(acl, message.getEnvelope(),
                                    message.getPayload());
                        }
                    }
                }
                if (myLogger.isLoggable(Level.FINE)) {
                    myLogger.log(Level.FINE,
                            "[In>] Processing message from {0} to {1} [{2}]"
                            + " [altered: {3}]...", new Object[]{
                                sender.getName(), receiver.getName(),
                                MessagingUtil.msgToString(message),
                                Boolean.toString(altered)});
                    if (altered) {
                        myLogger.log(Level.FINEST, message.toString());
                    }
                }
            }
        } catch (Exception ex) {
            ACLMessage acl = ((GenericMessage) cmd.getParam(1)).getACLMessage();
            MessagingUtil.notifyFailureToSender(myLogger, ac, acl,
                    "uknown failure");
            myLogger.log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Handles the decryption of the ACL message.
     * 
     * The message is decrypted from the ciphertext included in the
     * {@code pkiCrypt} and all relevant fields are rewritten to the
     * {@code acl}.  These fields are:
     * 
     * <ul>
     *   <li>content</li>
     *   <li>conversationId</li>
     *   <li>encoding</li>
     *   <li>language</li>
     *   <li>ontology</li>
     *   <li>performative</li>
     *   <li>protocol</li>
     *   <li>replyByDate</li>
     *   <li>replyWith</li>
     * </ul>
     * 
     * @param pkiCrypt the {@link PKICrypt} object extracted from the envelope
     *      of ACL message, i.e. {@code acl}.
     * @param acl the decrypted ACL message.
     * @return whether the operation was successful or not.
     */
    protected boolean handleDecryption(PKICrypt pkiCrypt, ACLMessage acl) {
        myLogger.log(Level.FINE, "PKICrypt: {0}", pkiCrypt);
        ACLMessage aclDecr = null;
        try {
            ContainerPKI cPKI = getContainerPKI();
            aclDecr = SymmetricCrypto.decrypt2(pkiCrypt.getCiphertext(),
                    pkiCrypt.getCipherTransformation(),
                    cPKI.getAsymCipher().decryptKey(pkiCrypt.getEncryptedKey(),
                    pkiCrypt.getAlgorithm()));
        } catch (Exception ex) {
            myLogger.log(Level.SEVERE, null, ex);
            return false;
        }
        acl.setContent(aclDecr.getContent());
        acl.setConversationId(aclDecr.getConversationId());
        acl.setEncoding(aclDecr.getEncoding());
        acl.setLanguage(aclDecr.getLanguage());
        acl.setOntology(aclDecr.getOntology());
        acl.setPerformative(aclDecr.getPerformative());
        acl.setProtocol(aclDecr.getProtocol());
        acl.setReplyByDate(aclDecr.getReplyByDate());
        acl.setReplyWith(aclDecr.getReplyWith());
        return true;
    }

    /**
     * Handles the verification of the ACL message against the signature and
     * the certificate attached to it.  The certificate is verified before use
     * its public key.
     * 
     * @param pkiSign the {@link PKISign} object extracted from the envelope
     *      of ACL message, i.e. {@code acl}.
     * @param acl the signed ACL message.
     * @return whether the operation was successful or not, i.e. if verification
     *      was successful it returns {@code true} and if in verification
     *      process the inconsistency has been detected or some error has
     *      occurred then it returns {@code false}.
     */
    protected boolean handleSigVerification(PKISign pkiSign, ACLMessage acl) {
        myLogger.log(Level.FINE, "PKISign: {0}", pkiSign);
        boolean verified = false;
        try {
            ContainerPKI cPKI = getContainerPKI();
            X509Certificate certificate = pkiSign.getCertificate();
            if (cPKI.verifyCertificate(certificate)) {
                verified = cPKI.getSigner().verifySignature(acl,
                        pkiSign.getSignature(), certificate);
            }
        } catch (Exception ex) {
            myLogger.log(Level.SEVERE, null, ex);
            return false;
        }
        String receiverName = null;
        if (acl.getAllReceiver().hasNext()) {
            receiverName = ((AID) acl.getAllReceiver().next()).getName();
        }
        String content = acl.getContent();
        try {
            Object contentObj = acl.getContentObject();
            if (contentObj instanceof X509Certificate) {
                content = ((X509Certificate) contentObj).toString();
            }
        } catch (UnreadableException ex) {
        }
        myLogger.log(Level.INFO, "[In] Verified: {0} (from: {1}, to: {2}, "
                + "perf: {3}, content: {4})", new Object[]{
                    Boolean.toString(verified), acl.getSender().getName(),
                    receiverName, acl.getPerformative(), content});
        return verified;
    }
}
