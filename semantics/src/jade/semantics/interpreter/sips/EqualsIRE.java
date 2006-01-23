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
 * EqualsIRE.java
 * Created on 6 janv. 2006
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This is applicable on formulae of the form (B agent (= ??ire ??phi)). The 
 * identifying expression ??ire is of the form (op t f) where "op" is an
 * operator of the set {iota, any, all, some}, "t" a term, and "f" a formula.
 * <ul> 
 * <li>If the operator is equal to  "all", and "??phi" is an no empty set, 
 * the sip returns new several SR:
 * <ul>
 * <li>A SR of the form (B agent (= ??ire (set))) that indicates the formula "f" 
 * of the identifying expression is closed.
 * <li>For each value of the set, a SR of the form (B agent f) is generated, where 
 * the variables in f are replaced by the values of the set.
 * </ul>
 * <li>If the operator is equals to "iota", the sip returns two SR:
 * <ul>
 * <li>A SR of the form (B agent (= ??ire (set))) that indicates the formula "f" 
 * of the identifying expression is closed.
 * <li>a SR of the form (B agent f) is generated, where the variables in f are 
 * replaced by the corresponding values in phi.
 * </ul>
 * </ul>
 * In other cases, it returns null.  
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class EqualsIRE extends SemanticInterpretationPrinciple {

    /**
     * Pattern used to test the applicability of the semantic interpretation principle
     */
    private Formula pattern;
    
    /**
     * Patterns used by the semantic interpretation principle
     */
    private Formula formulaPattern;
    
    private Term termPattern;

    /**
     * A list of nodes
     */
    private ListOfNodes listOfNodes;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public EqualsIRE(SemanticCapabilities capabilities) {
        super(capabilities);
        pattern = SLPatternManip.fromFormula("(B " + myCapabilities.getAgentName() + " (= ??ire ??phi))");        
    } // End of EqualsIRE/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * This is applicable on formulae of the form (B agent (= ??ire ??phi)). The 
     * identifying expression ??ire is of the form (op t f) where "op" is an
     * operator of the set {iota, any, all, some}, "t" a term, and "f" a formula.
     * <ul> 
     * <li>If the operator is equal to  "all", and "??phi" is a no empty set, 
     * the sip returns new several SR:
     * <ul>
     * <li>A SR of the form (B agent (= ??ire (set))) that indicates the formula "f" 
     * of the identifying expression is closed.
     * <li>For each value of the set, a SR of the form (B agent f) is generated, where 
     * the variables in f are replaced by the values of the set.
     * </ul>
     * <li>If the operator is equals to "iota", the sip returns two SR:
     * <ul>
     * <li>A SR of the form (B agent (= ??ire (set))) that indicates the formula "f" 
     * of the identifying expression is closed.
     * <li>a SR of the form (B agent f) is generated, where the variables in f are 
     * replaced by the corresponding values in phi.
     * </ul>
     * </ul>
     * In other cases, it returns null.
     * @see jade.semantics.interpreter.SemanticInterpretationPrinciple#apply(jade.semantics.interpreter.SemanticRepresentation)
     */
    public ArrayList apply(SemanticRepresentation sr)
            throws SemanticInterpretationPrincipleException {
        try {
            MatchResult applyResult = SLPatternManip.match(pattern, sr.getSLRepresentation());
            if (applyResult != null) {
                IdentifyingExpression ire = (IdentifyingExpression)applyResult.getTerm("ire");
                if (ire instanceof AllNode || ire instanceof IotaNode) {
                    getPatterns(applyResult);
                    ArrayList listOfSR = new ArrayList();
                    if (ire instanceof AllNode) {
                        if (applyResult.getTerm("phi") instanceof TermSetNode &&
                        ((TermSetNode)applyResult.getTerm("phi")).as_terms().size() > 0) {
                            addClosureSR(listOfSR, sr, (AllNode)applyResult.getTerm("ire"));
                        } else {
                            return null;
                        }
                        ListOfTerm list = ((TermSetNode)applyResult.getTerm("phi")).as_terms();
                        for(int i = 0; i < list.size(); i++) {
                            Formula toBelieve = formulaPattern;
                            MatchResult termMatchResult = SLPatternManip.match(termPattern, list.get(i));
                            for (int j = 0; j < listOfNodes.size(); j++) {
                                toBelieve = (Formula)SLPatternManip.instantiate(toBelieve, 
                                        ((VariableNode)listOfNodes.get(j)).lx_name(), termMatchResult.getTerm(((VariableNode)listOfNodes.get(j)).lx_name()));
                            }
                            SemanticRepresentation newSR = new SemanticRepresentation();
                            newSR.setSLRepresentation(new BelieveNode(myCapabilities.getAgentName(), toBelieve));
                            newSR.setMessage(sr.getMessage());
                            listOfSR.add(newSR);
                        }
                    } else {
                        addClosureSR(listOfSR, sr,new AllNode(((IdentifyingExpression)applyResult.getTerm("ire")).as_term(),
                        ((IdentifyingExpression)applyResult.getTerm("ire")).as_formula()));
                        Formula toBelieve = formulaPattern;
                        MatchResult termMatchResult = SLPatternManip.match(termPattern, applyResult.getTerm("phi"));
                        for (int j = 0; j < listOfNodes.size(); j++) {
                            toBelieve = (Formula)SLPatternManip.instantiate(toBelieve, 
                                    ((VariableNode)listOfNodes.get(j)).lx_name(), termMatchResult.getTerm(((VariableNode)listOfNodes.get(j)).lx_name()));
                        }
                        SemanticRepresentation newSR = new SemanticRepresentation();
                        newSR.setSLRepresentation(new BelieveNode(myCapabilities.getAgentName(), toBelieve));
                        newSR.setMessage(sr.getMessage());
                        listOfSR.add(newSR);
                    } 
                    return listOfSR;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    }
    
    /**
     * Adds a new SR of the form (B ??agent (= ??ire (set))) in the given list.
     * The value of ??agent is in the MatchResult given in parameter, and the 
     * value of ??ire is given in parameter (the AllNode).   
     * @param list the list that contains the SR. 
     * @param sr the incoming SR
     * @param node the node to put in the new SR
     */
    private void addClosureSR(ArrayList list, SemanticRepresentation sr, AllNode node) {
        try {
            SemanticRepresentation closureSR = new SemanticRepresentation();
            closureSR.setSLRepresentation((Formula)SLPatternManip.instantiate(pattern, 
                    "ire", node,
                    "phi", new TermSetNode(new ListOfTerm())));
            closureSR.setMessage(sr.getMessage());
            list.add(closureSR);
        } catch (SLPatternManip.WrongTypeException wte) {
            wte.printStackTrace();
        }
    }
    /**
     * Builds the patterns of the formula part and the term part of the ire which
     * appears in the matchResult given in parameter. All the variables of the 
     * term part are changed to metavariables in the terme and in the formula. 
     * @param applyResult a MatchEesult that contains an IdentifyingExpression
     * that names is "ire".
     */
    private void getPatterns(MatchResult applyResult) {
        try {
            listOfNodes = new ListOfNodes();
            if (((IdentifyingExpression)applyResult.getTerm("ire")).as_term().childrenOfKind(VariableNode.class, listOfNodes)) {
                formulaPattern = (Formula)SLPatternManip.toPattern(((IdentifyingExpression)applyResult.getTerm("ire")).as_formula(), listOfNodes, null);
                termPattern = (Term)SLPatternManip.toPattern(((IdentifyingExpression)applyResult.getTerm("ire")).as_term(), listOfNodes, null);
            }             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
