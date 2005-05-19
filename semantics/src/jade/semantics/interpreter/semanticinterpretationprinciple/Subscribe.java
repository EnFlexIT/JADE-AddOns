/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France T�l�com

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
 * SubscribeFilter.java
 * Created on 14 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.semanticinterpretationprinciple;

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
public class Subscribe extends SemanticInterpretationPrincipleImpl {

    /**
     * Pattern that must match to apply the filter
     */
    Formula subscribePattern;
    
    /**
     * Pattern used to build the formula to monitor for the requested subscription
     */
    Formula observedPattern;
    
    /**
     * Pattern used to buid the internal event to trigger for the requested subscription 
     */
    Formula eventPattern;
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    
    /**
     * Constructor
     */
    public Subscribe(SemanticAgent agent) {
        setAgent(agent);
        subscribePattern = SLPatternManip.fromFormula("(B "+ agent.getAgentName() +" (or (I ??subscriber ??goal) (not (done (action null before) (not (B "+ agent.getAgentName() +" ??property))))))");
        observedPattern = SLPatternManip.fromFormula("(B "+ agent.getAgentName() +" ??property)");
        eventPattern = SLPatternManip.fromFormula("(B "+ agent.getAgentName() +" (I ??subscriber ??goal))");
    } // End of SubscribeFilter/1

    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/

    /**
     * 
     */
    public ArrayList apply(SemanticRepresentation sr)
            throws SemanticInterpretationPrincipleException {
        try {
            Term subscriber;
            MatchResult applyResult = SLPatternManip.match(subscribePattern, sr.getSLRepresentation());
            if (applyResult != null 
                    && !(subscriber = applyResult.getTerm("??subscriber")).equals(agent.getAgentName())) {         
                Formula observedFormula = (Formula)SLPatternManip
                    .instantiate(observedPattern,
                                 "??property", applyResult.getFormula("??property"));
                Formula subscribedEvent = (Formula)SLPatternManip
                    .instantiate(eventPattern,
                                 "??subscriber", subscriber,
                                 "??goal", applyResult.getFormula("??goal"));
               
                agent.getMyKBase().addObserver(new EventCreationObserver(agent, observedFormula, subscribedEvent));
                return new ArrayList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1
    
} // End of class SubscribeFilter
