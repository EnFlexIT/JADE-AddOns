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
 * BeliefTransferFilter.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.query;

import jade.semantics.interpreter.StandardCustomization;
import jade.semantics.kbase.Bindings;
import jade.semantics.kbase.BindingsImpl;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Is the semantic agent cooperative towards the agent <i>agent</i> regarding a specific belief?
 * This question is asked whenever whenever an agent <i>agent</i> expresses a piece of 
 * information towards the semantic agent.
 * For example, this filter may be applied when the Jade agent receives a 
 * <code>RequestWhenever</code>, a <code>Refuse</code>, <code>Subscribe</code>, a
 * <code>Propose</code>, a <code>Confirm</code>, a <code>Failure</code>, 
 * <code>Disconfirm</code>, or <code>Inform</code> message with the appropriate
 * content.  
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class BeliefTransferFilter extends KBQueryFilter {
    
    /**
     * The filter manager
     */
    private StandardCustomization standardCustomization;
    
    /**
     * Pattern that must match to apply the filter
     */
    private Formula pattern = 
        SLPatternManip.fromFormula("(or (not (I ??agent1 (B ??agent2 ??belief))) (B ??agent2 ??belief))");   
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new filter
     * @param standardCustomization the customization object of the agent that 
     * owns this filter
     */
    public BeliefTransferFilter(StandardCustomization standardCustomization) {
        this.standardCustomization = standardCustomization;
    } // End of BeliefTransferFilter/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    public boolean isApplicable(Formula formula, Term agent) {
        try {
            applyResult = SLPatternManip.match(pattern,formula);
            if (applyResult != null 
                    && agent.equals(applyResult.getTerm("agent2")) 
                    && !agent.equals(applyResult.getTerm("agent1")))
                return true;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    } // End of isApplicable/2
    
    /**
     * Returns true if the method {@link StandardCustomization#acceptBeliefTransfer(Formula, Term)} returns true,
     * false if not.
     * @inheritDoc
     */
    public Bindings apply(Formula formula) {
        try {
            if (standardCustomization.acceptBeliefTransfer(applyResult.getFormula("belief"), applyResult.getTerm("agent1")))
                return new BindingsImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of apply/1
    
} // End of class BeliefTransferFilter
