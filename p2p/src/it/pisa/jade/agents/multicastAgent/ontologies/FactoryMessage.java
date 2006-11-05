/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
package it.pisa.jade.agents.multicastAgent.ontologies;

import it.pisa.jade.data.activePlatform.RecordPlatform;



/**
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
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
