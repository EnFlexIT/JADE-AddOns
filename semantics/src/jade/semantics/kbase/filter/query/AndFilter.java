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
 * AndFilter.java
 * Created on 23 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.query;

import jade.semantics.kbase.Bindings;
import jade.semantics.kbase.BindingsImpl;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * This filter applies when an "and formula" is asserted in the Knowledge Base.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class AndFilter extends KBQueryFilter {
    
    /**
     * First part of the formula
     */
    private Formula phi ;
    
    /**
     * Second part of the formula
     */
    private Formula psi;
    
    
    /**
     * Pattern that must match to apply the filter
     */
    private Formula pattern;
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new Filter
     */
    public AndFilter() {
        pattern = SLPatternManip.fromFormula("(and ??phi ??psi)");
    } // End of AndFilter/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /** 
     * @inheritDoc
     */
    public boolean isApplicable(Formula formula, Term agent) {
        try {
            applyResult = SLPatternManip.match(pattern,formula);
            if (applyResult != null) {
                phi = applyResult.getFormula("??phi");
                psi = applyResult.getFormula("??psi");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    } // End of isApplicable/2
    
    /**
     * 
     * Returns true if the two parts of the formula are in the base, false if 
     * not.
     * @inheritDoc
     */
    public Bindings apply(Formula formula) {
        if ((myKBase.query(phi) != null) && (myKBase.query(psi) != null))
            return new BindingsImpl();
        return null;
    } // End of apply/1
    
} // End of class AndFilter
