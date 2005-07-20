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
 * KBQueryRefFilter.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter;

import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;

/**
 * This abstact class provides methods the developer has to override to create
 * a new filter for querying the knowledge base about the presence of objects 
 * verifying a formula 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public abstract class KBQueryRefFilter extends KBFilter {
    
    /**
     * Matching result between the pattern of the filter and the formula to query
     */
    protected MatchResult applyResult = null;
    
    /**
     * Returns true if the filter is applicable to the formula, false if not.
     * @param ide an identifying expression
     * @param agent a term that represent the agent is trying to apply the filter
     * @return true if the filter is applicable, false if not.
     */
    public abstract boolean isApplicable(IdentifyingExpression ide, Term agent);
    
    /**
     * Peforms the filter on the given identifying expression
     * @param ide an identifying expression
     * @return a list of solution
     */
    public abstract ListOfTerm apply(IdentifyingExpression ide);
    
} // End of KBQueryRefFilter
