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
import jade.core.BaseService;
import jade.core.Filter;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.ServiceHelper;
import jade.core.Sink;

/**
 * This service provides filter and sink which are responsible for securing the
 * agent code and data when it moves between containers.  Agent must implement
 * the {@link jade.security.pki.mobility.agent.Signable} interface to be handled
 * by any of the component of the service.
 *
 * @author Amadeusz Żołnowski
 */
public class PKIAgentMobilityService extends BaseService {

    /**
     * The canonical name of the service.
     */
    public static final String NAME =
            "jade.security.pki.mobility.PKIAgentMobility";
    /**
     * The command handled by the {@link
     * PKIAgentMobilitySink#handleRequestSign(jade.core.Agent)}.
     */
    public static final String REQUEST_SIGN = "Request-Sign-Agent";
    /**
     * The command handled by the {@link
     * PKIAgentMobilitySink#handleRequestVerify(jade.core.Agent)}.
     */
    public static final String REQUEST_VERIFY = "Request-Verify-Agent";
    private static final String[] OWNED_COMMANDS = new String[]{
        PKIAgentMobilityService.REQUEST_SIGN,
        PKIAgentMobilityService.REQUEST_VERIFY
    };
    private AgentContainer ac;
    private Sink sink;
    private Filter inFilter;

    /**
     * Initializes the service along with the filter and the sink for this
     * service.
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
        this.sink = new PKIAgentMobilitySink(myLogger, this.ac);
        this.inFilter = new PKIAgentMobilityIncomingFilter(myLogger, this.ac);
    }

    /**
     * Returns the sink for this service.
     * 
     * @param side {@link Sink#COMMAND_SOURCE} only.
     * @return the {@link PKIAgentMobilitySink} instance for the {@link
     *      Sink#COMMAND_SOURCE} and {@code null} for the other value of the
     *      {@code side} parameter.
     */
    @Override
    public Sink getCommandSink(boolean side) {
        if (side == Sink.COMMAND_SOURCE) {
            return sink;
        }
        return null;
    }

    /**
     * Creates instance of {@code PKIAgentMobilitySink} for the agent which
     * requested the helper and returns it to the requester.
     * 
     * @param a the agent which the helper is requested for.
     * @return the ServiceHelper to be used by the agent.
     */
    @Override
    public ServiceHelper getHelper(Agent a) {
        return new PKIAgentMobilityHelper(myLogger, ac);
    }

    /**
     * Returns the name of the service.
     * 
     * @return the {@link #NAME}.
     */
    @Override
    public String getName() {
        return PKIAgentMobilityService.NAME;
    }

    /**
     * Returns new commands introduced by this service and which are handled by
     * {@link PKIAgentMobilitySink}.
     * 
     * @return the array of names of new commands.
     */
    @Override
    public String[] getOwnedCommands() {
        return OWNED_COMMANDS;
    }

    /**
     * Returns incoming filter of this service.
     * 
     * @param direction {@link Filter#INCOMING} only.
     * @return the {@link PKIAgentMobilityIncomingFilter} instance for the
     *      {@link Filter#INCOMING} and {@code null} for the other value of the
     *      {@code direction} parameter.
     */
    @Override
    public Filter getCommandFilter(boolean direction) {
        if (direction == Filter.INCOMING) {
            return inFilter;
        } else {
            return null;
        }
    }
}
