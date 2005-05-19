/*
 * ObserverAdapter.java
 * Created on 11 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.observer;



import jade.semantics.kbase.Bindings;
import jade.semantics.lang.sl.grammar.Formula;


/**
 * @author Vincent Pautret
 * @version 
 */
public class ObserverAdapter implements Observer {

    /**
     * The observed pattern
     */
    Formula observedFormula;
    
    

    public ObserverAdapter(Formula formula) {
        observedFormula = formula;
    }
    
   /**
     * @see jade.semantics.kbase.Observer#notify(jade.lang.sl.grammar.Formula)
     */
    public void notify(Bindings bindings) {
        System.out.println("OBSERVER ADAPTER NOTIFY !!! : " + this.getObservedFormula());
    }

    
    
    /**
     * @return Returns the observedFormula.
     */
    public Formula getObservedFormula() {
        return observedFormula;
    }
    
    /**
     * @param observedFormula The observedFormula to set.
     */
    public void setObservedFormula(Formula formula) {
        this.observedFormula = formula;
    }

    
    public String toString() {
        return observedFormula.toString();
    }
    
} // End of class ObserverAdapter
