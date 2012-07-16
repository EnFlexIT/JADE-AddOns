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
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import javax.net.ssl.X509ExtendedKeyManager;

/**
 * This class loads the certificate and the private key for the container and
 * provides methods for accessing them.
 *
 * @author Amadeusz Żołnowski
 */
public class ContainerKeyManager extends X509ExtendedKeyManager {

    private final X509Certificate[] certificateChain;
    private final KeyStore ks;
    private final Logger logger = Logger.getMyLogger(this.getClass().getName());
    private final PrivateKey privateKey;

    /**
     * Creates {@code ContainerKeyManager} object and loads the certificate and
     * the key from the specified keyStore file.
     * 
     * @param containerAlias should the name of the container.
     * @param keyStore the path to key store file.
     * @param keyStorePass the password to key store above.
     * @param prvKeyPass the password to the private key with which the key is
     *      encrypted.
     * @throws KeyStoreException if error occurred when opening/reading key
     *      store file.
     */
    public ContainerKeyManager(String containerAlias, String keyStore,
            String keyStorePass, String prvKeyPass) throws KeyStoreException {
        final String prvKeyExMsg =
                "Could not get private key from key store file: ".concat(
                keyStore);
        final String aliasExMsg =
                "Key store is expected to have exactly one alias: ".concat(
                keyStore);

        ks = KeyStoreLoader.load(keyStore, keyStorePass);

        String alias = getOneAlias();
        if (alias == null) {
            throw new KeyStoreException(aliasExMsg);
        }

        try {
            privateKey = (PrivateKey) ks.getKey(alias,
                    prvKeyPass.toCharArray());
        } catch (NoSuchAlgorithmException ex) {
            throw new KeyStoreException(prvKeyExMsg, ex);
        } catch (UnrecoverableKeyException ex) {
            throw new KeyStoreException(prvKeyExMsg, ex);
        }

        certificateChain = toX509CertificateChain(
                ks.getCertificateChain(alias));

        logger.log(Level.INFO, "Retrieved private key and certificate chain "
                + "aliased {0} from key store {1}",
                new Object[]{alias, keyStore});
    }

    /**
     * This method is not supported by this class, yet.
     * 
     * @param strings
     * @param prncpls
     * @param socket
     * @return nothing
     */
    @Override
    public String chooseClientAlias(String[] strings, Principal[] prncpls,
            Socket socket) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * This method is not supported by this class, yet.
     * 
     * @param string
     * @param prncpls
     * @param socket
     * @return nothing
     */
    @Override
    public String chooseServerAlias(String string, Principal[] prncpls,
            Socket socket) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * This method is not supported by this class, yet.
     * 
     * @param string
     * @param prncpls
     * @return nothing
     */
    @Override
    public String[] getClientAliases(String string, Principal[] prncpls) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * This method is not supported by this class, yet.
     * 
     * @param string
     * @param prncpls
     * @return nothing
     */
    @Override
    public String[] getServerAliases(String string, Principal[] prncpls) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns the certificate of the container.
     * 
     * @return the certificate.
     */
    public X509Certificate getCertificate() {
        return getCertificateChain()[0];
    }

    /**
     * Returns the certificate chain for the container.
     * 
     * @return the certificate chain.
     */
    public X509Certificate[] getCertificateChain() {
        return certificateChain;
    }

    /**
     * The same as {@link #getCertificateChain()}.
     * 
     * @param string it doesn't matter, yet.
     * @return the certificate chain.
     */
    @Override
    public X509Certificate[] getCertificateChain(String string) {
        return getCertificateChain();
    }

    /**
     * Gets alias from the key store.  The key store is expected to have exactly
     * one alias.
     * 
     * @return the only alias from the key store.
     */
    private String getOneAlias() {
        String alias = null;
        Enumeration<String> aliases = null;

        try {
            aliases = ks.aliases();
        } catch (KeyStoreException ex) {
            return null;
        }
        if (aliases.hasMoreElements()) {
            alias = aliases.nextElement();
        } else {
            return null;
        }
        if (aliases.hasMoreElements()) {
            return null;
        }

        return alias;
    }

    /**
     * Returns the private key of the container.
     * 
     * @return the private key of the container.
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * The same as {@link #getPrivateKey()}.
     * 
     * @param string doesn't matter.
     * @return the private key of the container.
     */
    @Override
    public PrivateKey getPrivateKey(String string) {
        return getPrivateKey();
    }

    static private X509Certificate[] toX509CertificateChain(
            Certificate[] chain) {
        X509Certificate[] x509Chain = new X509Certificate[chain.length];

        for (int i = 0; i < chain.length; i++) {
            x509Chain[i] = (X509Certificate) chain[i];
        }

        return x509Chain;
    }
}
