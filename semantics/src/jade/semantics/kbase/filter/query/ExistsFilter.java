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
 * ExistsFilter.java
 * Created on 24 oct. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.query;

import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.Set;

/**
 * This filter applies when an "exists formula" is asserted in the belief Base.
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class ExistsFilter extends KBQueryFilter {
    
    
    /**
     * Pattern that must match to apply the filter
     */
    private Formula pattern;
    private Formula pattern2;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a new Filter on the pattern (B ??agt (exists ??var ??phi))
     */
    public ExistsFilter() {
        pattern = SLPatternManip.fromFormula("(B ??agt (exists ??var ??phi))");
        pattern2 = SLPatternManip.fromFormula("(exists ??var (B ??agt ??phi))");
    } // End of ExistsFilter/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /** 
     * Returns true as first element if the formula matches the pattern 
     * (B ??agt (exists ??var ??phi)) and if the current agent is the "agent" 
     * of the pattern. In this case, the second element is a list of MatchResults
     * that corresponds to the answer to the query on the incoming formula. In all 
     * the MatchResults of the list, the meta variable corresponding to the 
     * variable "??var" of the pattern are deleted.
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
            if (applyResult == null) {
                applyResult = SLPatternManip.match(pattern2,formula);
            }
            if (applyResult != null && applyResult.getTerm("agt").equals(agent)) {
                Formula form = (Formula)SLPatternManip.toPattern(applyResult.getFormula("phi"), 
                        applyResult.getVariable("var"), applyResult.getVariable("var").lx_name());
                ListOfMatchResults list = myKBase.query(form);
                cleanMatches(list, applyResult.getVariable("var").lx_name());
                queryResult.setResult(list);
                queryResult.setFilterApplied(true);
                return queryResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryResult;
    } // End of apply/2
    
    /**
     * Deletes from all MatchResult of the ListOfMatchResults given in parameter,
     * the variable that corresponds to the string given in parameter. 
     * @param list a list of MatchResult 
     * @param varName the name of the variable that should be deletes in all the
     * MatchResults of the given list.
     */
    private void cleanMatches(ListOfMatchResults list, String varName) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j< ((MatchResult)list.get(i)).size(); j++) {
                    if (((MetaTermReferenceNode)((MatchResult)list.get(i)).get(j)).lx_name().equals(varName)) {
                        ((MatchResult)list.get(i)).remove(((MatchResult)list.get(i)).get(j));
                        if (((MatchResult)list.get(i)).size() == 0) list.remove((list.get(i)));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Adds the formula corresponding to phi (see the patterns) where the variable
     * is transform into a MetaVariable. 
     * @param formula an observed formula
     * @param set set of patterns. Each pattern corresponds to a kind a formula
     * which, if it is asserted in the base, triggers the observer that
     * observes the formula given in parameter.
     */
    public void getObserverTriggerPatterns(Formula formula, Set set) {
        try {
            MatchResult applyResult = SLPatternManip.match(pattern,formula);
            if (applyResult == null) {
                applyResult = SLPatternManip.match(pattern2,formula);
            }
            if (applyResult != null) {
                set.add(SLPatternManip.toPattern(applyResult.getFormula("phi"), applyResult.getVariable("var"), applyResult.getVariable("var").lx_name()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} // End of class ExistsFilter
