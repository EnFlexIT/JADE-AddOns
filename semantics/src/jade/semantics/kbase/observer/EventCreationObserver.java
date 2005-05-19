/*
 * KBObserver.java
 * Created on 9 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.observer;

import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticRepresentationImpl;
import jade.semantics.kbase.Bindings;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * @author Vincent Pautret
 * @version 
 */
public class EventCreationObserver extends ObserverAdapter {

    /**
     * The formula that represents the subscribed internal event
     */
    Formula subscribedEvent;
    
    /**
     * A flag that memorizes wether the observed formula is already true before a formula is asserted into the KBase
     */
    boolean isObservedFormulaTrue;

    /**
     * 
     */
    SemanticAgent myAgent;
    
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/


    public EventCreationObserver(SemanticAgent agent, Formula observedFormula, Formula subscribedEvent) {
        super(observedFormula);
        myAgent = agent;
        this.subscribedEvent = subscribedEvent;
    } // End of EventCreationObserver

    /**
     * @return Returns the subscribedEvent.
     */
    public Formula getSubscribedEvent() {
        return subscribedEvent;
    } // End of getSubscribedEvent/0

    /**
     * @param subscribedEvent The subscribedEvent to set.
     */
    public void setSubscribedEvent(Formula subscribedEvent) {
        this.subscribedEvent = subscribedEvent;
    } // End of setSubscribedEvent/1
    
    
    public void notify(Bindings bindings) {
        try {
            for (int i =0; i < bindings.size(); i++) {
                SLPatternManip.instantiate(subscribedEvent,
                        bindings.getBind(i).getVarName(), bindings.getBind(i).getVarValue());
            }
            myAgent.interpret(new SemanticRepresentationImpl(subscribedEvent));
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // End of notify/1
} // End of class EventCreationObserver
