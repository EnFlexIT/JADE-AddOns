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
 * ObserverAdapter.java
 * Created on 11 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.observer;



import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.ListOfMatchResults;


/**
 * Base observer that implements the <code>Observer</code> interface. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/11 Revision: 1.0
 */
public class ObserverAdapter implements Observer {
    
    /**
     * The observed pattern
     */
    private Formula observedFormula;
    
    /**
     * Creates a new Observer
     * @param formula the formula to observe
     */
    public ObserverAdapter(Formula formula) {
        observedFormula = formula;
    }
    
    /**
     * Should be overridden
     * @see Observer#notify(ListOfMatchResults)
     * @inheritDoc
     */
    public void notify(ListOfMatchResults listOfMatchResults) {
        System.out.println("OBSERVER ADAPTER NOTIFY !!! : " + this.getObservedFormula());
    }
    
    
    /**
     * Returns the observed formula.
     * @return the observed formula.
     */
    public Formula getObservedFormula() {
        return observedFormula;
    }
    
    /**
     * Sets the formula to observe
     * @param formula The observedFormula to set.
     */
    public void setObservedFormula(Formula formula) {
        this.observedFormula = formula;
    }
    
    /**
     * Returns the observed formula string representation
     * @return the observed formula string representation
     */
    public String toString() {
        return observedFormula.toString();
    }
    
} // End of class ObserverAdapter
