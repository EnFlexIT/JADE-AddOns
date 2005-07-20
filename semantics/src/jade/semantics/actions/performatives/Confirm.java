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
 * Confirm.java
 * Created on 9 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * This class represents the semantic action: <code>Confirm</code>.<br>
 * The sender informs the receiver that a given proposition is true, where the
 * receiver is known to be uncertain about the proposition. <br>
 * The content of this action is a proposition. <br>
 * <code>Confirm</code> indicates that the sending agent:
 * <ul>
 * <li>believes that some proposition is true,
 * <li>intends that the receiving agent also comes to believe that the
 * propositon is true,
 * <li>believes that the receiver is uncertain of the truth of the proposition.
 * </ul>
 * From the receiver's viewpoint, receiving a <code>Confirm</code> message
 * entitles it to believe that:
 * <ul>
 * <li>the sender believes the proposition that is the content of the message,
 * <li>the sender whishes the receiver to believe that proposition also.
 * </ul>
 * 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class Confirm extends Assertive {
    
	/**
	 * Creates a new <code>Confirm</code> Action prototype. The performative is set to 
     * <code>CONFIRM</code>. The feasibility precondition pattern,
     * the persistent feasibility pattern and the postcondition pattern are
     * respectively set to:
     * <ul>
     * <li>"(and (B ??sender ??formula) (B ??sender (U ??receiver ??formula)))"
     * <li>"(B ??sender ??formula)"
     * <li>"(B ??sender (B ??receiver ??formula))"
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
    public Confirm(SemanticActionTable table, int surfacePerformative,
            Class[] surfaceContentFormat, String surfaceContentFormatMessage, Formula rationalEffectRecognition) {
        super(table, surfacePerformative, surfaceContentFormat, surfaceContentFormatMessage, rationalEffectRecognition,
                SLPatternManip.fromFormula("(and (B ??sender ??formula) (B ??sender (U ??receiver ??formula)))"),
                SLPatternManip.fromFormula("(B ??sender ??formula)"),
                SLPatternManip.fromFormula("(B ??sender (B ??receiver ??formula))"));
        setPerformative(ACLMessage.CONFIRM);
    }
    
    /**
     * Creates a new <code>Confirm</code> Action prototype.
     * The surface content format, the surface content format message, and
     * the rational effect recognition pattern are the default ones. 
     * The surface performative is set to <code>CONFIRM</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public Confirm(SemanticActionTable table) {
        this(table, ACLMessage.CONFIRM, null, null, null);
    }
    
    /**
     * Returns a new <code>Confirm</code> instance
     * @return a new <code>Confirm</code> instance
     */
    public CommunicativeActionImpl createInstance() {
        return new Confirm(table);
    }
} // End of class Confirm
