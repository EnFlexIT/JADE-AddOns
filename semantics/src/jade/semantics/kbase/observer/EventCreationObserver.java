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
 * EventCreationObserver.java
 * Created on 9 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.observer;

import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticInterpreterBehaviour;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.kbase.KBase;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;

/**
 * Observer that triggers a subscribe event. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/09 Revision: 1.0
 * @version Date: 2006/12/21 Revision: 1.4 (Thierry Martinez)
 */
public class EventCreationObserver extends ObserverAdapter {
    
    /**
     * The formula that represents the subscribed internal event
     */
    private SemanticRepresentation subscribedEvent;
    
    /**
     * The interpreter to submit the subsribed event when triggered.
     */
    private SemanticInterpreterBehaviour interpreter;
    
    /**
     * Indicates if the observer should be done only one time or not
     */
    private boolean isOneShot;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    /**
     * Creates a new Observer
     * @param agent The agent that has this observer on its belief base 
     * @param observedFormula the formula to observe
     * @param subscribedEvent the event to trigger
     * @param interpreter the interpreter to submit the subscribed event
     * @param isOneShot should the observer be done only one time or not
     */
    public EventCreationObserver(KBase kbase, 
    			                 Formula observedFormula, 
    			                 SemanticRepresentation subscribedEvent,
    			                 SemanticInterpreterBehaviour interpreter,
    			                 boolean isOneShot) {
        super(kbase, observedFormula);
        this.interpreter = interpreter;
        this.subscribedEvent = subscribedEvent;
        this.isOneShot = isOneShot;
    } // End of EventCreationObserver
    
   /**
     * Creates a new Observer
     * @param agent The agent that has this observer on its belief base 
     * @param observedFormula the formula to observe
     * @param subscribedEvent the event to trigger
     * @param interpreter the interpreter to submit the subscribed event
     */
    public EventCreationObserver(KBase kbase, 
    						     Formula observedFormula, 
    						     Formula subscribedEvent, 
       			                 SemanticInterpreterBehaviour interpreter,
       			                 boolean isOneShot) {
        this(kbase, 
        	 observedFormula, 
        	 new SemanticRepresentation(subscribedEvent),
        	 interpreter, 
        	 isOneShot);
    }

	/**
     * Creates a new Observer
     * @param agent The agent that has this observer on its belief base 
     * @param observedFormula the formula to observe
     * @param subscribedEvent the event to trigger
     * @param interpreter the interpreter to submit the subscribed event
     */
    public EventCreationObserver(KBase kbase, 
    		                     Formula observedFormula, 
    		                     Formula subscribedEvent,
    		                     SemanticInterpreterBehaviour interpreter) {
        this(kbase, observedFormula, subscribedEvent, interpreter, false);
    }
    
    /**
     * Returns the subscribedEvent.
     * @return Returns the subscribedEvent.
     */
    public Formula getSubscribedEvent() {
        return subscribedEvent.getSLRepresentation();
    } 
    
    /**
     * Interprets the subcribe event and removes the suitable observer if the 
     * observer should be done only one time.
     * @param value list of MatchResults which represents the last queried value of the observed formula.
     */
    public void action(ListOfMatchResults value) {
        try {
            if (value != null) {
                if (value.size() >= 1) {
                    for (int i =0; i < ((MatchResult)value.get(0)).size(); i++) {
						subscribedEvent.setSLRepresentation(
								subscribedEvent.getSLRepresentation().instantiate(
                                ((MetaTermReferenceNode)((MatchResult)value.get(0)).get(i)).lx_name(),
                                ((MetaTermReferenceNode)((MatchResult)value.get(0)).get(i)).sm_value()));
                    }
                } 
                interpreter.interpret(new SemanticRepresentation(subscribedEvent), false);
                if (isOneShot) myKBase.removeObserver(new Finder() {
                    public boolean identify(Object object) {
                        return EventCreationObserver.this == object;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

} 
