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
 * Subscribe.java
 * Created on 14 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * This principle is intended to be applied when an agent receives a Subscribe 
 * message.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/14 Revision: 1.0
 */
public class Subscribe extends Subscription {
    
//  /**
//  * Pattern used to test the applicability of the principle
//  */
//  private Formula anyPattern = SLPatternManip.fromFormula("??property");
    /**
     * Pattern used to test the applicability of the principle
     */
    private Formula iotaPattern = SLPatternManip.fromFormula("(= (iota ??x ??property) ??y)");
    /**
     * Pattern used to test the applicability of the principle
     */
    private Formula allPattern = SLPatternManip.fromFormula("(= (all ??x ??property) ??y))");
    
    /**
     * Pattern used to test the applicability of the principle
     */
    private Formula somePattern = SLPatternManip.fromFormula("(= (some ??x ??property) ??y))");
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    
    /**
     * Creates a new principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public Subscribe(SemanticCapabilities capabilities) {
        super(capabilities, "(or (I ??subscriber ??goal) " +
                "    (or (forall ??y (not (B ??agent ??ire )))" +
                "        (forall ??e (not (done ??e (forall ??y (not (B ??agent ??ire )))))))))", false);
    } // End of Subscribe/1
    
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    protected Formula computePropertyToObserve(MatchResult applyResult) throws WrongTypeException {
        Formula observedFormula = null;
        Formula property = applyResult.getFormula("ire");
        MatchResult matchResult;
        if ((matchResult = SLPatternManip.match(iotaPattern, property)) != null) {
            observedFormula = (Formula)SLPatternManip.toPattern(matchResult.getFormula("property"), (Variable)matchResult.getTerm("x"));
        } else if ((matchResult = SLPatternManip.match(allPattern, property)) != null) {
            observedFormula = (Formula)SLPatternManip.toPattern(matchResult.getFormula("property"), (Variable)matchResult.getTerm("x"));
        } else if ((matchResult = SLPatternManip.match(somePattern, property)) != null) {
            observedFormula = (Formula)SLPatternManip.toPattern(matchResult.getFormula("property"), (Variable)matchResult.getTerm("x"));
        } else {
            observedFormula = (Formula)SLPatternManip.toPattern(property, (Variable)applyResult.getTerm("y"));
        }
        return observedFormula;
    } // End of computePropertyToObserve/1
    
    /**
     * @inheritDoc
     */
    protected Formula computeEventToExecute(MatchResult applyResult) throws WrongTypeException {
        return applyResult.getFormula("goal");
    }
    
} // End of class Subscribe
