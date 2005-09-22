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
 * FiltersDefinition.java
 * Created on 15 déc. 2004
 * Author : louisvi
 */
package jade.semantics.kbase;

import jade.semantics.kbase.filter.KBFilter;
import jade.util.leap.ArrayList;

/**
 * Sorted list of <code>FilterDefinition</code>.
 * @author Vincent Louis - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0
 */
public class FiltersDefinition extends ArrayList {
    
    /**
     * Creates a new FilterDefinition.
     */
    public FiltersDefinition() {}
    
    /**
     * Creates a new FilterDefinition witha list of filters.
     * @param list list of <code>KBFilter</code>
     */
    public FiltersDefinition(ArrayList list) {
        super();
        for (int i=0 ; i<list.size() ; i++) {
            add(new FilterDefinition(-1, (KBFilter)list.get(i)));
        }
    } // End of FiltersDefinition/1
    
    /**
     * Adds a new filter in the list.
     * @param index the specified index to add the filter
     * @param filter a <code>KBFilter</code>
     */
    public void defineFilter(int index, KBFilter filter) {
        add(new FilterDefinition(index, filter));
    } // End of defineFilter/2
    
    /**
     * Adds a new filter at the begining of the list
     * @param filter a <code>KBFilter</code>
     */
    public void defineFilter(KBFilter filter) {
        defineFilter(-1, filter);
    } // End of defineFilter/1
    
    /**
     * Returns the <code>FilterDefinition</code> at the specified index
     * @return the <code>FilterDefinition</code> at the specified index
     * @param i index if the <code>FilterDefinition</code> 
     */
    public FilterDefinition getFilterDefinition(int i) {
        return (FilterDefinition)super.get(i);
    } // End of get/1
    
} // End of FiltersDefinition
