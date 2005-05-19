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
 * Propose.java
 * Created on 24 févr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * @author Vincent Pautret
 * @version 
 */
public class Propose extends CommunicativeAction {

    /**
     * Pattern used to recognize a propose action
     */
    Term proposeMainPattern;
    
    /**
     * Pattern used to build the action expression 
     */
    Formula formulaPattern;

    /**
     * Pattern used to build the action expression 
     */
    Content contentPattern;

    /**
     * Pattern used to build the action expression 
     */
    Term actionPattern;

    
    Formula rationalEffectPattern;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Constructor
     * @param table the semantic action table
     */
    public Propose(SemanticActionTable table) {
        super(table);
        proposeMainPattern = SLPatternManip.fromTerm("(action ??sender (PROPOSE :sender ??sender :receiver ??receiver :content ??content))");
        formulaPattern = SLPatternManip.fromFormula("(done ??act ??condition)");
        contentPattern = SLPatternManip.fromContent("((or (not (I ??agent1 ??phi)) (I ??agent2 ??phi)))");
        actionPattern = SLPatternManip.fromTerm("(action ??sender (INFORM :sender ??sender :receiver ??receiverList :content ??content))");
        rationalEffectPattern = SLPatternManip.fromFormula("(or (not (I ??agent1 (done ??act ??condition))) (I ??agent2 (done ??act ??condition)))");
        setACLMessageCode(ACLMessage.PROPOSE);
    } // End of constructor Propose/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/

    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
        try {
            MatchResult matchResult = SLPatternManip.match(proposeMainPattern, actionExpression);
            if (matchResult != null) {
                Content content = (SLParser.getParser().parseContent(((StringConstantNode)matchResult.getTerm("??content")).lx_value()));
                ListOfContentExpression listOfContentExpr = ((ContentNode)content).as_expressions();
                if (listOfContentExpr.size() == 2 && listOfContentExpr.element(0) instanceof ActionContentExpressionNode 
                        && listOfContentExpr.element(1) instanceof FormulaContentExpressionNode) {
                    if (((ActionExpressionNode)((ActionContentExpressionNode)listOfContentExpr.element(0)).as_action_expression()).as_agent().equals(matchResult.getTerm("??sender"))) {
                        Formula f = (Formula)SLPatternManip.instantiate(formulaPattern,
                            "??act", ((ActionContentExpressionNode)listOfContentExpr.element(0)).as_action_expression(),
                            "??condition", listOfContentExpr.element(1));
                        Content cont = (Content)SLPatternManip.instantiate(contentPattern,
                            "??agent1", ((TermSetNode) matchResult.getTerm("??receiver")).as_terms().first(),
                            "??agent2", matchResult.getTerm("??sender"),
                            "??phi", f);
                        ActionExpression actionExpr = (ActionExpression)SLPatternManip.instantiate(actionPattern,
                            "??sender", matchResult.getTerm("??sender"),
                            "??receiverList", matchResult.getTerm("??receiver"),
                            "??content", new StringConstantNode(cont.toString()));
                        return new Inform(table).newAction(actionExpr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of newAction/1
    
    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.Term)
     */
    public SemanticAction newAction(Formula rationalEffect, Term agentName) {
        try {
            MatchResult matchResult = SLPatternManip.match(rationalEffectPattern, rationalEffect);
            if (matchResult != null) {
                return new Inform(table).newAction(rationalEffect.getSimplifiedFormula(), agentName);
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of newAction/2

} // End of class Propose
