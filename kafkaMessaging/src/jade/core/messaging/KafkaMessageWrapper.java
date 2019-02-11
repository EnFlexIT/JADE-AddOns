package jade.core.messaging;

import jade.core.AID;

public class KafkaMessageWrapper {
	private AID senderID;
	private AID receiverID;
	private GenericMessage msg;
	
	public KafkaMessageWrapper() {
	}
	
	public KafkaMessageWrapper(AID senderID, GenericMessage msg, AID receiverID) {
		this.senderID = senderID;
		this.msg = msg;
		this.receiverID = receiverID;
	}

	public AID getSenderID() {
		return senderID;
	}
	public AID getReceiverID() {
		return receiverID;
	}
	public GenericMessage getMsg() {
		return msg;
	}
}
