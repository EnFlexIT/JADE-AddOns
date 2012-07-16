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

import jade.util.leap.Iterator;
import jade.util.leap.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This class is a base for JADE-PKI message markers which are put into
 * an ACL message envelope to indicate the operation to be performed by the
 * {@link jade.security.pki.messaging.PKIAgentMessagingService}.
 *
 * @author Amadeusz Żołnowski
 */
public abstract class PKIObject implements Serializable {

    /**
     * Constant for the content of a message with a certificate request.
     */
    public static final String REQUEST_CERTIFICATE = "Request-Certificate";

    /**
     * Clear fields of the object.
     * 
     * @param full if {@code true} - clear all fields, if {@code false} - leave
     *      fields which might have some use for other entities.
     */
    public abstract void clear(boolean full);

    /**
     * This method must be implemented in the subclass and it must create a new
     * independent object.  The deep copy must be performed.
     */
    @Override
    protected abstract PKIObject clone();

    /**
     * Get the short name for use in the properties (see {@link PKIProperty}).
     * 
     * @return the name.
     */
    public abstract String getName();

    /**
     * Append the {@code baos} stream with the data from this object which is
     * going to be included in the process of generating the signature of the
     * ACL message.
     * 
     * This method is used by {@link
     * jade.security.pki.messaging.util.SigLEAPACLCodec}.
     * 
     * @param baos the stream passed by {@link
     * jade.security.pki.messaging.util.SigLEAPACLCodec#encode(jade.lang.acl.ACLMessage,
     * java.lang.String)}.
     * @throws IOException might be thrown by {@link ByteArrayOutputStream}.
     */
    public abstract void writeSignable(ByteArrayOutputStream baos)
            throws IOException;

    /**
     * Returns string of the form of:
     * {@code   :field1 value1  :field2 value2  :field3 value3 ...}.
     * 
     * @return the string representing the object.
     */
    @Override
    public abstract String toString();

    /**
     * Searches for a property named {@code name} in the {@link Envelope}
     * {@code env} and returns it if found.
     * 
     * @param env the envelope of the ACL message to be searched.
     * @param name the name of the property.
     * @return the {@link Property} object if found or {@code null} if not
     *      found.
     */
    protected static Property findPropertyIn(Envelope env, String name) {
        Property p = null;

        for (Iterator it = env.getAllProperties(); it.hasNext();) {
            p = (Property) it.next();
            if (p.getName().equals(name)) {
                return p;
            }
        }

        return null;
    }

    /**
     * Searches for a {@code PKIObject} named {@code name} in the {@link
     * Envelope} {@code env} and returns it if found.
     * 
     * @param env the envelope of the ACL message to be searched.
     * @param name the name of the {@code PKIObject}.
     * @return the {@link PKIObject} object if found or {@code null} if not
     *      found.
     */
    protected static PKIObject findIn(Envelope env, String name) {
        Property p = findPropertyIn(env, name);
        if (p == null) {
            return null;
        }
        return (PKIObject) p.getValue();
    }

    /**
     * Removes a property named {@code name} from the {@link Envelope} env.
     * 
     * No error is signaled if there's no such property.
     * 
     * @param env the envelope of the ACL message to be searched.
     * @param name the name of the property to be removed.
     */
    protected static void removeProperty(Envelope env, String name) {
        Property p = null;

        for (Iterator it = env.getAllProperties(); it.hasNext();) {
            p = (Property) it.next();
            if (p.getName().equals(name)) {
                it.remove();
            }
        }
    }
}
