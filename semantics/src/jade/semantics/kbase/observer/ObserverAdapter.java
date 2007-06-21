/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

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



import jade.semantics.kbase.KBase;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.ListOfMatchResults;


/**
 * Base observer that implements the <code>Observer</code> interface. 
 * @author Thierry Martinez - France Telecom
 * @version Date: 2006/12/22 Revision: 1.0
 */
public abstract class ObserverAdapter extends Observer {
    
    /**
     * Creates a new Observer
     * @param formula the formula to observe
     */
    public ObserverAdapter(KBase kbase, Formula formula) {
		super(kbase, formula);
    }
	
	/**
	 * @return the formula passed to the constructor.
	 */
	public Formula getObservedFormula() {
		return (Formula)observedFormulas.get(DIRECTLY_OBSERVED);
	}
    
    /**
     * Should be overridden. It is triggered when
     * the observer state changes. 
     * @param value is the last value queried of the observed formula. 
     */
    public abstract void action(ListOfMatchResults value);
	
	@Override
	public boolean update(Formula formula) {
		boolean change = super.update(formula);
		if ( change ) {
			action(lastValueOfDirectlyObserved);
		}
		return change;
	}
} 
