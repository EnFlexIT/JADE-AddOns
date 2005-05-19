/*
 * Observer.java
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
public interface Observer {

    public void notify(Bindings bindings);
    
    public Formula getObservedFormula();
    
} // End of interface Observer
