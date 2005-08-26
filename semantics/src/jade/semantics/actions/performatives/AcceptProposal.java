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
 * AcceptProposal.java
 * Created on 24 f�vr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * This class represents the semantic action: <code>Accept Proposal</code>.<br>
 * It is the action of accepting a previously submitted proposal to perform an 
 * action. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/12 Revision: 1.0
 */
public class AcceptProposal extends ActConditionInform {

    /**
     * Creates a new AcceptProposal prototype. By default, the inform content
     * is set to "(I ??sender (done ??act ??condition))". The action is not from the
     * sender (the boolean <code>isActFromSender</code> is set to <code>false</code>
     *  {@link ActConditionInform#ActConditionInform(SemanticActionTable, int, Class[], String, boolean, Formula)}).
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     */
    public AcceptProposal(SemanticActionTable table, int surfacePerformative, Class[] surfaceContentFormat, String surfaceContentFormatMessage) {
        super(table, surfacePerformative, surfaceContentFormat, surfaceContentFormatMessage, false,
                SLPatternManip.fromFormula("(I ??sender (done ??act ??condition))"));
    } // End of AcceptProposal/4
    
    /**
     * Creates a new <code>AcceptProposal</code> prototype.
     * The surface content format, and the surface content format message 
     * are the default ones. 
     * The surface performative is set to <code>ACCEPT_PROPOSAL</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public AcceptProposal(SemanticActionTable table) {
        this(table, ACLMessage.ACCEPT_PROPOSAL, null, null);
    } // End of AcceptProposal/1

    /**
     * Returns an instance of AcceptProposal
     * @return an instance of AcceptProposal
     */
    public CommunicativeActionImpl createInstance() {
        return new AcceptProposal(table);
    } // End of createInstance/0
} // End of class AcceptProposal