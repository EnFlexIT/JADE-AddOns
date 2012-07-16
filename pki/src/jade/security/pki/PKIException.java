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
package jade.security.pki;

/**
 * The base exception for JADE-PKI unless there's more appropriate exception
 * class to derive from.
 *
 * @author Amadeusz Żołnowski
 */
public class PKIException extends Exception {

    /**
     * Creates an instance of an exception.  It nests another exception.
     * 
     * @param cause exception to nest into this one.
     */
    public PKIException(Throwable cause) {
        super(cause);
    }
}
