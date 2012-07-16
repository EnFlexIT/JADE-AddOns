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
package jade.security.pki.mobility.agent;

import jade.core.Agent;
import jade.core.ServiceException;
import jade.security.pki.core.PKICoreHelper;
import jade.security.pki.core.PKICoreService;
import jade.security.pki.mobility.PKIAgentMobilityHelper;
import jade.security.pki.mobility.PKIAgentMobilityService;
import jade.util.Logger;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * This class extends the {@link Agent} class with basic implementation of the
 * {@link Signable} interface.  Methods {@link Signable#getImmutableData()} and
 * {@link Signable#getMutableData()} are not implemented in this class.
 *
 * @author Amadeusz Żołnowski
 */
public abstract class SMobileAgent extends Agent implements Signable {

    private X509Certificate homeContainerCertificate;
    private byte[] homeContainerSignature;
    private X509Certificate lastContainerCertificate;
    private byte[] lastContainerSignature;
    private int safeMoveStatus;
    private transient PKIAgentMobilityHelper pkiMob;
    private transient PKICoreHelper pkiCore;
    private transient Logger myLogger;

    @Override
    public X509Certificate getHomeContainerCertificate() {
        return homeContainerCertificate;
    }

    @Override
    public byte[] getHomeContainerSignature() {
        return homeContainerSignature;
    }

    @Override
    public X509Certificate getLastContainerCertificate() {
        return lastContainerCertificate;
    }

    @Override
    public byte[] getLastContainerSignature() {
        return lastContainerSignature;
    }

    /**
     * Returns the myLogger and initializes it with agent's identifier before
     * if it's not initialized yet.
     * 
     * @return the myLogger.
     */
    protected Logger getLogger() {
        if (myLogger == null) {
            myLogger = Logger.getMyLogger(getAID().toString());
        }

        return myLogger;
    }

    /**
     * Retrieves the {@link PKIAgentMobilityHelper}.  If the instance is already
     * initialized just returns it.
     * 
     * @return the instance of the {@link PKIAgentMobilityHelper}.
     * @throws ServiceException if the helper couldn't be retrieved.
     */
    protected PKIAgentMobilityHelper getPKIAgentMobilityHelper()
            throws ServiceException {
        if (pkiMob == null) {
            pkiMob = (PKIAgentMobilityHelper) getHelper(
                    PKIAgentMobilityService.NAME);
        }
        return pkiMob;
    }

    /**
     * Retrieves the {@link PKICoreHelper}.  If the instance is already
     * initialized just returns it.
     * 
     * @return the instance of the {@link PKICoreHelper}.
     * @throws ServiceException if the helper couldn't be retrieved.
     */
    protected PKICoreHelper getPKICoreHelper()
            throws ServiceException {
        if (pkiCore == null) {
            pkiCore = (PKICoreHelper) getHelper(PKICoreService.NAME);
        }
        return pkiCore;
    }

    /**
     * Returns the status indicating success or failure of the secure transfer
     * of an agent.  It is used by the
     * {@link jade.security.pki.mobility.PKIAgentMobilityService}.
     * 
     * @return the safeMoveStatus.
     */
    protected int getSafeMoveStatus() {
        return safeMoveStatus;
    }

    /**
     * First it sets the current (should be home) container certificate to
     * field.  Next it triggers Request-Sign command of the {@link
     * PKIAgentMobilityService} and sets returned signature to a field.
     * 
     * @return whether all operations has been successful or not.
     */
    protected boolean ownMe() {
        if (homeContainerSignature != null) {
            return false;
        }

        try {
            homeContainerSignature =
                    getPKIAgentMobilityHelper().makeHomeContainerSignature();
            homeContainerCertificate =
                    getPKICoreHelper().getContainerCertificate(); // or chain?
            getLogger().log(Level.INFO, "Owner''s signature: {0}",
                    Arrays.toString(homeContainerSignature));
        } catch (ServiceException ex) {
            getLogger().log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    @Override
    public void setLastContainerCertificate(X509Certificate certificate) {
        this.lastContainerCertificate = certificate;
    }

    @Override
    public void setLastContainerSignature(byte[] signature) {
        this.lastContainerSignature = signature;
    }

    /**
     * Sets status indicating success or failure of the secure transfer of an
     * agent.  It is used by the
     * {@link jade.security.pki.mobility.PKIAgentMobilityService}.  The value
     * is set to a field.
     * 
     * @param status the status indicating success or failure of the secure
     *      transfer of an agent..
     */
    @Override
    public void setSafeMoveStatus(int status) {
        this.safeMoveStatus = status;
        getLogger().log(Level.INFO, "Safe move status: {0}",
                Integer.toString(status));
    }

    /**
     * Setups agents (see {@link Agent#setup()} for details).  Calls the {@link
     * #ownMe()} method.
     */
    @Override
    protected void setup() {
        super.setup();
        ownMe();
    }

    /**
     * Triggers Request-Verify command of the {@link PKIAgentMobilityService}.
     * 
     * @return value returned by Request-Verify command.
     */
    protected boolean verifyMe() {
        try {
            return getPKIAgentMobilityHelper().verifyMe();
        } catch (ServiceException ex) {
            getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
