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
package jade.domain.FIPAAgentManagement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

/**
 * This class is a encryption marker.  If it is present in the properties of the
 * envelope of an ACL message, the
 * {@link jade.security.pki.messaging.PKIAgentMessagingService} must perform
 * appropriate actions, i.e. encryption or decryption of the ACL message.
 *
 * @author Amadeusz Żołnowski
 */
public class PKICrypt extends PKIObject {

    /**
     * The short name for use in the properties (see @link PKIProperty).
     */
    public static final String NAME = "pki-crypt";
    private final String cipherTransformation;
    private final AlgorithmParameterSpec keyParams;
    private byte[] ciphertext;
    private byte[] encryptedKey;

    /**
     * Creates the {@code PKICrypt} object initialized with a key and
     * an algorithm parameters.  The rest of the fields are left {@code null}.
     * 
     * @param cipherTransformation the name of the transformation, e.g.,
     *      AES/CBC/PKCS7Padding.  See <a href="http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html#AppA">Appendix
     *      A in the Java Cryptography Extension Reference Guide</a> for
     *      information about standard transformation names. 
     * @param keyParams the parameter set for the session key generator.
     */
    public PKICrypt(String cipherTransformation,
            AlgorithmParameterSpec keyParams) {
        this.cipherTransformation = cipherTransformation;
        this.keyParams = keyParams;
    }

    /**
     * Creates the new {@code PKICrypt} object from the existing
     * {@code PKICrypt} object.  All the fields are deeply copied from the
     * source object.
     * 
     * @param pkiCrypt the source {@code PKICrypt} object.
     */
    public PKICrypt(final PKICrypt pkiCrypt) {
        if (pkiCrypt.getCiphertext() != null) {
            this.ciphertext = Arrays.copyOf(pkiCrypt.getCiphertext(),
                    pkiCrypt.getCiphertext().length);
        }
        this.cipherTransformation = pkiCrypt.getCipherTransformation();
        if (pkiCrypt.getEncryptedKey() != null) {
            this.encryptedKey = Arrays.copyOf(pkiCrypt.getEncryptedKey(),
                    pkiCrypt.getEncryptedKey().length);
        }
        this.keyParams = pkiCrypt.getKeyParams();
    }

    /**
     * Clears {@code ciphertext} and {@code encryptedKey} regardless of the
     * {@code full} argument value.
     * 
     * @param full doesn't matter for this {@link PKIObject} subclass.
     */
    @Override
    public void clear(boolean full) {
        ciphertext = null;
        encryptedKey = null;
    }

    /**
     * Creates a new independent instance of this object.  The deep copy is
     * performed.
     * 
     * @return the new {@code PKICrypt}.
     */
    @Override
    protected PKICrypt clone() {
        return new PKICrypt(this);
    }

    /**
     * Searches for a property storing a {@code PKICrypt} object in the
     * {@link Envelope} {@code env} and returns it if found.
     * 
     * @param env the envelope of the ACL message to be searched.
     * @return the {@link Property} object if found or {@code null} if not
     *      found.
     */
    public static Property findPropertyIn(final Envelope env) {
        return PKIObject.findPropertyIn(env, NAME);
    }

    /**
     * Searches for a {@code PKICrypt} in the {@link Envelope} {@code env} and
     * returns it if found.
     * 
     * @param env the envelope of the ACL message to be searched.
     * @return the {@link PKICrypt} object if found or {@code null} if not
     *      found.
     */
    public static PKICrypt findIn(final Envelope env) {
        return (PKICrypt) PKIObject.findIn(env, NAME);
    }

    /**
     * Removes a property storing a {@code PKICrypt} object from the
     * {@link Envelope} env.
     * 
     * No error is signaled if there's no such property.
     * 
     * @param env the envelope of the ACL message to be searched.
     */
    public static void removeProperty(Envelope env) {
        PKIObject.removeProperty(env, NAME);
    }

    /**
     * Returns the ciphertext.
     * 
     * @return the ciphertext.
     */
    public byte[] getCiphertext() {
        return ciphertext;
    }

    /**
     * Sets the ciphertext.
     * 
     * @param ciphertext the ciphertext to set.
     */
    public void setCiphertext(byte[] ciphertext) {
        this.ciphertext = ciphertext;
    }

    @Override
    public String getName() {
        return PKICrypt.NAME;
    }

    /**
     * Append all fields of this object to the stream, especially the ciphertext
     * which must be included in the process of generating the signature.
     * 
     * @see PKIObject#writeSignable(java.io.ByteArrayOutputStream)
     * @param baos the stream passed by {@link
     * jade.security.pki.messaging.util.SigLEAPACLCodec#encode(jade.lang.acl.ACLMessage,
     * java.lang.String)}.
     * @throws IOException if the {@code keyParams} field couldn't be
     *      serialized.
     */
    @Override
    public void writeSignable(ByteArrayOutputStream baos) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        try {
            oos.writeObject(keyParams);
        } finally {
            oos.close();
        }

        baos.write(cipherTransformation.getBytes());
        baos.write(ciphertext);
        baos.write(encryptedKey);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("  :cipher-transf ");
        sb.append(cipherTransformation);
        sb.append("  :key-params ");
        sb.append(keyParams);
        sb.append("  :ciphertext ");
        sb.append(Arrays.toString(ciphertext));
        sb.append("  :enc-key ");
        sb.append(Arrays.toString(encryptedKey));

        return sb.toString();
    }

    /**
     * Returns the cipherTransformation.
     * 
     * @return the cipherTransformation.
     */
    public String getCipherTransformation() {
        return cipherTransformation;
    }

    public String getAlgorithm() {
        return cipherTransformation.split("/", 3)[0];
    }

    /**
     * Returns the encryptedKey.
     * 
     * @return the encryptedKey
     */
    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    /**
     * Sets the encryptedKey.
     * 
     * @param encryptedKey the encryptedKey to set.
     */
    public void setEncryptedKey(byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    /**
     * Returns keyParams.
     * 
     * @return the keyParams.
     */
    public AlgorithmParameterSpec getKeyParams() {
        return keyParams;
    }
}
