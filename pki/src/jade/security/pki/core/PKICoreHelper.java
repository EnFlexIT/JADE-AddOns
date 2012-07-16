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

import jade.security.pki.core.exceptions.AsymmetricCipherException;
import jade.core.AgentContainer;
import jade.security.pki.PKIServiceHelper;
import jade.util.Logger;
import java.security.SignatureException;
import java.security.cert.X509Certificate;

/**
 * This class is a service helper for {@link PKICoreService}.  It exposes
 * a few cryptographic methods to an agent and an access to container
 * certificate.
 *
 * @author Amadeusz Żołnowski
 */
public class PKICoreHelper extends PKIServiceHelper {

    /**
     * Creates a {@code PKICoreHelper} object.
     * 
     * @param logger the logger which should be passed by the service
     *      initializing the helper.
     * @param ac the agent container on which the service runs.
     */
    protected PKICoreHelper(Logger logger, AgentContainer ac) {
        super(logger, ac);
    }

    /**
     * Encrypts the specified data with a public key extracted from the
     * specified certificate.
     * 
     * This method is delegated to the method {@link
     * jade.security.pki.core.util.ContainerAsymmetricCipher#encrypt(byte[],
     * java.security.cert.X509Certificate)}.
     * 
     * @param data the data to be encrypted.
     * @param certificate the certificate of the public key to be used for
     *      encryption.
     * @return the ciphertext.
     * @throws AsymmetricCipherException is thrown by the {@link
     *          jade.security.pki.core.util.ContainerAsymmetricCipher#encrypt(byte[],
     *          java.security.cert.X509Certificate)}.
     */
    public byte[] asymEncrypt(byte[] data, X509Certificate certificate)
            throws AsymmetricCipherException {
        return cPKI.getAsymCipher().encrypt(data, certificate);
    }

    /**
     * Returns the certificate of the container on which the service is running.
     * 
     * This method is delegated to the method {@link
     * jade.security.pki.core.util.ContainerPKI#getMyContainerCertificate()}.
     * 
     * @return the certificate.
     */
    public X509Certificate getContainerCertificate() {
        return cPKI.getMyContainerCertificate();
    }

    /**
     * Returns the certificate chain of the container on which the service is
     * running.
     * 
     * This method is delegated to the method {@link
     * jade.security.pki.core.util.ContainerPKI#getMyContainerCertificateChain()}.
     * 
     * @return the certificate chain.
     */
    public X509Certificate[] getContainerCertificateChain() {
        return cPKI.getMyContainerCertificateChain();
    }

    /**
     * Verifies correctness of the certificate.  It is verified if it is signed
     * by trusted CA and if it hasn't expired already.
     * 
     * This method is delegated to the method {@link
     * jade.security.pki.core.util.ContainerPKI#verifyCertificate(
     * java.security.cert.X509Certificate)}.
     * 
     * @param certificate the certificate to verify.
     * @return {@code true} if it is correct or {@code false} if the certificate
     *      is not signed by a trusted CA or it has already expired.
     */
    public boolean verifyCertificate(X509Certificate certificate) {
        return cPKI.verifyCertificate(certificate);
    }

    /**
     * Verifies the specified data against the specified signature and the
     * public key from the specified certificate.
     * 
     * This method is delegated to the method {@link
     * jade.security.pki.core.util.ContainerSigner#verifySignature(byte[],
     * byte[], java.security.cert.X509Certificate)}.
     * 
     * @param data the data to be verified.
     * @param signature the signature.
     * @param certificate the certificate from which public key will be used in
     *      the verification.
     * @return {@code true} if it is correct or {@code false} if the certificate
     *      is not signed by a trusted CA or it has already expired.
     * @throws SignatureException is thrown by the {@link
     * jade.security.pki.core.util.ContainerSigner#verifySignature(byte[],
     * byte[], java.security.cert.X509Certificate)}.
     */
    public boolean verifySignature(byte[] data, byte[] signature,
            X509Certificate certificate) throws SignatureException {
        return cPKI.getSigner().verifySignature(data, signature, certificate);
    }
}
