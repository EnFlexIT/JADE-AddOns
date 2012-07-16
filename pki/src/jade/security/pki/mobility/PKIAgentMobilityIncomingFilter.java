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
package jade.security.pki.mobility;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.GenericCommand;
import jade.core.IMTPException;
import jade.core.Location;
import jade.core.Service;
import jade.core.ServiceException;
import jade.core.VerticalCommand;
import jade.core.mobility.AgentMobilityHelper;
import jade.security.pki.PKIFilter;
import jade.security.pki.mobility.agent.Signable;
import jade.security.pki.core.util.ContainerPKI;
import jade.util.Logger;
import java.security.SignatureException;
import java.util.logging.Level;

/**
 * This filter is responsible for handling (to be) secured leaving/arriving
 * agents.  It handles only agents which implement {@link
 * jade.security.pki.mobility.agent.Signable} interface.
 *
 * @author Amadeusz Żołnowski
 */
public class PKIAgentMobilityIncomingFilter extends PKIFilter {

    private final AgentContainer ac;
    private Service pkiMobSrv;

    /**
     * Creates a {@code PKIAgentMobilityIncomingFilter} object and sets its
     * position to 1.
     * 
     * @param logger the logger used by the service which creates an instance of
     *      a filter.
     * @param ac the agent container on which the service which creates an
     *      instance of a filter is activated on.
     */
    public PKIAgentMobilityIncomingFilter(Logger logger, AgentContainer ac) {
        super(logger, ac);
        this.setPreferredPosition(FIRST);
        this.ac = ac;
    }

    /**
     * Processes the {@link AgentMobilityHelper#REQUEST_MOVE} and {@link
     * AgentMobilityHelper#INFORM_MOVED} commands.  The only parameter taken
     * into account is the first (index 0) for the first command and the second
     * (index 1) for the second command.  The first command is processed by
     * {@link #handleSigning(jade.core.Agent)} and the second by {@link
     * #handleSigVerification(jade.core.Agent)}.
     * 
     * @param cmd the command to process.
     * @return {@code true} if {@code cmd} is out of interest of the filter or
     *      if agent doesn't implement the {@link
     *      jade.security.pki.mobility.agent.Signable} interface.  If command
     *      is in the interest of the filter and has been processed successfully
     *      it returns {@code true}, too.  It returns {@code false} only if an
     *      error has occurred in the signing or verification process or
     *      verification was unsuccessful.
     */
    @Override
    public boolean accept(VerticalCommand cmd) {
        myLogger.log(Level.FINEST, "### INCOMING CMD: {0}", cmd.getName());

        if (AgentMobilityHelper.REQUEST_MOVE.equals(cmd.getName())) {
            AID agentID = (AID) cmd.getParam(0);

            try {
                Agent agent = ac.acquireLocalAgent(agentID);
                if (agent instanceof Signable) {
                    return handleSigning(agent);
                }
            } finally {
                ac.releaseLocalAgent(agentID);
            }
        } else if (AgentMobilityHelper.INFORM_MOVED.equals(cmd.getName())) {
            Agent agent = (Agent) cmd.getParam(1);
            if (agent instanceof Signable) {
                return handleSigVerification(agent);
            }
        }

        return true;
    }

    /**
     * Handles the signature verification of an agent.  The task is delegated to
     * the Request-Verify command handled by {@link
     * jade.security.pki.mobility.PKIAgentMobilitySink#handleRequestVerify(jade.core.Agent)}.  After successful verification {@link
     * Signable#setSafeMoveStatus(int)} is invoked with {@link
     * Signable#SAFE_MOVE_OK} argument value and left with original value
     * otherwise.
     * 
     * @param agent the agent to be verified.
     * @return {@code true} if verification has succeed or {@code false} if not.
     *      The {@code false} is also returned if the agent doesn't implement
     *      the {@link jade.security.pki.mobility.agent.Signable} interface or
     *      if the verification has failed in any other way.
     */
    protected boolean handleSigVerification(Agent agent) {
        boolean ret = false;

        try {
            ret = verifyAgent(agent);
            myLogger.log(Level.INFO, "Incoming agent verification result: {0}",
                    Boolean.toString(ret));
            if (ret) {
                ((Signable) agent).setSafeMoveStatus(Signable.SAFE_MOVE_OK);
            }
        } catch (Exception ex) {
            myLogger.log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    /**
     * Handles signing of mutable data of an agent.  The data retrieved with the
     * {@link jade.security.pki.mobility.agent.Signable#getMutableData()} is
     * signed with the private key of the container.  After signing the
     * verification is performed to make sure both mutable and immutable data
     * are correctly signed.
     * 
     * Before agent is signed, some checks are done.  If an agent has any
     * messages in the queue, the process is abandoned and
     * Signable#setSafeMoveStatus(int)} is invoked with {@link
     * Signable#SAFE_MOVE_FAILURE_UNPROCESSED_MESSAGES} argument value.
     * Otherwise the {@link Signable#setSafeMoveStatus(int)} is invoked with the
     * {@link Signable#SAFE_MOVE_UKNOWN_FAILURE} argument value.  The status is
     * supposed to be set to OK after verification on agent arrival on the
     * destination container.
     * 
     * @param agent the agent to be signed.
     * @return whether the process was successful or not.
     */
    protected boolean handleSigning(Agent agent) {
        boolean ret = false;

        if (agent.getCurQueueSize() > 0) {
            myLogger.log(Level.WARNING, "Not moving agent because it has "
                    + "unprocessed messages in the queue which may contain "
                    + "sensitive information.");
            ((Signable) agent).setSafeMoveStatus(
                    Signable.SAFE_MOVE_FAILURE_UNPROCESSED_MESSAGES);
            return false;
        }

        ((Signable) agent).setSafeMoveStatus(Signable.SAFE_MOVE_UKNOWN_FAILURE);

        if (!signMutableData(agent)) {
            return false;
        }

        try {
            ret = verifyAgent(agent);
            myLogger.log(Level.INFO, "Outgoing agent test verification result: "
                    + "{0}", Boolean.toString(ret));
        } catch (Exception ex) {
            myLogger.log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    private Service getPKIMobSrv() throws ServiceException {
        if (this.pkiMobSrv == null) {
            try {
                this.pkiMobSrv = ac.getServiceFinder().findService(
                        PKIAgentMobilityService.NAME);
            } catch (IMTPException ex) {
                throw new ServiceException("", ex);
            }
        }

        return pkiMobSrv;
    }

    private boolean signMutableData(Agent agent) {
        Signable signable = (Signable) agent;
        byte[] sig = null;

        ContainerPKI cPKI;
        try {
            cPKI = getContainerPKI();
        } catch (ServiceException ex) {
            myLogger.log(Level.SEVERE, null, ex);
            return false;
        }

        try {
            sig = cPKI.getSigner().sign(signable.getMutableData());
        } catch (SignatureException ex) {
            myLogger.log(Level.WARNING, null, ex);
            return false;
        }

        signable.setLastContainerSignature(sig);
        signable.setLastContainerCertificate(cPKI.getMyContainerCertificate());

        return true;
    }

    private boolean verifyAgent(Agent agent) throws ServiceException {
        GenericCommand gCmd = new GenericCommand(
                PKIAgentMobilityService.REQUEST_VERIFY,
                PKIAgentMobilityService.NAME, null);
        gCmd.addParam(agent);
        return ((Boolean) getPKIMobSrv().submit(gCmd)).booleanValue();
    }
}
