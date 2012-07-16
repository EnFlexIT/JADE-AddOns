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

import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.BaseService;
import jade.core.Filter;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.ServiceHelper;

/**
 * This service provides filters which are responsible for encryption,
 * decryption, signing and verification of ACL messages sent between agents.
 * It also provides a service helper for agent to control whether to apply
 * encryption or signature or not.
 *
 * @author Amadeusz Żołnowski
 */
public class PKIAgentMessagingService extends BaseService {

    /**
     * The canonical name of the service.
     */
    public static final String NAME =
            "jade.security.pki.messaging.PKIAgentMessaging";
    private AgentContainer ac;
    private Filter inFilter;
    private Filter outFilter;

    /**
     * Initializes the service and filters for this service.
     * 
     * @param ac the agent container this service is activated on.
     * @param profile this service doesn't make any use from the configuration
     *      profile - just passes it {@link
     *      BaseService#init(jade.core.AgentContainer, jade.core.Profile)}.
     * @throws ProfileException if the given profile is not valid.
     */
    @Override
    public void init(AgentContainer ac, Profile profile)
            throws ProfileException {
        super.init(ac, profile);
        this.ac = ac;
        this.inFilter = new PKIAgentMessagingIncomingFilter(myLogger, ac);
        this.outFilter = new PKIAgentMessagingOutgoingFilter(myLogger, ac);
    }

    /**
     * Returns incoming and outgoing filters of this service.
     * 
     * @param direction {@link Filter#INCOMING} or {@link Filter#OUTGOING}.
     * @return the incoming or outgoing filter instance.
     */
    @Override
    public Filter getCommandFilter(boolean direction) {
        if (direction == Filter.INCOMING) {
            return inFilter;
        } else { // Filter.OUTGOING
            return outFilter;
        }
    }

    /**
     * Creates instance of {@code PKIAgentMessagingHelper} for the agent which
     * request the helper and returns it to the requester.
     * 
     * @param a the agent which the helper is requested for.
     * @return the ServiceHelper to be used by the agent.
     */
    @Override
    public ServiceHelper getHelper(Agent a) {
        return new PKIAgentMessagingHelper(myLogger, ac);
    }

    /**
     * Returns the name of the service.
     * 
     * @return the {@link #NAME}.
     */
    @Override
    public String getName() {
        return PKIAgentMessagingService.NAME;
    }
}
