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
 * Subscribe.java
 * Created on 23 févr. 2005
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
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * The act of requesting a persistent intention to notify the sender of the value
 * of a reference, and to notify again whenever the object identified by the 
 * reference changes.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/02/23 Revision: 1.0
 */
public class Subscribe extends RequestWhenever {
    
    /**
     * Pattern used to build and recognize the request-whenever condition
     * corresponding to the subscribe
     */
    private Formula conditionPattern = SLPatternManip.fromFormula("(exists ?y (B ??receiver (= ??ire ?y)))");
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a new <code>Subscribe</code> prototype. By default, the surface content format
     * is set to [IdentifyingExpression].
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     */
    public Subscribe(SemanticActionTable table, int surfacePerformative, Class[] surfaceContentFormat, String surfaceContentFormatMessage) {
        super(table, surfacePerformative,
                (surfaceContentFormat == null ? new Class[] {IdentifyingExpression.class} : surfaceContentFormat),
                (surfaceContentFormatMessage == null ? "an IRE" : surfaceContentFormatMessage));
    }
    
    /**
     * Creates a new <code>Subscribe</code> prototype. The surface content 
     * format, and the surface content format message are the default ones. 
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public Subscribe(SemanticActionTable table) {
        this(table, ACLMessage.SUBSCRIBE, null, null);
    }

    /**
     * Returns an instance of <code>Subscribe</code>
     * @return an instance of <code>Subscribe</code>
     */
    public CommunicativeActionProto createInstance() {
        return new Subscribe(table);
    }
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * {@inheritDoc}
     */
    public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        Content requestWhenEverContent = new ContentNode(new ListOfContentExpression());
        requestWhenEverContent.addContentElement(new InformRef(table).newAction(
                getReceiver(),
                new ListOfTerm(new Term[] {getAuthor()}),
                surfaceContent,
                this).toActionExpression());
        try {
            requestWhenEverContent.addContentElement(SLPatternManip.instantiate(conditionPattern,
                    "receiver", getReceiver(),
                    "ire", surfaceContent.getContentElement(0)));
        }
        catch (WrongTypeException e) {
            throw new SemanticInterpretationException("ill-formed-message", new WordConstantNode(""));
        }
        SLPatternManip.clearMetaReferences(conditionPattern);
        return super.doNewAction(requestWhenEverContent);

    }
    /**
     * {@inheritDoc}
     */
    public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        if (getAuthor().equals(rationalEffectMatching.getTerm("sender"))) {
            InformRef informRef = (InformRef)table.getSemanticActionInstance((ActionExpression)rationalEffectMatching.getTerm("act"));
            MatchResult conditionMatching = SLPatternManip.match(conditionPattern, rationalEffectMatching.getFormula("condition"));
            if (conditionMatching != null
                    && conditionMatching.getTerm("receiver").equals(rationalEffectMatching.getTerm("receiver"))
                    && conditionMatching.getTerm("ire").equals(informRef.getContentElement(0))) {
                setReceiver(rationalEffectMatching.getTerm("receiver"));
                setSurfaceContentElement(0, conditionMatching.getTerm("ire"));
                return true;
            }
        }
        return false;
    }
} // End of class Subscribe
