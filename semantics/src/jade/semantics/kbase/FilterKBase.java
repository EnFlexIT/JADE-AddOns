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
 * FilterKBase.java
 * Created on 21 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;

import jade.semantics.interpreter.Finder;
import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.kbase.filter.KBQueryRefFilter;

/**
 * Interface that defines a knowledge base based upon filters. These filters are
 * used to access the base.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/21 Revision: 1.0
 */
public interface FilterKBase extends KBase {
    
    /**
     * Adds an assert filter to the knowledge base
     * @param assertFilter an assert filter
     */
    public void addKBAssertFilter(KBAssertFilter assertFilter);
    
    /**
     * Removes the assert filters that are identified by 
     * the specified finder. 
     * @param finder a finder
     */
    public void removeKBAssertFilter(Finder finder);
    
    /**
     * Adds a query filter to the knowledge base
     * @param queryFilter a queryFilter
     */
    public void addKBQueryFilter(KBQueryFilter queryFilter);
    
    /**
     * Removes the query filters that are identified by 
     * the specified finder. 
     * @param finder a finder
     */
    public void removeKBQueryFilter(Finder finder);
    
    /**
     * Adds a queryRef filter to the knowledge base
     * @param queryRefFilter a queryRefFilter
     */
    public void addKBQueryRefFilter(KBQueryRefFilter queryRefFilter);
    
    /**
     * Removes the queryref filters that are identified by 
     * the specified finder. 
     * @param finder a finder
     */
    public void removeKBQueryRefFilter(Finder finder);
    
    /**
     * Adds a list of filters to the KBase (useful for defining specific predicate management)
     * @param filtersDefinition the list of filters
     */
    public void addFiltersDefinition(FiltersDefinition filtersDefinition);

} // End of interface FilterKBase
