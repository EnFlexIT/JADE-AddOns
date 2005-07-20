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
 * UnreachableGoal.java
 * Created on 20 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This principle is intented to be applied to all intentions that have not be 
 * realised. These intentions are considered as not feasible.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/20 Revision: 1.0
 */
public class UnreachableGoal extends SemanticInterpretationPrinciple {
    
    /**
     * Pattern used to test the applicability of the semantic interpretation principle
     */
    private Formula pattern;
    
    /**
     * Pattern of the new Semantic Representation
     */
    private Formula notFeasiblePattern;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public UnreachableGoal(SemanticCapabilities capabilities) {
        super(capabilities);
        pattern = SLPatternManip.fromFormula("(I " + myCapabilities.getAgentName() + " ??phi)");
        notFeasiblePattern = SLPatternManip.fromFormula("(I " + myCapabilities.getAgentName() + " (B ??receiver (forall ?e (not (B " + myCapabilities.getAgentName() + " (feasible ?e ??phi))))))");
    } // End of Failure/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    public ArrayList apply(SemanticRepresentation sr)
    throws SemanticInterpretationPrincipleException {
        MatchResult matchResult = SLPatternManip.match(pattern,sr.getSLRepresentation());
        if (matchResult != null) {
            if (sr.getDataToFeedback() != null) {
                SemanticRepresentation newSR = new SemanticRepresentation();
                ArrayList listOfSR = new ArrayList();
                newSR.setMessage(sr.getMessage());
                try {
                    newSR.setSLRepresentation(
                            ((Formula)SLPatternManip.instantiate(notFeasiblePattern, 
                                    "receiver", sr.getDataToFeedback().getReceiver(),
                                    "phi", sr.getDataToFeedback().getGoal())).getSimplifiedFormula());
                    listOfSR.add(newSR);
                } catch (SLPatternManip.WrongTypeException e) {
                    e.printStackTrace();
                }
                return listOfSR;
            }
            return new ArrayList();
        }
        return null;
    } // End of apply/1
    
} // End of class Failure
