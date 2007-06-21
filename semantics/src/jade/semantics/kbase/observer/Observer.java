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
 * Observer.java
 * Created on 21 décembre 2006
 * Author : Thierry Martinez
 */
package jade.semantics.kbase.observer;


import jade.semantics.kbase.KBase;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.util.leap.ArrayList;

/**
 * Defines a KBase observer. 
 * Such an observer provides a means to observe several
 * formulas, possibly asserted in the KBase, and to
 * react if their values change.
 * @author Thierry Martinez - France Telecom
 * @version Date: 2006/12/22 Revision: 1.0 
 */
public abstract class Observer {
	
	static final int DIRECTLY_OBSERVED = 0;
	
	/**
	 * Stores the formulas to be observed. 
	 * Among these formulas, the first one is the one directly 
	 * observed, while the other ones are the associated pattern 
	 * that could also trigger this observer. 
	 */
	ArrayList observedFormulas = new ArrayList();
	
	/**
	 * Store the last queried value of the DIRECTLY_OBSERVED formula.
	 */
	ListOfMatchResults lastValueOfDirectlyObserved = null;
	
	/**
	 * The KBase the observer belongs to.
	 */
	KBase myKBase = null;
	
	/**
	 * true if the observer value has been queried at least one time.
	 */
	boolean updatedOnce = false;
	
	/**
	 * Build an Observer for the kbase given as an argument. 
	 * @param kbase the kbase the observer belongs to. It should not be null.
	 */
	public Observer(KBase kbase, Formula formula) {
		myKBase = kbase;
		addFormula(formula);
	}
    
	/**
	 * This method returns all observed formulas
	 * @return an array of all observed formulas
	 */
	public Formula[] getObservedFormulas()
	{
		Formula[] result = new Formula[observedFormulas.size()];
		for (int i=0; i<result.length; i++) {
			result[i] = (Formula)observedFormulas.get(i);
		}
		return result;
	}
	
	/**
     * Add a formula to observe. Notice that observe f means observe f and (not f).
     * @param the formula to observe.
     */
    public void addFormula(Formula formula) {
    	Term myself = myKBase.getAgentName();
    	formula = formula.instantiate("myself", myself);
    	Formula f = new BelieveNode(myself, formula).getSimplifiedFormula();
		if ( ! observedFormulas.contains(f) ) {
			observedFormulas.add(f);
		}
		Formula notF = new BelieveNode(myself, new NotNode(formula)).getSimplifiedFormula();
		if ( ! observedFormulas.contains(notF) ) {
			observedFormulas.add(notF);
		}
    }
	
	/**
	 * This method is called to update the observer according to
	 * the change that has just occurs on the given formula.
	 * @param formula the formula which has just changed (newly asserted or removed) 
	 *        or null to force the update of the observer.
	 * @return true if one of the observed formulas is concerned by this recent change.
	 */
	public boolean update(Formula formula) { 
		boolean hasChanged = false;
		if ( updatedOnce ) {
			boolean isConcerned = (formula == null); // If formula is null, the observer is necessarily concerned by the update operation
			if (!isConcerned) {
		    	formula = new BelieveNode(myKBase.getAgentName(), formula).getSimplifiedFormula();
				for (int i=0; !isConcerned && i<observedFormulas.size(); i++) {
					Formula f = (Formula)observedFormulas.get(i);
					isConcerned = ( f.match(formula) != null );
				}
			}
			if ( isConcerned ) {
				ListOfMatchResults val = myKBase.query((Formula)observedFormulas.get(DIRECTLY_OBSERVED));
				hasChanged = lastValueOfDirectlyObserved==null?(val!=null):!lastValueOfDirectlyObserved.equals(val);
				lastValueOfDirectlyObserved = val;
			}		
		}
		else {
			if ( myKBase != null ) {
				lastValueOfDirectlyObserved = myKBase.query((Formula)observedFormulas.get(DIRECTLY_OBSERVED));
				updatedOnce = true;
				return false;
			}
		}
		return hasChanged;
	}
	
	@Override
	public String toString() {
		String result = "Observer("+observedFormulas.get(DIRECTLY_OBSERVED)+") {\n";
		for (int i=0; i<observedFormulas.size(); i++) {
			result += "\t"+observedFormulas.get(i)+",\n";
		}
		result+= "}";
		return result;
	}
}

