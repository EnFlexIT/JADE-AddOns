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
package jade.security.pki.core.exceptions;

import jade.security.pki.PKIException;

/**
 * This exception signals error in an asymmetric cryptographic operation.  The
 * detailed exception is always nested within this one.
 *
 * @author Amadeusz Żołnowski
 */
public class AsymmetricCipherException extends PKIException {

    /**
     * Creates an instance of an exception and nests detailed exception.
     * 
     * @param cause the detailed exception.
     */
    public AsymmetricCipherException(Throwable cause) {
        super(cause);
    }
}
