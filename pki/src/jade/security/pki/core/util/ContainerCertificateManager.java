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

import jade.core.AID;
import jade.util.Logger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for temporarily storing certificates and verifying
 * them against CA-s loaded with help of KeyStoreLoader.
 * 
 * CA-s are loaded into KeyStore object and certificates for specific containers
 * are stored in map per AID of the agent.
 *
 * @author Amadeusz Żołnowski
 */
public class ContainerCertificateManager {

    private final CertificateFactory cf;
    private final KeyStore ts;
    private final PKIXParameters params;
    private final CertPathValidator certPathValidator;
    private final Logger logger = Logger.getMyLogger(
            ContainerCertificateManager.class.getName());
    private Map<AID, X509Certificate> certsByAID;

    /**
     * Creates {@code ContainerCertificateManager} object and loads certificates
     * from {@code trustStore} secured with {@code trustStorePass}.  It used
     * {@link KeyStoreLoader} to load certificates from key store.
     * 
     * @param trustStore path to key store storing certificates of trusted CA-s.
     * @param trustStorePass password to the key store above.
     * @throws KeyStoreException if error occurred when opening/reading key
     *      store file.
     */
    public ContainerCertificateManager(String trustStore, String trustStorePass)
            throws KeyStoreException {
        ts = KeyStoreLoader.load(trustStore, trustStorePass);

        try {
            cf = CertificateFactory.getInstance("X.509");
            params = new PKIXParameters(ts);
            params.setRevocationEnabled(false);
            certPathValidator = CertPathValidator.getInstance(
                    CertPathValidator.getDefaultType());
        } catch (CertificateException ex) {
            throw new KeyStoreException(ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new KeyStoreException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new KeyStoreException(ex);
        }

        this.certsByAID = new HashMap<AID, X509Certificate>();

        logger.log(Level.INFO, "Loaded trust store: {0}", trustStore);
    }

    /**
     * Adds certificate of the container on which resides the agent with given
     * aid.
     * 
     * @param aid Identifier of the agent.
     * @param cert The certificate of the container on which resides the agent.
     */
    public void add(AID aid, X509Certificate cert) {
        certsByAID.put(aid, cert);
    }

    /**
     * Gets certificate of the container on which resides the agent with given
     * aid.
     * 
     * @param aid Identifier of the agent.
     * @return The certificate of the container on which resides the agent.
     */
    public X509Certificate get(AID aid) {
        return certsByAID.get(aid);
    }

    /**
     * Verify correctness of the certificate.  It is verified if it is signed by
     * trusted CA (one of those loaded from trustStore) and if it hasn't expired
     * already.
     * 
     * @param cert The certificate to verify.
     * @throws CertificateException if path of the certificate isn't valid or if
     *      the certificate has already expired.
     */
    public void verify(X509Certificate cert) throws CertificateException {
        cert.checkValidity();

        List<Certificate> certList = new ArrayList<Certificate>();
        certList.add(cert);
        CertPath certPath = cf.generateCertPath(certList);

        try {
            certPathValidator.validate(certPath, params);
        } catch (CertPathValidatorException ex) {
            throw new CertificateException("Invalid certifcate path", ex);
        } catch (InvalidAlgorithmParameterException ex) {
            // It shouldn't happen, because if it's invalid, the exception
            // should be already raised in ContainerCertificateManager().
            throw new NullPointerException(ex.toString());
        }
    }
}
