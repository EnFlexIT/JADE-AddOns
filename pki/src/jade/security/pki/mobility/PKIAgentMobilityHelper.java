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

import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.GenericCommand;
import jade.core.IMTPException;
import jade.core.Service;
import jade.core.ServiceException;
import jade.security.pki.PKIServiceHelper;
import jade.util.Logger;
import java.util.logging.Level;

/**
 * This class is a service helper for {@link PKIAgentMobilityService}.
 *
 * @author Amadeusz Żołnowski
 */
public class PKIAgentMobilityHelper extends PKIServiceHelper {

    /**
     * the agent initialized in {@link #init(jade.core.Agent)} and to be used in
     * the other methods as a subject. 
     */
    protected Agent myAgent;
    private final AgentContainer ac;

    /**
     * Creates a {@code PKIAgentMobilityHelper} object.
     * 
     * @param logger the logger which should be passed by the service
     *      initializing the helper.
     * @param ac the agent container on which the service runs.
     */
    public PKIAgentMobilityHelper(Logger logger, AgentContainer ac) {
        super(logger, ac);
        this.ac = ac;
    }

    /**
     * Additionally to the method in the super class, it remembers the agent
     * which requested the helper.  It is going to be used in the later methods.
     * 
     * @param a the myAgent which the helper is requested for.
     */
    @Override
    public void init(Agent a) {
        super.init(a);
        this.myAgent = a;
    }

    /**
     * Sends a request to the service to sign the agent (see
     * {@link #init(jade.core.Agent)}).  See {@link
     * jade.security.pki.mobility.PKIAgentMobilitySink#handleRequestSign(jade.core.Agent)}
     * for details.
     * 
     * @return the agent signature made with home container private key.
     */
    public byte[] makeHomeContainerSignature() {
        try {
            Service srv = ac.getServiceFinder().findService(
                    PKIAgentMobilityService.NAME);
            GenericCommand gCmd = new GenericCommand(
                    PKIAgentMobilityService.REQUEST_SIGN,
                    PKIAgentMobilityService.NAME, null);
            gCmd.addParam(myAgent.getAID());
            return (byte[]) srv.submit(gCmd);
        } catch (IMTPException ex) {
            myLogger.log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            myLogger.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Sends a request to the service to verify the agent's (see
     * {@link #init(jade.core.Agent)}) signature.  See {@link
     * jade.security.pki.mobility.PKIAgentMobilitySink#handleRequestVerify(jade.core.Agent)}
     * for details.
     * 
     * @return whether the verification succeed or not.
     */
    public boolean verifyMe() {
        try {
            Service srv = ac.getServiceFinder().findService(
                    PKIAgentMobilityService.NAME);
            GenericCommand gCmd = new GenericCommand(
                    PKIAgentMobilityService.REQUEST_VERIFY,
                    PKIAgentMobilityService.NAME, null);
            gCmd.addParam(myAgent.getAID());
            boolean ret = ((Boolean) srv.submit(gCmd)).booleanValue();
            myLogger.log(Level.INFO, "Agent verification result: {0}",
                    Boolean.toString(ret));
            return ret;
        } catch (IMTPException ex) {
            myLogger.log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            myLogger.log(Level.SEVERE, null, ex);
        }

        return false;
    }
}
