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

import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.ServiceException;
import jade.core.ServiceHelper;
import jade.domain.FIPAAgentManagement.Unauthorised;
import jade.security.pki.core.PKICoreService;
import jade.security.pki.core.util.ContainerPKI;
import jade.util.Logger;
import java.util.logging.Level;

/**
 * This class provides common methods and fields for service helpers in the
 * JADE-PKI.
 *
 * @author Amadeusz Żołnowski
 */
public abstract class PKIServiceHelper implements ServiceHelper {

    private AgentContainer ac;
    /**
     * The logger used by the service which exposes this helper.
     */
    protected final Logger myLogger;
    /**
     * The instance of {@link ContainerPKI} from the {@link PKICoreService}.
     */
    protected ContainerPKI cPKI;

    /**
     * Initializes {@link #myLogger} field.
     * 
     * @param logger the logger used by the service which creates an instance of
     *      a service helper.
     * @param ac the agent container on which the service which creates an
     *      instance of a service helper is activated on.
     */
    public PKIServiceHelper(Logger logger, AgentContainer ac) {
        this.myLogger = logger;
        this.ac = ac;
        this.cPKI = null;
    }

    /**
     * Initializes the helper with the instance of {@link ContainerPKI} from the
     * {@link PKICoreService}.
     * 
     * @param a the agent which the helper is requested for.
     */
    @Override
    public void init(Agent a) {
        try {
            this.cPKI = PKICoreService.getService(ac).getContainerPKI(ac);
        } catch (Unauthorised ex) {
            myLogger.log(Level.SEVERE, "Access to ContainerPKI is restricted.",
                    ex);
        } catch (ServiceException ex) {
            myLogger.log(Level.SEVERE, "", ex);
        }

        if (cPKI == null) {
            myLogger.log(Level.SEVERE,
                    "Error on initialization of helper {0} for agent {1} on "
                    + "container {2}",
                    new Object[]{this.getClass().getCanonicalName(),
                        a.getAID().getName(), ac.getID().getName()});
            return;
        }

        myLogger.log(Level.INFO,
                "Helper {0} initialized for agent {1} on container {2}",
                new Object[]{this.getClass().getCanonicalName(),
                    a.getAID().getName(), ac.getID().getName()});
    }
}
