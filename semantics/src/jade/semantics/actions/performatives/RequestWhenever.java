/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

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
 * RequestWhenever.java
 * Created on 19 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * This class represents the semantic action: <code>RequestWhenever</code>. <br>
 * The sender wants the receiver to perform some action as soon as some 
 * proposition becomes true ans thereafter each time the proposition becomes true again..<br>
 * The content of this action is a tuple of an action expression and a 
 * proposition.<br>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class RequestWhenever extends ActConditionInform {
    
    /**
     * Creates a new <code>RequestWhenever</code> prototype. By default, the inform content
     * is set to "(or (I ??sender (done ??act )) (or (forall ?e (not (done ?e (not (B ??receiver ??condition))))) (not (B ??receiver ??condition))))". 
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
    public RequestWhenever(SemanticCapabilities capabilities,
			               int surfacePerformative, 
			               Class[] surfaceContentFormat, 
			               String surfaceContentFormatMessage) {
        super(capabilities,
			  surfacePerformative, 
			  surfaceContentFormat, 
			  surfaceContentFormatMessage, 
			  false,
			  SL.fromFormula("(or (I ??sender (done ??act )) " +
                                           "    (or (forall ?e (not (done ?e (not (B ??receiver ??condition)))))" +
                                           "        (not (B ??receiver ??condition))))"));
    }

    /**
     * Creates a new <code>RequestWhenever</code> prototype.
     * The surface content format, and the surface content format message 
     * are the default ones. 
     * The surface performative is set to <code>REQUEST_WHENEVER</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */   
    public RequestWhenever(SemanticCapabilities capabilities) {
        this(capabilities, ACLMessage.REQUEST_WHENEVER, null, null);
    }

    /**
     * Returns an instance of <code>RequestWhenever</code>
     * @return an instance of <code>RequestWhenever</code>
     */
    public CommunicativeActionProto createInstance() {
        return new RequestWhenever(getSemanticCapabilities());
    }
    
    /**
     * @inheritDoc
     */
    protected Node instantiateInformContentPattern(Content surfaceContent) throws WrongTypeException {
        return SL.instantiate(super.instantiateInformContentPattern(surfaceContent),
                "receiver", getReceiver());
    }
} // End of class RequestWhenever
