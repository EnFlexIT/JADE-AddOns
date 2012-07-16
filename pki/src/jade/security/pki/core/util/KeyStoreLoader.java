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

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * This class provides method to load certificates from key store file into
 * KeyStore object.
 *
 * @author Amadeusz Żołnowski
 */
public class KeyStoreLoader {

    /**
     * Loads certificates from key store file into {@link KeyStore} object.
     * 
     * @param path path to the key store file.
     * @param pass password to the key store.
     * @return the {@link KeyStore} object.
     * @throws KeyStoreException if error occurred when opening/reading key
     *      store file.
     */
    public static KeyStore load(String path, String pass)
            throws KeyStoreException {
        final String ksExMsg =
                "Could not open key store file: ".concat(path);

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        try {
            ks.load(new FileInputStream(path),
                    pass == null ? null : pass.toCharArray());
        } catch (IOException ex) {
            throw new KeyStoreException(ksExMsg, ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new KeyStoreException(ksExMsg, ex);
        } catch (CertificateException ex) {
            throw new KeyStoreException(ksExMsg, ex);
        }

        return ks;
    }
}
