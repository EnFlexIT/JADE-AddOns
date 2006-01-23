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
 * CFPFilter.java
 * Created on 17 janv. 2006
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.query;

import jade.semantics.interpreter.StandardCustomization;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.Util;
import jade.util.leap.Set;

/**
 * This filter applies whenever an agent <i>agent1</i> is calling for a proposal 
 * (consisting in performing the action <i>act</i> under the condition 
 * <i>condition</i>) towards the Jade agent <i>agent2</i>.
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class CFPFilter extends KBQueryFilter {
    
    /**
     * The filter manager
     */
    private StandardCustomization standardCustomization;
    
    /**
     * Pattern that must match to apply the filter
     */
    private Formula pattern1;
    private Formula pattern2;
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a new Filter. Instantiates the pattern.
     * @param standardCustomization the customization object of the agent that 
     * owns this filter
     */
    public CFPFilter(StandardCustomization standardCustomization) {
        this.standardCustomization = standardCustomization;
        pattern1 = SLPatternManip.fromFormula("(B ??agent (= ??ire ??Result))");
        pattern2 = SLPatternManip.fromFormula("(or (not (I ??agent1 (done ??act ??proposition))) (I ??agent2 (done ??act ??proposition)))");
    } // End of CFPFilter/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * Returns true if the formula part of the given identifying expression
     * matches the pattern 
     * <i>(or (not (I ??agent1 (done ??act ??proposition))) (I ??agent2 (done ??act ??proposition)))</i>,
     *  if the current agent is the agent of the action expression <i>act</i>, if the 
     *  current agent is <i>agent2</i>, and if the current agent is not <i>agent1</i>.
     * @param ide an identifying expression on which the filter is tested
     * @param agent a term that represents the agent is trying to apply the filter
     * @return an array with a Boolean meaning the applicability of the filter,
     * and a ListOfMatchResults that is the result of performing the filter. 
     */
    public QueryResult apply(Formula formula, Term agent) {
        QueryResult queryResult = new QueryResult();
        try {
            MatchResult applyResult1 = SLPatternManip.match(pattern1,formula);
            if (applyResult1 != null) {
                IdentifyingExpression ire;
                String resultName;
                if (applyResult1.getTerm("ire") instanceof IdentifyingExpression) {
                    ire = (IdentifyingExpression)applyResult1.getTerm("ire");
                    resultName = "Result";
                } else if (applyResult1.getTerm("Result") instanceof IdentifyingExpression) {
                    ire = (IdentifyingExpression)applyResult1.getTerm("Result");
                    resultName= "ire";
                } else {
                    return queryResult;
                }
                MatchResult applyResult2 = SLPatternManip.match(pattern2,ire.as_formula());
                if (applyResult2 != null) {
                    ActionExpression act = (ActionExpression)applyResult2.getTerm("act");
                    if ( act instanceof ActionExpressionNode 
                    && agent.equals(((ActionExpressionNode)act).as_agent())
                    && agent.equals(applyResult2.getTerm("agent2")) 
                    && !agent.equals(applyResult2.getTerm("agent1"))) {
                        ListOfTerm list; 
                        ListOfMatchResults result = null;
                            if (ire instanceof IotaNode) {
                                list = standardCustomization.handleCFPIota((Variable)ire.as_term(), applyResult2.getFormula("proposition"), (ActionExpression)applyResult2.getTerm("act"),  applyResult2.getTerm("agent1"));
                                if (list != null && list.size() == 1) {
                                    if (Util.instantiateInMatchResult(applyResult1, resultName, list.get(0))) {
                                        result = new ListOfMatchResults();
                                        result.add(applyResult1);
                                        queryResult.setResult(result);
                                    }
                                } 
                                //queryResult.setResult(null);
                            } else if (ire instanceof AnyNode) {
                                list = standardCustomization.handleCFPAny((Variable)ire.as_term(), applyResult2.getFormula("proposition"), (ActionExpression)applyResult2.getTerm("act"),  applyResult2.getTerm("agent1"));
                                if (list != null && list.size() >= 1) {
                                    if (Util.instantiateInMatchResult(applyResult1, resultName, list.get(0))) {
                                        result = new ListOfMatchResults();
                                        result.add(applyResult1);
                                        queryResult.setResult(result);
                                    }
                                } 
                                //queryResult.setResult(null);
                            } else if (ire instanceof AllNode){
                                handleList(standardCustomization.handleCFPAll((Variable)ire.as_term(), applyResult2.getFormula("proposition"), (ActionExpression)applyResult2.getTerm("act"),  applyResult2.getTerm("agent1")),
                                        applyResult1, queryResult, resultName);
                            } else {
                                handleList(standardCustomization.handleCFPSome((Variable)ire.as_term(), applyResult2.getFormula("proposition"), (ActionExpression)applyResult2.getTerm("act"),  applyResult2.getTerm("agent1")),
                                        applyResult1, queryResult, resultName);
                            }
                            queryResult.setFilterApplied(true);
                            return queryResult;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryResult;
    }
    
    private void handleList(ListOfTerm list, MatchResult applyResult, QueryResult queryResult, String varName) {
        ListOfMatchResults result = new ListOfMatchResults();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                MatchResult match = (MatchResult)applyResult.getClone();
                if (Util.instantiateInMatchResult(match, varName, list.get(i))) {
                    result.add(match);
                } else {
                    queryResult.setResult(null);
                    return;
                }
            }
            queryResult.setResult(result);
        }
        //queryResult.setResult(null);
    }
    
    /**
     * @see jade.semantics.kbase.filter.KBQueryFilter#getObserverTriggerPatterns(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.Set)
     */
    public void getObserverTriggerPatterns(Formula formula, Set set) {
    }

}
