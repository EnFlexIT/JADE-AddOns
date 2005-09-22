/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France T�l�com

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

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.InformRefBehaviour;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
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
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class InformRef extends CommunicativeActionImpl {
    
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
   
    /**
     * Creates a new <code>InformRef</code> prototype. By default, the surface content format
     * is set to [IdentifyingExpression]. 
     * The rational effect, the feasibility precondition, the persistent 
     * feasibility preconditon, and the postcondition are respectively set to:
     * <ul>
     * <li>"(exists ?y (B ??receiver (= ??ire ?y)))"
     * <li>"(and (exists ?y (B ??sender (= ??ire ?y))) (not (B ??sender (or (exists ?y (B ??receiver (= ??ire ?y))) (exists ?y (U ??receiver (= ??ire ?y))) ))))"
     * <li>"(exists ?y (B ??sender (= ??ire ?y)))"
     * <li>"(B ??sender (exists ?y (B ??receiver (= ??ire ?y))))"
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
    public InformRef(SemanticActionTable table, int surfacePerformative,
            Class[] surfaceContentFormat, String surfaceContentFormatMessage, Formula rationalEffectRecognition) {
        super(table, surfacePerformative,
                (surfaceContentFormat == null ? new Class[] {IdentifyingExpression.class} : surfaceContentFormat),
                (surfaceContentFormatMessage == null ? "an IRE" : surfaceContentFormatMessage),
                (rationalEffectRecognition == null ? SLPatternManip.fromFormula("(exists ??var (B ??receiver ??formula))") : rationalEffectRecognition),
                SLPatternManip.fromFormula("(exists ?y (B ??receiver (= ??ire ?y)))"),
                SLPatternManip.fromFormula("(and (exists ?y (B ??sender (= ??ire ?y))) (not (B ??sender (or (exists ?y (B ??receiver (= ??ire ?y))) (exists ?y (U ??receiver (= ??ire ?y))) ))))"),
                SLPatternManip.fromFormula("(exists ?y (B ??sender (= ??ire ?y)))"),
                SLPatternManip.fromFormula("(B ??sender (exists ?y (B ??receiver (= ??ire ?y))))"));
        setPerformative(ACLMessage.INFORM_REF);
        this.contentSize = 1;
    }
    
    /**
     * Creates a new <code>InformRef</code> prototype.
     * The surface content format, the surface content format message, and
     * the rational effect recognition pattern are the default ones. 
     * The surface performative is set to <code>INFORM_REF</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public InformRef(SemanticActionTable table) {
        this(table, ACLMessage.INFORM_REF, null, null, null);
    }

    /**
     * Returns an instance of <code>InformRef</code>
     * @return an instance of <code>InformRef</code>
     */
    public CommunicativeActionProto createInstance() {
        return new InformRef(table);
    }
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        setContent(surfaceContent);
        return this;
    }

    /**
     * @inheritDoc
     */
    public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        setReceiver(rationalEffectMatching.getTerm("receiver"));
        if (!(rationalEffectMatching.getFormula("formula") instanceof EqualsNode)) {
            setSurfaceContentElement(0, new AnyNode(rationalEffectMatching.getTerm("var"), rationalEffectMatching.getFormula("formula")));
            return true;
        }
        else {
            EqualsNode equalsNode = (EqualsNode)rationalEffectMatching.getFormula("formula");
            if (equalsNode.as_left_term() instanceof IdentifyingExpression
                    && equalsNode.as_right_term().equals(rationalEffectMatching.getTerm("var"))) {
                setSurfaceContentElement(0, equalsNode.as_left_term());
                return true;
            }
        }
        return false;
    } // End of newAction/2
    
    /**
     * @inheritDoc
     */
    public Behaviour computeBehaviour() {
        return new InformRefBehaviour(this);
    }

    /**
	 * @inheritDoc
	 */
    public Formula computeFeasibilityPrecondition() throws WrongTypeException {
        return (Formula)SLPatternManip.instantiate(feasibilityPreconditionPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "ire", getContentElement(0));
    } // End of feasibilityPreconditionCalculation/0
    
    /**
     * @inheritDoc
     */
    public  Formula computePersistentFeasibilityPreconditon() throws WrongTypeException{
        return (Formula)SLPatternManip.instantiate(persistentFeasibilityPreconditionPattern,
                "sender", getAuthor(),
                "ire", getContentElement(0));
    } // End of persistentFeasibilityPreconditonCalculation/0
    
    /**
     * @inheritDoc
     */
    public  Formula computeRationalEffect() throws WrongTypeException {
        return (Formula)SLPatternManip.instantiate(rationalEffectPattern,
                "receiver", getReceiver(),
                "ire", getContentElement(0));
    } // End of rationalEffectCalculation/0
    
    /**
     * @inheritDoc
     */
    public  Formula computePostCondition() throws WrongTypeException {
        return (Formula)SLPatternManip.instantiate(postConditionPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "ire", getContentElement(0));
    } // End of postConditionCalculation/0
} // End of class InformRef
