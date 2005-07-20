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
package jade.semantics.kbase.filter.query;

import jade.semantics.kbase.Bindings;
import jade.semantics.kbase.BindingsImpl;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Manages the event memory of the knowledge base. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/17 Revision: 1.0
 */
public class EventMemoryFilter extends KBQueryFilter {
    
    /**
     * Exist pattern
     */
    private Formula existPattern;
    
    /**
     * Done pattern
     */
    private Formula donePattern;
    
    /**
     * Creates a new filter
     */
    public EventMemoryFilter() {
        existPattern = SLPatternManip.fromFormula("(B ??agent (exists ??e (done ??act)))");
        donePattern = SLPatternManip.fromFormula("(B ??agent (done ??act true))");
    }
    
    /**
     * Returns true, if <code>existPattern</code> or <code>donePattern</code> 
     * matches.
     * @inheritDoc
     */
    public boolean isApplicable(Formula formula, Term agent) {
        try {
            applyResult = SLPatternManip.match(existPattern,formula);
            if (applyResult != null && agent.equals(applyResult.getTerm("agent"))) { 
                return true;
            } else {
                applyResult = SLPatternManip.match(donePattern,formula);
                return (applyResult != null && agent.equals(applyResult.getTerm("agent")));
            }
        } catch (SLPatternManip.WrongTypeException wte) {
            wte.printStackTrace();
        }
        return false;
    } // End of isApplicable/2
    
    /**
     * Returns a new list (i.e. true) if the action recovered in the match result is a
     * sequence of action already done by the agent, <code>null</code> if not.
     * @inheritDoc
     */
    public Bindings apply(Formula formula) {
        try {
            if (analyzeActionExpression(applyResult.getTerm("act"), 0, false)) {
                return new BindingsImpl();
            }
        } catch (SLPatternManip.WrongTypeException wte) {
            wte.printStackTrace();
        }
        return null;
    } // End of apply/1
    
    /**
     * Tests if a sequence of actions (can be reduced to only one) is in the
     * event memory or not. If VariableNode appears that means the actions could
     * appears between them. For example, a1;a2;e;a3 , means that a2 must follow
     * a1 in the memory whereas there can be several actions between a1 and a2.     
     * @param action the action expression to test
     * @param index current index
     * @param goOn true if a VariableNode is met
     * @return true if the action expression is in the memory, false if not.
     */
    private boolean analyzeActionExpression(Term action, int index, boolean goOn) {
        if (index == myKBase.getEventMemory().size() && !(action instanceof VariableNode)) {
            return false;
        }
        if (action instanceof SequenceActionExpressionNode) {
            try {
                if (((SequenceActionExpressionNode)action).as_right_action() instanceof VariableNode 
                        && ((SequenceActionExpressionNode)action).as_right_action().equals(applyResult.getTerm("e"))) {
                    return analyzeActionExpression(((SequenceActionExpressionNode)action).as_left_action(), index, true);    
                } else if (((SequenceActionExpressionNode)action).as_right_action().equals(myKBase.getEventMemory().get(index))) {
                    return analyzeActionExpression(((SequenceActionExpressionNode)action).as_left_action(), index + 1, true);
                } else {
                    if (goOn) {
                        return analyzeActionExpression(action, index + 1, true);
                    } else {
                        return false;
                    }
                }    
            }catch (Exception wte) {
                wte.printStackTrace();
                return false;
            }
        } else {
            try {
                if (action instanceof VariableNode && action.equals(applyResult.getTerm("e"))) return true;
            } catch (SLPatternManip.WrongTypeException wte) {
                wte.printStackTrace();
                return false;
            }
            if (myKBase.getEventMemory().get(index).equals(action)) {
                return true;
            } else if (goOn) {
                return analyzeActionExpression(action, index + 1, goOn);
            } else {
                return false;
            }
        }
    } // End of analyzeActionExpression/3
    
} // End of class EventMemoryFilter
