/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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

/*
 * HashMapForCollections.java
 * Version 0.1 
 * Created on 19 april 2004
 * Author : Vincent Pautret
 */

package jade.semantics.kbase;


import jade.semantics.kbase.FilterKBaseImpl.Observation;
import jade.semantics.kbase.observer.Observer;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;
import jade.util.leap.HashMap;
import jade.util.leap.Iterator;

import java.util.HashSet;

import sun.security.tools.KeyTool;

/**
 * Subclass of <code>HashMap</code> used when the values of the HashMap are 
 * collections. This class overloads the method {@link #put put(clé, valeur)} so
 * that this one makes it possible to create a couple (access key - list) if the
 * key does not exist in the HashMap, or to add the element in an existing 
 * {@link HashSet HashSet} if the key exists.<br>
 * In this subClass the keys are Formulae (patterns) and the values are HashSet
 * of FilterKbaseImpl.Observation.
 * @author Vincent Pautret
 * @version 0.1
 */
public class HashMapForCollections extends HashMap {


/******************************************************************************/
/** 							CONSTRUCTEUR								***/
/******************************************************************************/
	

	/**
	 * Constructor of a collection map. 
	 */
	public HashMapForCollections() {
		super();
	} 

/******************************************************************************/
/** 							METHODES PUBLIQUES							***/
/******************************************************************************/
	
	/**
     * This method overrides the one defined in {@link HashMap HashMap}.<br>
     * If the key does not already exist, it adds it while putting the <code>value</code>
     * object in a {@link HashSet HashSet} which is created at the same time.<br> 
     * If the key already exists, the <code>value</code> Object is added in a 
     * {@link HashSet HashSet} corresponding to the key.
	 * @param key key of the HashMap
	 * @param value value to add
	 * @return the added HashSet object if the key already existed, <code>null</code>
     * if the key did not exist.
	 */
	public Object put(Object key, Object value) {
		Formula f = this.containsKeyFormula(key);
		if (f != null && value != null) {
			((HashSet)this.get(f)).add(value);
			return this.get(f);
		} else {
			HashSet setValue = new HashSet(5,1);
			if (value != null) setValue.add(value);
			// Attention de bien rappeler le put/2 de la classe HashMap, sinon
			// ça boucle sur cette méthode put
			super.put(key, setValue);
			return null;
		}
		
	} 
	
    /**
     * Uses the matching to determinates if a key is already or not in the map.
     * @param formula the new key
     * @return the formula of the map which matches the formula given in 
     * parameter. If the matching does not succeed, returns null. 
     *  
     */
    private Formula containsKeyFormula(Object formula) {
        for (Iterator iter = keySet().iterator(); iter.hasNext();) {
            Formula f = (Formula)iter.next();
            if (SLPatternManip.match((Formula)formula, f) != null) {
                return  f;
            }
        }
        return null;
    }
    
	/**
     * Calculates the cumulated size of the whole of the elements which are 
     * contained in the collections values of HashMap. 
     * @return the cumulated size of the whole of the elements which are 
     * contained in the collections values of HashMap.
	 */
	public int spreadedSize(){
		int spreadedSize = 0;
		for (Iterator it = values().iterator() ;it.hasNext() ;) {
			HashSet currentValue = (HashSet)it.next();
			spreadedSize += currentValue.size();
		}
		return spreadedSize;
	} 
    
    /**
     * Removes the keys whose value is an empty HashSet.
     */
    public void removeUselessKeys() {
        ArrayList keysToBeDeleted = new ArrayList();
        for (Iterator iter = keySet().iterator(); iter.hasNext();) {
            Object elem = iter.next();
            if (((HashSet)get(elem)).size() == 0) {
                keysToBeDeleted.add(elem);
            }
        }
        for (int i = 0; i < keysToBeDeleted.size(); i++) {
            this.remove(keysToBeDeleted.get(i));
        }
    }
    
    /**
     * Returns the String corresponding to the String representation of the 
     * HashMap.
     * @return the String corresponding to the String representation of the 
     * HashMap.
     */
    public String toString() {
        String result = "";
        for (Iterator iter = keySet().iterator(); iter.hasNext();) {
            Object elem = iter.next();
            result = result + "--- key : " + elem +"\n";
            result = result + "+++ values : \n";
            for (java.util.Iterator iter2 = ((HashSet)get(elem)).iterator(); iter2.hasNext();) {
                result = result + iter2.next() + " \n";
            }
        }
        return result;
    }
} // Fin de la classe HashMapForCollections
