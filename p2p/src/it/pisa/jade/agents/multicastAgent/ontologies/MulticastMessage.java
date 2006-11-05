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

import java.io.Serializable;

/**
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
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
