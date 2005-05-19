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
 * Confirm.java
 * Created on 9 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.PrimitiveBehaviour;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ContentExpression;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * This class represents the semantic action: <code>Confirm</code>.<br>
 * The sender informs the receiver that a given proposition is true, where the
 * receiver is known to be uncertain about the proposition. <br>
 * The content of this action is a proposition. <br>
 * <code>Confirm</code> indicates that the sending agent:
 * <ul>
 * <li>believes that some proposition is true,
 * <li>intends that the receiving agent also comes to believe that the
 * propositon is true,
 * <li>believes that the receiver is uncertain of the truth of the proposition.
 * </ul>
 * From the receiver's viewpoint, receiving a <code>Confirm</code> message
 * entitles it to believe that:
 * <ul>
 * <li>the sender believes the proposition that is the content of the message,
 * <li>the sender whishes the receiver to believe that proposition also.
 * </ul>
 * 
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class Confirm extends CommunicativeAction {
    
    /**
     * Specific content of this action
     */
	Formula myContent;
	
	/**
	 * Pattern used to recognize a confirm action
	 */
	ActionExpression confirmPattern;
	
	/**
	 * Pattern used to build the feasibility precondition
	 */
	Formula feasibilityPreconditionPattern;
	
	/**
	 * Pattern used to build the rational effect
	 */
	Formula rationalEffectPattern;
	
	/**
	 * Pattern used to build the postcondition 
	 */
	Formula postConditionPattern;

	/** ****************************************************************** */
	/** CONSTRUCTOR * */
	/** ****************************************************************** */

	/**
	 * Constructor
	 * @param table the semantic action table
	 */
	public Confirm(SemanticActionTable table) {
		super(table);
        confirmPattern = (ActionExpression) SLPatternManip.fromTerm("(action ??sender (CONFIRM :sender ??sender :receiver ??receiver :content ??content))");
        feasibilityPreconditionPattern = SLPatternManip.fromFormula("(and (B ??sender ??content) (B ??sender (U ??receiver ??content)))");
        rationalEffectPattern = SLPatternManip.fromFormula("(B ??receiver ??content)");
        postConditionPattern = SLPatternManip.fromFormula("(B ??sender (B ??receiver ??content))");
		setACLMessageCode(ACLMessage.CONFIRM);
	} // End of constructor Confirm/1

	/** ****************************************************************** */
	/** METHODS * */
	/** ****************************************************************** */

	/**
	 * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
	 */
	public SemanticAction newAction(ActionExpression actionExpression) {
		Confirm result = new Confirm(table);
		MatchResult matchResult = SLPatternManip.match(confirmPattern, actionExpression);
		if (matchResult != null) {
			try {
				result.setSender(matchResult.getTerm("??sender"));
				result.setReceiverList(((TermSetNode) matchResult.getTerm("??receiver")).as_terms());
				result.setContent(SLParser.getParser().parseContent(
						((StringConstantNode)matchResult.getTerm("??content")).lx_value()));
				ListOfContentExpression listOfContentExpr = ((ContentNode)result.getContent()).as_expressions();
				if (listOfContentExpr.size() == 1) {
					result.setMyContent(((FormulaContentExpressionNode)listOfContentExpr.first()).as_formula());
					//result.setSemanticFeatures();
                    result.setBehaviour(new PrimitiveBehaviour(result));
					return result;
				}
			} catch (Exception e) {}
		}
		return null;
	} // End of newAction/3

	/**
	 * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.Term)
	 */
	public SemanticAction newAction(Formula rationalEffect, Term agentName) {
		Confirm result = new Confirm(table);
		result.setSender(agentName);
		try {
			MatchResult matchResult = SLPatternManip.match(rationalEffectPattern, rationalEffect);
			if (matchResult != null) {
				result.setReceiverList(new ListOfTerm(new Term[] {matchResult.getTerm("??receiver")}));
				result.setMyContent(matchResult.getFormula("??content"));
				result.setContent(new ContentNode(new ListOfContentExpression(
						new ContentExpression[] {new FormulaContentExpressionNode(result.getMyContent())})));
				//result.setSemanticFeatures();
                result.setBehaviour(new PrimitiveBehaviour(result));
				return result;
			}
		} catch (Exception e) {}
		return null;
	} // End of newAction/2

	/**
	 * Computes all semantic features of the action (feasibility precondition,
	 * persistent feasibility precondition, rational effect and postcondition)
	 * @throws SLPattern.WrongVariableType
	 * @throws SLPattern.PatternCannotBeinstantiated
	 */
	protected void setSemanticFeatures() throws WrongTypeException {
		// TODO Handle all receivers of receiverList
		setFeasibilityPrecondition(((Formula) SLPatternManip
				.instantiate(feasibilityPreconditionPattern,
						     "??sender", getSender(),	
				             "??receiver", getReceiver(),
				             "??content", getMyContent())));

		setRationalEffect(((Formula) SLPatternManip
				.instantiate(rationalEffectPattern,
							 "??receiver", getReceiver(),
							 "??content", getMyContent())));

		setPersistentFeasibilityPrecondition(((Formula) SLPatternManip
				.instantiate(rationalEffectPattern,
						     "??receiver", getReceiver(),
		                     "??content", getMyContent())));

		setPostCondition(((Formula) SLPatternManip
				.instantiate(postConditionPattern,
							 "??sender", getSender(),
							 "??receiver", getReceiver(),
							 "??content", getMyContent())));
		
		setBehaviour(new PrimitiveBehaviour(this));
	} // End of setSemanticFeatures/0

    public  Formula feasibilityPreconditionCalculation() throws WrongTypeException {
        return ((Formula) SLPatternManip
                .instantiate(feasibilityPreconditionPattern,
                         "??sender", getSender(),   
                         "??receiver", getReceiver(),
                         "??content", getMyContent()));
    }
    public  Formula persistentFeasibilityPreconditonCalculation() throws WrongTypeException{
        return ((Formula) SLPatternManip
                .instantiate(rationalEffectPattern,
                         "??receiver", getReceiver(),
                         "??content", getMyContent()));
    }
    public  Formula rationalEffectCalculation() throws WrongTypeException {
        return ((Formula) SLPatternManip
                .instantiate(rationalEffectPattern,
                         "??receiver", getReceiver(),
                         "??content", getMyContent()));
    }
    public  Formula postConditionCalculation() throws WrongTypeException {
        return ((Formula) SLPatternManip
                .instantiate(postConditionPattern,
                         "??sender", getSender(),
                         "??receiver", getReceiver(),
                         "??content", getMyContent()));
    }

	/**
	 * @return Returns the content.
	 */
	public Formula getMyContent() {
		return myContent;
	} // End of getMyContent/0
	
	/**
	 * Sets the specific content of this action 
	 * @param content The content to set.
	 */
	public void setMyContent(Formula content) {
		myContent = content;
	} // End of setMyContent/1
	
} // End of class Confirm
