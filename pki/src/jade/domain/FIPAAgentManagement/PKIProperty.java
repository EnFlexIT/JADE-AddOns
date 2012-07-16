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
 * This subclass just restricts {@code Property} to accept only objects of the
 * class {@link PKIObject}.
 *
 * @author Amadeusz Żołnowski
 */
public class PKIProperty extends Property {

    /**
     * Creates the {@code PKIProperty} and initialize it with
     * a {@link PKIObject}.  The name of the property is retrieved with the
     * {@link PKIObject#getName()} method.
     * 
     * @param po the instance of a {@link PKIObject}.
     */
    public PKIProperty(final PKIObject po) {
        super(po.getName(), po);
    }

    /**
     * Creates a new independent instance of this object.  The deep copy is
     * performed.
     * 
     * @return the new {@code PKIProperty}.
     */
    @Override
    public Object clone() {
        if (getValue() instanceof PKIObject) {
            return new PKIProperty(((PKIObject) getValue()).clone());
        }

        return clone();
    }
}
