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
 * RequestWhenever.java
 * Created on 19 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpression;
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
 * This class represents the semantic action: <code>RequestWhenever</code>. <br>
 * The sender wants the receiver to perform some action as soon as some 
 * proposition becomes true ans thereafter each time the proposition becomes true again..<br>
 * The content of this action is a tuple of an action expression and a 
 * proposition.<br>
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class RequestWhenever extends CommunicativeAction {

	/**
	 * Pattern used to recognize a request whenever action
	 */
    ActionExpression requestWheneverMainPattern;
    
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
    ActionExpression actionPattern;

	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Constructor
     * @param table the semantic action table
     */
    public RequestWhenever(SemanticActionTable table) {
    	super(table);
        requestWheneverMainPattern = (ActionExpression) SLPatternManip.fromTerm("(action ??sender (REQUEST-WHENEVER :sender ??sender :receiver ??receiver :content ??content))");
        formulaPattern = SLPatternManip.fromFormula("(done ??act true)");
        contentPattern = SLPatternManip.fromContent("((or (not (done (action null before) (not (B ??agent1 ??phi)))) (I ??agent2 ??psi)))");
        actionPattern = (ActionExpression) SLPatternManip.fromTerm("(action ??sender (INFORM :sender ??sender :receiver ??receiverList :content ??content))");
        setACLMessageCode(ACLMessage.REQUEST_WHENEVER);
    } // End of constructor RequestWhenever/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
        try {
			MatchResult matchResult = SLPatternManip.match(requestWheneverMainPattern, actionExpression);
	        if (matchResult != null) {
			    Content content = (SLParser.getParser().parseContent(((StringConstantNode)matchResult.getTerm("??content")).lx_value()));
	            ListOfContentExpression listOfContentExpr = ((ContentNode)content).as_expressions();
	            if (listOfContentExpr.size() == 2 && listOfContentExpr.element(0) instanceof ActionContentExpressionNode && listOfContentExpr.element(1) instanceof FormulaContentExpressionNode) {
                	SLPatternManip.set(formulaPattern, "??act", ((ActionContentExpressionNode)listOfContentExpr.element(0)).as_action_expression());
	                Formula f = (Formula)SLPatternManip.instantiate(formulaPattern);

					SLPatternManip.set(contentPattern, "??agent1", ((TermSetNode) matchResult.getTerm("??receiver")).as_terms().first());
					SLPatternManip.set(contentPattern, "??agent2", matchResult.getTerm("??sender"));
					SLPatternManip.set(contentPattern, "??phi", listOfContentExpr.element(1));
					SLPatternManip.set(contentPattern, "??psi", f);
					Content cont = (Content)SLPatternManip.instantiate(contentPattern);

					SLPatternManip.set(actionPattern, "??sender", matchResult.getTerm("??sender"));
					SLPatternManip.set(actionPattern, "??receiverList", matchResult.getTerm("??receiver"));
					SLPatternManip.set(actionPattern, "??content", new StringConstantNode(cont.toString()));
					ActionExpression actionExpr = (ActionExpression)SLPatternManip.instantiate(actionPattern);

					return new Inform(table).newAction(actionExpr);
	            }
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of newAction/3

    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.Term)
     */
    public SemanticAction newAction(Formula rationalEffect, Term agentName) {
        return new Inform(table).newAction(rationalEffect, agentName);
    } // End of newAction/1

} // End of class RequestWhenever
