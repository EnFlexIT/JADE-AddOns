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
 * RequestWhen.java
 * Created on 18 mai 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * The sender wants the receiver to perform some action when some given 
 * proposition becomes true. The content contains a description of the action
 * to perform, and the proposition. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/18 Revision: 1.0
 */
public class RequestWhen extends ActConditionInform {
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a new <code>RequestWhen</code> prototype. By default, the inform content
     * is set to "(I ??sender (done ??act (and (exists ?e (done ?e (not (B ??receiver ??condition)))) (B ??receiver ??condition))))". 
     * The action is not from the sender (the boolean <code>isActFromSender</code> is set to <code>false</code>
     * {@link ActConditionInform#ActConditionInform(SemanticActionTable, int, Class[], String, boolean, Formula)}).
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     */
    public RequestWhen(SemanticActionTable table, int surfacePerformative, Class[] surfaceContentFormat, String surfaceContentFormatMessage) {
        super(table, surfacePerformative, surfaceContentFormat, surfaceContentFormatMessage, false,
                SLPatternManip.fromFormula("(I ??sender (done ??act (and (exists ?e (done ?e (not (B ??receiver ??condition))))" +
                "                             (B ??receiver ??condition))))"));
    }
    
    /**
     * Creates a new <code>RequestWhen</code> prototype.
     * The surface content format, and the surface content format message 
     * are the default ones. 
     * The surface performative is set to <code>REQUEST_WHEN</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */   
    public RequestWhen(SemanticActionTable table) {
        this(table, ACLMessage.REQUEST_WHEN, null, null);
    }
    
    /**
     * Returns an instance of <code>RequestWhen</code>
     * @return an instance of <code>RequestWhen</code>
     */
    public CommunicativeActionProto createInstance() {
        return new RequestWhen(table);
    }
    
    /**
     * @inheritDoc
     */
    protected Node instantiateInformContentPattern(Content surfaceContent) throws WrongTypeException {
        return SLPatternManip.instantiate(super.instantiateInformContentPattern(surfaceContent),
                "receiver", getReceiver());
    }
} // End of class RequestWhen
