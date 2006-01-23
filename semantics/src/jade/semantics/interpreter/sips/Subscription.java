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
 * Subscription.java
 * Created on 24 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.kbase.observer.EventCreationObserver;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;
import jade.util.leap.ArrayList;

/**
 * Used to gather the principles which deal with the same domain.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/24 Revision: 1.0
 */
public abstract class Subscription extends SemanticInterpretationPrinciple {
    
    /**
     * Pattern that must match to apply the filter
     */
    private Formula subscribePattern;
    
    /**
     * Pattern used to build the formula to monitor for the requested subscription
     */
    private Formula observedPattern;
    
    /**
     * Pattern used to buid the internal event to trigger for the requested subscription 
     */
    private Formula eventPattern;
    
    /**
     * True if the subscription should be done only one time (requestWhen)
     */
    private boolean isOneShot;
    
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    
    /**
     * Creates a new principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     * @param subscribe
     * @param isOneShot only one time or not
     */
    public Subscription(SemanticCapabilities capabilities, String subscribe, boolean isOneShot) {
        super(capabilities);
        try {
            subscribePattern = (Formula)SLPatternManip.instantiate(SLPatternManip.fromFormula("(B ??agent " + subscribe + ")"),
                    "agent", myCapabilities.getAgentName());
        } catch (SLPatternManip.WrongTypeException wte) {
            wte.printStackTrace();
        }
        observedPattern = SLPatternManip.fromFormula("(B "+ myCapabilities.getAgentName() +" ??property)");
        eventPattern = SLPatternManip.fromFormula("(B "+ myCapabilities.getAgentName() +" (I ??subscriber ??goal))");
        this.isOneShot = isOneShot;
    } // End of Subscription/1
    
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    
    /**
     * @inheritDoc
     */
    public ArrayList apply(SemanticRepresentation sr)
    throws SemanticInterpretationPrincipleException {
        try {
            Term subscriber;
            MatchResult applyResult = SLPatternManip.match(subscribePattern, sr.getSLRepresentation());
            if (applyResult != null 
                    && !(subscriber = applyResult.getTerm("subscriber")).equals(myCapabilities.getAgentName())) {
                
                Formula observedFormula = (Formula)SLPatternManip.instantiate(observedPattern,
                        "property", computePropertyToObserve(applyResult));
                
                Formula subscribedEvent = (Formula)SLPatternManip
                .instantiate(eventPattern,
                        "subscriber", subscriber,
                        "goal", computeEventToExecute(applyResult));                
                myCapabilities.getMyKBase().addObserver(new EventCreationObserver(myCapabilities.getAgent(), observedFormula, subscribedEvent, isOneShot));
                myCapabilities.getMyStandardCustomization().notifySubscribe(subscriber, observedFormula, applyResult.getFormula("goal"));
                return new ArrayList();
            }
        } catch (SLPatternManip.WrongTypeException e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;    
    }
    
    /**
     * Builds the observed property with the elements of the matching result.
     * @param applyResult matching result 
     * @return a formula representing the property to observe
     * @throws WrongTypeException if it raised during the SL formulae analyzis
     */
    abstract protected Formula computePropertyToObserve(MatchResult applyResult) throws WrongTypeException;
    
    /**
     * Builds the event to execute with the elements of the matching result.
     * @param applyResult matching result
     * @return a formula representing the event to execute
     * @throws WrongTypeException if it raised during the SL formulae analyzis
     */
    abstract protected Formula computeEventToExecute(MatchResult applyResult)throws WrongTypeException;
    
}
