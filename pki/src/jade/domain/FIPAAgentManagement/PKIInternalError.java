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

/**
 * The exception signals error related to cryptographic operations performed by
 * the {@link jade.security.pki.messaging.PKIAgentMessagingService} filters.
 * 
 * It is supposed to be nested into an ACL message sent to the sender to notify
 * it about the failure of message delivery caused by failure in some of
 * cryptographic operations.
 * 
 * The {@link jade.domain.FIPAAgentManagement.InternalError} string is prefixed
 * with {@code internal-error} and {@code PKIInternalError} is prefixed with
 * {@code pki-internal error}.
 *
 * @author Amadeusz Żołnowski
 */
public class PKIInternalError
        extends jade.domain.FIPAAgentManagement.InternalError {

    String s2;

    /**
     * Creates the {@code PKIInternalError} exception object.
     * 
     * @param s 
     */
    public PKIInternalError(String s) {
        s2 = s;
        setMessage("(pki-internal-error \"" + s2 + "\")");
    }

    /**
     * Sets the error message and prefixes it with {@code pki-internal-error}
     * prefix instead of {@code internal-error} (see
     * {@link jade.domain.FIPAAgentManagement.InternalError#setErrorMessage(java.lang.String)}.
     * 
     * @param s the error message.
     */
    @Override
    public void setErrorMessage(String s) {
        s2 = s;
        setMessage("(pki-internal-error \"" + s2 + "\")");
    }

    /**
     * Returns the error message prefixed with {@code pki-internal-error}
     * instead of {@code internal-error} (see
     * {@link jade.domain.FIPAAgentManagement.InternalError#setErrorMessage(java.lang.String)}.
     * 
     * @return the prefixed error message.
     */
    @Override
    public String getErrorMessage() {
        return s2;
    }
}
