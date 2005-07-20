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
 * Alternative.java
 * Created on 2 nov. 2004
 * Author : Vincent Pautret
 */

package jade.semantics.actions.operators;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionImpl;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.AlternativeBehaviour;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;
import jade.util.leap.ArrayList;

/**
 * Defines a prototype for the alternative operator.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class Alternative extends SemanticActionImpl {
    
    /**
     * Left action of the alternative
     */
    private SemanticAction leftAction;
    
    /**
     * Right action of the alternative
     */
    private SemanticAction rightAction;
    
    /**
     * Pattern used to recognize an alternative of actions
     */
    private Term alternativePattern = SLPatternManip.fromTerm("(| ??leftPart ??rightPart)");;
    
    /**
     * Pattern used to recognize an alternative of formulae
     */
    private Formula orPattern = SLPatternManip.fromFormula("(or ??left ??right)");
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new Alternative Action prototype.
     * @param table the semantic action table
     */
    public Alternative(SemanticActionTable table) {
        super(table);
    } // End of Alternative/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     * 
     */
    public SemanticAction newAction(ActionExpression actionExpression) throws SemanticInterpretationException {
        MatchResult matchResult = SLPatternManip.match(alternativePattern, actionExpression);
        if (matchResult != null) {
            ActionExpression leftPart;
            ActionExpression rightPart;
            try {
                leftPart = (ActionExpression)matchResult.getTerm("leftPart");
                rightPart = (ActionExpression)matchResult.getTerm("rightPart");
            }
            catch (WrongTypeException e) {
                throw new SemanticInterpretationException("ill-formed-message", new WordConstantNode(""));
            }
            Alternative result = new Alternative(table);
            result.setLeftAction(table.getSemanticActionInstance(leftPart));
            result.setRightAction(table.getSemanticActionInstance(rightPart));
            if (result.getLeftAction() != null) {
                if (result.getRightAction() != null) {
                    return result;
                }
                else {
                    throw new SemanticInterpretationException("not-understood-action-expression", rightPart);
                }
            }
            else {
                throw new SemanticInterpretationException("not-understood-action-expression", leftPart);
            }
        } 
        return null;
    } // End of newAction/1
    
    /**
     * Returns an <code>Alternative</code> that is an alternative of all the 
     * actions which are in the list.
     * @param list a list of actions
     * @return an <code>Alternative</code> if the size of the list is more than 
     * 1, a <code>SemanticAction</code> if the size equals 1, null if the list 
     * is empty or if an exception occurs. 
     */
    public SemanticAction newAction(ArrayList list) {
        try {
            if (list.size() == 1) {
                return (SemanticAction)list.get(0);
            } else if (list.size() > 0 ) {
                Alternative result = new Alternative(table);
                result.setLeftAction((SemanticAction)list.remove(0));
                result.setRightAction(newAction(list));
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of newAction/1
    
    /**
     * @inheritDoc
     */
    public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
        try {
            MatchResult matchResult = SLPatternManip.match(orPattern, rationalEffect);
            if (matchResult != null) {
                Formula leftRationalEffect = matchResult.getFormula("left");
                Formula rightRationalEffect = matchResult.getFormula("right");
                ArrayList actionList = new ArrayList(); 
                table.getSemanticActionInstance(actionList, leftRationalEffect, inReplyTo);
                table.getSemanticActionInstance(actionList, rightRationalEffect, inReplyTo);
                return newAction(actionList);
            }       
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of newAction/2
    
    /**
     * @inheritDoc
     */
    public ActionExpression toActionExpression() throws SemanticInterpretationException {
        ActionExpression result;
        try {
            result = (ActionExpression)SLPatternManip.instantiate(alternativePattern,
                    "leftPart", getLeftAction().toActionExpression(),
                    "rightPart", getRightAction().toActionExpression());
        }
        catch (WrongTypeException e) {
            e.printStackTrace();
            throw new SemanticInterpretationException("internal-error", new WordConstantNode(""));
        }
        SLPatternManip.clearMetaReferences(alternativePattern);
        return result;
    } // End of toActionExpression/0
    
    /**
     * @inheritDoc
     */
    public Formula computeFeasibilityPrecondition() throws WrongTypeException {
        return new OrNode(getLeftAction().getFeasibilityPrecondition(), getRightAction().getFeasibilityPrecondition()).getSimplifiedFormula();
    } // End of computeFeasibilityPrecondition/0
    
    /**
     * @inheritDoc
     */
    public Formula computePersistentFeasibilityPreconditon() throws WrongTypeException {
        return new OrNode(getLeftAction().getPersistentFeasibilityPrecondition(), getRightAction().getPersistentFeasibilityPrecondition()).getSimplifiedFormula();
    } // End of computePersistentFeasibilityPreconditon/0
    
    /**
     * @inheritDoc
     */
    public Formula computeRationalEffect() throws WrongTypeException  {
        return new OrNode(getLeftAction().getRationalEffect(), getRightAction().getRationalEffect()).getSimplifiedFormula();
    } // End of computeRationalEffect/0
    
    /**
     * @inheritDoc
     */
    public Formula computePostCondition() throws WrongTypeException {
        return new OrNode(getLeftAction().getPostCondition(), getRightAction().getPostCondition()).getSimplifiedFormula();
    } // End of computePostCondition/0
    
    /**
     * @inheritDoc
     * @return an <code>AlternativeBehaviour</code>
     */
    public Behaviour computeBehaviour() {
        return new AlternativeBehaviour(getLeftAction(), getRightAction());
    } // End of computeBehaviour/0
    
    /**
     * Returns the leftAction.
     * @return the leftAction.
     */
    public SemanticAction getLeftAction() {
        return leftAction;
    } // End of getLeftAction/0 
    
    /**
     * Sets the left action.
     * @param leftAction The leftAction to set.
     */
    public void setLeftAction(SemanticAction leftAction) {
        this.leftAction = leftAction;
    } // End of setLeftAction/1
    
    /**
     * Returns the rightAction.
     * @return the rightAction.
     */
    public SemanticAction getRightAction() {
        return rightAction;
    } // End of getRightAction/0
    
    /**
     * Sets the right action.
     * @param rightAction The rightAction to set.
     */
    public void setRightAction(SemanticAction rightAction) {
        this.rightAction = rightAction;
    } // End of setRightAction/1
    
} // End of class Alternative
