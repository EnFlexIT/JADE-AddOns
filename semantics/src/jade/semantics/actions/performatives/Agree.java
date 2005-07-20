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
 * Agree.java
 * Created on 24 févr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * The action of agreeing to perform some action, possibly in the future. <br>
 * <code>Agree</code> is the general-purpose agreement to a previously submitted <code>request</code>
 * to perform some action. The agent sending the agreement informs the receiver
 * that it does intend to perform the action, but not until the given precondition
 * is true.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/02/24 Revision: 1.0 
 */
public class Agree extends ActConditionInform {
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Creates a new <code>Agree</code> prototype. By default, the inform content
     * is set to "(I ??sender (done ??act ??condition))". The action is from the
     * sender (the boolean <code>isActFromSender</code> is set to <code>true</code>
     * {@link ActConditionInform#ActConditionInform(SemanticActionTable, int, Class[], String, boolean, Formula)}).
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     */
    public Agree(SemanticActionTable table, int surfacePerformative, Class[] surfaceContentFormat, String surfaceContentFormatMessage) {
        super(table, surfacePerformative, surfaceContentFormat, surfaceContentFormatMessage, true,
                SLPatternManip.fromFormula("(I ??sender (done ??act ??condition))"));
    } // End of Agree/4

    /**
     * Creates a new <code>Agree</code> prototype.
     * The surface content format, and the surface content format message 
     * are the default ones. 
     * The surface performative is set to <code>AGREE</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public Agree(SemanticActionTable table) {
        this(table, ACLMessage.AGREE, null, null);
    } // End of Agree/1
    
    /**
     * Returns an instance of <code>Agree</code>
     * @return an instance of <code>Agree</code>
     */
    public CommunicativeActionImpl createInstance() {
        return new Agree(table);
    } // End of createInstance/0
} // End of class Agree
