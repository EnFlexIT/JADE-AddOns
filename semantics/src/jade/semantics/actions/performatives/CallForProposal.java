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
 * CallForProposal.java
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
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingContentExpressionNode;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
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
public class CallForProposal extends CommunicativeAction {

    /**
     * Pattern used to recognize an call for proposal action
     */
    Term cfpMainPattern;

    /**
     * Pattern used to build the action expression 
     */
    Formula formulaPattern;

    /**
     * Pattern used to build the action expression 
     */
    Formula orPattern;
    
    /**
     * Pattern used to build the action expression 
     */
    Content contentPattern;

    /**
     * Pattern used to build the action expression 
     */
    Term actionPattern;

    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Constructor
     * @param table the semantic action table
     */
    public CallForProposal(SemanticActionTable table) {
        super(table);
        cfpMainPattern = SLPatternManip.fromTerm("(action ??sender (CFP :sender ??sender :receiver ??receiver :content ??content))");
        formulaPattern = SLPatternManip.fromFormula("(done ??act ??condition)");
        orPattern = SLPatternManip.fromFormula("(or (not (I ??agent1 ??phi)) (I ??agent2 ??phi))");
        contentPattern = SLPatternManip.fromContent("(??ide)");
        actionPattern = SLPatternManip.fromTerm("(action ??sender (INFORM :sender ??sender :receiver ??receiverList :content ??content))");
        setACLMessageCode(ACLMessage.CFP);
    } // End of constructor CallForProposal/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/

    
    /** 
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
        try {
            MatchResult matchResult = SLPatternManip.match(cfpMainPattern, actionExpression);
            if (matchResult != null) {
                Content content = (SLParser.getParser().parseContent(((StringConstantNode)matchResult.getTerm("??content")).lx_value()));
                ListOfContentExpression listOfContentExpr = ((ContentNode)content).as_expressions();
                if (listOfContentExpr.size() == 2 
                        && listOfContentExpr.element(0) instanceof ActionContentExpressionNode 
                        && listOfContentExpr.element(1) instanceof IdentifyingContentExpressionNode) {
                    Formula f = (Formula)SLPatternManip.instantiate(formulaPattern,
                        "??act", ((ActionContentExpressionNode)listOfContentExpr.element(0)).as_action_expression(),
                        "??condition", ((IdentifyingContentExpressionNode)listOfContentExpr.element(1)).as_identifying_expression().as_formula());
                    Formula or = (Formula)SLPatternManip.instantiate(orPattern,
                        "??agent1", matchResult.getTerm("??sender"),
                        "??agent2", ((TermSetNode) matchResult.getTerm("??receiver")).as_terms().first(),
                        "??phi", f);
                    IdentifyingExpression ide;
                    if (((IdentifyingContentExpressionNode)listOfContentExpr.element(1)).as_identifying_expression() instanceof AnyNode)
                        ide = new AnyNode();
                    else if (((IdentifyingContentExpressionNode)listOfContentExpr.element(1)).as_identifying_expression() instanceof IotaNode)
                        ide = new IotaNode();
                    else ide = new AllNode();
                    ide.as_formula(or);
                    ide.as_term(((IdentifyingContentExpressionNode)listOfContentExpr.element(1)).as_identifying_expression().as_term());
                    Content cont = (Content)SLPatternManip.instantiate(contentPattern,
                        "??ide", ide);
                        
                    ActionExpression actionExpr = (ActionExpression)SLPatternManip.instantiate(actionPattern,
                        "??sender", matchResult.getTerm("??sender"),
                        "??receiverList", matchResult.getTerm("??receiver"),
                        "??content", new StringConstantNode(cont.toString()));
                    return new QueryRef(table).newAction(actionExpr);
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
        return null;
    } // End of newAction/2
    
} // End of class CallForProposal
