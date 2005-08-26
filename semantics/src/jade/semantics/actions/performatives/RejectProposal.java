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
 * RejectProposal.java
 * Created on 24 f�vr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.UnexpectedContentSIException;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * The action of rejecting a proposal to perform some action during a negotiation.
 * It is a general-purpose rejection to a previously submitted proposal. The agent
 * sending the rejection informs the receiver that it has no intention that the
 * recipient performs the given action under the given preconditions. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/02/24 Revision: 1.0
 */
public class RejectProposal extends NonPrimitiveInform {
    
    
    /**
     * Creates a new <code>RejectProposal</code> Action prototype. By default, the inform content
     * is set to "(and (not (I ??sender (done ??act ??phi))) ??psi)". By default, 
     * the surface content format is set to 
     * <code>[ActionExpression, Formula, Formula]</code>.  
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param informContentPattern pattern of the inform content
     */
    public RejectProposal(SemanticActionTable table, int surfacePerformative, Class[] surfaceContentFormat,
            String surfaceContentFormatMessage, Formula informContentPattern) {
        super(table, surfacePerformative,
                (surfaceContentFormat == null ? new Class[] {ActionExpressionNode.class, Formula.class, Formula.class} : surfaceContentFormat),
                (surfaceContentFormatMessage == null ? "an action and two formulas" : surfaceContentFormatMessage),
                (informContentPattern == null ? SLPatternManip.fromFormula("(and (not (I ??sender (done ??act ??phi))) ??psi)") : informContentPattern));
    }
    
    /**
     * Creates a new <code>RejectProposal</code> Action prototype.
     * The surface content format, the surface content format message, and
     * the inform content pattern are the default ones. 
     * The surface performative is set to <code>REJECT_PROPOSAL</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public RejectProposal(SemanticActionTable table) {
        this(table, ACLMessage.REJECT_PROPOSAL, null, null, null);
    }
    
    /**
     * Returns an instance of <code>RejectProposal</code>
     * @return an instance of <code>RejectProposal</code>
     */
    public CommunicativeActionImpl createInstance() {
        return new RejectProposal(table);
    }
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        ActionExpressionNode act = (ActionExpressionNode)surfaceContent.getContentElement(0);
        if (act.as_agent().equals(getReceiver())) {
            return super.doNewAction(surfaceContent);
        }
        else {
            throw new UnexpectedContentSIException(getSurfacePerformative(),
                    "an action from the receiver [" + getReceiver() + "]", act.as_agent().toString());
        }
    }        
    
    /**
     * @inheritDoc
     */
    protected Node instantiateInformContentPattern(Content surfaceContent) throws WrongTypeException {
        return SLPatternManip.instantiate(informContentPattern,
                "sender", getAuthor(),
                "act", surfaceContent.getContentElement(0),
                "phi", surfaceContent.getContentElement(1),
                "psi", surfaceContent.getContentElement(2));
    }    
} // End of class RejectProposal