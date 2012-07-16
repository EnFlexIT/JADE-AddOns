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

import jade.lang.acl.ACLCodec.CodecException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.LEAPACLCodec;
import jade.security.pki.core.exceptions.SymmetricCryptoException;
import jade.security.pki.core.spec.KeySizeAlgorithmParameterSpec;
import jade.util.Logger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class provides methods of symmetric encryption, decryption and the
 * session key generator.  The class is a wrapper around standard cryptographic
 * functions provided by Java Cryptographic Extension framework.
 * 
 * @author Amadeusz Żołnowski
 */
public abstract class SymmetricCrypto {

    static private Logger getLogger() {
        return Logger.getMyLogger(SymmetricCrypto.class.getName());
    }

    /**
     * Generates and returns a secret key for the specified algorithm.
     * 
     * @param symAlgo the standard name of the requested key algorithm.  See
     *      <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html#AppA">Appendix
     *      A in the Java Cryptography Architecture Reference Guide</a> for
     *      information about standard algorithm names. 
     * 
     * @param params the parameter set for the key generator.
     * @return the new generated secret key.
     * @throws SymmetricCryptoException if error occurred on key generation
     *      causing {@link NoSuchPaddingException} or
     *      {@link InvalidKeyException} which is nested into this exception.
     */
    static public SecretKeySpec genKey(String symAlgo,
            AlgorithmParameterSpec params) throws SymmetricCryptoException {
        try {
            KeyGenerator gen = KeyGenerator.getInstance(symAlgo);
            if (params instanceof KeySizeAlgorithmParameterSpec) {
                gen.init(((KeySizeAlgorithmParameterSpec) params).getSize());
            } else {
                gen.init(params);
            }
            return (SecretKeySpec) gen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            throw new SymmetricCryptoException(ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new SymmetricCryptoException(ex);
        }
    }

    static private Cipher getSymCipher(int op, String transformation, Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException {
        Cipher cipher = Cipher.getInstance(transformation);
        getLogger().log(Level.INFO, "Cipher symmetric algorithm: {0}",
                cipher.getAlgorithm());
        cipher.init(op, key);
        return cipher;
    }

    /**
     * Decrypts the ciphertext with the specified secret key.
     * 
     * @param ciphertext the ciphertext to be decrypted.
     * @param cipherTransformation the name of the transformation, e.g.,
     *      AES/CBC/PKCS7Padding.  See <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html#AppA">Appendix
     *      A in the Java Cryptography Extension Reference Guide</a> for
     *      information about standard transformation names. 
     * @param key the encryption secret key.
     * @return decrypted bytes
     * @throws SymmetricCryptoException if error occured on decryption causing
     *      {@link NoSuchAlgorithmException}, {@link NoSuchPaddingException},
     *      {@link InvalidKeyException}, {@link IllegalBlockSizeException} or
     *      {@link BadPaddingException} which is nested into this exception.
     */
    static public byte[] decrypt(byte[] ciphertext,
            String cipherTransformation, Key key)
            throws SymmetricCryptoException {
        try {
            return getSymCipher(Cipher.DECRYPT_MODE, cipherTransformation, key).
                    doFinal(ciphertext);
        } catch (BadPaddingException ex) {
            throw new SymmetricCryptoException(ex);
        } catch (IllegalBlockSizeException ex) {
            throw new SymmetricCryptoException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new SymmetricCryptoException(ex);
        } catch (NoSuchPaddingException ex) {
            throw new SymmetricCryptoException(ex);
        } catch (InvalidKeyException ex) {
            throw new SymmetricCryptoException(ex);
        }
    }

    /**
     * Decrypts the ciphertext (which is supposed to be a serialized
     * {@link ACLMessage}) with the specified secret key and deserializes the
     * result into {@link ACLMessage}.
     * 
     * @param ciphertext the ciphertext (which is supposed to be a serialized
     *      {@link ACLMessage}) to be decrypted.
     * @param cipherTransformation the name of the transformation, e.g.,
     *      AES/CBC/PKCS7Padding.  See <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html#AppA">Appendix
     *      A in the Java Cryptography Extension Reference Guide</a> for
     *      information about standard transformation names. 
     * @param key the encryption secret key.
     * @return the decrypted ACL message.
     * @throws jade.lang.acl.ACLCodec.CodecException if decrypted bytes cannot
     *      be deserialized into {@link ACLMessage}.
     * @throws SymmetricCryptoException if error occured on decryption causing
     *      {@link NoSuchAlgorithmException}, {@link NoSuchPaddingException},
     *      {@link InvalidKeyException}, {@link IllegalBlockSizeException} or
     *      {@link BadPaddingException} which is nested into this exception.
     */
    static public ACLMessage decrypt2(byte[] ciphertext,
            String cipherTransformation, Key key) throws CodecException,
            SymmetricCryptoException {
        LEAPACLCodec leapCodec = new LEAPACLCodec();
        byte[] data = decrypt(ciphertext, cipherTransformation, key);
        return leapCodec.decode(data, null);
    }

    /**
     * Encrypts the specified data with the specified key with the described
     * cipher transformation.
     * 
     * @param data the data to be encrypted.
     * @param cipherTransformation the name of the transformation, e.g.,
     *      AES/CBC/PKCS7Padding.  See <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html#AppA">Appendix
     *      A in the Java Cryptography Extension Reference Guide</a> for
     *      information about standard transformation names. 
     * @param key the encryption secret key.
     * @return the ciphertext.
     * @throws SymmetricCryptoException if error occured on encryption causing
     *      {@link NoSuchAlgorithmException}, {@link NoSuchPaddingException},
     *      {@link InvalidKeyException}, {@link IllegalBlockSizeException} or
     *      {@link BadPaddingException} which is nested into this exception.
     */
    static public byte[] encrypt(byte[] data, String cipherTransformation,
            Key key) throws SymmetricCryptoException {
        try {
            return getSymCipher(Cipher.ENCRYPT_MODE, cipherTransformation, key).
                    doFinal(data);
        } catch (NoSuchAlgorithmException ex) {
            throw new SymmetricCryptoException(ex);
        } catch (NoSuchPaddingException ex) {
            throw new SymmetricCryptoException(ex);
        } catch (InvalidKeyException ex) {
            throw new SymmetricCryptoException(ex);
        } catch (IllegalBlockSizeException ex) {
            throw new SymmetricCryptoException(ex);
        } catch (BadPaddingException ex) {
            throw new SymmetricCryptoException(ex);
        }
    }

    /**
     * Encrypts the message with the specified key with the described cipher
     * transformation.
     * 
     * @param message the ACL message to be encrypted.
     * @param cipherTransformation the name of the transformation, e.g.,
     *      AES/CBC/PKCS7Padding.  See <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html#AppA">Appendix
     *      A in the Java Cryptography Extension Reference Guide</a> for
     *      information about standard transformation names. 
     * @param key the encryption secret key.
     * @return the ciphertext.
     * @throws SymmetricCryptoException if error occurred on encryption causing
     *      {@link NoSuchAlgorithmException}, {@link NoSuchPaddingException},
     *      {@link InvalidKeyException}, {@link IllegalBlockSizeException} or
     *      {@link BadPaddingException} which is nested into this exception.
     */
    static public byte[] encrypt(ACLMessage message,
            String cipherTransformation, Key key)
            throws SymmetricCryptoException {
        LEAPACLCodec leapCodec = new LEAPACLCodec();
        return encrypt(leapCodec.encode(message, null), cipherTransformation,
                key);
    }
}
