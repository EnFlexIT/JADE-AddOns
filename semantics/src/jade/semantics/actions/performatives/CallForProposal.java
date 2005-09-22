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
 * CallForProposal.java
 * Created on 24 févr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.UnexpectedContentSIException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * The action of calling for proposals to perform a given action. The content 
 * of this action contains an action expression denoting the action to be done, 
 * and a referential expression defining a single-parameter proposition which
 * gives the preconditions of the action.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/02/24 Revision: 1.0 
 */
public class CallForProposal extends QueryRef {

    /**
     * Pattern used to build the content of the informRef/queryRef
     * corresponding to a cfp action
     */
    private Formula informRefContentPattern = SLPatternManip.fromFormula("(or (not (I ??sender (done ??act ??phi))) (I ??receiver (done ??act ??phi)))");
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Creates a new <code>CallForProposal</code> prototype. By default, the surface content format
     * is set to [ActionExpression, IdentifyingExpression]. 
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
    public CallForProposal(SemanticActionTable table, int surfacePerformative,
            Class[] surfaceContentFormat, String surfaceContentFormatMessage, Formula rationalEffectRecognition) {
        super(table, surfacePerformative,
                (surfaceContentFormat == null ? new Class[] {ActionExpressionNode.class, IdentifyingExpression.class} : surfaceContentFormat),
                (surfaceContentFormatMessage == null ? "an action and an IRE" : surfaceContentFormatMessage),
                rationalEffectRecognition);
    }
    
    /**
     * Creates a new <code>CallForProposal</code> prototype.
     * The surface content format, the surface content format message, and
     * the rational effect recognition pattern are the default ones. 
     * The surface performative is set to <code>QUERY_REF</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public CallForProposal(SemanticActionTable table) {
        this(table, ACLMessage.CFP, null, null, null);
    }
    
    /**
     * Returns an instance of <code>CallForProposal</code>
     * @return an instance of <code>CallForProposal</code>
     */
    public CommunicativeActionProto createInstance() {
        return new CallForProposal(table);
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
            Content queryRefContent = new ContentNode(new ListOfContentExpression());
            try {
                IdentifyingExpression ire = (IdentifyingExpression)surfaceContent.getContentElement(1).getClone();
                ire.as_formula((Formula)SLPatternManip.instantiate(informRefContentPattern,
                        "sender", getAuthor(),
                        "receiver", getReceiver(),
                        "act", act,
                        "phi", ire.as_formula()));
                queryRefContent.addContentElement(ire);
                return super.doNewAction(queryRefContent);
            }
            catch (WrongTypeException e) {
                throw new SemanticInterpretationException("ill-formed-message", new WordConstantNode(""));
            }
        }
        else {
            throw new UnexpectedContentSIException(getSurfacePerformative(),
                    "an action from the receiver [" + getReceiver() + "]", act.as_agent().toString());
        }
    } // End of doNewAction/1
    
    /**
     * @inheritDoc
     */
    public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        InformRef informRef = (InformRef)table.getSemanticActionInstance((ActionExpression)rationalEffectMatching.getTerm("action"));
        MatchResult informRefContentMatching = SLPatternManip.match(informRefContentPattern,
                ((IdentifyingExpression)informRef.getContentElement(0)).as_formula());
        if (informRefContentMatching != null && getAuthor().equals(informRefContentMatching.getTerm("sender"))) {
            ActionExpressionNode act = (ActionExpressionNode)informRefContentMatching.getTerm("act");
            if (act.as_agent().equals(informRefContentMatching.getTerm("receiver"))) {
                setReceiver(act.as_agent());
                IdentifyingExpression ire = (IdentifyingExpression)informRef.getContentElement(0).getClone();
                ire.as_formula(informRefContentMatching.getFormula("phi"));
                setSurfaceContentElement(0, act);
                setSurfaceContentElement(1, ire);
                return true;
            }
        }
        return false;
    } // End of setFeaturesFromRationalEffect/1
} // End of class CallForProposal
