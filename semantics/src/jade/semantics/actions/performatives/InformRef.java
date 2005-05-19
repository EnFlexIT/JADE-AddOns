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
 * InformRef.java
 * Created on 24 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.InformRefBehaviour;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.ContentExpression;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingContentExpressionNode;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
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
 * This class represents the semantic action: <code>InformRef</code>. <br>
 * The sender informs the receiver the object which corresponds to a descriptor,
 * for example, a name.<br>
 * The content of this action is an object proposition (a referential description).<br>
 * In fact, this action is a macro action that represents an unbounded, 
 * possibly infinite set of possible courses of action, in which <i>sender</i>
 * informs <i>receiver</i> of the referent <i>x</i>.  
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class InformRef extends CommunicativeAction {
    
    /**
     * Specific content of this action
     */
    IdentifyingExpression myContent;
 
	/**
	 * Pattern used to recognize an inform-ref action
	 */
    ActionExpression informRefPattern = 
		(ActionExpression) SLPatternManip.fromTerm("(action ??sender (INFORM-REF :sender ??sender :receiver ??receiver :content ??content))");

    /**
     * Pattern used to recognize an inform-ref rational effect
     */
    Formula recognitionRationalEffectPattern1;
    Formula recognitionRationalEffectPattern2;

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
 
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
   
    /**
     * Constructor
     * @param table the semantic action table
     */
    public InformRef(SemanticActionTable table) {
    	super(table);
        informRefPattern = (ActionExpression) SLPatternManip.fromTerm("(action ??sender (INFORM-REF :sender ??sender :receiver ??receiver :content ??content))");
        recognitionRationalEffectPattern1 = SLPatternManip.fromFormula("(exists ??var (B ??receiver (= ??content ??var)))");
        recognitionRationalEffectPattern2 = SLPatternManip.fromFormula("(exists ??var (B ??receiver ??anycontent))");
        feasibilityPreconditionPattern = SLPatternManip.fromFormula("(and (exists ?y (B ??sender (= ??content ?y))) (not (B ??sender (or (exists ?y (B ??receiver (= ??content ?y))) (exists ?y (U ??receiver (= ??content ?y))) ))))");
        rationalEffectPattern = SLPatternManip.fromFormula("(exists ?y (B ??receiver (= ??content ?y)))");
        postConditionPattern = SLPatternManip.fromFormula("(B ??sender (exists ?y (B ??receiver (= ??content ?y))))");
        setACLMessageCode(ACLMessage.INFORM_REF);
    } // End of constructor InformRef/1
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
        InformRef result = new InformRef(table);
        try {
			MatchResult matchResult = SLPatternManip.match(informRefPattern, actionExpression);
	        if (matchResult != null) {
	            result.setSender(matchResult.getTerm("??sender"));
	            result.setReceiverList(((TermSetNode) matchResult.getTerm("??receiver")).as_terms());
	            result.setContent(SLParser.getParser().parseContent(
						((StringConstantNode)matchResult.getTerm("??content")).lx_value()));
	            ListOfContentExpression listOfContentExpr = ((ContentNode)result.getContent()).as_expressions();
	            if (listOfContentExpr.size() == 1 && listOfContentExpr.element(0) instanceof IdentifyingContentExpressionNode) {
					result.setMyContent(((IdentifyingContentExpressionNode)listOfContentExpr.first()).as_identifying_expression());
					//result.setSemanticFeatures();
                    result.setBehaviour(new InformRefBehaviour(result));
			        return result;
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
        InformRef result = new InformRef(table);
        result.setSender(agentName);
        try {
			MatchResult matchResult = null;
            if ((matchResult = SLPatternManip.match(recognitionRationalEffectPattern1, rationalEffect)) != null
                    && matchResult.getTerm("??content") instanceof IdentifyingExpression) {
                result.setMyContent((IdentifyingExpression)matchResult.getTerm("??content"));
            }
            else if ((matchResult = SLPatternManip.match(recognitionRationalEffectPattern2, rationalEffect)) != null) {
                result.setMyContent(new AnyNode(matchResult.getTerm("??var"), matchResult.getFormula("??anycontent")));
            }
            else return null;

			result.setReceiverList(new ListOfTerm(new Term[] {matchResult.getTerm("??receiver")}));
            result.setContent(new ContentNode(new ListOfContentExpression(
                            new ContentExpression[] {new IdentifyingContentExpressionNode(result.getMyContent())})));
			//result.setSemanticFeatures();
            result.setBehaviour(new InformRefBehaviour(result));
			return result;
        } catch (Exception pe) {
            pe.printStackTrace();
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
		SLPatternManip.set(feasibilityPreconditionPattern, "??sender", getSender());
		SLPatternManip.set(feasibilityPreconditionPattern, "??receiver", getReceiver());
		SLPatternManip.set(feasibilityPreconditionPattern, "??content", getMyContent());
		setFeasibilityPrecondition(((Formula) SLPatternManip.instantiate(feasibilityPreconditionPattern)).getSimplifiedFormula());

		SLPatternManip.set(rationalEffectPattern, "??receiver", getReceiver());
		SLPatternManip.set(rationalEffectPattern, "??content", getMyContent());
		setRationalEffect(((Formula) SLPatternManip.instantiate(rationalEffectPattern)).getSimplifiedFormula());

		setPersistentFeasibilityPrecondition(((Formula) SLPatternManip.instantiate(rationalEffectPattern)).getSimplifiedFormula());

		SLPatternManip.set(postConditionPattern, "??sender", getSender());
		SLPatternManip.set(postConditionPattern, "??receiver", getReceiver());
		SLPatternManip.set(postConditionPattern, "??content", getMyContent());
		setPostCondition(((Formula) SLPatternManip.instantiate(postConditionPattern)).getSimplifiedFormula());
		
		setBehaviour(new InformRefBehaviour(this));
	} // End of setSemanticFeatures/0

    
    public  Formula feasibilityPreconditionCalculation() throws WrongTypeException {
        SLPatternManip.set(feasibilityPreconditionPattern, "??sender", getSender());
        SLPatternManip.set(feasibilityPreconditionPattern, "??receiver", getReceiver());
        SLPatternManip.set(feasibilityPreconditionPattern, "??content", getMyContent());
        return ((Formula) SLPatternManip.instantiate(feasibilityPreconditionPattern)).getSimplifiedFormula();
    }
    public  Formula persistentFeasibilityPreconditonCalculation() throws WrongTypeException{
        SLPatternManip.set(rationalEffectPattern, "??receiver", getReceiver());
        SLPatternManip.set(rationalEffectPattern, "??content", getMyContent());
        return ((Formula) SLPatternManip.instantiate(rationalEffectPattern)).getSimplifiedFormula();
    }
    public  Formula rationalEffectCalculation() throws WrongTypeException {
        SLPatternManip.set(rationalEffectPattern, "??receiver", getReceiver());
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
	public IdentifyingExpression getMyContent() {
		return myContent;
	}  // End of getMyContent/0

	/**
	 *  Sets the specific content of this action 
	 * @param content The content to set.
	 */
	public void setMyContent(IdentifyingExpression content) {
		myContent = content;
	} // End of setMyContent/1
} // End of class InformRef
