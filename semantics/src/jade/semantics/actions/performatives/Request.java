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
 * Request.java
 * Created on 8 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;


import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.PrimitiveBehaviour;
import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ContentExpression;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
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
 * This class represents the semantic action: <code>Request</code>. <br>
 * The sender requests the receiver to perform some action.<br>
 * The content of this action is an action expression.<br>
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class Request extends CommunicativeAction {

    /**
     * Specific content of this action
     */
    ActionExpression myContent;

    /**
	 * Pattern used to recognize a request action
	 */
    ActionExpression requestPattern;
   
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
	
    /**
     * 
     */
    SemanticAction requestedActionForDoubleMiror;
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Constructor
     * @param table the semantic action table
     */
    public Request(SemanticActionTable table) {
    	super(table);
        requestPattern = (ActionExpression) SLPatternManip.fromTerm("(action ??sender (REQUEST :sender ??sender :receiver ??receiver :content ??content))");
        feasibilityPreconditionPattern = SLPatternManip.fromFormula("(and ??miror (not (B ??sender (I ??receiver (done ??content true)))))");
        rationalEffectPattern = SLPatternManip.fromFormula("(done ??content true)");
        postConditionPattern = SLPatternManip.fromFormula("(B ??sender (I ??receiver (done ??content true)))");
   		setACLMessageCode(ACLMessage.REQUEST);
    } // End of constructor Request/1
    
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/


    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
    	Request result = new Request(table);
		MatchResult matchResult = SLPatternManip.match(requestPattern, actionExpression);
        if (matchResult != null) {
         	try {
        		result.setSender(matchResult.getTerm("??sender"));
        		result.setReceiverList(((TermSetNode) matchResult
						.getTerm("??receiver")).as_terms());
				result.setContent(SLParser.getParser().parseContent(
						((StringConstantNode)matchResult.getTerm("??content")).lx_value()));
				ListOfContentExpression listOfContentExpr = ((ContentNode)result.getContent()).as_expressions();
				if (listOfContentExpr.size() == 1) {
					ActionExpression requestedAction = ((ActionContentExpressionNode)listOfContentExpr.first()).as_action_expression();
					ListOfTerm requestedActionAgents = requestedAction.getAgents();
					if (requestedActionAgents.size() == 1 && requestedActionAgents.get(0).equals(result.getReceiver())) {
						result.setMyContent(requestedAction);
						//result.setSemanticFeatures();
                        requestedActionForDoubleMiror = table.getSemanticActionInstance(result.getMyContent());
                        result.setBehaviour(new PrimitiveBehaviour(result));
						return result;
					}
				}
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}
        }
    	return null;
    } // End of newAction/1

    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.Term)
     */
    public SemanticAction newAction(Formula rationalEffect, Term agentName) {
    	Request result = new Request(table);
		result.setSender(agentName);
		try {
			MatchResult matchResult = SLPatternManip.match(rationalEffectPattern, rationalEffect);
	        if (matchResult != null) {
				result.setMyContent(matchResult.getActionExpression("??content"));
				result.setContent(new ContentNode(new ListOfContentExpression(
						new ContentExpression[] {new ActionContentExpressionNode(result.getMyContent())})));
				ListOfTerm requestedAgents = result.getMyContent().getAgents();
				if (requestedAgents.size() == 1 && !requestedAgents.first().equals(agentName)) {
					result.setReceiverList(new ListOfTerm(new Term[] {requestedAgents.first()}));
                    requestedActionForDoubleMiror = table.getSemanticActionInstance(result.getMyContent());
					//result.setSemanticFeatures();
                    result.setBehaviour(new PrimitiveBehaviour(result));
					return result;					
				}
			}
		} catch (Exception e) {
		}
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
		SemanticAction requestedAction = table.getSemanticActionInstance(getMyContent());

		SLPatternManip.set(feasibilityPreconditionPattern, "??sender", getSender());
		SLPatternManip.set(feasibilityPreconditionPattern, "??receiver", getReceiver());
		SLPatternManip.set(feasibilityPreconditionPattern, "??content", getMyContent());
		SLPatternManip.set(feasibilityPreconditionPattern, "??miror", requestedAction.getFeasibilityPrecondition().getSimplifiedFormula().getDoubleMirror(getSender(), getReceiver(), true).getSimplifiedFormula());
		setFeasibilityPrecondition(((Formula)SLPatternManip.instantiate(feasibilityPreconditionPattern)).getSimplifiedFormula());

		SLPatternManip.set(rationalEffectPattern, "??content", getMyContent());
		setRationalEffect(((Formula) SLPatternManip.instantiate(rationalEffectPattern)).getSimplifiedFormula());

		setPersistentFeasibilityPrecondition((requestedAction.getPersistentFeasibilityPrecondition().getSimplifiedFormula().getDoubleMirror(getSender(), getReceiver(), true).getSimplifiedFormula()).getSimplifiedFormula());

		SLPatternManip.set(postConditionPattern, "??sender", getSender());
		SLPatternManip.set(postConditionPattern, "??receiver", getReceiver());
		SLPatternManip.set(postConditionPattern, "??content", getMyContent());
		setPostCondition(((Formula) SLPatternManip.instantiate(postConditionPattern)).getSimplifiedFormula());
		
		setBehaviour(new PrimitiveBehaviour(this));
	} // End of setSemanticFeatures/0

    public  Formula feasibilityPreconditionCalculation() throws WrongTypeException {
        requestedActionForDoubleMiror = table.getSemanticActionInstance(getMyContent());
        SLPatternManip.set(feasibilityPreconditionPattern, "??sender", getSender());
        SLPatternManip.set(feasibilityPreconditionPattern, "??receiver", getReceiver());
        SLPatternManip.set(feasibilityPreconditionPattern, "??content", getMyContent());
        SLPatternManip.set(feasibilityPreconditionPattern, "??miror", requestedActionForDoubleMiror.getFeasibilityPrecondition().getSimplifiedFormula().getDoubleMirror(getSender(), getReceiver(), true).getSimplifiedFormula());
        return ((Formula)SLPatternManip.instantiate(feasibilityPreconditionPattern)).getSimplifiedFormula();
    }
    public  Formula persistentFeasibilityPreconditonCalculation() throws WrongTypeException{
        return (requestedActionForDoubleMiror.getPersistentFeasibilityPrecondition().getSimplifiedFormula().getDoubleMirror(getSender(), getReceiver(), true).getSimplifiedFormula()).getSimplifiedFormula();
    }
    public  Formula rationalEffectCalculation() throws WrongTypeException {
        SLPatternManip.set(rationalEffectPattern, "??content", getMyContent());
        return ((Formula) SLPatternManip.instantiate(rationalEffectPattern)).getSimplifiedFormula();
    }
    public  Formula postConditionCalculation() throws WrongTypeException {
        SLPatternManip.set(postConditionPattern, "??sender", getSender());
        SLPatternManip.set(postConditionPattern, "??receiver", getReceiver());
        SLPatternManip.set(postConditionPattern, "??content", getMyContent());
        return ((Formula) SLPatternManip.instantiate(postConditionPattern)).getSimplifiedFormula();
    }

	/**
	 * @return Returns the content.
	 */
	public ActionExpression getMyContent() {
		return myContent;
	}  // End of getMyContent/0

	/**
	 * Sets the specific content of this action 
	 * @param content The content to set.
	 */
	public void setMyContent(ActionExpression content) {
		myContent = content;
	} // End of setMyContent/1
} // End of class Request
