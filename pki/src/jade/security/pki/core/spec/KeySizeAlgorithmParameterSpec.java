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
package jade.security.pki.core.spec;

import jade.util.leap.Serializable;
import java.security.spec.AlgorithmParameterSpec;

/**
 * This class specifies the parameter used in cryptographic algorithms requiring
 * only key size parameter which is for example the AES.
 *
 * @author Amadeusz Żołnowski
 */
public class KeySizeAlgorithmParameterSpec implements AlgorithmParameterSpec,
        Serializable {

    private final int size;

    /**
     * Creates a new {@code KeySizeAlgorithmParameterSpec} object from the given
     * key size.
     * 
     * @param size the key size.
     */
    public KeySizeAlgorithmParameterSpec(int size) {
        this.size = size;
    }

    /**
     * Returns the key size.
     * 
     * @return the key size.
     */
    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "size=" + Integer.toString(size);
    }
}
