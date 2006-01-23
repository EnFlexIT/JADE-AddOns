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

import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.Set;

/**
 * Manages the event memory of the belief base. 
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
     * Creates a new filter. Instantiates the patterns.
     */
    public EventMemoryFilter() {
        existPattern = SLPatternManip.fromFormula("(B ??agent (exists ??e (done ??act)))");
        donePattern = SLPatternManip.fromFormula("(B ??agent (done ??act true))");
    }
    
    /**
     * Returns true as first element, if one of these patterns match:
     * <ul>
     * <li>(B ??agent (exists ??e (done ??act)))
     * <li>(B ??agent (done ??act true))
     * <ul>
     * and if the current agent is the "agent" of the pattern. In this case, the
     * second element is an empty ListOfMatchResults if the action 
     * recovered in the match result is a sequence of action already done by the 
     * agent, null in the others cases.
     * If the filter returns false as first element, the second element is null.
     * @param formula a formula on which the filter is tested
     * @param agent a term that represents the agent is trying to apply the filter
     * @return an array with a Boolean meaning the applicability of the filter,
     * and a ListOfMatchResults that is the result of performing the filter.
     */
    public QueryResult apply(Formula formula, Term agent) {
        QueryResult queryResult = new QueryResult();
        try {
            MatchResult applyResult = SLPatternManip.match(existPattern,formula);
            if (applyResult != null && agent.equals(applyResult.getTerm("agent"))) { 
                queryResult.setResult(apply(applyResult));
                queryResult.setFilterApplied(true);
                return queryResult;
            } else {
                applyResult = SLPatternManip.match(donePattern,formula);
                if (applyResult != null && agent.equals(applyResult.getTerm("agent"))) {
                    queryResult.setResult(apply(applyResult));
                    queryResult.setFilterApplied(true);
                    return queryResult;
                }
            }
        } catch (SLPatternManip.WrongTypeException wte) {
            wte.printStackTrace();
        }
        return queryResult;
    } // End of isApplicable/2
    
    /**
     * Returns a new ListOFMatchResults if the action recovered in the match result is a
     * sequence of action already done by the agent, <code>null</code> if not.
     * @param applyResult the MatchResult corresponding to the match between
     * the incoming formula and the pattern of the filter.
     * @return an empty ListOfMatchResults or null.
     */
    private ListOfMatchResults apply(MatchResult applyResult) {
        try {
            if (analyzeActionExpression(applyResult, applyResult.getTerm("act"), 0, false)) {
                return new ListOfMatchResults();
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
     * @param applyResult the MatchResult corresponding to the match between
     * the incoming formula and the pattern of the filter.
     * @param action the action expression to test
     * @param index current index
     * @param goOn true if a VariableNode is met
     * @return true if the action expression is in memory, false if not.
     */
    private boolean analyzeActionExpression(MatchResult applyResult, Term action, int index, boolean goOn) {
        if (index == myKBase.getEventMemory().size() && !(action instanceof VariableNode)) {
            return false;
        }
        if (action instanceof SequenceActionExpressionNode) {
            try {
                if (((SequenceActionExpressionNode)action).as_right_action() instanceof VariableNode 
                        && ((SequenceActionExpressionNode)action).as_right_action().equals(applyResult.getTerm("e"))) {
                    return analyzeActionExpression(applyResult, ((SequenceActionExpressionNode)action).as_left_action(), index, true);    
                } else if (((SequenceActionExpressionNode)action).as_right_action().equals(myKBase.getEventMemory().get(index))) {
                    return analyzeActionExpression(applyResult, ((SequenceActionExpressionNode)action).as_left_action(), index + 1, true);
                } else {
                    if (goOn) {
                        return analyzeActionExpression(applyResult, action, index + 1, true);
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
                return analyzeActionExpression(applyResult, action, index + 1, goOn);
            } else {
                return false;
            }
        }
    } // End of analyzeActionExpression/3
    
    /**
     * By default, this method does nothing. 
     * @param formula an observed formula
     * @param set set of patterns. Each pattern corresponds to a kind a formula
     * which, if it is asserted in the base, triggers the observer that
     * observes the formula given in parameter.
     */
    public void getObserverTriggerPatterns(Formula formula, Set set) {
    }

} // End of class EventMemoryFilter
