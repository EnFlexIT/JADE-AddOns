package it.pisa.jade.agents.peerAgent;

/*
 * Created on 23-set-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */



import it.pisa.jade.agents.peerAgent.ontologies.LookFor;

/**
 * 
 * @author Fabrizio Marozzo
 *
 */
public interface Command {
	public void startNewSearch(LookFor lookfor);
	public void removeSearch(String keyString);
	public void chooseFile(String searchKey, int pos);
	public void exitFromGroup();
	//public void addNewSharedDirectory(String s);
    //public void removeSharedDirectory(String string);
    //public LinkedList getSharedDirectories();
    

}
