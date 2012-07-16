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
package jade.security.pki;

import jade.core.AgentContainer;
import jade.core.Filter;
import jade.core.ServiceException;
import jade.domain.FIPAAgentManagement.Unauthorised;
import jade.security.pki.core.PKICoreService;
import jade.security.pki.core.util.ContainerPKI;
import jade.util.Logger;

/**
 * This class provides common methods and fields for filters in the JADE-PKI.
 *
 * @author Amadeusz Żołnowski
 */
public abstract class PKIFilter extends Filter {

    private AgentContainer ac;
    /**
     * The logger used by the service which uses this filter.
     */
    protected final Logger myLogger;

    /**
     * Initializes {@link #myLogger} field.
     * 
     * @param logger the logger used by the service which creates an instance of
     *      a filter.
     * @param ac the agent container on which the service which creates an
     *      instance of a filter is activated on.
     */
    protected PKIFilter(Logger logger, AgentContainer ac) {
        this.myLogger = logger;
        this.ac = ac;
    }

    /**
     * Retrieves the instance of {@link ContainerPKI} from the
     * {@link PKICoreService}.
     * 
     * @return the instance of {@link ContainerPKI}.
     * @throws ServiceException if the {@link PKICoreService} couldn't be found
     *      or retrieved or filter has been initialized with the different
     *      container than the one for {@link PKICoreService}.
     */
    protected ContainerPKI getContainerPKI() throws ServiceException {
        try {
            return PKICoreService.getService(ac).getContainerPKI(ac);
        } catch (Unauthorised ex) {
            throw new ServiceException("Access to ContainerPKI is restricted.",
                    ex);
        }
    }
}
