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
 * GetProposalFilter.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.queryref;


import jade.semantics.interpreter.StandardCustomization;
import jade.semantics.kbase.filter.KBQueryRefFilter;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * This filter applies whenever an agent <i>agent1</i> is calling for a proposal 
 * (consisting in performing the action <i>act</i> under the condition 
 * <i>condition</i>) towards the Jade agent <i>agent2</i>.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class GetProposalFilter extends KBQueryRefFilter {

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
    public GetProposalFilter(StandardCustomization standardCustomization) {
        this.standardCustomization = standardCustomization;
        pattern = SLPatternManip.fromFormula("(or (not (I ??agent1 (done (action ??agent2 ??act) ??proposition))) (I ??agent2 (done (action ??agent2 ??act) ??proposition)))");
    } // End of GetProposalFilter/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * @see jade.semantics.kbase.filter.KBQueryRefFilter#isApplicable(jade.lang.sl.grammar.IdentifyingExpression, jade.lang.sl.grammar.Term)
     */
    public boolean isApplicable(IdentifyingExpression ide, Term agent) {
        try {
			applyResult = SLPatternManip.match(pattern,ide.as_formula());
            return (applyResult != null 
					&& agent.equals(applyResult.getTerm("??agent2")) 
					&& !agent.equals(applyResult.getTerm("??agent1")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    } // End of isApplicable/2

    /**
     * Depending of the class of the ide, retruns true if <code>StandardCustomization.callForProposalIota</code>,
     * or <code>StandardKBFilterMgt.callForProposalAny</code>, or 
     * <code>StandardCustomization.callForProposalAll</code> returns true, false 
     * if not.
     * @see jade.semantics.kbase.filter.KBQueryRefFilter#apply(jade.lang.sl.grammar.IdentifyingExpression)
     */
    public ListOfTerm apply(IdentifyingExpression ide) {
        try {
	        if (ide instanceof IotaNode) {
	            return standardCustomization.callForProposalIota((Variable)ide.as_term(), applyResult.getFormula("??proposition"), applyResult.getActionExpression("??act"),  applyResult.getTerm("??agent1"));
	        } else if (ide instanceof AnyNode) {
	            return standardCustomization.callForProposalAny((Variable)ide.as_term(), applyResult.getFormula("??proposition"), applyResult.getActionExpression("??act"),  applyResult.getTerm("??agent1"));
	        } else {
	            return standardCustomization.callForProposalAll((Variable)ide.as_term(), applyResult.getFormula("??proposition"), applyResult.getActionExpression("??act"),  applyResult.getTerm("??agent1"));
	        }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } // End of apply/1

} // End of class GetProposalFilter
