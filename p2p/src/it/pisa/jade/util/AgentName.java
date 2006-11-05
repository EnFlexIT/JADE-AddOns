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
package it.pisa.jade.util;

/**
 * @author Domenico Trimboli
 * 
 * 
 */
public enum AgentName {
	subDf("agente estenzione di un DF"), 
	multicastManagerAgent("Mantiene e Aggiorna la lista delle piattaforme"), 
	ping("risponde alle rischieste delle altri piattaforme"), 
	federatorAgent("Agente che si occupa di effettuare la federazione degli agenti"),
	chatAgent("Agente che implementa una semplice chat"),
	searchServiceAgent("agente che effettua ricerche temporizzate sul df"),
	guiAgent("Agente di supporto per la gui esegue tutte le operazioni di competenza degli agenti"),
	rma("Interfaccia grafica standard di JADE"),
	peerAgent("Agente per la condivisione delle informazioni");

	AgentName(String s) {
		description = s;
	}

	private String description;

	public String getDescription() {
		return description;
	}

}
