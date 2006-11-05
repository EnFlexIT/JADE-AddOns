/**
 * 
 */
package it.pisa.jade.data.activePlatform;

import java.util.Iterator;

/**
 * Questa interfaccia rende attivi i metodi che permettono di modificare la
 * struttura dati che contiene, i record relativi alle piattaforme inoltre mette
 * adisposizione il metodo getIterator che abilita il remove
 * 
 * 
 * @author Domenico Trimboli
 * 
 */
public interface ActivePlatformStub extends ActivePlatform {
	/**
	 * Trova il record relativo alla piattaforma e setta il ttl passato come
	 * parametro. Se ttl è uguale a -1 incrementa il ttl corrente del record di
	 * 1 unità. se il record non è presente viene restituito false
	 * 
	 * @param record
	 *            elemento di cui effettuare il refresh
	 * @param ttl
	 *            time to live del record
	 * @return true se l'operazione è andata a uon fine false altrimenti
	 */
	public boolean refreshRecord(RecordPlatform record, int ttl);

	/**
	 * Rimuove dalla lista delle piattaforme attive la piattaforma passata come
	 * parametro.
	 * 
	 * @param record
	 *            piattaforma da eliminare
	 * @return risultato dell'operazione true a buon fine false altrimenti
	 */
	public boolean removePlatform(RecordPlatform record);

	/**
	 * Permette di aggiungere un record relativo ad una piattaforma appena
	 * rilevata.
	 * 
	 * @param record
	 *            piattaforma rilevata
	 * @return risultato dell'operazione true a buon fine false altrimenti
	 */
	public boolean addPlatform(RecordPlatform record);

	public int size();

	/**
	 * Questo metodo deve restituire la lista dei record relativi alle
	 * piattaforme non presenti nella lista di piattaforme passate come
	 * parametro. Inoltre la lista su cui è invocato il metodo deve contenere
	 * solo i record presenti nella lista passata come parametro.
	 * 
	 * @param l
	 *            lista di record consistenti
	 * @return record eliminati dall'oggetto this
	 */
	public ActivePlatformStub removeAll(ActivePlatform l);

	/**
	 * decrementa i ttl di tutti i record nella lista
	 * 
	 */
	public void tick();

	/**
	 * questo iteratore supporta il metodo remove
	 * 
	 * @return
	 */
	public Iterator<RecordPlatform> getIterator();
}
