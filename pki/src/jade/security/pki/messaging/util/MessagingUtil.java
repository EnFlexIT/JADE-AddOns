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
package jade.security.pki.messaging.util;

import jade.core.AID;
import jade.core.AgentContainer;
import jade.core.ServiceFinder;
import jade.core.messaging.GenericMessage;
import jade.core.messaging.MessagingService;
import jade.core.messaging.MessagingSlice;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.PKIInternalError;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.util.leap.Iterator;
import java.util.logging.Level;

/**
 * This class provides some helper methods operating on ACL messages.
 *
 * @author Amadeusz Żołnowski
 */
public abstract class MessagingUtil {

    /**
     * This method converts the message to the string in a form useful in
     * debugging.
     * 
     * @param message the message.
     * @return the string.
     */
    public static String msgToString(GenericMessage message) {
        return msgToString(message, false);
    }

    private static void msgToStringAppendEnv(StringBuilder sb, Envelope env,
            boolean verbose) {
        sb.append("env=(");
        if (env != null) {
            if (verbose) {
                sb.append(env.toString());
            } else {
                sb.append('+');
            }
        } else {
            sb.append("null");
        }
        sb.append(')');
    }

    /**
     * This method converts the message to the string in a form useful in
     * debugging.
     * 
     * @param message the message.
     * @param verbose be verbose or not.
     * @return the string.
     */
    public static String msgToString(GenericMessage message, boolean verbose) {
        Envelope env = message.getEnvelope();
        StringBuilder sb = new StringBuilder();

        msgToStringAppendEnv(sb, env, verbose);
        if (message.getACLMessage() != null) {
            Envelope aclEnv = message.getACLMessage().getEnvelope();
            sb.append(", acl.");
            msgToStringAppendEnv(sb, aclEnv, verbose);

            if (aclEnv != null && env != null) {
                sb.append(", env");
                if (env == aclEnv) {
                    sb.append("==");
                } else {
                    sb.append("!=");
                }
                sb.append("acl.env");
            }
        } else {
            sb.append(", acl=(null)");
        }

        return sb.toString();
    }

    /**
     * Sends the failure notification the message {@code msg} sender.  The
     * notification message is sent by an AMS agent.
     * 
     * @param logger this should be the logger instance used by the service from
     *      which this method is called.
     * @param ac the agent container which the service runs on.
     * @param msg the message which delivery failed and its sender needs to be
     *      notified about that.
     * @param error the error message which include in the notification.
     */
    public static void notifyFailureToSender(Logger logger, AgentContainer ac,
            ACLMessage msg, String error) {
        try {
            AID receiver = singleReceiver(logger, msg);
            ServiceFinder myFinder = ac.getServiceFinder();
            MessagingService msgSvc = (MessagingService) myFinder.findService(
                    MessagingSlice.NAME);
            msgSvc.notifyFailureToSender(new GenericMessage(msg), receiver,
                    new PKIInternalError(error));
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error when trying to send failure "
                    + "notification with AMS", ex);
        }
    }

    /**
     * Extracts only the single receiver from the ACL message.
     * 
     * The first receiver is taken and the rest is removed from the message.
     * 
     * @param logger the logger to use in case of warning.
     * @param acl the ACL message which extract receiver from.
     * @return the first receiver.
     */
    public static AID singleReceiver(Logger logger, ACLMessage acl) {
        AID retAID = null;
        Iterator allToIt = acl.getAllReceiver();
        if (allToIt.hasNext()) {
            retAID = (AID) allToIt.next();
        }
        while (allToIt.hasNext()) {
            AID aid = (AID) allToIt.next();
            logger.log(Level.WARNING, "Multiple receivers.  Removing all but "
                    + "first.  Removing: {0}", aid.getName());
            allToIt.remove();
        }
        return retAID;
    }
}
