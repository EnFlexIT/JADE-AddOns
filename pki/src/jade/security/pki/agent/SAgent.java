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
package jade.security.pki.agent;

import jade.security.pki.messaging.PKIAgentMessagingHelper;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.security.pki.messaging.PKIAgentMessagingService;
import jade.security.pki.messaging.behaviours.SendCertificateBehaviour;
import jade.security.pki.mobility.agent.SMobileAgent;

/**
 * This class combines the {@link SMobileAgent} class with the enhancements for
 * the {@link PKIAgentMessagingService}.  This enhancement is the behaviour of
 * replying for the certificates requests.
 *
 * @author Amadeusz Żołnowski
 */
public abstract class SAgent extends SMobileAgent {

    private transient PKIAgentMessagingHelper pkiMsg;
    private Behaviour sendCertBehaviour;

    /**
     * Retrieves the {@link PKIAgentMessagingHelper}.  If the instance is
     * already initialized just returns it.
     * 
     * @return the instance of the {@link PKIAgentMessagingHelper}.
     * @throws ServiceException if the helper couldn't be retrieved.
     */
    protected PKIAgentMessagingHelper getPKIAgentMessagingHelper()
            throws ServiceException {
        if (pkiMsg == null) {
            pkiMsg = (PKIAgentMessagingHelper) getHelper(
                    PKIAgentMessagingService.NAME);
        }
        return pkiMsg;
    }

    /**
     * Creates new instance of a {@link SendCertificateBehaviour} and starts it.
     */
    protected void startSendCertificateBehaviour() {
        if (sendCertBehaviour != null) {
            stopSendCertificateBehaviour();
        }
        sendCertBehaviour = new SendCertificateBehaviour(this);
        addBehaviour(sendCertBehaviour);
    }

    /**
     * Stops the instance of a {@link SendCertificateBehaviour} and removes it.
     */
    protected void stopSendCertificateBehaviour() {
        if (sendCertBehaviour == null) {
            return;
        }
        removeBehaviour(sendCertBehaviour);
        sendCertBehaviour = null;
    }

    /**
     * Additionally to {@link SMobileAgent#setup()} calls the {@link
     * #startSendCertificateBehaviour()} method.
     */
    @Override
    protected void setup() {
        super.setup();
        startSendCertificateBehaviour();
    }
}
