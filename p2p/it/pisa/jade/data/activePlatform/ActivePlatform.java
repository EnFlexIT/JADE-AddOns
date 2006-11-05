/**
 * 
 */
package it.pisa.jade.data.activePlatform;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Questa interfaccia si occupa di definire i metodi che devono possedere le
 * classi dedicate a mantenere gli indirizzi delle piattaforme disponibili in
 * remoto. notare che l'iteretor non supporta i metodi di modifica, quindi
 * attenzione sollevano un eccezione <b>UnsupportedOperationException</b>
 * Inoltre definisce il nome dell'agente che si occupa dell'ereditare i servizi.
 * 
 * @author Domenico Trimboli
 * 
 */
public interface ActivePlatform extends Serializable {
	/**
	 * questo iterator non permette di rimuovere i record contenuti nella lista
	 * e dato che i record a loro volta non posseggono metodi di set non possono
	 * essere modificati
	 * 
	 * @return
	 */
	public Iterator<RecordPlatform> iterator();

	/**
	 * restituisce il record completo della piattaforma a partire dal nome,
	 * metodo utile se si vuole inviare un messaggio spacifico ad una
	 * piattaforma.
	 * 
	 * @param nomePiattaforma
	 *            nome della piattaforma
	 * @return record relativo al nome della piattaforma, se non presente
	 *         restituisce null.
	 */
	public RecordPlatform trova(String nomePiattaforma);

	/**
	 * verifica che un record sia o meno presente
	 * 
	 * @param r
	 * @return
	 */
	public boolean contains(RecordPlatform r);

}
