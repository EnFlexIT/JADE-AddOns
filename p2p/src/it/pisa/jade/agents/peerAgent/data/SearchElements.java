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
package it.pisa.jade.agents.peerAgent.data;



import it.pisa.jade.agents.peerAgent.ontologies.Found;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;



/**
 * 
 * @author Fabrizio Marozzo
 * @author Domenico Trimboli
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