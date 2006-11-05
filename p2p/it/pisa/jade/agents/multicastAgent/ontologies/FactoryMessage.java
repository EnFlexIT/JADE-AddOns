/**
 * 
 */
package it.pisa.jade.agents.multicastAgent.ontologies;

import it.pisa.jade.data.activePlatform.RecordPlatform;



/**
 * @author domenico
 * 
 */
public class FactoryMessage {

	/**
	 * Crea il messaggio in base alla stringa passata come parametro, se non
	 * corrisponde a nessun tipo di messaggio restituisce null.
	 * 
	 * @param message
	 *            messaggio da parserizzare
	 * @return un istanza di tipo ping o pong message, null se non corrisponde a
	 *         nessuna dei due
	 */
	public static MulticastMessage parseMessage(String message) {
		if (PingMessage.isPingMessage(message))
			return new PingMessage(message);
		if (PongMessage.isPongMessage(message))
			return new PongMessage(message);
		if (ExitMessage.isExitMessage(message))
			return new ExitMessage(message);
		return null;
	}

	/**
	 * Crea il messaggio in base ai byte passatati come parametri, se non
	 * corrisponde a nessun tipo di messaggio restituisce null.
	 * 
	 * @param data
	 * @return
	 */
	public static MulticastMessage parseMessage(byte[] data) {
		return parseMessage(new String(data));
	}

	/**
	 * crea un messaggio del tipo passato come parametro, e la descrizione della
	 * piattaforma di appartenenza
	 * 
	 * @param type
	 *            tipo di messaggio da creare
	 * @param r
	 *            dettaggli relativi alla piattaforma
	 * @return
	 */
	public static MulticastMessage createMessage(ConstantTypeMsg type, RecordPlatform r) {
		MulticastMessage msg = null;
		if (type.equals(ConstantTypeMsg.PING)) {
			msg = new PingMessage(r);
		} else if (type.equals(ConstantTypeMsg.PONG)) {
			msg = new PongMessage(r);
		} else if (type.equals(ConstantTypeMsg.EXIT)) {
			msg = new ExitMessage(r);
		}
		return msg;
	}

}
