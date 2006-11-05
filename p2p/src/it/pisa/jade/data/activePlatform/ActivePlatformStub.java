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
package it.pisa.jade.data.activePlatform;

import java.util.Iterator;

/**
 * Questa interfaccia rende attivi i metodi che permettono di modificare la
 * struttura dati che contiene, i record relativi alle piattaforme inoltre mette
 * adisposizione il metodo getIterator che abilita il remove
 * 
 * 
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
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
