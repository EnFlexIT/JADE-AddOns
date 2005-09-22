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
 * Disconfirm.java
 * Created on 9 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * This class represents the semantic action: <code>Disconfirm</code>. <br>
 * The sender informs the receiver that a given proposition is false, where the
 * receiver is known to believe, or believe it likely that, the proposition is 
 * true.<br>
 * The content of this action is a proposition.<br>
 * <code>Disconfirm</code> indicates that the sending agent:
 * <ul>
 * <li> believes that some proposition is false,
 * <li> intends that the receiving agent also comes to believe that the 
 * propositon is false,
 * <li> believes that the receiver either believes the proposition, or is 
 * uncertain of the thruth of the proposition.
 * </ul>
 * From the receiver's viewpoint, receiving a <code>Disconfirm</code> message 
 * entitles it to believe that:
 * <ul>
 * <li> the sender believes the proposition that is the content of the message
 * is false,
 * <li> the sender whishes the receiver to believe the negated proposition also.
 * </ul> 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class Disconfirm extends Assertive {

	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Creates a new <code>DisConfirm</code> Action prototype. The performative is set to 
     * <code>DISCONFIRM</code>. The feasibility precondition pattern,
     * the persistent feasibility pattern and the postcondition pattern are
     * respectively set to:
     * <ul>
     * <li>"(and (B ??sender (not ??formula)) (B ??sender (or (B ??receiver ??formula) (U ??receiver ??formula))))"
     * <li>"(B ??sender (not ??formula))"
     * <li>"(B ??sender (B ??receiver (not ??formula)))"
     * </ul>
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param rationalEffectRecognition pattern used to recognized the
     * rational effect of this action
     */
    public Disconfirm(SemanticActionTable table, int surfacePerformative,
            Class[] surfaceContentFormat, String surfaceContentFormatMessage, Formula rationalEffectRecognition) {
        super(table, surfacePerformative, surfaceContentFormat, surfaceContentFormatMessage, rationalEffectRecognition,
                SLPatternManip.fromFormula("(and (B ??sender (not ??formula)) (B ??sender (or (B ??receiver ??formula) (U ??receiver ??formula))))"),
                SLPatternManip.fromFormula("(B ??sender (not ??formula))"),
                SLPatternManip.fromFormula("(B ??sender (B ??receiver (not ??formula)))"));
        setPerformative(ACLMessage.DISCONFIRM);
    }
    
    /**
     * Creates a new <code>DisConfirm</code> Action prototype.
     * The surface content format, the surface content format message, and
     * the rational effect recognition pattern are the default ones. 
     * The surface performative is set to <code>DISCONFIRM</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public Disconfirm(SemanticActionTable table) {
        this(table, ACLMessage.DISCONFIRM, null, null, null);
    }
    /**
     * Returns a new <code>DisConfirm</code> instance
     * @return a new <code>DisConfirm</code> instance
     */
    public CommunicativeActionProto createInstance() {
        return new Disconfirm(table);
    }

    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * {@inheritDoc}
     */
    public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        setReceiver(rationalEffectMatching.getTerm("receiver"));
        setSurfaceContentElement(0, new NotNode(rationalEffectMatching.getFormula("formula")));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Formula computeRationalEffect() throws WrongTypeException {
        return (Formula)SLPatternManip.instantiate(rationalEffectRecognitionPattern,
                "receiver", getReceiver(),
                "formula", new NotNode((Formula)getContentElement(0)));
    }
} // End of class Disconfirm
