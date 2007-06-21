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
 * KBase.java
 * Created on 28 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;

import jade.semantics.interpreter.Finder;
import jade.semantics.kbase.observer.Observer;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.ListOfMatchResults;

/**
 * Definition of the belief base api. 
 * @author Vincent Pautret - France Telecom
 * @version 2004/11/30 Revision: 1.0
 * @version Date: 2006/12/21 Revision: 1.4 (Thierry Martinez)
 */
public interface KBase {
    
    /**
     * The KBase mechanism follows the decoration design pattern.
     * A KBase can be enclosed within another KBase, wich can
     * also be enclosed in another KBase and so on.  
     * @return the most enclosing KBase, on which to proceed assertion 
     * and query operations, which is also the KBase attached to the 
     * semantic capabilities of an agent. It can be this KBase.
     */
    public KBase getWrappingKBase();
    
    /**
     * A KBase stores the beliefs of only one agent, even if these beliefs
     * can concern beliefs of other agents.
     * @return the name of the agent believing the facts stored in this KBase. 
     */
    public Term getAgentName();

    /**
     * Modifies the agent the beliefs stored in this KBase belongs to.
     * @param agent the agent the beliefs of the KBase belongs to. 
     */
    public void setAgentName(Term agent);

	/**
     * Asserts a formula in the belief base.
     * @param formula a formula
     **/
    public void assertFormula(Formula formula);
    
    /**
     * Shortcut for the assertion of (not (B agent formula)). 
     * It retracts the formula given in parameter from the belief base. The 
     * formula to retract may include metavariables.
     * @param formula the formula to retract 
     */
    public void retractFormula(Formula formula);
	
    /**
     * Removes from the kowledge base all formulae recognized by the finder
     * @param finder a finder
     * @deprecated use retractFormula(Formula) instead
     */
    public void removeFormula(Finder finder);
    
    /**
     * Returns a list <code>ListOfTerm</code> of objects that satisfy a property 
     * belonging to the belief base. The method return <code>null</code> if the 
     * agent can not answer.
     * @return a list of solutions to the query 
     * @param expression an IdentifyingExpression
     **/
    public ListOfTerm queryRef(IdentifyingExpression expression);
    
    /**
     * Queries the knoweledge base. Returns a list of MatchResults. 
     * These MatchResults are solutions to the query.
     * If the methods returns <code>null</code>, that means there is no solution.
     * @return a list of solutions to the query
     * @param formula a formula 
     **/
    public ListOfMatchResults query(Formula formula);
    
    /**
     * Adds an observer to the kbase at the end of the list of observers. 
     * @param o the observer to add
     */
    public void addObserver(Observer o);
    
    /**
     * Removes from the kbase all the observers that are identified by the 
     * finder
     * @param finder a finder
     */
    public void removeObserver(Finder finder);
    
    /**
     * Removes from the kbase the observer given in parameter
     * @param obs the observer to be removed 
     */
    public void removeObserver(Observer obs);
	
	/**
	 * This operation causes all the contained observers to be updated,
	 * considering the given formula has changed. It is needed to 
	 * trigger the observers even when an assertion doesn't lead to
	 * a real assertion in the KBase.
	 */
	public void updateObservers(Formula formula);
	
    /**
     * Returns true if the formula given in parameter is closed on the domain of
     * values defined by the list of MatchResult given in parameter.
     * In details, it returns true if:
     * <ul>
     * <li>the formula is a mental attitude of the current agent;
     * <li>if the formula is an And Formula, the left part or the right part of
     * the formula should be closed;
     * <li>if the formula is an Or Formula, the left part and the right part of
     * the formula should be closed;
     * <li>in the other cases, the formula should be match one of the patterns
     * stored in the closed patterns list. In this case, the solutions on the
     * query on the formula should be the same as the domain of values.
     * </ul>
     * Otherwise, returns false.
     * @param pattern the pattern to be tested
     * @param values the domain of values
     * @return true if the pattern is closed on the given domain, false if not.
     */
    public boolean isClosed(Formula pattern, ListOfMatchResults values);
	
    /**
     * By calling this operation on a given pattern of predicate,
     * you assert that this predicate is closed, which means
     * the agent (owner of this kbase) knows all the values that
     * satisfy the predicate.
     * @param pattern the pattern of the predicate to be considered as closed
     */
    public void addClosedPredicate(Formula pattern);

	
    /**
     * Removes the assertion that a given predicate is closed.
     * @see jade.semantics.kbase.KBase#addClosedPredicate
     * @param finder the finder used to find the predicate
     */
    public void removeClosedPredicate(Finder finder);
    
    
	/**
	 * This operation returns all the content of the KBase as strings.
	 * It could be usefull for debug purpose.
	 * @return the content of the KBase.
	 */
	public String[] toStrings();
    
} 
