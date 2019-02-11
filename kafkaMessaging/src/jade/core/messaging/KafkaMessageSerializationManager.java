package jade.core.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.LEAPACLCodec;

public class KafkaMessageSerializationManager implements Serializer<KafkaMessageWrapper>, Deserializer<KafkaMessageWrapper> {

	@Override
	public byte[] serialize(String arg0, KafkaMessageWrapper w) {
		byte[] bytes = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			GenericMessage msg = w.getMsg();
			if (msg == null) {
				dos.writeByte(0);
			}
			else {
				boolean multiple = (msg instanceof MultipleGenericMessage);
				dos.writeByte(multiple ? 2 : 1);
				if (multiple) {
					serializeMGM(dos, (MultipleGenericMessage) msg);
				}
				else {
					LEAPACLCodec.serializeAID(w.getSenderID(), dos);
					serializeGenericMessage(dos, msg);
				}
				LEAPACLCodec.serializeAID(w.getReceiverID(), dos);
			}
			bytes = baos.toByteArray();
		} 
		catch (IOException ioe){
			ioe.printStackTrace();
		} 
		return bytes;
	}

	private void serializeGenericMessage(DataOutputStream dos, GenericMessage msg) throws IOException {
		byte[] payload = msg.getPayload();
		if (payload == null) {
			payload = (new LEAPACLCodec()).encode(msg.getACLMessage(), null);
		}
		dos.writeInt(payload.length);
		dos.write(payload, 0, payload.length);
	}
	
	private void serializeMGM(DataOutputStream dos, MultipleGenericMessage mgm) throws IOException {
		List<GenericMessage> messages = mgm.getMessages();
		dos.writeInt(messages.size());
		for (GenericMessage g : messages) {
			LEAPACLCodec.serializeAID(g.getSender(), dos);
			serializeGenericMessage(dos, g);
		}
	}
	
	
	@Override
	public KafkaMessageWrapper deserialize(String arg0, byte[] bytes) {
		KafkaMessageWrapper w = null;
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
		try {
			byte type = dis.readByte();
			if (type == 0) {
				// Null (Topic initialization)
				w = new KafkaMessageWrapper();
			}
			else if (type == 1) {
				// Normal GenericMessage
				AID senderID = LEAPACLCodec.deserializeAID(dis);
				GenericMessage msg = deserializeGenericMessage(dis);
				AID receiverID = LEAPACLCodec.deserializeAID(dis);
				w = new KafkaMessageWrapper(senderID, msg, receiverID);
			}
			else {
				// MultipleGenericMessage
				GenericMessage msg = deserializeMGM(dis);
				AID receiverID = LEAPACLCodec.deserializeAID(dis);
				w = new KafkaMessageWrapper(null, msg, receiverID);
			}
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return w;
	}


	private GenericMessage deserializeGenericMessage(DataInputStream dis) throws IOException {
		byte[] payload = new byte[dis.readInt()];
		dis.read(payload, 0, payload.length);
		return new GenericMessage(null, payload);
	}
	
	private MultipleGenericMessage deserializeMGM(DataInputStream dis) throws IOException {
		int nMessages = dis.readInt();
		int length = 0;
		List<GenericMessage> messages = new ArrayList<GenericMessage>(nMessages);
		for (int i = 0; i < nMessages; ++i) {
			AID sender = LEAPACLCodec.deserializeAID(dis);
			GenericMessage g = deserializeGenericMessage(dis);
			g.setSender(sender);
			messages.add(g);
			length += g.length();
		}
		MultipleGenericMessage mgm = new MultipleGenericMessage(length);
		mgm.setMessages(messages);
		return mgm;
	}
	
	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> arg0, boolean arg1) {
	}
	
	public static void main(String[] args) {
		KafkaMessageSerializationManager mgr = new KafkaMessageSerializationManager();
		
		ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
		acl.addReceiver(new AID("r", AID.ISGUID));
		acl.setSender(new AID("s", AID.ISGUID));
		acl.setContent("CIAO");
		GenericMessage msg = new GenericMessage(acl);
		KafkaMessageWrapper w = new KafkaMessageWrapper(new AID("s", AID.ISGUID), msg, new AID("r", AID.ISGUID));
		
		byte[] bb = mgr.serialize(null, w);
		
		w = mgr.deserialize(null, bb);
		System.out.println(w.getMsg());
		
	}
}
