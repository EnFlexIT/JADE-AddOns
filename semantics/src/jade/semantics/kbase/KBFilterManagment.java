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
 * KBFilterManagment.java
 * Created on 10 déc. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;

import jade.semantics.interpreter.Finder;
import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.kbase.filter.KBQueryRefFilter;

/**
 * Interface that offers methods to manage the filters.
 * @author Vincent Pautret
 * @version Date: 10 déc. 2004 11:20:36 Revision: 1.0
 */
public interface KBFilterManagment {

    /**
     * Adds a formula filter for the assertion operation.
     * @param filter an assertion filter
     */
    public void addKBAssertFilter(KBAssertFilter filter);
    
    /**
     * Adds a formula filter for the assertion operation at the sepcified index.
     * @param filter an assertion filter
     * @param index the insertion position
     */
    public void addKBAssertFilter(KBAssertFilter filter, int index);

    /**
     * Removes some specified AssertFilters from the KB.
     * @param filterIdentifier a filter specification
     */
    public void removeKBAssertFilter(Finder filterIdentifier);
    
    /**
     * Adds a formula filter for the query operation.
     * @param filter a query filter
     */
    public void addKBQueryFilter(KBQueryFilter filter);

    /**
     * Adds a formula filter for the query operation at the specified index.
     * @param filter a query filter
     * @param index the insertion position
     */
    public void addKBQueryFilter(KBQueryFilter filter, int index);

    /**
     * Removes some specified QueryFilters from the KB.
     * @param filterIdentifier a filter specification
     */
    public void removeKBQueryFilter(Finder filterIdentifier);
    
    /**
     * Adds a formula filter for the query-ref operation.
     * @param filter an query-ref filter
     */
    public void addKBQueryRefFilter(KBQueryRefFilter filter);

    /**
     * Adds a formula filter for the query-ref operation at the specified index.
     * @param filter an query-ref filter
     * @param index the insertion position
     */
    public void addKBQueryRefFilter(KBQueryRefFilter filter, int index);

    /**
     * Removes some specified QueryRefFilters from the KB.
     * @param filterIdentifier a filter specification
     */
    public void removeKBQueryRefFilter(Finder filterIdentifier);
    
    /**
     * Adds a list of filters to the KBase (useful for defining specific predicate management)
     * @param filtersDefinition the list of filters
     */
    public void addFiltersDefinition(FiltersDefinition filtersDefinition);
    
} // End of interface KBFilterManagment
