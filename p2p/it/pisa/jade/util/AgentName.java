/**
 * 
 */
package it.pisa.jade.util;

/**
 * @author Domenico Trimboli
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
