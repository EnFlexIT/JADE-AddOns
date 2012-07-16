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
import jade.core.IMTPException;
import jade.core.VerticalCommand;
import jade.core.mobility.AgentMobilityProxy;
import jade.security.pki.PKISink;
import jade.security.pki.mobility.agent.Signable;
import jade.security.pki.mobility.exceptions.AgentNotSignableException;
import jade.security.pki.core.util.ContainerPKI;
import jade.util.Logger;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * This class handles the new JADE-PKI commands (defined in {@link
 * jade.security.pki.mobility.PKIAgentMobilityService}) securing the agent code
 * and data.
 *
 * @author Amadeusz Żołnowski
 */
public class PKIAgentMobilitySink extends PKISink {

    private final AgentContainer ac;
    private final AgentMobilityProxy amp;

    /**
     * Creates a {@code PKIAgentMobilitySink} object.
     * 
     * @param logger the logger used by the service which creates an instance of
     *      a sink.
     * @param ac the agent container on which the service which creates an
     *      instance of a sink is activated on.
     */
    public PKIAgentMobilitySink(Logger logger, AgentContainer ac) {
        super(logger, ac);
        this.ac = ac;
        this.amp = new AgentMobilityProxy();
        this.amp.setNode(ac.getNodeDescriptor().getNode());
    }

    private void checkAgent(Agent agent) throws AgentNotSignableException {
        if (!(agent instanceof Signable)) {
            throw new AgentNotSignableException(agent.getName());
        }
    }

    /**
     * Executes the commands defined in {@link
     * jade.security.pki.mobility.PKIAgentMobilityService} which are:
     * Request-Sign and Request-Verify.  Both commands takes single parameter
     * which is an agent (as an {@link AID} or {@link Agent}) and requires that
     * the agent implements {@link jade.security.pki.mobility.agent.Signable}
     * interface.
     * 
     * The first command is handled by {@link
     * #handleRequestSign(jade.core.Agent)} and the second by {@link
     * #handleRequestVerify(jade.core.Agent)}.
     * 
     * @param cmd the command to execute.
     */
    @Override
    public void consume(VerticalCommand cmd) {
        if (PKIAgentMobilityService.REQUEST_SIGN.equals(cmd.getName())
                || PKIAgentMobilityService.REQUEST_VERIFY.equals(
                cmd.getName())) {
            String name = null;
            if (cmd.getParam(0) instanceof AID) {
                name = ((AID) cmd.getParam(0)).getName();
            } else {
                name = ((Agent) cmd.getParam(0)).getName();
            }
            myLogger.log(Level.INFO, "{0}({1})", new Object[]{cmd.getName(),
                        name});
        }

        if (PKIAgentMobilityService.REQUEST_SIGN.equals(cmd.getName())) {
            byte[] sig = null;

            if (cmd.getParam(0) instanceof AID) {
                AID aid = (AID) cmd.getParam(0);
                Agent agent = ac.acquireLocalAgent(aid);
                try {
                    sig = handleRequestSign(agent);
                } finally {
                    ac.releaseLocalAgent(aid);
                }
            } else {
                Agent agent = (Agent) cmd.getParam(0);
                sig = handleRequestSign(agent);
            }

            cmd.setReturnValue(sig);
        } else if (PKIAgentMobilityService.REQUEST_VERIFY.equals(
                cmd.getName())) {
            boolean verif = false;

            if (cmd.getParam(0) instanceof AID) {
                AID aid = (AID) cmd.getParam(0);
                Agent agent = ac.acquireLocalAgent(aid);
                try {
                    verif = handleRequestVerify(agent);
                } finally {
                    ac.releaseLocalAgent(aid);
                }
            } else {
                Agent agent = (Agent) cmd.getParam(0);
                verif = handleRequestVerify(agent);
            }

            cmd.setReturnValue(Boolean.valueOf(verif));
        }
    }

    /**
     * Retrieves all agent data that is supposed to be immutable which are:
     * agent class, agent identifier ({@link AID}) and {@link
     * jade.security.pki.mobility.agent.Signable#getImmutableData()} - in that
     * order.
     * 
     * The data is returned as a 2D array where the first dimension is the data
     * type (class, identifier, …) and the second - byte arrays of the actual
     * data to be processed by the other methods.
     * 
     * @param agent the agent from which retrieve the data.
     * @return the 2D array of the immutable data of the agent.
     * @throws IMTPException the exception might be thrown by {@link
     *      AgentMobilityProxy#fetchClassFile(java.lang.String,
     *      java.lang.String)}.
     * @throws ClassNotFoundException the exception might be thrown by {@link
     *      AgentMobilityProxy#fetchClassFile(java.lang.String,
     *      java.lang.String)}.
     */
    protected byte[][] retrieveAgentImmutableData(Agent agent)
            throws IMTPException, ClassNotFoundException {
        String agentClassName = agent.getClass().getName();
        String agentName = agent.getName();

        byte[][] data = new byte[][]{
            amp.fetchClassFile(agentClassName, agentName),
            agent.getAID().toString().getBytes(),
            ((Signable) agent).getImmutableData()};

        myLogger.log(Level.INFO, "Loaded agent {0} ({1}) class: {2}",
                new Object[]{agentName, agentClassName,
                    DigestUtils.md5Hex(data[0])});
        myLogger.log(Level.INFO, "Loaded agent {0} ({1}) data: {2}",
                new Object[]{agentName, agentClassName,
                    DigestUtils.md5Hex(data[1])});

        return data;
    }

    /**
     * Handles the request of signing an agent.
     * 
     * The method signs data retrieved with {@link
     * #retrieveAgentImmutableData(jade.core.Agent)} and returns the signature.
     * 
     * @param agent the agent to be signed.
     * @return the signature of the agent data.  The {@code null} is returned if
     *      the agent doesn't implement the {@link
     *      jade.security.pki.mobility.agent.Signable} interface or the {@link
     *      #retrieveAgentImmutableData(jade.core.Agent)} has failed.
     */
    protected byte[] handleRequestSign(Agent agent) {
        byte[] sig = null;

        try {
            checkAgent(agent);
            sig = getContainerPKI().getSigner().sign(
                    retrieveAgentImmutableData(agent));
        } catch (Exception ex) {
            myLogger.log(Level.SEVERE, null, ex);
        }

        return sig;
    }

    /**
     * Handles the request of a verification of the agent.
     * 
     * Before the verification of the signature, the certificates are verified
     * with the {@link
     * jade.security.pki.core.util.ContainerCertificateManager#verify(java.security.cert.X509Certificate)}
     * method.  Both of the mutable and immutable data are verified (with the
     * certificates of the home and last containers).
     * 
     * @param agent the agent to be verified.
     * @return {@code true} if verification has succeed or {@code false} if not.
     *      The {@code false} is also returned if the agent doesn't implement
     *      the {@link jade.security.pki.mobility.agent.Signable} interface or
     *      the {@link #retrieveAgentImmutableData(jade.core.Agent)} has failed.
     */
    protected boolean handleRequestVerify(Agent agent) {
        boolean verif = false;

        try {
            checkAgent(agent);
            Signable signable = (Signable) agent;

            X509Certificate homeCert = signable.getHomeContainerCertificate();
            X509Certificate lastContCert =
                    signable.getLastContainerCertificate();
            ContainerPKI cPKI = getContainerPKI();

            if (cPKI.verifyCertificate(homeCert)
                    && cPKI.verifyCertificate(lastContCert)) {
                byte[][] imData = retrieveAgentImmutableData(agent);
                verif = cPKI.getSigner().verifySignature(imData,
                        signable.getHomeContainerSignature(),
                        homeCert)
                        && cPKI.getSigner().verifySignature(
                        signable.getMutableData(),
                        signable.getLastContainerSignature(), lastContCert);
            }
        } catch (Exception ex) {
            myLogger.log(Level.SEVERE, null, ex);
        }

        return verif;
    }
}
