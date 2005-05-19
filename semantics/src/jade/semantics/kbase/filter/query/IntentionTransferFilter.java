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
 * IntentionTransferFilter.java
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
 * This filter applies whenever an agent <i>agent1</i> expresses the intention 
 * of its own towards the agent Jade agent <i>agent2</i>.
 * For example, this filter may be applied when the Jade agent receives a 
 * <code>Request</code>, a <code>Query-if</code>, <code>Query-ref</code>, a
 * <code>CallForProposal</code> message or a <code>Confirm</code>, <code>Disconfirm</code>,
 * or <code>Inform</code> message with the appropriate content.  
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class IntentionTransferFilter extends KBQueryFilter {
    
    /**
     * The filter manager
     */
    StandardCustomization standardCustomization;

    /**
     * Pattern that must match to apply the filter
     */
    Formula pattern;
   
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Constructor
     * @param stdkbFilterMgt the filter manager
     */
    public IntentionTransferFilter(StandardCustomization standardCustomization) {
        this.standardCustomization = standardCustomization;
        pattern = SLPatternManip.fromFormula("(or (not (I ??agent1 ??goal)) (I ??agent2 ??goal))");
    } // End of IntentionTransferFilter/1

    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * @see jade.semantics.kbase.filter.KBQueryFilter#isApplicable(jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.Term)
     */
    public boolean isApplicable(Formula formula, Term agent) {
        try {
            applyResult = SLPatternManip.match(pattern,formula);
            if (applyResult != null 
                   && agent.equals(applyResult.getTerm("??agent2")) 
                   && !agent.equals(applyResult.getTerm("??agent1"))
                    ) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    } // End of isApplicable/2
 
    /**
     * Returns true if the method <code>StandardKBFilterMgt.acceptIntentionTransfer</code>
     * returns true, false if not.
     * @see jade.semantics.kbase.filter.KBQueryFilter#apply(jade.lang.sl.grammar.Formula)
     */
    public Bindings apply(Formula formula) {
        try {
            if (standardCustomization.acceptIntentionTransfer(applyResult.getFormula("??goal"), applyResult.getTerm("??agent1")))
                return new BindingsImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of apply/1

} // End of class IntentionTransfer
