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

import jade.lang.acl.ACLMessage;
import jade.lang.acl.LEAPACLCodec;
import jade.security.pki.messaging.util.SigLEAPACLCodec;
import jade.util.Logger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;

/**
 * This class provides methods of signing and verification.  The class is
 * a wrapper around standard cryptographic functions provided by Java
 * Cryptographic Extension framework.
 * 
 * The object is initialized with a key manager which stores a private key and
 * a certificate of the container which are used in the methods of this class.
 *
 * @author Amadeusz Żołnowski
 */
public class ContainerSigner {

    private final Logger logger = Logger.getMyLogger(
            ContainerSigner.class.getName());
    private final ContainerKeyManager km;
    private final LEAPACLCodec sigLEAPCodec;

    /**
     * Creates {@code ContainerSigner} object initialized with the given key
     * manager.
     * 
     * @param km initialized key manager which stores private key of the
     *      container and its certificate.
     */
    public ContainerSigner(ContainerKeyManager km) {
        this.km = km;
        this.sigLEAPCodec = new SigLEAPACLCodec();
    }

    private Signature getSigner() throws SignatureException {
        try {
            logger.log(Level.INFO, "Signature algorithm: {0}",
                    km.getCertificate().getSigAlgName());
            Signature signer = Signature.getInstance(
                    km.getCertificate().getSigAlgName());
            signer.initSign(km.getPrivateKey());
            return signer;
        } catch (InvalidKeyException ex) {
            throw new SignatureException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new SignatureException(ex);
        }
    }

    private Signature getVerifier(X509Certificate certificate)
            throws SignatureException {
        try {
            String sigAlg = certificate.getSigAlgName();
            logger.log(Level.INFO, "Signature algorithm: {0}", sigAlg);
            Signature verifier = Signature.getInstance(sigAlg);
            verifier.initVerify(certificate);
            return verifier;
        } catch (InvalidKeyException ex) {
            throw new SignatureException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new SignatureException(ex);
        }
    }

    /**
     * Signs the specified data with the private key from the key manager.
     * 
     * @param data the data to be signed.
     * @return the signature.
     * @throws SignatureException if an error occurred when signing, i.e. one of
     * the following exceptions has been thrown:
     *      {@link InvalidKeyException}, {@link NoSuchAlgorithmException}.
     */
    public byte[] sign(byte[] data) throws SignatureException {
        Signature signer = getSigner();
        signer.update(data);
        return signer.sign();
    }

    /**
     * Signs the concatenation of the data in the specified array of data with
     * the private key from the key manager.
     * 
     * @param data the array of data to be signed.
     * @return the signature.
     * @throws SignatureException if an error occurred when signing, i.e. one of
     * the following exceptions has been thrown:
     *      {@link InvalidKeyException}, {@link NoSuchAlgorithmException}.
     */
    public byte[] sign(byte[][] data) throws SignatureException {
        Signature signer = getSigner();
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                signer.update(data[i]);
            }
        }
        return signer.sign();
    }

    /**
     * Serializes the {@link ACLMessage} and then signs these bytes with the
     * private key from the key manager.
     * 
     * It uses {@link SigLEAPACLCodec} for serialization.
     * 
     * @param message the ACL message to be signed.
     * @return the signature.
     * @throws SignatureException if an error occurred when signing, i.e. one of
     * the following exceptions has been thrown:
     *      {@link InvalidKeyException}, {@link NoSuchAlgorithmException}.
     */
    public byte[] sign(ACLMessage message) throws SignatureException {
        return sign(sigLEAPCodec.encode(message, null));
    }

    /**
     * Verifies the specified data against the specified signature and the
     * public key from the specified certificate.
     * 
     * @param data the data to be verified.
     * @param signature the signature.
     * @param certificate the certificate from which public key will be used in
     *      the verification.
     * @return {@code true} if it is correct or {@code false} if the certificate
     *      is not signed by a trusted CA or it has already expired.
     * @throws SignatureException if an error occurred when signing, i.e. one of
     * the following exceptions has been thrown:
     *      {@link InvalidKeyException}, {@link NoSuchAlgorithmException}.
     */
    public boolean verifySignature(byte[] data, byte[] signature,
            X509Certificate certificate) throws SignatureException {
        Signature verifier = getVerifier(certificate);
        verifier.update(data);
        return verifier.verify(signature);
    }

    /**
     * Verifies the concatenation of the data in the specified array of data
     * against the specified signature and the public key from the specified
     * certificate.
     * 
     * @param data the array of data to be verified.
     * @param signature the signature.
     * @param certificate the certificate from which public key will be used in
     *      the verification.
     * @return {@code true} if it is correct or {@code false} if the certificate
     *      is not signed by a trusted CA or it has already expired.
     * @throws SignatureException if an error occurred when signing, i.e. one of
     * the following exceptions has been thrown:
     *      {@link InvalidKeyException}, {@link NoSuchAlgorithmException}.
     */
    public boolean verifySignature(byte[][] data, byte[] signature,
            X509Certificate certificate) throws SignatureException {
        Signature verifier = getVerifier(certificate);
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                verifier.update(data[i]);
            }
        }
        return verifier.verify(signature);
    }

    /**
     * Serializes the {@link ACLMessage} and then verifies these bytes against
     * the specified signature and the public key from the specified
     * certificate.
     * 
     * It uses {@link SigLEAPACLCodec} for serialization.
     * 
     * @param message the ACL message to be verified.
     * @param signature the signature.
     * @param certificate the certificate from which public key will be used in
     *      the verification.
     * @return {@code true} if it is correct or {@code false} if the certificate
     *      is not signed by a trusted CA or it has already expired.
     * @throws SignatureException if an error occurred when signing, i.e. one of
     * the following exceptions has been thrown:
     *      {@link InvalidKeyException}, {@link NoSuchAlgorithmException}.
     */
    public boolean verifySignature(ACLMessage message, byte[] signature,
            X509Certificate certificate) throws SignatureException {
        return verifySignature(sigLEAPCodec.encode(message, null), signature,
                certificate);
    }
}
