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
import jade.core.ServiceException;
import jade.core.VerticalCommand;
import jade.core.messaging.GenericMessage;
import jade.core.messaging.MessagingSlice;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.PKICrypt;
import jade.domain.FIPAAgentManagement.PKISign;
import jade.lang.acl.ACLMessage;
import jade.security.pki.PKIFilter;
import jade.security.pki.core.util.ContainerPKI;
import jade.security.pki.core.util.SymmetricCrypto;
import jade.util.Logger;
import java.security.Key;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;

/**
 * This filter is responsible for handling outgoing messages intended to be
 * secured.  If a message is marked for encryption, the filter encrypts it with
 * the public key of the container which the receiver agent resides on and if
 * the message is marked for signing, the filter signs it the private key of the
 * current container (which the sender resides on).
 * 
 * @author Amadeusz Żołnowski
 */
public class PKIAgentMessagingOutgoingFilter extends PKIFilter {

    private AgentContainer ac;

    /**
     * Creates a {@code PKIAgentMessagingOutgoingFilter} object and sets its
     * position to 9 - which is very important.  It is after other services has
     * made necessary changes into the message and before the message is encoded
     * for transport.
     * 
     * @param logger the logger used by the service which creates an instance of
     *      a filter.
     * @param ac the agent container on which the service which creates an
     *      instance of a filter is activated on.
     */
    public PKIAgentMessagingOutgoingFilter(Logger logger, AgentContainer ac) {
        super(logger, ac);
        this.ac = ac;
        this.setPreferredPosition(9);
    }

    /**
     * Processes the {@link MessagingSlice#SEND_MESSAGE} command.
     * 
     * The command comes with {@code sender}, {@code message} and
     * {@code receiver}.  If the message contains {@link PKICrypt} or
     * {@link PKISign} mark, it is handled by one of or by all of methods {@link
     * #handleEncryption(jade.domain.FIPAAgentManagement.PKICrypt,
     * jade.lang.acl.ACLMessage)} and {@link
     * #handleSigning(jade.domain.FIPAAgentManagement.PKISign,
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
     *      error has occurred when processing message containing JADE-PKI
     *      marks.
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
                            "[Out<] Processing message from {0} to {1} "
                            + "[{2}]...", new Object[]{sender.getName(),
                                receiver.getName(),
                                MessagingUtil.msgToString(message)});
                    myLogger.log(Level.FINEST, message.toString());
                }
                boolean altered = false;
                if (acl != null) {
                    Envelope env = acl.getEnvelope();
                    if (env != null) {
                        PKICrypt pkiCrypt = PKICrypt.findIn(env);
                        if (pkiCrypt != null) {
                            if (!handleEncryption(pkiCrypt, acl)) {
                                MessagingUtil.notifyFailureToSender(myLogger,
                                        ac, acl, "encryption failed");
                                return false;
                            }
                            altered = true;
                        }
                        PKISign pkiSign = PKISign.findIn(env);
                        if (pkiSign != null) {
                            if (!handleSigning(pkiSign, acl)) {
                                MessagingUtil.notifyFailureToSender(myLogger,
                                        ac, acl, "signing failed");
                                return false;
                            }
                            altered = true;
                        }
                        if (altered) {
                            message.update(acl, env, null);
                        }
                    }
                }
                if (myLogger.isLoggable(Level.FINE)) {
                    myLogger.log(Level.FINE,
                            "[Out>] Processing message from {0} to {1} "
                            + "[{2}] [altered: {3}]...", new Object[]{
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
     * Handles the encryption of the ACL message.
     * 
     * The message is encrypted with the session key which is generated within
     * this method and this key is encrypted with the public key from the
     * certificate included in {@code pkiCrypt}.  The certificate is verified
     * before use.  The ciphertext is put into {@code pkiCrypt} and all
     * following fields in the message {@code acl} are wiped:
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
     * @param acl the ACL message to encrypt.
     * @return whether the operation was successful or not.
     */
    protected boolean handleEncryption(PKICrypt pkiCrypt, ACLMessage acl) {
        AID receiver = MessagingUtil.singleReceiver(myLogger, acl);
        if (receiver == null) {
            myLogger.log(Level.WARNING, "No receiver!");
            return false;
        }
        ContainerPKI cPKI;
        try {
            cPKI = getContainerPKI();
        } catch (ServiceException ex) {
            myLogger.log(Level.SEVERE, "Could not get ContainerPKI", ex);
            return false;
        }
        X509Certificate recvCert = cPKI.getCertificateManager().get(receiver);
        if (recvCert == null) {
            myLogger.log(Level.WARNING, "No certificate for {0}",
                    receiver.getName());
            return false;
        }
        if (!cPKI.verifyCertificate(recvCert)) {
            myLogger.log(Level.WARNING, "Certificate for {0} is invalid",
                    receiver.getName());
            return false;
        }
        Key key = null;
        byte[] encryptedKey = null;
        byte[] ciphertext = null;
        try {
            key = SymmetricCrypto.genKey(pkiCrypt.getAlgorithm(),
                    pkiCrypt.getKeyParams());
            encryptedKey = cPKI.getAsymCipher().encryptKey(key, recvCert);
            ciphertext = SymmetricCrypto.encrypt(acl,
                    pkiCrypt.getCipherTransformation(), key);
            key = null;
        } catch (Exception ex) {
            myLogger.log(Level.SEVERE, null, ex);
            return false;
        }
        pkiCrypt.setCiphertext(ciphertext);
        pkiCrypt.setEncryptedKey(encryptedKey);
        acl.setContent(null);
        acl.setConversationId(null);
        acl.setEncoding(null);
        acl.setLanguage(null);
        acl.setOntology(null);
        acl.setPerformative(ACLMessage.UNKNOWN);
        acl.setProtocol(null);
        acl.setReplyByDate(null);
        acl.setReplyWith(null);
        myLogger.log(Level.FINE, "PKICrypt: {0}", pkiCrypt);
        return true;
    }

    /**
     * Signs the message {@code acl} with the private key of the current
     * container which agent resides on and attaches the container certificate.
     * 
     * Signature is put into {@code pkiSign} along with the container
     * certificate.
     * 
     * @param pkiSign the {@link PKISign} object extracted from the envelope
     *      of ACL message, i.e. {@code acl}.
     * @param acl the ACL message to be signed.
     * @return whether the operation was successful or not.
     */
    protected boolean handleSigning(PKISign pkiSign, ACLMessage acl) {
        byte[] sig = null;
        ContainerPKI cPKI;
        try {
            cPKI = getContainerPKI();
        } catch (ServiceException ex) {
            myLogger.log(Level.SEVERE, "Could not get ContainerPKI", ex);
            return false;
        }
        pkiSign.setCertificate(cPKI.getMyContainerCertificate());
        try {
            sig = cPKI.getSigner().sign(acl);
        } catch (SignatureException ex) {
            myLogger.log(Level.SEVERE, null, ex);
            return false;
        }
        pkiSign.setSignature(sig);
        myLogger.log(Level.FINE, "PKISign: {0}", pkiSign);
        return true;
    }
}
