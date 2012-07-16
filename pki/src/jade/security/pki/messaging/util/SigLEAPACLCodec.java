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
package jade.security.pki.messaging.util;

import jade.domain.FIPAAgentManagement.PKIObject;
import jade.domain.FIPAAgentManagement.Property;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.LEAPACLCodec;
import jade.util.leap.Iterator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * The {@link LEAPACLCodec} doesn't include the properties of the envelope of
 * an ACL message while including them is required in the process of generating
 * the signature and also in verification process, too.  This subclass overrides
 * the {@code encode} method to fulfil this requirement.
 * 
 * The {@code decode} method is not needed in the processes of signing or
 * verifying a message, therefore it is unsupported.
 *
 * @author Amadeusz Żołnowski
 */
public class SigLEAPACLCodec extends LEAPACLCodec {

    /**
     * Serializes the ACL message.  In contrary to the method in the super class
     * this includes the properties of the envelope.
     * 
     * @param msg the ACL message to be serialized.
	 * @param charset this parameter is just passed to the method in the super
     *      class, but even there it is not taken into account.
	 * @return a byte array, containing the encoded message.
     */
    @Override
    public byte[] encode(ACLMessage msg, String charset) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            baos.write(super.encode(msg, charset));

            if (msg.getEnvelope() != null) {
                for (Iterator it = msg.getEnvelope().getAllProperties();
                        it.hasNext();) {
                    Property p = (Property) it.next();
                    baos.write(p.getName().getBytes());
                    Object value = p.getValue();

                    if (value instanceof PKIObject) {
                        PKIObject po = (PKIObject) value;
                        po.writeSignable(baos);
                    } else {
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        try {
                            oos.writeObject(value);
                        } finally {
                            oos.close();
                        }
                    }
                }
            }

            return baos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            return new byte[0];
        } finally {
            try {
                baos.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * This method is unsupported, but it is overridden to prevent a confusion
     * if someone would try to decode message encoded with the {@link #encode}
     * method.
     * 
     * @param data
     * @param charset
     * @return nothing
     * @throws jade.lang.acl.ACLCodec.CodecException 
     */
    @Override
    public ACLMessage decode(byte[] data, String charset)
            throws CodecException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
