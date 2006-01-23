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
 * QueryResult.java
 * Created on 16 nov. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.query;

import jade.semantics.lang.sl.tools.ListOfMatchResults;

/**
 * This class corresponds to a data structure representing the result of 
 * a queryFilter. This structure gathers two attributes :
 * <ul>
 * <li> a boolean that indicates if the filter is applicable or not;
 * <li> a list of matchResults, corresponding to the result of the filter in 
 * case of the filter is applicable. 
 * </ul> 
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class QueryResult {

    /**
     * True if the filter is applicable, false if not. 
     */
    boolean filterApplied;
    
    /**
     * Result of the filter performance.
     */
    ListOfMatchResults result;
    
    /**
     * Constructor.
     * @param b the value of the applicability
     * @param list the list of result
     */
    public QueryResult(boolean b, ListOfMatchResults list) {
        filterApplied = b;
        result = list;
    }

    /**
     * The applicability is set to false and the result to null.
     */
    public QueryResult() {
        this(false, null);
    }

    /**
     * 
     * @return true if the filter is apllicable, false if not.
     */
    public boolean isFilterApplied() {
        return filterApplied;
    }
    
    /**
     * Sets the value of the applicability.
     * @param filterApplied true if the filter should be applied, false if not.
     */
    public void setFilterApplied(boolean filterApplied) {
        this.filterApplied = filterApplied;
    }
    /**
     * Returns the result of the filter performance.
     * @return a list of MatchResult
     */
    public ListOfMatchResults getResult() {
        return result;
    }
    /**
     * Sets the result of the filter performance.
     * @param result the result of the filter performance
     */
    public void setResult(ListOfMatchResults result) {
        this.result = result;
    }
   
}
