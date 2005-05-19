/*
 * KBase_Filter.java
 * Created on 21 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;

import jade.semantics.interpreter.Finder;
import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.kbase.filter.KBQueryRefFilter;

/**
 * @author Vincent Pautret
 * @version 
 */
public interface KBase_Filter extends KBase {
    
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
     * @param queryFilter
     */
    public void addKBQueryFilter(KBQueryFilter queryFilter);
    
    /**
     * Removes the query filters that are identified by 
     * the specified finder. 
     * @param finder
     */
    public void removeKBQueryFilter(Finder finder);
    
    /**
     * Adds a queryRef filter to the knowledge base
     * @param queryRefFilter
     */
    public void addKBQueryRefFilter(KBQueryRefFilter queryRefFilter);
    
    /**
     * Removes the queryref filters that are identified by 
     * the specified finder. 
     * @param finder
     */
    public void removeKBQueryRefFilter(Finder finder);
    
} // End of interface KBase_Filter
