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
 * KBAssertFilter.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter;

import jade.semantics.lang.sl.grammar.Formula;

/**
 * This class provides methods the developer has to subclass to create
 * a new filter for asserting belief into the belief base 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class KBAssertFilter extends KBFilter {
    
    /**
     * True if the method <code>afterAssert</code> should be apply, false if not.
     */
    public boolean mustApplyAfter;
    
    /**
     * Applies the filter before asserting the formula into the KBase.
     * This method may modify the formula to assert by returning another formula.
     * By default, it returns the same formula. The boolean <code>mustApplyAfter</code>
     * is set to true.<br> 
     * Should be overridden.
     * @param formula the formula to assert
     * @return the formula to assert into the KBase
     */
    public Formula beforeAssert(Formula formula) {
        mustApplyAfter = true;
        return formula;
    } // End of beforeAssert/1
    
    /**
     * Applies the filter after the given formula has been asserted into the KBase.
     * By default, does nothing.
     * @param formula the formula that has been asserted into the KBase.
     */
    public void afterAssert(Formula formula) {
    } // End of AfterAssert/1
    
} // End of class KBAssertFilter
