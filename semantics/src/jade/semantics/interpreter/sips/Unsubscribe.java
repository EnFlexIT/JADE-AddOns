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
 * Unsubscribe.java
 * Created on 14 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.kbase.observer.EventCreationObserver;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This principle is intented to be applied when an agent receives a UnSubscribe 
 * message.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/14 Revision: 1.0
 */
public class Unsubscribe extends SemanticInterpretationPrinciple {
    
    /**
     * A subscribed internal event that identifies the subscription to drop
     */
    private Formula subscribedEvent;
    
    /**
     * A pattern describing the formula to monitor, that identifies the subscription to drop 
     */
    private Formula observedPattern;
    
    /**
     * Pattern used to build the observed pattern (observedPattern)
     */
    private Formula subscribeObservedPattern;
    
    /**
     * Pattern used to build the subscribed event (subscribedEvent)
     */
    private Formula subscribeEventPattern;
    
    /**
     * Pattern that must match to apply the filter.
     * This pattern identifies a formula to monitor and a subscribed internal event
     */
    private Formula unsubscribePattern1;
    
    /**
     * Pattern that must match to apply the filter.
     * This pattern identifies a subscribed internal event
     */
    private Formula unsubscribePattern2;
    
    /**
     * Generic pattern that matches any formula
     */
    private Formula universalPattern;
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     * @param observedProperty the observed property
     * @param goal the goal to reach
     */
    public Unsubscribe(SemanticCapabilities capabilities, String observedProperty, String goal) {
        super(capabilities);
        String property = "??property";
        if (observedProperty != null) property = observedProperty;
        String g = "??goal";
        if (goal != null) g = goal;
        subscribeObservedPattern = SLPatternManip.fromFormula("(B "+ myCapabilities.getAgentName() + " " + property +")");
        subscribeEventPattern = SLPatternManip.fromFormula("(B "+ myCapabilities.getAgentName() +" (I ??subscriber " + g + "))");
        unsubscribePattern1 = 
            SLPatternManip.fromFormula("(B "+ myCapabilities.getAgentName() +" (or " +
                    "     (not (B " + myCapabilities.getAgentName() + " " + property + " )) " +
                    "     (or (not (I ??subscriber ??goal))" +
                    "(forall ??e (not (done ??e (not (B " + myCapabilities.getAgentName() + " " + property + " ))))))))");
        unsubscribePattern2 = 
            SLPatternManip.fromFormula("(B "+ myCapabilities.getAgentName() +" (not (I ??subscriber " + g + ")))");
        universalPattern = 
            SLPatternManip.fromFormula("??phi");
    } // End of Unsubscribe/3
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public Unsubscribe(SemanticCapabilities capabilities) {
        this(capabilities, null, null);
    } // End of Unsubscribe
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    
    /**
     * Removes observers from the knowledge base if the principle is applicable.
     * @inheritDoc
     */
    public ArrayList apply(SemanticRepresentation sr)
    throws SemanticInterpretationPrincipleException {
        try {
            Term subscriber;
            MatchResult applyResult = SLPatternManip.match(unsubscribePattern1,sr.getSLRepresentation());
            if (applyResult != null 
                    && !(subscriber = applyResult.getTerm("subscriber")).equals(myCapabilities.getAgentName())) { 
                observedPattern = (Formula)SLPatternManip
                .instantiate(subscribeObservedPattern,
                        "property", applyResult.getFormula("property"));
                subscribedEvent = (Formula)SLPatternManip
                .instantiate(subscribeEventPattern,
                        "subscriber", subscriber,
                        "goal", applyResult.getFormula("goal"));
                myCapabilities.getMyKBase().removeObserver(new Finder() {
                    public boolean identify(Object object) {
                        if (object instanceof EventCreationObserver) {
                            return (SLPatternManip.match(observedPattern, ((EventCreationObserver)object).getObservedFormula()) != null
                                    && SLPatternManip.match(subscribedEvent, ((EventCreationObserver)object).getSubscribedEvent()) != null);
                        } else {
                            return false;
                        }
                    }
                });
                myCapabilities.getMyStandardCustomization().notifyUnsubscribe(subscriber, observedPattern, applyResult.getFormula("??goal"));
                return new ArrayList();
            } else if ((applyResult = SLPatternManip.match(unsubscribePattern2, sr.getSLRepresentation())) != null 
                    && !(subscriber = applyResult.getTerm("subscriber")).equals(myCapabilities.getAgentName())) {
                observedPattern = universalPattern;
                subscribedEvent = (Formula)SLPatternManip
                .instantiate(subscribeEventPattern, 
                        "subscriber", subscriber,
                        "goal", applyResult.getFormula("goal"));
                myCapabilities.getMyKBase().removeObserver(new Finder() {
                    public boolean identify(Object object) {
                        if (object instanceof EventCreationObserver) {
                            return (SLPatternManip.match(observedPattern, ((EventCreationObserver)object).getObservedFormula()) != null
                                    && SLPatternManip.match(subscribedEvent, ((EventCreationObserver)object).getSubscribedEvent()) != null);
                        } else {
                            return false;
                        }
                    }
                });
                myCapabilities.getMyStandardCustomization().notifyUnsubscribe(subscriber, observedPattern, applyResult.getFormula("??goal"));
                return new ArrayList();
            }
        }  catch (Exception  e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1
    
} // End of class Unsubscribe
