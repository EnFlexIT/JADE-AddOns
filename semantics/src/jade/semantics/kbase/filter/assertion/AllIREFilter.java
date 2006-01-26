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

import jade.semantics.interpreter.Finder;
import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Filter for the identifying expression of the form <i>(= (all ??X ??formula) ??set)</i>
 * or <i>(= (iota ??X ??formula) ??set)</i>.
 * Asserts in the belief base each element which appears in the set for the 
 * first pattern and the single value for the second pattern.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/07/01 Revision: 1.0
 */
public class AllIREFilter extends KBAssertFilter {
    
    /**
     * Pattern used to assert formula in the belief base
     */
   // private Formula bPattern;
    /**
     * Pattern that must match to apply the filter
     */
    protected Formula generalPattern;
    
    protected Formula generalNotPattern;
    
    protected Formula formulaPattern;
    
    protected Formula closedPattern;
    
    protected Term termPattern;
    
    private ListOfNodes listOfNodes;
    
    private Finder finder;
    /**
     * Constructor of the filter. Instantiates the patterns.
     */
    public AllIREFilter() {
        generalPattern = SLPatternManip.fromFormula("(B ??agent (= ??ide ??phi))");
        generalNotPattern = SLPatternManip.fromFormula("(B ??agent (not (= ??ide ??phi)))");
     //   bPattern = SLPatternManip.fromFormula("(B ??agent ??formula)" ); 
        finder = new Finder() {
            public boolean identify(Object object) {
                 if (object instanceof Formula) {
                     return SLPatternManip.match(formulaPattern, (Formula)object) != null;
                 }
                 return false;
            }
        };
    } // End of AllIREFilter/0
    
    /**
     * If the filter is applicable, asserts in the belief base each element 
     * which appears in the set, and returns a <code>TrueNode</code>.
     * @param formula a formula to assert
     * @return TrueNode if the filter is applicable, the given formula in the 
     * other cases.
     */
    public final Formula apply(Formula formula) {
        mustApplyAfter = false;
        try {
            MatchResult applyResult = SLPatternManip.match(generalPattern, formula);
            if (applyResult != null && applyResult.getTerm("ide") instanceof IdentifyingExpression && 
                    ((IdentifyingExpression)applyResult.getTerm("ide") instanceof AllNode ||
                     (IdentifyingExpression)applyResult.getTerm("ide") instanceof IotaNode)) {
                getPatterns(applyResult);
                return generalPatternProcess(formula, applyResult);
            } else {            
                applyResult = SLPatternManip.match(generalNotPattern, formula);
                if (applyResult != null && applyResult.getTerm("ide") instanceof IdentifyingExpression &&
                        ((IdentifyingExpression)applyResult.getTerm("ide") instanceof AllNode ||
                         (IdentifyingExpression)applyResult.getTerm("ide") instanceof IotaNode)) {
                    getPatterns(applyResult);
                    return generalNotPatternProcess(formula, applyResult);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formula;
    } // End of beforeAssert/1
    
    
    private Formula generalPatternProcess(Formula formula, MatchResult applyResult) {
        try {
            myKBase.retractFormula(formulaPattern);
            myKBase.addClosedPredicate(closedPattern);
            if (((IdentifyingExpression)applyResult.getTerm("ide")) instanceof AllNode &&
                    applyResult.getTerm("phi") instanceof TermSetNode) {
                ListOfTerm list = ((TermSetNode)applyResult.getTerm("phi")).as_terms();
                for(int i = 0; i < list.size(); i++) {
                    Formula toBelieve = formulaPattern;
                    MatchResult termMatchResult = SLPatternManip.match(termPattern, list.get(i));
                    for (int j = 0; j < listOfNodes.size(); j++) {
                        toBelieve = (Formula)SLPatternManip.instantiate(toBelieve, 
                                ((VariableNode)listOfNodes.get(j)).lx_name(), termMatchResult.getTerm(((VariableNode)listOfNodes.get(j)).lx_name()));
                    }
                    myKBase.assertFormula(new BelieveNode(applyResult.getTerm("agent"), toBelieve));
                    //return new BelieveNode(applyResult.getTerm("agent"), toBelieve);
                }
                return new TrueNode();
            } else {
                Formula toBelieve = formulaPattern;
                MatchResult termMatchResult = SLPatternManip.match(termPattern, applyResult.getTerm("phi"));
                for (int j = 0; j < listOfNodes.size(); j++) {
                    toBelieve = (Formula)SLPatternManip.instantiate(toBelieve, 
                            ((VariableNode)listOfNodes.get(j)).lx_name(), termMatchResult.getTerm(((VariableNode)listOfNodes.get(j)).lx_name()));
                }
                myKBase.assertFormula(new BelieveNode(applyResult.getTerm("agent"), toBelieve));
                //return new BelieveNode(applyResult.getTerm("agent"), toBelieve);
                
                return new TrueNode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TrueNode();
        }
        
    }
    
    private Formula generalNotPatternProcess(Formula formula, MatchResult applyResult) {
        try {
            ListOfTerm solutionsInKB = myKBase.queryRef((IdentifyingExpression)applyResult.getTerm("ide"));
            if (solutionsInKB != null) {
                if ((IdentifyingExpression)applyResult.getTerm("ide") instanceof IotaNode) {
                    if (solutionsInKB.get(0).equals(applyResult.getTerm("phi"))) {
                        myKBase.removeClosedPredicate(finder);
                    }
                } else if (applyResult.getTerm("phi") instanceof TermSetNode && ((TermSetNode)applyResult.getTerm("phi")).as_terms().size() == solutionsInKB.size()) {
                    ListOfTerm list = ((TermSetNode)applyResult.getTerm("phi")).as_terms();
                    for(int i = 0; i < list.size(); i++) {
                        if (!solutionsInKB.contains(list.get(i))) {
                            return formula;
                        }
                    }
                    myKBase.removeClosedPredicate(finder);
                }
                return new TrueNode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formula;
    }
    
    private void getPatterns(MatchResult applyResult) {
        try {
            listOfNodes = new ListOfNodes();
            if (((IdentifyingExpression)applyResult.getTerm("ide")).as_term().childrenOfKind(VariableNode.class, listOfNodes)) {
                formulaPattern = (Formula)SLPatternManip.toPattern(((IdentifyingExpression)applyResult.getTerm("ide")).as_formula(), listOfNodes, null);
                closedPattern = (Formula)SLPatternManip.toPattern(((IdentifyingExpression)applyResult.getTerm("ide")).as_formula(), listOfNodes, "_");
                termPattern = (Term)SLPatternManip.toPattern(((IdentifyingExpression)applyResult.getTerm("ide")).as_term(), listOfNodes, null);
            }             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} // End of class AllIREFilter
