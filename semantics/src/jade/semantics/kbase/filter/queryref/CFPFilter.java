/*****************************************************************
 JADE - Java Agent DEvelopment Framework is a framework to develop 
 multi-agent systems in compliance with the FIPA specifications.
 Copyright (C) 2004 France T�l�com
 
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
 * CFPFilter.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.queryref;


import jade.semantics.interpreter.StandardCustomization;
import jade.semantics.kbase.filter.KBQueryRefFilter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
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
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class CFPFilter extends KBQueryRefFilter {
    
    /**
     * The filter manager
     */
    private StandardCustomization standardCustomization;
    
    /**
     * Pattern that must match to apply the filter
     */
    private Formula pattern;
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new Filter. Instantiates the pattern.
     * @param standardCustomization the customization object of the agent that 
     * owns this filter
     */
    public CFPFilter(StandardCustomization standardCustomization) {
        this.standardCustomization = standardCustomization;
        pattern = SLPatternManip.fromFormula("(or (not (I ??agent1 (done ??act ??proposition))) (I ??agent2 (done ??act ??proposition)))");
    } // End of CFPFilter/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Returns true if the formula part of the given identifying expression
     * matches the pattern 
     * <i>(or (not (I ??agent1 (done ??act ??proposition))) (I ??agent2 (done ??act ??proposition)))</i>,
     *  if the current agent is the agent of the action expression <i>act</i>, if the 
     *  current agent is <i>agent2</i>, and if the current agent is not <i>agent1</i>.
     * @inheritDoc
     */
    public boolean isApplicable(IdentifyingExpression ide, Term agent) {
        try {
            applyResult = SLPatternManip.match(pattern,ide.as_formula());
            if (applyResult != null) {
                ActionExpression act = (ActionExpression)applyResult.getTerm("act");
                return act instanceof ActionExpressionNode 
                && agent.equals(((ActionExpressionNode)act).as_agent())
                && agent.equals(applyResult.getTerm("agent2")) 
                && !agent.equals(applyResult.getTerm("agent1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    } // End of isApplicable/2
    
    /**
     * 
     * Depending of the class of the ide, returns true if {@link StandardCustomization#handleCFPIota(Variable, Formula, ActionExpression, Term)},
     * or {@link StandardCustomization#handleCFPAny(Variable, Formula, ActionExpression, Term)}, or 
     * {@link StandardCustomization#handleCFPAll(Variable, Formula, ActionExpression, Term)}, <code>null</code> if an exception
     * occurs. 
     * @inheritDoc
     */
    public ListOfTerm apply(IdentifyingExpression ide) {
        try {
            if (ide instanceof IotaNode) {
                return standardCustomization.handleCFPIota((Variable)ide.as_term(), applyResult.getFormula("proposition"), (ActionExpression)applyResult.getTerm("act"),  applyResult.getTerm("agent1"));
            } else if (ide instanceof AnyNode) {
                return standardCustomization.handleCFPAny((Variable)ide.as_term(), applyResult.getFormula("proposition"), (ActionExpression)applyResult.getTerm("act"),  applyResult.getTerm("agent1"));
            } else {
                return standardCustomization.handleCFPAll((Variable)ide.as_term(), applyResult.getFormula("proposition"), (ActionExpression)applyResult.getTerm("act"),  applyResult.getTerm("agent1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } // End of apply/1
    
} // End of class CFPFilter
