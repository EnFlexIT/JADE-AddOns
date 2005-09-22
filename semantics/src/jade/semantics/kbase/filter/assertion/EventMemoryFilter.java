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
 * EventMemoryFilter.java
 * Created on 17 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.assertion;

import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Filter used to store in knowledge base the actions done by the agent. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/17 Revision 1.0 
 */
public class EventMemoryFilter extends KBAssertFilter {
    
    /**
     * Pattern used to test the applicability of the filter
     */
    private Formula pattern;
    
    /**
     * Creates a new EventMemoryFilter. Instantiates the pattern to test the
     * applicability.
     */
    public EventMemoryFilter() {
        pattern = SLPatternManip.fromFormula("(B ??agent (done ??action ??phi))");
    } // End of EventMemoryFilter
    
    /**
     * Asserts a formula.
     * If the formula given in parameter is a 
     * <code>SequenceActionExpressionNode</code>,
     * this method asserts all the elements of 
     * the sequence in the event list of the knowledge base. 
     * If the action expression given in parameter is an ActionExpressionNode,
     * the method asserts the action expression.
     * In these two cases, returns a <code>TrueNode</code>. Otherwise, does nothing,
     * and returns the formula given in parameter.
     * @param formula a formula to assert
     * @return <code>TrueNode</code> if the filter is applicable, the given formula in the 
     * other cases.
     */
    public Formula beforeAssert(Formula formula) {
        mustApplyAfter = false;
        MatchResult matchResult = SLPatternManip.match(pattern, formula);
        if (matchResult != null) {
            try {
                storeInBase((ActionExpression)matchResult.getTerm("action"));
                return new TrueNode();
            } catch (SLPatternManip.WrongTypeException wte) {}
        }
        return formula;
    } // End of beforeAssert/1
    
    
    /**
     * If the action expression given in parameter is a 
     * SequenceActionExpressionNode, this method asserts all the elements of 
     * the sequence in the event list of the knowledge base. 
     * If the action expression given in parameter is an ActionExpressionNode,
     * the method asserts the action expression.
     * @param action an action expression
     */
    private void storeInBase(ActionExpression action) {
        if (action instanceof SequenceActionExpressionNode) {
            myKBase.addEventInMemory((ActionExpression)((SequenceActionExpressionNode)action).as_left_action());
            storeInBase((ActionExpression)((SequenceActionExpressionNode)action).as_right_action());
        } else {
            myKBase.addEventInMemory(action);
        }
    } // End of storeInBase/1
    
} // End of class EventMemoryFilter
