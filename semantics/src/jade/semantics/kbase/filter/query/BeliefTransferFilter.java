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
 * BeliefTransferFilter.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.query;

import jade.semantics.interpreter.StandardCustomization;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.Set;

/**
 * Is the semantic agent cooperative towards the agent <i>agent</i> regarding a specific belief?
 * This question is asked whenever an agent <i>agent</i> expresses a piece of 
 * information towards the semantic agent.
 * For example, this filter may be applied when the Jade agent receives a 
 * <code>RequestWhenever</code>, a <code>Refuse</code>, <code>Subscribe</code>, a
 * <code>Propose</code>, a <code>Confirm</code>, a <code>Failure</code>, 
 * <code>Disconfirm</code>, or <code>Inform</code> message with the appropriate
 * content.  
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class BeliefTransferFilter extends KBQueryFilter {
    
    /**
     * The customization object of the agent which owns this filter
     */
    private StandardCustomization standardCustomization;
    
    /**
     * Pattern that must match to apply the filter
     */
    private Formula pattern;    
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new filter. Instantiate the pattern.
     * @param standardCustomization the customization object of the agent which 
     * owns this filter
     */
    public BeliefTransferFilter(StandardCustomization standardCustomization) {
        pattern = SLPatternManip.fromFormula("(or (not (I ??agent1 (B ??agent2 ??belief))) (B ??agent2 ??belief))");
        this.standardCustomization = standardCustomization;
    } // End of BeliefTransferFilter/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Returns true as first element if the formula matches the pattern
     * (or (not (I ??agent1 (B ??agent2 ??belief))) (B ??agent2 ??belief)), if 
     * the current agent is agent2 but not agent1, false if not. In this case, 
     * returns an empty ListOfMatchResults as second element and if if the method 
     * {@link StandardCustomization#acceptBeliefTransfer(Formula, Term)} returns true,
     * null in the others cases.
     * If the filter returns false as first element, the second element is null.
     * @param formula a formula on which the filter is tested
     * @param agent a term that represents the agent is trying to apply the filter
     * @return an array with a Boolean meaning the applicability of the filter,
     * and a ListOfMatchResults that is the result of performing the filter. 
     */
    public QueryResult apply(Formula formula, Term agent) {
        QueryResult queryResult = new QueryResult();
        try {
            MatchResult applyResult = SLPatternManip.match(pattern,formula);
            if (applyResult != null 
                    && agent.equals(applyResult.getTerm("agent2")) 
                    && !agent.equals(applyResult.getTerm("agent1"))) {
                if (standardCustomization.acceptBeliefTransfer(applyResult.getFormula("belief"), applyResult.getTerm("agent1"))) {
                    queryResult.setResult(new ListOfMatchResults());    
                } else {
                    queryResult.setResult(null);
                }
                queryResult.setFilterApplied(true);
                return queryResult;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryResult;
    } // End of isApplicable/2
    
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
} // End of class BeliefTransferFilter
