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
package jade.security.pki.mobility.exceptions;

/**
 * This exception signals that agent has to implement {@link
 * jade.security.pki.mobility.agent.Signable} interface for the operation to be
 * performed.
 *
 * @author Amadeusz Żołnowski
 */
public class AgentNotSignableException extends Exception {

    /**
     * Creates a new instance of exception {@code AgentNotSignableException}.
     * 
     * @param agentName the local name of the agent which is expected to
     *      implement the {@link jade.security.pki.mobility.agent.Signable}
     *      interface.
     */
    public AgentNotSignableException(String agentName) {
        super("Agent " + agentName + " does not implement "
                + "jade.core.security.pki.Signable interface!");
    }
}
