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
package jade.security.pki.core.util;

import jade.util.Logger;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;

/**
 * This class exposes all needed cryptographic methods to the container
 * services.  It is created only one per container.  The private key and the
 * certificate are taken from {@link ContainerKeyManager} passed as an argument
 * to constructor.  The access to the certificates of the trusted CA-s is
 * provided by {@link ContainerCertificateManager} which is also passed as an
 * argument to constructor.  The private key isn't accessible outside the
 * {@code ContainerPKI} object.
 * 
 * @author Amadeusz Żołnowski
 */
public class ContainerPKI {

    private final Logger logger = Logger.getMyLogger(
            ContainerPKI.class.getName());
    private final ContainerCertificateManager cm;
    private final ContainerKeyManager km;
    private final ContainerAsymmetricCipher asymCipher;
    private final ContainerSigner signer;

    /**
     * Creates a {@code ContainerPKI} object.
     * 
     * @param cm container certificate manager providing the certificates of the
     *      trusted CA-s.
     * @param km key manager providing the private key for the container and its
     *      certificate.
     */
    public ContainerPKI(ContainerCertificateManager cm,
            ContainerKeyManager km) {
        this.cm = cm;
        this.km = km;
        this.signer = new ContainerSigner(km);
        this.asymCipher = new ContainerAsymmetricCipher(km);
    }

    /**
     * Returns instance of the {@link ContainerAsymmetricCipher} initialized
     * with the key manager passed in the constructor of the
     * {@code ContainerPKI}.  This object uses for its operations the private
     * key and the certificate of the container for which it is intended.
     * 
     * @return the {@link ContainerAsymmetricCipher} object.
     */
    public ContainerAsymmetricCipher getAsymCipher() {
        return asymCipher;
    }

    /**
     * Returns the certificate manager providing the certificates of the trusted
     * CA-s.
     * 
     * @return the {@link ContainerCertificateManager} object.
     */
    public ContainerCertificateManager getCertificateManager() {
        return cm;
    }

    /**
     * Returns instance of the {@link ContainerSigner} initialized with the key
     * manager passed in the constructor of the {@code ContainerPKI}.  This
     * object uses for its operations the private key and the certificate of the
     * container for which it is intended.
     * 
     * @return the {@link ContainerSigner} object.
     */
    public ContainerSigner getSigner() {
        return signer;
    }

    /**
     * Returns the certificate of the container for which the
     * {@code ContainerPKI} object is initialized.
     * 
     * @return the certificate of the container.
     */
    public X509Certificate getMyContainerCertificate() {
        return km.getCertificate();
    }

    /**
     * Returns the certificate chain of the container for which the
     * {@code ContainerPKI} object is initialized.
     * 
     * @return the certificate chain of the container.
     */
    public X509Certificate[] getMyContainerCertificateChain() {
        return km.getCertificateChain();
    }

    /**
     * @throws CertificateException if path of the certificate isn't valid or if
     *      the certificate has already expired.
     */

    /**
     * Verifies correctness of the certificate.  It is verified if it is signed
     * by trusted CA and if it hasn't expired already.
     * 
     * @param certificate the certificate to verify.
     * @return {@code true} if it is correct or {@code false} if the certificate
     *      is not signed by a trusted CA or it has already expired.
     */
    public boolean verifyCertificate(X509Certificate certificate) {
        try {
            cm.verify(certificate);
        } catch (CertificateException ex) {
            logger.log(Level.WARNING, null, ex);
            return false;
        }

        return true;
    }
}
