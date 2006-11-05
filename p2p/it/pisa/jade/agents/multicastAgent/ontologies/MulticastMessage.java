/**
 * 
 */
package it.pisa.jade.agents.multicastAgent.ontologies;

import it.pisa.jade.data.activePlatform.RecordPlatform;

import java.io.Serializable;

/**
 * @author domenico
 * 
 */
public interface MulticastMessage extends Serializable {

	/**
	 * Restituisce l'indirizzo ip del sender inizializzato al momento dell'invio
	 * 
	 * @return stringa relativa al sender
	 */
	public String getSender();

	/**
	 * Restituisce i dettagli necessari a collegare le piattaforme remote.
	 * 
	 * @return oggetto contenente i dettagli
	 */
	public RecordPlatform getPlatform();

	public String toString();

	/**
	 * restituisce il tipo di messaggio che si è ricevuto
	 * 
	 * @return
	 */
	public ConstantTypeMsg getType();

}
