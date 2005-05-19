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
 * Subscribe.java
 * Created on 23 févr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingContentExpressionNode;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
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
public class Subscribe extends CommunicativeAction {
    
    /**
     * Pattern used to recognize a subscribe action
     */
    Term subscribeMainPattern;
    
    /**
     * Pattern used to build the inform-ref action
     */
    Term informRefPattern;

    /**
     * Pattern used to build the action expression 
     */
    Formula conditionPattern;
    
    /**
     * Pattern used to build the action expression 
     */
    Content contentPattern = SLPatternManip.fromContent("((??act, ??condition))");
    
    /**
     * Pattern used to build the action expression 
     */
    Term actionPattern = SLPatternManip.fromTerm("(action ??sender (REQUEST-WHENEVER :sender ??sender :receiver ??receiverList :content ??content))");

    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Constructor
     * @param table the semantic action table
     */
    public Subscribe(SemanticActionTable table) {
        super(table);
        subscribeMainPattern = SLPatternManip.fromTerm("(action ??sender (SUBSCRIBE :sender ??sender :receiver ??receiver :content ??content))");
        informRefPattern = SLPatternManip.fromTerm("(action ??sender (INFORM-REF :sender ??sender :receiver ??receiverList :content ??content))");
        conditionPattern = SLPatternManip.fromFormula("(exists ?y (= ?y ??ide))");
        contentPattern = SLPatternManip.fromContent("((??act, ??condition))");
        setACLMessageCode(ACLMessage.SUBSCRIBE);
    } // End of constructor Subscribe/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
        try {
            MatchResult matchResult = SLPatternManip.match(subscribeMainPattern, actionExpression);
            if (matchResult != null) {
                Content content = (SLParser.getParser().parseContent(((StringConstantNode)matchResult.getTerm("??content")).lx_value()));
                ListOfContentExpression listOfContentExpr = ((ContentNode)content).as_expressions();
                if (listOfContentExpr.size() == 1 
                        && listOfContentExpr.element(0) instanceof IdentifyingContentExpressionNode) {
                    TermSetNode senderList = new TermSetNode(new ListOfTerm(new Term[] {matchResult.getTerm("??sender")}));
                    ActionExpression informRef = (ActionExpression)SLPatternManip.instantiate(informRefPattern,
                        "??sender", ((TermSetNode) matchResult.getTerm("??receiver")).as_terms().first(),
                        "??receiverList", senderList,
                        "??content", content);
                        
                    Formula condition = (Formula)SLPatternManip.instantiate(conditionPattern,
                        "??ide", listOfContentExpr.element(0));
                    Content cont = (Content)SLPatternManip.instantiate(contentPattern,
                        "??act", informRef,
                        "??condition", condition);
                    ActionExpression actionExpr = (ActionExpression)SLPatternManip.instantiate(actionPattern,
                        "??sender", matchResult.getTerm("??sender"),
                        "??receiverList", matchResult.getTerm("??receiver"),
                        "??content", new StringConstantNode(cont.toString()));
                    return new RequestWhenever(table).newAction(actionExpr);
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
        return new RequestWhenever(table).newAction(rationalEffect, agentName);
    } // End of newAction/2

} // End of class Subscribe
