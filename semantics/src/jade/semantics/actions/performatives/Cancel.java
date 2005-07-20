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
 * Cancel.java
 * Created on 21 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * The action of one agent informing another agent that the first agent no longer
 * has the intention that the second agent perform some action. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/04/21 Revision: 1.0
 */
public class Cancel extends Disconfirm {
    
    /**
     * Pattern used to build the disconfirm content corresponding
     * to a cancel
     */
    private Formula disconfirmContentPattern = SLPatternManip.fromFormula("(I ??sender (done ??action))");
        
    /** ****************************************************************** */
    /** CONSTRUCTOR * */
    /** ****************************************************************** */

    /**
     * Creates a new <code>Cancel</code> prototype. The performative is set to 
     * <code>CANCEL</code>. 
     * By default, the surface content format is set to <code>ActionExpression</code>.
     * By default, the rational effect recognition pattern is set to  
     * @param table the SemanticActionTable, which this action prototype belongs
     * to "(B ??receiver (not (I ??sender (done ??action))))".
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param rationalEffectRecognition pattern used to recognized the
     * rational effect of this action
     */
    public Cancel(SemanticActionTable table, int surfacePerformative,
            Class[] surfaceContentFormat, String surfaceContentFormatMessage, Formula rationalEffectRecognition) {
        super(table, surfacePerformative,
                (surfaceContentFormat == null ? new Class[] {ActionExpression.class} : surfaceContentFormat),
                (surfaceContentFormatMessage == null ? "an action expression" : surfaceContentFormatMessage),
                (rationalEffectRecognition == null ? SLPatternManip.fromFormula("(B ??receiver (not (I ??sender (done ??action))))") : rationalEffectRecognition));
    } // End of Cancel/4
    
    /**
     * Creates a new <code>Cancel</code> prototype.
     * The surface content format, the surface content format message, and
     * the rational effect recognition pattern are the default ones. 
     * The surface performative is set to <code>CANCEL</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public Cancel(SemanticActionTable table) {
        this(table, ACLMessage.CANCEL, null, null, null);
    } // End of Cancel/1
    
    /**
     * Returns an instance of <code>Cancel</code>
     * @return an instance of <code>Cancel</code>
     */
    public CommunicativeActionImpl createInstance() {
        return new Cancel(table);
    } // End of createInstance/0

    /** ****************************************************************** */
    /** METHODS * */
    /** ****************************************************************** */

    /**
     * @see jade.semantics.actions.performatives.CommunicativeActionProto#doNewAction(Content)
     */
    public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        Content disconfirmContent = new ContentNode(new ListOfContentExpression());
        try {
            disconfirmContent.addContentElement(SLPatternManip.instantiate(disconfirmContentPattern,
                            "sender", getAuthor(),
                            "action", surfaceContent.getContentElement(0)));
            return super.doNewAction(disconfirmContent);
        }
        catch (WrongTypeException e) {
            throw new SemanticInterpretationException("ill-formed-message", new WordConstantNode(""));
        }
    } // End of doNewAction/1

    /**
     * 
     * @see jade.semantics.actions.performatives.CommunicativeActionProto#setFeaturesFromRationalEffect(jade.semantics.lang.sl.tools.MatchResult)
     */
    public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        if (getAuthor().equals(rationalEffectMatching.getTerm("sender"))) {
            setReceiver(rationalEffectMatching.getTerm("receiver"));
            setSurfaceContentElement(0, rationalEffectMatching.getTerm("action"));
            return true;
        }
        return false;
    } // End of setFeaturesFromRationalEffect/1
    
} // End of class Cancel
