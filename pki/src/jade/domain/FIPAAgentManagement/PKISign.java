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
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * This class is a signature marker.  If it is present in the properties of the
 * envelope of an ACL message, the
 * {@link jade.security.pki.messaging.PKIAgentMessagingService} must perform
 * appropriate actions, i.e. signing or verification of the ACL message.
 * 
 * @author Amadeusz Żołnowski
 */
public class PKISign extends PKIObject {

    /**
     * The short name for use in the properties (see @link PKIProperty).
     */
    public static final String NAME = "pki-sign";
    private X509Certificate certificate;
    private byte[] signature;

    /**
     * Creates the {@code PKISign} object.  None of the fields is initialized,
     * yet.
     */
    public PKISign() {
    }

    /**
     * Creates the new {@code PKISign} object from the existing
     * {@code PKISign} object.  All the fields are deeply copied from the
     * source object.
     * 
     * @param pkiSign the source {@code PKISign} object.
     */
    public PKISign(final PKISign pkiSign) {
        this.certificate = pkiSign.getCertificate();
        if (pkiSign.getSignature() != null) {
            this.signature = Arrays.copyOf(pkiSign.getSignature(),
                    pkiSign.getSignature().length);
        }
    }

    /**
     * Clears the {@code signature} field and optionally the {@code certificate}
     * field, too.
     * 
     * @param full if {@code true} clear all fields: (@code signature} and
     *      {@code certificate}, otherwise clear only {@code signature}.
     */
    @Override
    public void clear(boolean full) {
        signature = null;
        if (full) {
            certificate = null;
        }
    }

    /**
     * Creates a new independent instance of this object.  The deep copy is
     * performed.
     * 
     * @return the new {@code PKISign}.
     */
    @Override
    protected PKISign clone() {
        return new PKISign(this);
    }

    /**
     * Searches for a property storing a {@code PKISign} object in the
     * {@link Envelope} {@code env} and returns it if found.
     * 
     * @param env the envelope of the ACL message to be searched.
     * @return the {@link Property} object if found or {@code null} if not
     *      found.
     */
    public static Property findPropertyIn(Envelope env) {
        return PKIObject.findPropertyIn(env, NAME);
    }

    /**
     * Searches for a {@code PKISign} in the {@link Envelope} {@code env} and
     * returns it if found.
     * 
     * @param env the envelope of the ACL message to be searched.
     * @return the {@link PKISign} object if found or {@code null} if not found.
     */
    public static PKISign findIn(Envelope env) {
        return (PKISign) PKIObject.findIn(env, NAME);
    }

    /**
     * Removes a property storing a {@code PKISign} object from the
     * {@link Envelope} env.
     * 
     * No error is signaled if there's no such property.
     * 
     * @param env the envelope of the ACL message to be searched.
     */
    public static void removeProperty(Envelope env) {
        PKIObject.removeProperty(env, NAME);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("  :certficate ");
        if (certificate != null) {
            sb.append(certificate.getSubjectX500Principal().getName());
        } else {
            sb.append("null");
        }
        sb.append("  :signature ");
        sb.append(Arrays.toString(signature));

        return sb.toString();
    }

    /**
     * Returns the certificate.
     * 
     * @return the certificate.
     */
    public X509Certificate getCertificate() {
        return certificate;
    }

    /**
     * Extracts the specified field value from the subject field of the
     * certificate.
     * 
     * @param fieldName the name of the field which value extract.
     * @return the string value of the specified field from the subject field of
     *      the certificate.
     */
    public String getCertificateSubjectDNF(String fieldName) {
        String dn = certificate.getSubjectX500Principal().getName();
        if (dn == null) {
            return null;
        }

        fieldName = fieldName + "=";
        for (String field : dn.split(",")) {
            field = field.trim();
            if (field.startsWith(fieldName)) {
                return field.substring(fieldName.length());
            }
        }

        return null;
    }

    private String getCertificateSubjectDNCNF(int n) {
        String cn = getCertificateSubjectDNF("CN");
        if (cn == null) {
            return null;
        }
        String[] f = cn.split("/");
        if (f.length != 3) {
            return null;
        }
        return f[n];
    }

    /**
     * Extracts the container name from the CN field from the certificate.
     * 
     * @return the container name.
     */
    public String getCertificateSubjectDNContainerName() {
        return getCertificateSubjectDNCNF(2);
    }

    /**
     * Extracts the container host name from the CN field from the certificate.
     * 
     * @return the container host name.
     */
    public String getCertificateSubjectDNHostName() {
        return getCertificateSubjectDNCNF(0);
    }

    /**
     * Extracts the platform name from the CN field from the certificate.
     * 
     * @return the platform name.
     */
    public String getCertificateSubjectDNPlatformName() {
        return getCertificateSubjectDNCNF(1);
    }

    /**
     * Sets the certificate.
     * 
     * @param certificate the certificate to set.
     */
    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    /**
     * Returns the signature.
     * 
     * @return the signature.
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * Sets the signature.
     * 
     * @param signature the signature to set.
     */
    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    @Override
    public String getName() {
        return PKISign.NAME;
    }

    /**
     * Append only the {@code signature} field to the stream, therefore the
     * certificate has to be set before this method is called.
     * 
     * @see PKIObject#writeSignable(java.io.ByteArrayOutputStream)
     * @param baos the stream passed by {@link
     * jade.security.pki.messaging.util.SigLEAPACLCodec#encode(jade.lang.acl.ACLMessage,
     * java.lang.String)}.
     * @throws IOException if the {@code certificate} field couldn't be
     *      serialized.
     */
    @Override
    public void writeSignable(ByteArrayOutputStream baos) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        try {
            oos.writeObject(certificate);
        } finally {
            oos.close();
        }
    }
}
