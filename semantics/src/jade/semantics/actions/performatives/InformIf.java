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
 * InformIf.java
 * Created on 15 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.actions.operators.Alternative;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentExpression;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * This class represents the semantic action: <code>InformIf</code>. <br>
 * The sender informs the receiver whether or not a given proposition is true.<br>
 * The content of this action is a proposition.<br>
 * In fact, this action action is a macro action that represents two possible 
 * courses of action: 
 * <ul>
 * <li><i>sender</i> informs <i>receiver</i> that <i>proposition</i>
 * <li><i>sender</i> informs <i>receiver</i> that not <i>proposition</i>
 * </ul> 
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class InformIf extends CommunicativeAction {
    
	/**
	 * Pattern used to recognize an inform-if action
	 */
    ActionExpression informIfPattern;
    
	/**
	 * Pattern used to recognize an inform-if rational effect
	 */
    Formula rationalEffectInformIfPattern;
    
    /**
     * Pattern used to build action expression
     */
    ActionExpression actionPattern;
	  
    /**
     * Pattern used to build an alternative action using the rational effect
     */
    Formula formulaPattern;

	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Constructor
     * @param table the semantic action table
     */
    public InformIf(SemanticActionTable table) {
    	super(table);
        informIfPattern = (ActionExpression) SLPatternManip.fromTerm("(action ??sender (INFORM-IF :sender ??sender :receiver ??receiver :content ??content))");
        rationalEffectInformIfPattern = SLPatternManip.fromFormula("(or (B ??agent ??phi) (B ??agent (not(??phi))))");
        actionPattern = (ActionExpression) SLPatternManip.fromTerm("(| (action ??sender (INFORM :sender ??sender :receiver ??receiver :content ??content)) (action ??sender (INFORM :sender ??sender :receiver ??receiver :content ??notcontent)))");
        formulaPattern = SLPatternManip.fromFormula("(or (B ??agent ??phi) (B ?,agent (not (??phi))))");
        setACLMessageCode(ACLMessage.INFORM_IF);
    } // End of constructor InformIf/0
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
        try {
			MatchResult matchResult = SLPatternManip.match(informIfPattern, actionExpression);
	        if (matchResult != null) {
	            ListOfContentExpression listOfContentExpr = 
	                ((ContentNode)(SLParser.getParser().parseContent(((StringConstantNode)matchResult.getTerm("??content")).lx_value()))).as_expressions();
	            if (listOfContentExpr.size() == 1 && listOfContentExpr.element(0) instanceof FormulaContentExpressionNode) {
		            Formula notPhi = new NotNode(((FormulaContentExpressionNode)listOfContentExpr.element(0)).as_formula()).getSimplifiedFormula();
		            Content notContent = new ContentNode(new ListOfContentExpression(new ContentExpression[] {new FormulaContentExpressionNode(notPhi)}));
					SLPatternManip.set(actionPattern, "??sender", matchResult.getTerm("??sender"));
					SLPatternManip.set(actionPattern, "??receiver", matchResult.getTerm("??receiver"));
					SLPatternManip.set(actionPattern, "??content", new StringConstantNode(((StringConstantNode)matchResult.getTerm("??content")).lx_value()));
					SLPatternManip.set(actionPattern, "??notcontent", new StringConstantNode(notContent.toString()));
		            ActionExpression actionExpr = (ActionExpression)SLPatternManip.instantiate(actionPattern);
		            return new Alternative(table).newAction(actionExpr);
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
        try {
			MatchResult matchResult = SLPatternManip.match(rationalEffectInformIfPattern, rationalEffect);
	        if (matchResult != null) {
				SLPatternManip.set(formulaPattern, "??agent", matchResult.getTerm("??agent"));
				SLPatternManip.set(formulaPattern, "??phi", matchResult.getFormula("??phi"));	
	            Formula formula = ((Formula)SLPatternManip.instantiate(formulaPattern)).getSimplifiedFormula();
	            return new Alternative(table).newAction(formula, agentName);
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of newAction/1

} // End of class InformIf
