/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.pki;

import jade.security.pki.agent.SAgent;
import jade.security.pki.mobility.agent.SMobileAgent;
import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 *
 * @author aidecoe
 */
public class SAgentExmpl extends SMobileAgent {

    private int constantValue;
    private int counter;

    @Override
    protected void afterMove() {
        counter++;
        getLogger().log(Level.INFO, "counter = {0}", Integer.toString(counter));
        getLogger().log(Level.INFO, "Last container: {0}",
                getLastContainerCertificate().getSubjectX500Principal().
                getName());
    }

    @Override
    public byte[] getMutableData() {
        return ByteBuffer.allocate(4).putInt(counter).array();
    }

    @Override
    protected void setup() {
        super.setup();
        counter = 0;
        constantValue = 3;
    }

    @Override
    public byte[] getImmutableData() {
        return ByteBuffer.allocate(4).putInt(constantValue).array();
    }
}
