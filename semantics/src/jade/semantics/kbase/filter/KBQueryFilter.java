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
 * KBQueryFilter.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter;

import jade.semantics.kbase.filter.query.QueryResult;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.util.leap.Set;

/**
 * This abstact class provides methods the developer has to override to create
 * a new filter for querying the belief base about a formula.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public abstract class KBQueryFilter extends KBFilter {
    
    /**
     * Returns an array of size 2, with a Boolean at the first index and a 
     * ListOfMatchResults at the last index. The value of the Boolean is true
     * if the filter is applicable to the formula, false if not. 
     * The ListOfMatchResults is the result of performing the filter on the 
     * specified formula (could be null).   
     * @param formula a formula on which the filter is tested
     * @param agent a term that represents the agent is trying to apply the filter
     * @return an array with a Boolean meaning the applicability of the filter,
     * and a ListOfMatchResults that is the result of performing the filter. 
     */
    public abstract QueryResult apply(Formula formula, Term agent);
    
    /**
     * Fills the set given in parameter with the patterns manipulated by this 
     * filter and likely to trigger the observers which observe the formula given
     * in parameter.
     * @param formula an observed formula
     * @param set set of patterns. Each pattern corresponds to a kind a formula
     * which, if it is asserted in the base, triggers the observer that
     * observes the formula given in parameter.
     */
    public abstract void getObserverTriggerPatterns(Formula formula, Set set);
} // End of class KBQueryFilter
