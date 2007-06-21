/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

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
 * EventMemoryFilters.java
 * Created on 4 janvier 2007
 * Author : Thierry Martinez & Vincent Pautret
 */

package jade.semantics.kbase.filter;

import jade.semantics.kbase.FiltersDefinition;
import jade.semantics.kbase.filter.query.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.LoopingInstantiationException;
import jade.util.leap.ArrayList;
import jade.util.leap.Set;

public class EventMemoryFilters extends FiltersDefinition {

    /**
     * List of events already done
     */
    private ArrayList eventMemory;

    /**
     * The maximum size of the event memory list
     */
    private int event_memory_size = 10;

    /**
     * Adds a new event in the event memory
     * @param action an action expression of an done event
     */
    private void addEventInMemory(ActionExpression action) {
        try {
            SL.substituteMetaReferences(action);
            SL.removeOptionalParameter(action);
        } catch (LoopingInstantiationException e) {
            e.printStackTrace();
        }
        eventMemory.add(0, action);
        
        if (eventMemory.size() == event_memory_size + 1) eventMemory.remove(event_memory_size);
    }

    /**
     * Returns the event memory
     * @return the event memory
     */
    private ArrayList getEventMemory() {
        return eventMemory;
    }
    
    /**
     * Sets the size of the event memory
     * @param size the new size of the event memory
     */
    public void setEventMemorySize(int size) {
        event_memory_size = size;
    }

    /**
     * Constructor of a event memory filters environment
     */
    public EventMemoryFilters() {
    	this.eventMemory = new ArrayList();
    	
    	//
    	// Define a KBAssertFilter
    	// ------------------------
    	defineFilter(new KBAssertFilter() {
        
    		/**
    		 * Pattern used to test the applicability of the filter
    		 */
    		private Formula pattern = SL.fromFormula("(B ??agent (done ??action ??phi))");
        
    		/**
    		 * Asserts a formula.
    		 * If the formula given in parameter is a 
    		 * <code>SequenceActionExpressionNode</code>,
    		 * this method asserts all the elements of 
    		 * the sequence in the event list of the belief base. 
    		 * If the action expression given in parameter is an ActionExpressionNode,
    		 * the method asserts the action expression.
    		 * In these two cases, returns a <code>TrueNode</code>. Otherwise, does nothing,
    		 * and returns the formula given in parameter.
    		 * @param formula a formula to assert
    		 * @return <code>TrueNode</code> if the filter is applicable, the given formula in the 
    		 * other cases.
    		 */
    		public Formula apply(Formula formula) {
    			MatchResult matchResult = SL.match(pattern, formula);
    			if (matchResult != null) {
    				try {
    					storeInBase((ActionExpression)matchResult.getTerm("action"));
    					myKBase.updateObservers(((BelieveNode)formula).as_formula());
    					return new TrueNode(); 
    				} catch (SL.WrongTypeException wte) {}
    			}
    			return formula;
    		} 
            
    		/**
    		 * If the action expression given in parameter is a 
    		 * SequenceActionExpressionNode, this method asserts all the elements of 
    		 * the sequence in the event list of the belief base. 
    		 * If the action expression given in parameter is an ActionExpressionNode,
    		 * the method asserts the action expression.
    		 * @param action an action expression
    		 */
    		private void storeInBase(ActionExpression action) {
    			if (action instanceof SequenceActionExpressionNode) {
    				addEventInMemory((ActionExpression)((SequenceActionExpressionNode)action).as_left_action());
    				storeInBase((ActionExpression)((SequenceActionExpressionNode)action).as_right_action());
    			} else {
    				addEventInMemory(action); 
    			}
    		}             
    	});
    	
    	//
    	// Define a KBQueryFilter
    	// ------------------------
    	defineFilter(new KBQueryFilter() {
    	    
    	    /**
    	     * Exist pattern
    	     */
    	    private Formula existPattern = SL.fromFormula("(B ??agent (exists ??e (done ??act)))");
    	    
    	    /**
    	     * Done pattern
    	     */
    	    private Formula donePattern = SL.fromFormula("(B ??agent (done ??act true))");
    	    
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
    	            MatchResult applyResult = SL.match(existPattern,formula);
    	            if (applyResult != null && agent.equals(applyResult.getTerm("agent"))) { 
    	                queryResult.setResult(apply(applyResult));
    	                queryResult.setFilterApplied(true);
    	                return queryResult;
    	            } else {
    	                applyResult = SL.match(donePattern,formula);
    	                if (applyResult != null && agent.equals(applyResult.getTerm("agent"))) {
    	                    queryResult.setResult(apply(applyResult));
    	                    queryResult.setFilterApplied(true);
    	                    return queryResult;
    	                }
    	            }
    	        } catch (SL.WrongTypeException wte) {
    	            wte.printStackTrace();
    	        }
    	        return queryResult;
    	    } 
    	    
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
    	        } catch (SL.WrongTypeException wte) {
    	            wte.printStackTrace();
    	        }
    	        return null;
    	    } 
    	    
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
    	        if (index == getEventMemory().size() && !(action instanceof VariableNode)) {
    	            return false;
    	        }
    	        if (action instanceof SequenceActionExpressionNode) {
    	            try {
    	                if (((SequenceActionExpressionNode)action).as_right_action() instanceof VariableNode 
    	                        && ((SequenceActionExpressionNode)action).as_right_action().equals(applyResult.getTerm("e"))) {
    	                    return analyzeActionExpression(applyResult, ((SequenceActionExpressionNode)action).as_left_action(), index, true);    
    	                } else if (((SequenceActionExpressionNode)action).as_right_action().equals(getEventMemory().get(index))) {
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
    	            } catch (SL.WrongTypeException wte) {
    	                wte.printStackTrace();
    	                return false;
    	            }
    	            if (getEventMemory().get(index).equals(action)) {
    	                return true;
    	            } else if (goOn) {
    	                return analyzeActionExpression(applyResult, action, index + 1, goOn);
    	            } else {
    	                return false;
    	            }
    	        }
    	    } 
    	    
    	    /**
    	     * By default, this method does nothing. 
    	     * @param formula an observed formula
    	     * @param set set of patterns. Each pattern corresponds to a kind a formula
    	     * which, if it is asserted in the base, triggers the observer that
    	     * observes the formula given in parameter.
    	     */
    	    public boolean getObserverTriggerPatterns(Formula formula, Set set) {
    	    	return true;
    	    }

    	});
    }

}
