/*
 * BlindObserverAdapter.java
 * Created on 24 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.observer;


import jade.semantics.kbase.Bindings;
import jade.semantics.lang.sl.grammar.Formula;


/**
 * @author Vincent Pautret
 * @version 
 */
public class BlindObserverAdapter implements Observer {

    Formula observedFormula;
    
    public BlindObserverAdapter(Formula formula) {
        observedFormula = formula;
    }
    
    /* (non-Javadoc)
     * @see jade.core.semantics.kbase.observer.Observer#notify(jade.core.semantics.prolog3.Bindings)
     */
    public void notify(Bindings bindings) {
    }

    /* (non-Javadoc)
     * @see jade.core.semantics.kbase.observer.Observer#getObservedFormula()
     */
    public Formula getObservedFormula() {
        return observedFormula;
    }

}
