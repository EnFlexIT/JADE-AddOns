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

import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionImpl;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.AlternativeBehaviour;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;
import jade.util.leap.ArrayList;

/**
 * Class that represents the pattern for the alternative operator.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class Alternative extends SemanticActionImpl {
    
    /**
     * Left action of the alternative
     */
    SemanticAction leftAction;
    
    /**
     * Right action of the alternative
     */
    SemanticAction rightAction;
    
    /**
     * Pattern used to recognize an alternative of actions
     */
    ActionExpression alternativePattern;
       
    /**
     * Pattern used to recognize an alternative of formulae
     */
    Formula orPattern;

    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Constructor
     * @param table the semantic action table
     */
    public Alternative(SemanticActionTable table) {
        super(table);
        alternativePattern = (ActionExpression)SLPatternManip.fromTerm("(| ??leftPart ??rightPart)");
        orPattern = SLPatternManip.fromFormula("(or ??left ??right)");
    } // End of constructor Alternative/1
 
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
        Alternative result = new Alternative(table);
        try {
			MatchResult matchResult = SLPatternManip.match(alternativePattern, actionExpression);
	        if (matchResult != null) {
	            ActionExpression leftPart = matchResult.getActionExpression("??leftPart");
	            ActionExpression rightPart = matchResult.getActionExpression("??rightPart");
	            result.setLeftAction(table.getSemanticActionInstance(leftPart));
	            result.setRightAction(table.getSemanticActionInstance(rightPart));
	            if (result.getLeftAction() != null && result.getRightAction() != null) {
					//result.setSemanticFeatures();
                    result.setBehaviour(new AlternativeBehaviour(result.getLeftAction(), result.getRightAction()));
					return result;
	            }
	        } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    } // End of newAction/1
    
    /**
     * Returns an <code>Alternative</code> that is an alternative of all the 
     * actions which are in the list.
     * @param list a list of actions
     * @return an <code>Alternative</code> if the size of the list is more than 
     * 1, a <code>SemanticAction</code> if the size equals 1, null if the list 
     * is empty. 
     */
    public SemanticAction newAction(ArrayList list) {
        try {
            if (list.size() == 1) {
	            return (SemanticAction)list.get(0);
	        } else if (list.size() > 0 ) {
	            Alternative result = new Alternative(table);
	            result.setLeftAction((SemanticAction)list.remove(0));
	            result.setRightAction((SemanticAction)newAction(list));
	            //result.setSemanticFeatures();
                result.setBehaviour(new AlternativeBehaviour(result.getLeftAction(), result.getRightAction()));
	            return result;
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of newAction/1
    
    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.Term)
     */
    public SemanticAction newAction(Formula rationalEffect, Term agentName) {
        Formula leftRationalEffect = null;
        Formula rightRationalEffect = null;
        try {
			MatchResult matchResult = SLPatternManip.match(orPattern, rationalEffect);
            if (matchResult != null) {
	            leftRationalEffect = matchResult.getFormula("??left");
	            rightRationalEffect = matchResult.getFormula("??right");
	            ArrayList actionList = new ArrayList(); 
                table.getSemanticActionInstance(actionList, leftRationalEffect, agentName);
	            table.getSemanticActionInstance(actionList, rightRationalEffect, agentName);
   	            return newAction(actionList);
            }       
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of newAction/2
    
    /**
	 * Computes all semantic features of the action (feasibility precondition,
	 * persistent feasibility precondition, rational effect and postcondition)
	 * @throws SLPattern.WrongVariableType
	 * @throws SLPattern.PatternCannotBeinstantiated
	 */
	protected void setSemanticFeatures() throws WrongTypeException {
		// TODO Handle all receivers of receiverList
//		SLPatternManip.set(orPattern, "??left", getLeftAction().getFeasibilityPrecondition());
//		SLPatternManip.set(orPattern, "??right", getRightAction().getFeasibilityPrecondition());
//		setFeasibilityPrecondition(((Formula) SLPatternManip.instantiate(orPattern)).getSimplifiedFormula());
//
//		SLPatternManip.set(orPattern, "??left", getLeftAction().getRationalEffect());
//		SLPatternManip.set(orPattern, "??right", getRightAction().getRationalEffect());
//        setRationalEffect(((Formula) SLPatternManip.instantiate(orPattern)).getSimplifiedFormula());
//
//		SLPatternManip.set(orPattern, "??left", getLeftAction().getPersistentFeasibilityPrecondition());
//		SLPatternManip.set(orPattern, "??right", getRightAction().getPersistentFeasibilityPrecondition());
//		setPersistentFeasibilityPrecondition(((Formula) SLPatternManip.instantiate(orPattern)).getSimplifiedFormula());
//	
//		SLPatternManip.set(orPattern, "??left", getLeftAction().getPostCondition());
//		SLPatternManip.set(orPattern, "??right", getRightAction().getPostCondition());
//		setPostCondition(((Formula) SLPatternManip.instantiate(orPattern)).getSimplifiedFormula());
		
		setBehaviour(new AlternativeBehaviour(getLeftAction(), getRightAction()));
	} // End of setSemanticFeatures

    public Formula feasibilityPreconditionCalculation() throws WrongTypeException {
        SLPatternManip.set(orPattern, "??left", getLeftAction().getFeasibilityPrecondition());
        SLPatternManip.set(orPattern, "??right", getRightAction().getFeasibilityPrecondition());
        return ((Formula) SLPatternManip.instantiate(orPattern)).getSimplifiedFormula();
    }
    public Formula persistentFeasibilityPreconditonCalculation() throws WrongTypeException {
        SLPatternManip.set(orPattern, "??left", getLeftAction().getPersistentFeasibilityPrecondition());
        SLPatternManip.set(orPattern, "??right", getRightAction().getPersistentFeasibilityPrecondition());
        return ((Formula) SLPatternManip.instantiate(orPattern)).getSimplifiedFormula();
    }
    public Formula rationalEffectCalculation() throws WrongTypeException  {
        SLPatternManip.set(orPattern, "??left", getLeftAction().getRationalEffect());
        SLPatternManip.set(orPattern, "??right", getRightAction().getRationalEffect());
        return ((Formula) SLPatternManip.instantiate(orPattern)).getSimplifiedFormula();
    }
    public Formula postConditionCalculation() throws WrongTypeException {
        SLPatternManip.set(orPattern, "??left", getLeftAction().getPostCondition());
        SLPatternManip.set(orPattern, "??right", getRightAction().getPostCondition());
        return ((Formula) SLPatternManip.instantiate(orPattern)).getSimplifiedFormula();
    }

    /**
     * @return Returns the leftAction.
     */
    public SemanticAction getLeftAction() {
        return leftAction;
    } // End of getLeftAction/0 
    
    /**
     * @param leftAction The leftAction to set.
     */
    public void setLeftAction(SemanticAction leftAction) {
        this.leftAction = leftAction;
    } // End of setLeftAction/1
    
    /**
     * @return Returns the rightAction.
     */
    public SemanticAction getRightAction() {
        return rightAction;
    } // End of getRightAction/0
    
    /**
     * @param rightAction The rightAction to set.
     */
    public void setRightAction(SemanticAction rightAction) {
        this.rightAction = rightAction;
    } // End of setRightAction/1
    
} // End of class Alternative
