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
 * EventCreationObserver.java
 * Created on 9 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.observer;

import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Observer that triggers a subscribe event. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/09 Revision: 1.0
 */
public class EventCreationObserver extends ObserverAdapter {
    
    /**
     * The formula that represents the subscribed internal event
     */
    private Formula subscribedEvent;
    
    /**
     * The agent that has this observer on its belief base
     */
    private SemanticAgent myAgent;
    
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
     * @param isOneShot should the observer be done only one time or not
     */
    public EventCreationObserver(SemanticAgent agent, Formula observedFormula, Formula subscribedEvent, boolean isOneShot) {
        super(observedFormula);
        myAgent = agent;
        this.subscribedEvent = subscribedEvent;
        this.isOneShot = isOneShot;
    } // End of EventCreationObserver
    
    /**
     * Creates a new Observer
     * @param agent The agent that has this observer on its belief base 
     * @param observedFormula the formula to observe
     * @param subscribedEvent the event to trigger
     */
    public EventCreationObserver(SemanticAgent agent, Formula observedFormula, Formula subscribedEvent) {
        this(agent, observedFormula, subscribedEvent, false);
    }
    
    /**
     * Returns the subscribedEvent.
     * @return Returns the subscribedEvent.
     */
    public Formula getSubscribedEvent() {
        return subscribedEvent;
    } // End of getSubscribedEvent/0
    
    /**
     * Sets the subscribe event.
     * @param subscribedEvent The subscribedEvent to set.
     */
    public void setSubscribedEvent(Formula subscribedEvent) {
        this.subscribedEvent = subscribedEvent;
    } // End of setSubscribedEvent/1
    
    
    /**
     * Interprets the subcribe event and removes the suitable observer if the 
     * observer should be done only one time.
     * @param list list of MatchResults which made possible the notification
     */
    public void notify(ListOfMatchResults list) {
        try {
            if (list != null) {
                if (list.size() >= 1) {
                    for (int i =0; i < ((MatchResult)list.get(0)).size(); i++) {
                        SLPatternManip.instantiate(subscribedEvent,
                                ((MetaTermReferenceNode)((MatchResult)list.get(0)).get(i)).lx_name(),
                                ((MetaTermReferenceNode)((MatchResult)list.get(0)).get(i)).sm_value());
                    }
                } 
                myAgent.getSemanticCapabilities().getSemanticInterpreterBehaviour().interpret(new SemanticRepresentation(subscribedEvent));
                if (isOneShot) myAgent.getSemanticCapabilities().getMyKBase().removeObserver(new Finder() {
                    public boolean identify(Object object) {
                        return EventCreationObserver.this == object;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // End of notify/1

} // End of class EventCreationObserver
