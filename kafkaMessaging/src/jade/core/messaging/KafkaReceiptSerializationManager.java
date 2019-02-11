package jade.core.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import jade.core.AID;
import jade.core.IMTPException;
import jade.core.NotFoundException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.LEAPACLCodec;
import jade.security.JADESecurityException;

public class KafkaReceiptSerializationManager implements Serializer<KafkaReceiptWrapper>, Deserializer<KafkaReceiptWrapper> {

	@Override
	public byte[] serialize(String arg0, KafkaReceiptWrapper w) {
		byte[] bytes = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			Throwable t = w.getException();
			if (t == null) {
				dos.writeByte(0);
			}
			else {
				if (t instanceof NotFoundException) {
					dos.writeByte(1);
				}
				else if (t instanceof JADESecurityException) {
					dos.writeByte(2);
				}
				else {
					dos.writeByte(3);
				}
				dos.writeUTF(t.getMessage());
			}
			bytes = baos.toByteArray();
		} 
		catch (IOException ioe){
			ioe.printStackTrace();
		} 
		return bytes;
	}

	@Override
	public KafkaReceiptWrapper deserialize(String arg0, byte[] bytes) {
		KafkaReceiptWrapper w = null;
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
		try {
			byte failureType = dis.readByte();
			if (failureType == 0) {
				w = new KafkaReceiptWrapper();
			}
			else {
				Throwable t = null;
				String failureMessage = dis.readUTF();
				if (failureType == 1) {
					t = new NotFoundException(failureMessage) {
					    public Throwable fillInStackTrace() {
					        return this;
					    }
					};
				}
				else if (failureType == 2) {
					t = new JADESecurityException(failureMessage) {
					    public Throwable fillInStackTrace() {
					        return this;
					    }
					};
				}
				else {
					t = new IMTPException(failureMessage) {
					    public Throwable fillInStackTrace() {
					        return this;
					    }
					};
				}
				w = new KafkaReceiptWrapper(t);
			}
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return w;
	}


	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> arg0, boolean arg1) {
	}
}
