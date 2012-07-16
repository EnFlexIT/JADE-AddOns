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
package jade.security.pki.core;

import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.BaseService;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.ServiceException;
import jade.core.ServiceHelper;
import jade.domain.FIPAAgentManagement.Unauthorised;
import jade.security.pki.core.util.ContainerCertificateManager;
import jade.security.pki.core.util.ContainerKeyManager;
import jade.security.pki.core.util.ContainerPKI;
import jade.util.WrapperException;
import java.io.File;
import java.security.KeyStoreException;

/**
 * This service provides cryptographic methods to the rest of JADE-PKI services
 * and a few of the methods to the agents via helper.  This service is
 * responsible for loading the container certificate, its private key and
 * certificates of the trusted CA-s.
 *
 * @author Amadeusz Żołnowski
 */
public class PKICoreService extends BaseService {

    /**
     * The canonical name of the service.
     */
    public static final String NAME = "jade.security.pki.core.PKICore";
    private ContainerPKI cPKI;
    private AgentContainer ac;

    /**
     * Initializes the service and loads the container certificate, its private
     * key and certificates of the trusted CA-s, i.e. creates instance of
     * a {@link ContainerPKI}.
     * 
     * @param ac the agent container this service is activated on.
     * @param profile the configuration profile for this service.  It should
     *      include following parameters: jade_pki_keyStore,
     *      jade_pki_keyStorePassword, jade_pki_trustStore,
     *      jade_pki_trustStorePassword.  For details see the {@link
     *      jade.security.pki.core.util.ContainerKeyManager#ContainerKeyManager(
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)}.
     *  
     * @throws ProfileException if the given profile is not valid or there
     *      occurred some exception on initialization.  The exception would be
     *      nested into a {@code ProfileException}.
     */
    @Override
    public void init(AgentContainer ac, Profile profile)
            throws ProfileException {
        super.init(ac, profile);
        this.ac = ac;
        try {
            this.cPKI = new ContainerPKI(
                    newContainerCertificateManager(profile),
                    newContainerKeyManager(ac, profile));
        } catch (KeyStoreException ex) {
            throw new ProfileException(
                    "Could not initialize ContainerKeyManager", ex);
        }
    }

    private static ContainerCertificateManager newContainerCertificateManager(
            Profile profile) throws KeyStoreException {
        String trustStorePath = profile.getParameter(
                "jade_pki_trustStore", System.getProperty("java.home")
                + "/lib/security/cacerts".replace('/', File.separatorChar));
        String trustStorePass = profile.getParameter(
                "jade_pki_trustStorePassword", null);

        return new ContainerCertificateManager(
                trustStorePath, trustStorePass);
    }

    private static ContainerKeyManager newContainerKeyManager(
            AgentContainer ac, Profile profile) throws ProfileException,
            KeyStoreException {
        String keyStorePath = profile.getParameter(
                "jade_pki_keyStore", null);
        String keyStorePass = profile.getParameter(
                "jade_pki_keyStorePassword", null);
        String prvKeyPass = profile.getParameter(
                "jade_pki_privateKeyPassword", keyStorePass);

        if (keyStorePath == null) {
            throw new ProfileException(
                    "jade_pki_keyStore parameter is required!");
        }

        return new ContainerKeyManager(ac.getID().getName(), keyStorePath,
                keyStorePass, prvKeyPass);
    }

    /**
     * Creates instance of {@code PKICoreHelper} for the agent which request the
     * helper and returns it to the requester.
     * 
     * @param a the agent which the helper is requested for.
     * @return the ServiceHelper to be used by the agent.
     */
    @Override
    public ServiceHelper getHelper(Agent a) {
        return new PKICoreHelper(myLogger, ac);
    }

    /**
     * Returns the name of the service.
     * 
     * @return the {@link #NAME}.
     */
    @Override
    public String getName() {
        return PKICoreService.NAME;
    }

    /**
     * Returns the instance of {@link ContainerPKI} initialized for the
     * container on which this service is running.
     * 
     * @param ac the agent container on which the service runs.
     * @return the instance of {@link ContainerPKI}.
     * @throws Unauthorised if the specified container is not the same as the
     *      one on which the service is running.
     */
    public ContainerPKI getContainerPKI(AgentContainer ac) throws Unauthorised {
        if (ac == this.ac) {
            return cPKI;
        }
        throw new Unauthorised();
    }

    /**
     * Helper method for retrieving this service instance.
     * 
     * @param ac the agent container on which the service runs.
     * @return this service instance.
     * @throws ServiceException if the service couldn't be found or retrieved.
     */
    public static PKICoreService getService(AgentContainer ac)
            throws ServiceException {
        PKICoreService service = null;

        try {
            service = (PKICoreService) ac.getServiceFinder().findService(
                    PKICoreService.NAME);
        } catch (WrapperException ex) {
            throw new ServiceException("Could not find " + PKICoreService.NAME
                    + " service", ex);
        }

        if (service == null) {
            throw new ServiceException("Could not find " + PKICoreService.NAME
                    + " service");
        }

        return service;
    }
}
