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

import jade.security.pki.core.exceptions.AsymmetricCipherException;
import jade.util.Logger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class provides methods of asymmetric encryption and decryption.  The
 * class is a wrapper around standard cryptographic functions provided by Java
 * Cryptographic Extension framework.
 * 
 * The object is initialized with a key manager which stores a private key and
 * a certificate of the container which are used in the methods of this class.
 *
 * @author Amadeusz Żołnowski
 */
public class ContainerAsymmetricCipher {

    private final Logger logger = Logger.getMyLogger(
            ContainerAsymmetricCipher.class.getName());
    private final ContainerKeyManager km;

    /**
     * Creates {@code ContainerAsymmetricCipher} object initialized with given
     * key manager.
     * 
     * @param km initialized key manager which stores private key of the
     *      container and its certificate.
     */
    public ContainerAsymmetricCipher(ContainerKeyManager km) {
        this.km = km;
    }

    private Cipher getAsymEncrypter(X509Certificate certificate)
            throws AsymmetricCipherException {
        try {
            String algo = certificate.getPublicKey().getAlgorithm();
            logger.log(Level.INFO, "Cipher asymmetric algorithm: {0}", algo);
            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.ENCRYPT_MODE, certificate);
            return cipher;
        } catch (InvalidKeyException ex) {
            throw new AsymmetricCipherException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new AsymmetricCipherException(ex);
        } catch (NoSuchPaddingException ex) {
            throw new AsymmetricCipherException(ex);
        }
    }

    private Cipher getAsymDecrypter() throws AsymmetricCipherException {
        try {
            String algo = km.getPrivateKey().getAlgorithm();
            logger.log(Level.INFO, "Cipher asymmetric algorithm: {0}", algo);
            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.DECRYPT_MODE, km.getPrivateKey());
            return cipher;
        } catch (InvalidKeyException ex) {
            throw new AsymmetricCipherException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new AsymmetricCipherException(ex);
        } catch (NoSuchPaddingException ex) {
            throw new AsymmetricCipherException(ex);
        }
    }

    /**
     * Decrypts the specified ciphertext with private key of the container.
     * 
     * @param ciphertext the ciphertext to be decrypted.
     * @return the decrypted bytes.
     * @throws AsymmetricCipherException if an error occurred on decryption,
     *      i.e. one of the following exceptions has been thrown:
     *      {@link IllegalBlockSizeException}, {@link BadPaddingException}.
     */
    public byte[] decrypt(byte[] ciphertext)
            throws AsymmetricCipherException {
        try {
            return getAsymDecrypter().doFinal(ciphertext);
        } catch (IllegalBlockSizeException ex) {
            throw new AsymmetricCipherException(ex);
        } catch (BadPaddingException ex) {
            throw new AsymmetricCipherException(ex);
        }
    }

    /**
     * Encrypts the specified data with a public key extracted from the
     * specified certificate.
     * 
     * @param data the data to be encrypted.
     * @param certificate the certificate of the public key to be used for
     *      encryption.
     * @return the ciphertext.
     * @throws AsymmetricCipherException if an error occurred on decryption,
     *      i.e.  one of the following exceptions has been thrown:
     *      {@link IllegalBlockSizeException}, {@link BadPaddingException}.
     */
    public byte[] encrypt(byte[] data, X509Certificate certificate)
            throws AsymmetricCipherException {
        try {
            return getAsymEncrypter(certificate).doFinal(data);
        } catch (IllegalBlockSizeException ex) {
            throw new AsymmetricCipherException(ex);
        } catch (BadPaddingException ex) {
            throw new AsymmetricCipherException(ex);
        }
    }

    /**
     * Decrypts the specified ciphertext (which is supposed to be an encrypted
     * symmetric/session key) with the private key of the container and
     * deserializes it into {@link SecretKeySpec}.
     * 
     * @param encKey the ciphertext which is supposed to be an encrypted
     *      symmetric/session key.
     * @param keyAlgo the name of the algorithm for which the key is intended.
     * @return the decrypted symmetric/session key.
     * @throws AsymmetricCipherException if an error occurred on decryption,
     *      i.e. one of the following exceptions has been thrown:
     *      {@link IllegalBlockSizeException}, {@link BadPaddingException}.
     */
    public SecretKeySpec decryptKey(byte[] encKey, String keyAlgo)
            throws AsymmetricCipherException {
        return new SecretKeySpec(decrypt(encKey), keyAlgo);
    }

    /**
     * Encrypts the specified symmetric/session key with the private key of the
     * container.
     * 
     * @param key the symmetric/session key to be encrypted.
     * @param certificate the certificate of the public key to be used for
     *      encryption.
     * @return the ciphertext.
     * @throws AsymmetricCipherException if an error occurred on decryption,
     *      i.e. one of the following exceptions has been thrown:
     *      {@link IllegalBlockSizeException}, {@link BadPaddingException}.
     */
    public byte[] encryptKey(Key key, X509Certificate certificate)
            throws AsymmetricCipherException {
        return encrypt(key.getEncoded(), certificate);
    }
}
