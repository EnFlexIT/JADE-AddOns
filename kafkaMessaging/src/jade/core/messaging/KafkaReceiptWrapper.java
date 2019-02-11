package jade.core.messaging;

public class KafkaReceiptWrapper {
	private Throwable t;
	
	public KafkaReceiptWrapper() {
	}
	
	public KafkaReceiptWrapper(Throwable t) {
		this.t = t;
	}
	
	public Throwable getException() {
		return t;
	}
}
