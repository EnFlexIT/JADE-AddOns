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
 * @author Fabrizio Marozzo
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
