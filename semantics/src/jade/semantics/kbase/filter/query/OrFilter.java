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
 * OrFilter.java
 * Created on 23 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.query;

import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.Set;

/**
 * This filter applies when an "or formula" is asserted in the KBase.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class OrFilter extends KBQueryFilter {
    
    /**
     * Pattern that must match to apply the filter
     */
    private Formula pattern;
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new Filter on the pattern (B ??agent (or ??phi ??psi))
     */
    public OrFilter() {
        pattern = SL.fromFormula("(B ??agent (or ??phi ??psi))");
    } // End of OrFilter/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Returns true as first element if the formula matches the pattern 
     * (B ??agent (or ??phi ??psi)), false if not. If true, the ListOfMatchResults
     * contains the union of the whole of solutions of the first part of the formula and 
     * the whole of solutions of the second part of the formula.
     * If the filter returns false as first element, the second element is null.
     * @param formula a formula on which the filter is tested
     * @param agent a term that represents the agent is trying to apply the filter
     * @return an array with a Boolean meaning the applicability of the filter,
     * and a ListOfMatchResults that is the result of performing the filter. 
     */
    public QueryResult apply(Formula formula, Term agent) {
        QueryResult queryResult = new QueryResult();
        try {
            MatchResult applyResult = SL.match(pattern,formula);
            if (applyResult != null && applyResult.getTerm("agent").equals(agent)) {
                ListOfMatchResults phiList = myKBase.query(applyResult.getFormula("phi"));
                ListOfMatchResults psiList = myKBase.query(applyResult.getFormula("psi"));
                if (phiList == null) {
                    queryResult.setResult(psiList);
                } else if (psiList == null) {
                    queryResult.setResult(phiList);
                } else {
                    queryResult.setResult(phiList.union(psiList));    
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
     * Adds in the set, the patterns for the formula phi and for the formula
     * psi.
     * @param formula an observed formula
     * @param set set of patterns. Each pattern corresponds to a kind a formula
     * which, if it is asserted in the base, triggers the observer that
     * observes the formula given in parameter.
     */
    public boolean getObserverTriggerPatterns(Formula formula, Set set) {
 	   MatchResult match = SL.match(formula, pattern);
 	   if (match != null) {
 		   try {
			   myKBase.getObserverTriggerPatterns(match.getFormula("phi"), set);
			   myKBase.getObserverTriggerPatterns(match.getFormula("psi"), set);
 			   return false;
 		   } catch (SL.WrongTypeException wte) {
 			   wte.printStackTrace();
 		   }
 	   }
 	   return true;
    }
} // End of class OrFilter