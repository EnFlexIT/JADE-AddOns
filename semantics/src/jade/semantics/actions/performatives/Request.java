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
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.UnexpectedContentSIException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * This class represents the semantic action: <code>Request</code>. <br>
 * The sender requests the receiver to perform some action.<br>
 * The content of this action is an action expression.<br>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/08 Revision: 1.0 
 */
public class Request extends CommunicativeActionImpl {
    
    /**
     * Step for the computation of the feasibility precondition
     * (result of the "double mirror" transformation)
     */
    private Formula doubleMirrorPrecondition;
	
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Creates a new <code>Request</code> prototype. By default, the surface content format
     * is set to [ActionExpression]. 
     * The rational effect, the feasibility precondition, the persistent 
     * feasibility preconditon, and the postcondition are respectively set to:
     * <ul>
     * <li>"(done ??action)"
     * <li>"(and ??mirror (not (B ??sender (I ??receiver (done ??action true)))))"
     * <li>null
     * <li>"(B ??sender (I ??receiver (done ??action true)))"
     * </ul>
     * @param table the SemanticActionTable, which this action prototype belongs
     *            to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content (used to control the validity of the content)
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param rationalEffectRecognition pattern used to recognized the
     * rational effect of this action
      */   
    public Request(SemanticActionTable table, int surfacePerformative,
            Class[] surfaceContentFormat, String surfaceContentFormatMessage, Formula rationalEffectRecognition) {
    	super(table, surfacePerformative,
                (surfaceContentFormat == null ? new Class[] {ActionExpression.class} : surfaceContentFormat),
                (surfaceContentFormatMessage == null ? "an action expression" : surfaceContentFormatMessage),
                rationalEffectRecognition,
                SLPatternManip.fromFormula("(done ??action)"),
    	        SLPatternManip.fromFormula("(and ??mirror (not (B ??sender (I ??receiver (done ??action true)))))"),
                null,
                SLPatternManip.fromFormula("(B ??sender (I ??receiver (done ??action true)))"));
        setPerformative(ACLMessage.REQUEST);
        this.contentSize = 1;
    }
    
    /**
     * Creates a new <code>Request</code> prototype.
     * The surface content format, the surface content format message, and
     * the rational effect recognition pattern are the default ones. 
     * The surface performative is set to <code>REQUEST</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public Request(SemanticActionTable table) {
        this(table, ACLMessage.REQUEST, null, null, null);
    }

    /**
     * Returns an instance of <code>Request</code>
     * @return an instance of <code>Request</code>
     */
    public CommunicativeActionImpl createInstance() {
        return new Request(table);
    }
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * @inheritDoc
     */
    public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        ActionExpression actionExpression = (ActionExpression)surfaceContent.getContentElement(0);
        if (actionExpression.getAgents().size() == 1 && getReceiver().equals(actionExpression.getAgents().first())) {
            setContent(surfaceContent);
            return this;
        }
        else {
            throw new UnexpectedContentSIException(getSurfacePerformative(),
                    "an action expression from the receiver [" + getReceiver() + "]", actionExpression.getAgents().toString());
        }
    }

    /**
     * @inheritDoc
     */
    public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        ActionExpression actionExpression = (ActionExpression)rationalEffectMatching.getTerm("action");
        if (actionExpression.getAgents().size() == 1 && !actionExpression.getAgents().first().equals(getAuthor())) {
            setReceiver(actionExpression.getAgents().first());
            setSurfaceContentElement(0, actionExpression);
            return true;
        }
        return false;
    }
   
    /**
	 * @inheritDoc
	 */
    public Formula computeFeasibilityPrecondition() throws WrongTypeException {
        return (Formula)SLPatternManip.instantiate(feasibilityPreconditionPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "action", getContentElement(0),
                "mirror", getDoubleMirrorPrecondition());
    } // End of feasibilityPreconditionCalculation/0
    
    /**
     * @inheritDoc
     */
    public Formula computePersistentFeasibilityPreconditon() { //throws WrongTypeException
        return getDoubleMirrorPrecondition();
    } // End of persistentFeasibilityPreconditonCalculation/0
    
    /**
     * @inheritDoc
     */
    public Formula computeRationalEffect() throws WrongTypeException {
        return (Formula)SLPatternManip.instantiate(rationalEffectPattern,
                "action", getContentElement(0));
    } // End of rationalEffectCalculation/0
    
    /**
     * @inheritDoc
     */
    public Formula computePostCondition() throws WrongTypeException {
        return (Formula)SLPatternManip.instantiate(postConditionPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "action", getContentElement(0));
    } // End of postConditionCalculation/0
    
    /**
     * @return Returns the doubleMirrorPrecondition.
     */
    public Formula getDoubleMirrorPrecondition() {
        if (doubleMirrorPrecondition == null) {
            ActionExpression action = (ActionExpression)getContentElement(0);
            try {
                if (action.sm_action() == null) {
                    action.sm_action(table.getSemanticActionInstance(action));
                }
                doubleMirrorPrecondition = action.sm_action().getFeasibilityPrecondition().getDoubleMirror(getAuthor(), getReceiver(), true);
            }
            catch (SemanticInterpretationException e) {
                doubleMirrorPrecondition = new TrueNode();
            }
        }
        return doubleMirrorPrecondition;
    }
} // End of class Request
