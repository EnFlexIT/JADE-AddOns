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
 * Sequence.java
 * Created on 2 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.operators;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionImpl;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.SequenceBehaviour;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;
import jade.util.leap.ArrayList;

/**
 * Defines a prototype for the sequence operator.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class Sequence extends SemanticActionImpl {
    /**
     * Left action of the sequence
     */
    private SemanticAction leftAction;
    
    /**
     * Right action of the sequence
     */
    private SemanticAction rightAction;
    
    /**
     * Pattern used to recognize a sequence of formulae
     */
    private Formula andPattern;
    
    /**
     * Pattern used to recognize a sequence of actions
     */
    private ActionExpression sequencePattern;		
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new Sequence Action prototype.
     * @param table the semantic action table
     */
    public Sequence(SemanticActionTable table) {
        super(table);
        sequencePattern = (ActionExpression) SLPatternManip.fromTerm("(; ??leftPart ??rightPart)");
        andPattern = SLPatternManip.fromFormula("(and ??left ??right)");
    } // End of constructor Sequence/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    public SemanticAction newAction(ActionExpression actionExpression) throws SemanticInterpretationException {
        Sequence result = new Sequence(table);
        try {
            MatchResult matchResult = SLPatternManip.match(sequencePattern, actionExpression);
            if (matchResult != null) {
                ActionExpression leftPart = (ActionExpression)matchResult.getTerm("leftPart");
                ActionExpression rightPart = (ActionExpression)matchResult.getTerm("rightPart");
                result.setLeftAction(table.getSemanticActionInstance(leftPart));
                result.setRightAction(table.getSemanticActionInstance(rightPart));
                if (result.getLeftAction() != null && result.getRightAction() != null) {
                    return result;
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        
    } // End of newAction/1
    
    
    /**
     * Returns a <code>Sequence</code> that is an alternative of all the 
     * actions which are in the list.
     * @param list a list of actions
     * @return a <code>Sequence</code> if the size of the list is more than 
     * 1, a <code>SemanticAction</code> if the size equals 1, null if the list 
     * is empty or if an exception occurs. 
     */
    public SemanticAction newAction(ArrayList list) {
        try {
            if (list.size() == 1) {
                return (SemanticAction)list.get(0);
            } else if (list.size() > 0 ) {
                Sequence result = new Sequence(table);
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
        Formula leftRationalEffect = null;
        Formula rightRationalEffect = null;
        try {
            MatchResult matchResult = SLPatternManip.match(andPattern, rationalEffect);
            if (matchResult != null) {
                leftRationalEffect = matchResult.getFormula("??left");
                rightRationalEffect = matchResult.getFormula("??right");
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
    
    /***
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
    
    /**
     * @inheritDoc
     * @return a <code>TrueNode</code>.
     */
    public Formula computeFeasibilityPrecondition() throws WrongTypeException {
        return new TrueNode();
    } //End of computeFeasibilityPrecondition/0
    
    /**
     * @inheritDoc
     * @return a <code>TrueNode</code>.
     */
    public Formula computePersistentFeasibilityPreconditon() throws WrongTypeException {
        return new TrueNode();
    } // End of computePersistentFeasibilityPreconditon/0
    
    /**
     * @inheritDoc
     * @return a <code>TrueNode</code>.
     */
    public Formula computeRationalEffect() throws WrongTypeException {
        return new TrueNode();
    } // End of computeRationalEffect/0
    
    /**
     * @inheritDoc
     * @return a <code>TrueNode</code>.
     */
    public Formula computePostCondition() throws WrongTypeException {
        return new TrueNode();
    } // End of computePostCondition/0
    
    /**
     * @inheritDoc
     * @return a <code>SequenceBehaviour</code>.
     */
    public Behaviour computeBehaviour() {
        return new SequenceBehaviour(getLeftAction(), getRightAction());
    } // End of computeBehaviour/0
    
    /**
     * @inheritDoc
     */
    public ActionExpression toActionExpression() throws SemanticInterpretationException {
        ActionExpression result;
        try {
            result = (ActionExpression)SLPatternManip.instantiate(sequencePattern,
                    "leftPart", getLeftAction().toActionExpression(),
                    "rightPart", getRightAction().toActionExpression());
        }
        catch (WrongTypeException e) {
            e.printStackTrace();
            throw new SemanticInterpretationException("internal-error", new WordConstantNode(""));
        }
        SLPatternManip.clearMetaReferences(sequencePattern);
        return result;
    } // End of toActionExpression/0
    
} // End of class Sequence
