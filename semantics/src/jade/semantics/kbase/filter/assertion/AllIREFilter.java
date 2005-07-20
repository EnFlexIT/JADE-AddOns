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
 * AllIREFilter.java
 * Created on 1 juil. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.assertion;

import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Filter for the identifying expression of the form <i>(= (all ??X ??formula) ??set)</i>.
 * Asserts in the knowledge base each element which appears in the set.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/07/01 Revision: 1.0
 */
public class AllIREFilter extends KBAssertFilter {
    
    /**
     * Pattern that must match to apply the filter adapter
     */
    protected Formula pattern;
    
    /**
     * Pattern used to assert formula in the knowledge base
     */
    private Formula bPattern;
    
    /**
     * Constructor
     */
    public AllIREFilter() {
        pattern = SLPatternManip.fromFormula("(B ??agent (= (all ??X ??formula) ??set))");
        bPattern = SLPatternManip.fromFormula("(B ??agent ??formula)" ); 
    } // End of AllIREFilter/0
    
    /**
     * If the filter is applicable, asserts in the knowledge base each element 
     * which appears in the set, and returns a TrueNode.
     * @param formula a formula to assert
     * @return TrueNode if the filter is applicable, the given formula in the 
     * other case.
     */
    public final Formula beforeAssert(Formula formula) {
        mustApplyAfter = false;
        try {
            MatchResult applyResult = SLPatternManip.match(pattern, formula);
            if (applyResult != null && 
                    applyResult.getTerm("set") instanceof TermSetNode && 
                    applyResult.getTerm("X") instanceof VariableNode) {
                
                Formula formulaPattern = (Formula)SLPatternManip.toPattern(applyResult.getFormula("formula"), (VariableNode)applyResult.getTerm("X"));
                myKBase.removeAllFormulae(formulaPattern);
                ListOfTerm list = ((TermSetNode)applyResult.getTerm("set")).as_terms();
                for(int i = 0; i < list.size(); i++) {
                    Formula toBelieve = (Formula)SLPatternManip.instantiate(formulaPattern, 
                            "X", list.get(i));
                    myKBase.assertFormula((Formula)SLPatternManip.instantiate(bPattern,
                            "agent", applyResult.getTerm("agent"),
                            "formula", toBelieve));
                }
                return new TrueNode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formula;
    } // End of beforeAssert/1
    
} // End of class AllIREFilter
