package it.pisa.jade.agents.peerAgent.data;



import it.pisa.jade.agents.peerAgent.ontologies.Found;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;

/*
 * Created on 22-set-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * 
 * @author Fabrizio Marozzo
 *
 */
public class SearchElements extends Observable {
    Hashtable table = new Hashtable();

    public SearchElements() {
        this.table = new Hashtable();
    }

    @SuppressWarnings("unchecked")
	public void newSearch(String searchKey) {
        LinkedList l = new LinkedList<Found>();
        table.put(searchKey, l);
        notifyObservers(searchKey);
    }

    public void removeSearch(String searchKey) {
        table.remove(searchKey);
        //notifyObservers();
    }

    @SuppressWarnings("unchecked")
	public void addNewSearchElement(String searchKey, Found found) {
        Object ret = table.get(searchKey);
        if (ret == null)
            return;
        LinkedList <Found> l = (LinkedList <Found>) ret;
        l.add(found);
        System.out.println("New Element"+searchKey);
        notifyObservers(searchKey);
        
    }
    @SuppressWarnings("unchecked")
	public void addNewListElements(String searchKey, LinkedList <Found> list) {
        Object ret = table.get(searchKey);
        if (ret == null)
            return;
        LinkedList <Found> l = (LinkedList <Found>) ret;
        l.addAll(list);
        System.out.println("New ListElemets"+searchKey);
        notifyObservers(searchKey);
        
    }

    public LinkedList getSearchElements(String searchKey) {
        return (LinkedList) ((LinkedList) table.get(searchKey));
    }

    public void notifyObservers(Object id) {
        super.setChanged();
        super.notifyObservers(id);

    }

}