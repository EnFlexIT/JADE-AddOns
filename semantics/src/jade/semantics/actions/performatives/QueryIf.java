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
 * QueryIf.java
 * Created on 8 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;


/**
 * This class represents the semantic action: <code>QueryIf</code>. <br>
 * This action is the action of asking another agent whether or not a given 
 * proposition is true.<br>
 * The content of this action is a proposition.<br>
 * The sending agent is requesting the receiver to perform an <code>Inform</code>
 * act, to inform it of the thruth of the proposition.<br>
 * The agent performing the <code>QueryIf</code> act:
 * <ul>
 * <li> has no knowledge of the thruth value of the proposition
 * <li> believes that the other agent can inform the querying agent if it knows 
 * the thruth of the proposition.
 * </ul>
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class QueryIf extends CommunicativeAction {

	/**
	 * Pattern used to recognize a query-if action
	 */
    ActionExpression queryIfPattern;
    
    /**
     * Pattern used to build the action expression
     */
    ActionExpression actionPattern;
    
    /**
     * Pattern used to build the requets content
     */
    Content contentPattern;
    
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Constructor
     * @param table the semantic action table
     */
    public QueryIf(SemanticActionTable table) {
    	super(table);
        queryIfPattern = (ActionExpression) SLPatternManip.fromTerm("(action ??sender (QUERY-IF :sender ??sender :receiver ??receiver :content ??content))");
        actionPattern = (ActionExpression) SLPatternManip.fromTerm("(action ??sender (REQUEST :sender ??sender :receiver ??receiver :content ??content))");
        contentPattern = SLPatternManip.fromContent("((action ??receiver (INFORM-IF :sender ??receiver :receiver ??senderList :content ??content)))");
        setACLMessageCode(ACLMessage.QUERY_IF);
    } // End of constructor QueryIf/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
        try {
			MatchResult matchResult = SLPatternManip.match(queryIfPattern, actionExpression);
	        if (matchResult != null) {
				SLPatternManip.set(contentPattern, "??receiver", ((TermSetNode) matchResult.getTerm("??receiver")).as_terms().first());
				SLPatternManip.set(contentPattern, "??senderList", new TermSetNode(new ListOfTerm(new Term[] {matchResult.getTerm("??sender")})));
				SLPatternManip.set(contentPattern, "??content", matchResult.getTerm("??content"));
	            Node requestContent = SLPatternManip.instantiate(contentPattern);

				SLPatternManip.set(actionPattern, "??sender", matchResult.getTerm("??sender"));
				SLPatternManip.set(actionPattern, "??receiver", matchResult.getTerm("??receiver"));
				SLPatternManip.set(actionPattern, "??content",new StringConstantNode(requestContent.toString()));
				ActionExpression actionExpr = (ActionExpression)SLPatternManip.instantiate(actionPattern);

				return new Request(table).newAction(actionExpr);
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
        return new Request(table).newAction(rationalEffect, agentName);
    } // End of newAction/2

} // End of class QueryIf
