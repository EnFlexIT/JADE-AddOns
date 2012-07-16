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

import java.security.cert.X509Certificate;

/**
 * This is interface to be implemented by agents to be secured with the
 * {@link jade.security.pki.mobility.PKIAgentMobilityService}.
 *
 * @author Amadeusz Żołnowski
 */
public interface Signable {

    /**
     * This value is passed to {@link #setSafeMoveStatus(int)} and indicates
     * a successful verification on the destination container.
     */
    static final int SAFE_MOVE_OK = 0;
    /**
     * This value is passed to {@link #setSafeMoveStatus(int)} and indicates
     * a failure of the secure transfer of an agent.
     */
    static final int SAFE_MOVE_UKNOWN_FAILURE = 1;
    /**
     * This value is passed to {@link #setSafeMoveStatus(int)} and indicates
     * that agent couldn't be moved because it might store confidential
     * messages in the queue.
     */
    static final int SAFE_MOVE_FAILURE_UNPROCESSED_MESSAGES = 2;

    /**
     * Sets status indicating success or failure of the secure transfer of an
     * agent.  It is used by the
     * {@link jade.security.pki.mobility.PKIAgentMobilityService}.  The
     * implementation should set passed value to some field which agent could
     * read in the {@link jade.core.Agent#afterMove()} method.
     * 
     * @param status the status indicating success or failure of the secure
     *      transfer of an agent..
     */
    void setSafeMoveStatus(int status);

    /**
     * Returns the certificate of the home container where agent has been
     * created.
     * 
     * @return the certificate of a container.
     */
    X509Certificate getHomeContainerCertificate();

    /**
     * Returns the signature made by the service on the home container where
     * agent has been created.  This is signature of the concatenation of the
     * class of an agent, it's identifier and the immutable data returned with
     * the {@link #getImmutableData()}.
     * 
     * @return the signature.
     */
    byte[] getHomeContainerSignature();

    /**
     * Returns the data that is not supposed to change during the lifetime of
     * an agent.  This data is signed once by the service on the home container.
     * 
     * @return the immutable data.
     */
    byte[] getImmutableData();

    /**
     * Returns the certificate of the last container agent has been on.  This
     * certificate is set by the current container (with the {@link
     * #setLastContainerCertificate(java.security.cert.X509Certificate)}
     * method) when agent leaves it and is going to move to the another one.
     * 
     * @return the certificate of a container.
     */
    X509Certificate getLastContainerCertificate();

    /**
     * Returns the signature made by the service on the last container where
     * agent has been on.  The signature is made on the current container when
     * agent leaves it and is going to move to the another one.  This is
     * signature of the mutable data returned with the
     * {@link #getMutableData()}.
     * 
     * @return the signature.
     */
    byte[] getLastContainerSignature();

    /**
     * 
     * @param certificate 
     */
    void setLastContainerCertificate(X509Certificate certificate);

    /**
     * Sets the signature which is made on the current container when agent
     * leaves it and is going to move to the another one.  This is 
     * signature of the mutable data returned with the
     * {@link #getMutableData()}.
     * 
     * @param signature the signature of the mutable data.
     */
    void setLastContainerSignature(byte[] signature);

    /**
     * Returns data that might change in the result of an agent work.  This only
     * cannot be data which might change in the transit sate of an agent,
     * because verification will always fail after the move.
     * 
     * @return the mutable data.
     */
    byte[] getMutableData();
}
