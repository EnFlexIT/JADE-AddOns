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
 * UnsubscribeFilter.java
 * Created on 14 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.semanticinterpretationprinciple;

import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrincipleImpl;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.kbase.observer.EventCreationObserver;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * @author Vincent Pautret
 * @version 
 */
public class Unsubscribe extends SemanticInterpretationPrincipleImpl {

    /**
     * A subscribed internal event that identifies the subscription to drop
     */
    Formula subscribedEvent;
    
    /**
     * A pattern describing the formula to monitor, that identifies the subscription to drop 
     */
    Formula observedPattern;
    
    
    Formula subscribeObservedPattern;
    
    Formula subscribeEventPattern;
    
    /**
     * Pattern that must match to apply the filter.
     * This pattern identifies a formula to monitor and a subscribed internal event
     */
    Formula unsubscribePattern1;

    /**
     * Pattern that must match to apply the filter.
     * This pattern identifies a subscribed internal event
     */
    Formula unsubscribePattern2;

    /**
     * Generic pattern that matches any formula
     */
    Formula universalPattern;

    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

   public Unsubscribe(SemanticAgent agt) {
       setAgent(agt);
       subscribeObservedPattern = SLPatternManip.fromFormula("(B "+ agent.getAgentName() +" ??property)");
       subscribeEventPattern = SLPatternManip.fromFormula("(B "+ agent.getAgentName() +" (I ??subscriber ??goal))");
       unsubscribePattern1 = 
           SLPatternManip.fromFormula("(B "+ agent.getAgentName() +" (or (not (I ??subscriber ??goal)) (not (done (action null before) (not (B "+ agent.getAgentName() +" ??property))))))");
       unsubscribePattern2 = 
           SLPatternManip.fromFormula("(B "+ agent.getAgentName() +" (not (I ??subscriber ??goal)))");
       universalPattern = 
           SLPatternManip.fromFormula("??phi");
    } // End of UnsubscribeFilter/1

   
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/


    /**
     * @see jade.semantics.interpreter.DeductiveStepImpl#apply(jade.core.semantics.interpreter.SemanticRepresentation, jade.util.leap.ArrayList)
     */
    public ArrayList apply(SemanticRepresentation sr)
            throws SemanticInterpretationPrincipleException {
        try {
            Term subscriber;
            MatchResult applyResult = SLPatternManip.match(unsubscribePattern1,sr.getSLRepresentation());
            if (applyResult != null 
                    && !(subscriber = applyResult.getTerm("??subscriber")).equals(agent.getAgentName())) { 
                observedPattern = (Formula)SLPatternManip
                    .instantiate(subscribeObservedPattern,
                                 "??property", applyResult.getFormula("??property"));
                subscribedEvent = (Formula)SLPatternManip
                    .instantiate(subscribeEventPattern,
                                 "??subscriber", subscriber,
                                 "??goal", applyResult.getFormula("??goal"));
                agent.getMyKBase().removeObserver(new Finder() {
                    public boolean identify(Object object) {
                        if (object instanceof EventCreationObserver) {
                            return (SLPatternManip.match(observedPattern, ((EventCreationObserver)object).getObservedFormula()) != null
                                    && SLPatternManip.match(subscribedEvent, ((EventCreationObserver)object).getSubscribedEvent()) != null);
                        } else {
                            return false;
                        }
                    }
                });
                return new ArrayList();
            } else if ((applyResult = SLPatternManip.match(unsubscribePattern2, sr.getSLRepresentation())) != null 
                    && !(subscriber = applyResult.getTerm("??subscriber")).equals(agent.getAgentName())) {
                observedPattern = universalPattern;
                subscribedEvent = (Formula)SLPatternManip
                    .instantiate(subscribeEventPattern, 
                                 "??subscriber", subscriber,
                                 "??goal", applyResult.getFormula("??goal"));
                agent.getMyKBase().removeObserver(new Finder() {
                    public boolean identify(Object object) {
                        if (object instanceof EventCreationObserver) {
                            return (SLPatternManip.match(observedPattern, ((EventCreationObserver)object).getObservedFormula()) != null
                                    && SLPatternManip.match(subscribedEvent, ((EventCreationObserver)object).getSubscribedEvent()) != null);
                        } else {
                            return false;
                        }
                    }
                });
                return new ArrayList();
            }
        }  catch (Exception  e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1
    
} // End of class UnsubscribeFilter
