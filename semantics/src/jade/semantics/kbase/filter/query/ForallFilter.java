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
 * ForallFilter.java
 * Created on 24 oct. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.query;

import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.Set;

/**
 * This filter applies when an "forall formula" is asserted in the belief base.
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class ForallFilter extends KBQueryFilter {

    /**
     * Pattern that must match to apply the filter
     */
    private Formula pattern1;
    private Formula pattern2;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a new Filter on the pattern (forall ??var ??phi)
     */
    public ForallFilter() {
        pattern1 = SLPatternManip.fromFormula("(B ??agent (forall ??var ??phi))");
        pattern2 = SLPatternManip.fromFormula("(forall ??var ??phi)");
    } // End of ForallFilter/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /** 
     * Returns true as first element if the formula matches the pattern 
     * (forall ??var ??phi) and if the formula "??phi" is a mental attitude of the
     * agent given in parameter. In this case, the second element is an emplty 
     * list of MatchResults if the answer to the query on the opposite of incoming 
     * formula (not (phi)) is null and if (not (phi)) is a closed formula. 
     * The second element is null in the others cases.
     * If the filter returns false as first element, the second element is null.
     * @param formula a formula on which the filter is tested
     * @param agent a term that represents the agent is trying to apply the filter
     * @return an array with a Boolean meaning the applicability of the filter,
     * and a ListOfMatchResults that is the result of performing the filter.
     */
    public QueryResult apply(Formula formula, Term agent) {
        QueryResult queryResult = new QueryResult();
        try {
            MatchResult applyResult = SLPatternManip.match(pattern1,formula);
            if (applyResult == null || !agent.equals(applyResult.getTerm("agent"))) {
                applyResult = SLPatternManip.match(pattern2,formula);
            }

            if (applyResult != null) {
                Formula form = (Formula)SLPatternManip.toPattern(new NotNode(applyResult.getFormula("phi")).getSimplifiedFormula(), 
                        applyResult.getVariable("var"), applyResult.getVariable("var").lx_name());
                if (myKBase.isClosed(form, null) && (myKBase.query(form)) == null) {
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
    } // End of apply/2
    
    /**
     * By default, this method does nothing. 
     * @param formula an observed formula
     * @param set set of patterns. Each pattern corresponds to a kind a formula
     * which, if it is asserted in the base, triggers the observer that
     * observes the formula given in parameter.
     */
    public void getObserverTriggerPatterns(Formula formula, Set set) {
    }
}