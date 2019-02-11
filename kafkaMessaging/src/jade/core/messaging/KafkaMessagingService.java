package jade.core.messaging;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;

import jade.core.AID;
import jade.core.IMTPException;
import jade.util.Logger;

public class KafkaMessagingService extends MomMessagingService {
	public static final String RECEIPT_TOPIC_SUFFIX = "-RECEIPT";
	public static final String CREATE_ID_PREFIX = "CREATE-";
	
	private KafkaProducer<String, KafkaMessageWrapper> messageProducer;
	private KafkaProducer<String, KafkaReceiptWrapper> receiptProducer;
	private KafkaConsumer<String, KafkaReceiptWrapper> receiptConsumer;
	private KafkaConsumer<String, KafkaMessageWrapper> incomingMessageConsumer;

	private String createId;

	@Override
	protected void initMom() {
		myLogger.log(Logger.INFO, "KafkaMessagingService at "+myLocation+" starting...");
		createProducers();
		myLogger.log(Logger.INFO, "KafkaMessagingService at "+myLocation+" - Producers for sending messages and receipts created");
		activateReceiptConsumer();
		activateIncomingCommandConsumer();
		myLogger.log(Logger.INFO, "KafkaMessagingService at "+myLocation+" - Consumers for incoming messages and receipts activated");
		// Wait a bit to be sure the consumer threads started
		try {Thread.sleep(1000);} catch (InterruptedException ie) {};
		initTopics();
		myLogger.log(Logger.INFO, "KafkaMessagingService at "+myLocation+" - TOPIC initialization records sent");
	}
	
	@Override
	protected void shutdownMom() {
		receiptConsumer.wakeup();
		incomingMessageConsumer.wakeup();
	}
	
	private void createProducers() {
		// Create the producer to send messages
		Properties props = new Properties();
		props.put("bootstrap.servers", myProfile.getParameter("bootstrap.servers", "localhost:9092")); // Comma-separated list of servers in case kafka runs on a cluster
		props.put("acks", "0");
		props.put("retries", 0);
		// If records are produced faster than they can be transmitted to the Kafka
		// cluster this buffer gets exceeded. When this occurs successive call to send() 
		// will block for at most "max.block.ms" and then throws a TimeoutException
		props.put("buffer.memory", 33554432);
		props.put("max.block.ms", 10000); // Max 10 sec
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "jade.core.messaging.KafkaMessageSerializationManager");
		messageProducer = new KafkaProducer<String, KafkaMessageWrapper>(props);
		
		// Create the producer to send back receipts
		props = new Properties();
		props.put("bootstrap.servers", myProfile.getParameter("bootstrap.servers", "localhost:9092")); // Comma-separated list of servers in case kafka runs on a cluster
		props.put("acks", "0");
		props.put("retries", 0);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "jade.core.messaging.KafkaReceiptSerializationManager");
		receiptProducer = new KafkaProducer<String, KafkaReceiptWrapper>(props);
	}

	private void initTopics() {
		createId = CREATE_ID_PREFIX+String.valueOf(System.currentTimeMillis());
		// Send dummy messages to create the INCOMING-MESSAGE and RECEIPTS topics
		messageProducer.send(new ProducerRecord<String, KafkaMessageWrapper>(
				myLocation, 
				createId, 
				new KafkaMessageWrapper()));
		receiptProducer.send(new ProducerRecord<String, KafkaReceiptWrapper>(
				myLocation+RECEIPT_TOPIC_SUFFIX, 
				createId, 
				new KafkaReceiptWrapper()));
	}

	private void activateReceiptConsumer() {
		Properties props = new Properties();
		props.put("bootstrap.servers", myProfile.getParameter("bootstrap.servers", "localhost:9092")); // Comma-separated list of servers in case kafka runs on a cluster
		props.put("group.id", myLocation+RECEIPT_TOPIC_SUFFIX+"-GROUP");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "jade.core.messaging.KafkaReceiptSerializationManager");
		// Create the consumers to receive receipts
		receiptConsumer = new KafkaConsumer<String, KafkaReceiptWrapper>(props);
		receiptConsumer.subscribe(Collections.singletonList(myLocation+RECEIPT_TOPIC_SUFFIX)); 
		Thread receiptReader = new Thread() {
			public void run() {
				myLogger.log(Logger.INFO, "Thread "+getName()+" Started");
				try {
					boolean flushing = true;
					while (active) {
						ConsumerRecords<String, KafkaReceiptWrapper> records = receiptConsumer.poll(Duration.ofMillis(100));
						for (ConsumerRecord<String, KafkaReceiptWrapper> record : records) {
							String id = record.key();
							if (id.equals(createId)) {
								// This is the record we produced at initialization time
								myLogger.log(Logger.INFO, "KafkaMessagingService at "+myLocation+" - Receipt-TOPIC creation record correctly read from Kafka cluster");
								flushing = false;
							}
							else {
								if (flushing) {
									// Old receipt. Ignore 
									if (myLogger.isLoggable(Logger.FINER))
										myLogger.log(Logger.FINER, "KafkaMessagingService at "+myLocation+" - OLD response flushed from Kafka cluster. ID="+id);
								}
								else {
									// Normal receipt processing
									if (myLogger.isLoggable(Logger.FINE))
										myLogger.log(Logger.FINE, "KafkaMessagingService at "+myLocation+" - Receipt read from Kafka cluster. ID="+id);
									handleReceiptFromMom(id, record.value().getException());
								}
							}
						}
					}
				} 
				catch (WakeupException we) {
					// We are terminating. Just do nothing 
				}
				finally {
					receiptConsumer.unsubscribe();
					receiptConsumer.close();
					if (!active) {
						myLogger.log(Logger.INFO, "Thread "+getName()+" Terminated");
					}
					else {
						myLogger.log(Logger.SEVERE, "Thread "+getName()+" Terminated while KafkaMessagingService is still active!!!");
					}
				}			
			}
		};
		receiptReader.setName("KAFKA-"+myLocation+"-Receipt-Reader");
		receiptReader.start();
	}


	private void activateIncomingCommandConsumer() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092"); // Comma-separated list of servers in case kafka runs on a cluster
		props.put("group.id", myLocation+"-GROUP");
		// If the broker does not receive any poll in this timeout it will consider this 
		// consumer dead and will re-balance (default is 3 sec). Increase up to 10 sec  
		props.put("session.timeout.ms", 10000);
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "jade.core.messaging.KafkaMessageSerializationManager");
		// Create the consumers to receive RESPONSES
		incomingMessageConsumer = new KafkaConsumer<String, KafkaMessageWrapper>(props);
		incomingMessageConsumer.subscribe(Collections.singletonList(myLocation));
		Thread incomingMessageReader = new Thread() {
			public void run() {
				myLogger.log(Logger.INFO, "Thread "+getName()+" Started");
				try {
					boolean flushing = true;
					while (active) {
						ConsumerRecords<String, KafkaMessageWrapper> records = incomingMessageConsumer.poll(Duration.ofMillis(100));
						for (ConsumerRecord<String, KafkaMessageWrapper> record : records) {
							String id = record.key();
							if (id.equals(createId)) {
								// This is the record we produced at initialization time
								myLogger.log(Logger.INFO, "KafkaMessagingService at "+myLocation+" - Message-TOPIC creation record correctly read from Kafka cluster");
								flushing = false;
							}
							else {
								if (flushing) {
									// Old Message. Ignore
									if (myLogger.isLoggable(Logger.FINER))
										myLogger.log(Logger.FINER, "KafkaMessagingService at "+myLocation+" - OLD message flushed from Kafka cluster. ID="+id);
								}
								else  {
									// Normal incoming message processing
									String senderLocation = extractSource(id);
									if (myLogger.isLoggable(Logger.FINE))
										myLogger.log(Logger.FINE, "KafkaMessagingService at "+myLocation+" - Incoming message read from Kafka cluster. ID="+id+", senderLocation="+senderLocation);
									KafkaMessageWrapper w = record.value();
									handleMessageFromMom(id, w.getSenderID(), w.getMsg(), w.getReceiverID(), senderLocation);
								}
							}
						}
					}
				}
				catch (WakeupException we) {
					// We are terminating. Just do nothing 
				}
				finally {
					incomingMessageConsumer.unsubscribe();
					incomingMessageConsumer.close();
					if (!active) {
						myLogger.log(Logger.INFO, "Thread "+getName()+" Terminated");
					}
					else {
						myLogger.log(Logger.SEVERE, "Thread "+getName()+" Terminated while KafkaMessagingService is still active!!!");
					}
				}			
			}
		};
		incomingMessageReader.setName("KAFKA-"+myLocation+"-Incoming-Message-Reader");
		incomingMessageReader.start();
	}
	
		
	@Override
	protected void sendMessageViaMom(String deliveryID, AID senderID, GenericMessage msg, AID receiverID, String receiverLocation) throws IMTPException {
//		if (senderID == null) {
//			myLogger.log(Logger.WARNING, "KafkaMessagingService at "+myLocation+" - Deliverying message with null SENDER");
//		}
		messageProducer.send(new ProducerRecord<String, KafkaMessageWrapper>(
				receiverLocation, 
				deliveryID, 
				new KafkaMessageWrapper(senderID, msg, receiverID)));
		if (myLogger.isLoggable(Logger.FINE))
			myLogger.log(Logger.FINE, "KafkaMessagingService at "+myLocation+" - Message "+deliveryID+" for remote-location "+receiverLocation+" written to kafka cluster");
	}

	@Override
	protected void sendReceiptViaMom(String deliveryID, String messageSenderLocation, Throwable failure) {
		receiptProducer.send(new ProducerRecord<String, KafkaReceiptWrapper>(
				messageSenderLocation+RECEIPT_TOPIC_SUFFIX, 
				deliveryID, 
				new KafkaReceiptWrapper(failure)));
		if (myLogger.isLoggable(Logger.FINE))
			myLogger.log(Logger.FINE, "KafkaMessagingService at "+myLocation+" - Receipt to message "+deliveryID+" for remote-location "+messageSenderLocation+" written to kafka cluster");
	}
}
