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
 * Propose.java
 * Created on 24 f�vr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * The action of submitting a proposal to perform a certains action, given
 * certain preconditions. The content contains an action description, 
 * representing the action that the sender is proposing to perform, and a 
 * proposition representing the preconditions on the performance of the action.
 * <p>
 * This class is not intended to be directly used by developers. It is loaded
 * in semantic agents' semantic action table by the
 * {@link jade.semantics.actions.DefaultSemanticActionLoader}.
 * </p>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/02/24 Revision: 1.0
 * @since JSA 1.0
 */
public class Propose extends ActConditionInform {
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Creates a new <code>Propose</code> prototype. By default, the inform content
     * is set to "(or (not (I ??receiver (done ??act ??condition))) (I ??sender (done ??act ??condition)))".
     * 
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     */
    
	public Propose(SemanticCapabilities capabilities,
			       int surfacePerformative, 
			       Class[] surfaceContentFormat, 
			       String surfaceContentFormatMessage) {
        super(capabilities, 
			  surfacePerformative, 
			  surfaceContentFormat, 
			  surfaceContentFormatMessage, true,
			  SL.formula("(or (not (I ??receiver (done ??act ??condition))) (I ??sender (done ??act ??condition)))"));
    }
    
    /**
     * Creates a new <code>Propose</code> prototype.
     * The surface content format, and the surface content format message 
     * are set to <code>null</code>. 
     * The surface performative is set the default ones.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public Propose(SemanticCapabilities capabilities) {
        this(capabilities, ACLMessage.PROPOSE, null, null);
    }

    /**
     * Returns an instance of <code>Propose</code>
     * @return an instance of <code>Propose</code>
     */
    @Override
	public CommunicativeActionProto createInstance() {
        return new Propose(getSemanticCapabilities());
    }
    
    /**
     * @inheritDoc
     */
    @Override
	protected Formula instantiateInformContentPattern(Content surfaceContent) throws WrongTypeException {
        return (Formula)SL.instantiate(super.instantiateInformContentPattern(surfaceContent),
                "receiver", getReceiver());
    }
}
