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
 * Failure.java
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
 * The action of telling another agent that an action was attempted but the 
 * attempt failed. failure is an abbreviation for informing that an act was
 * considered feasible by the sender, but was not completed for some given 
 * reason. The first part of the content is the action not feasible. The second
 * part is the reason for the failure, which is represented by a proposition. 
 * It may be the constant <code>true</code>. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/18 Revision: 1.0
 */
public class Failure extends ActionReasonInform {
    /**
     * Creates a new <code>Failure</code> prototype. By default, the inform content
     * is set to "(and (failure ??action) (and (not (done ??action)) (and (not (I ??sender (done ??action))) ??reason)))".
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     */
     public Failure(SemanticActionTable table, int surfacePerformative, Class[] surfaceContentFormat, String surfaceContentFormatMessage) {
        super(table, surfacePerformative, surfaceContentFormat, surfaceContentFormatMessage,
                SLPatternManip.fromFormula("(and (failure ??action) (and (not (done ??action)) (and (not (I ??sender (done ??action))) ??reason)))"));
    }
    
    /**
     * Creates a new <code>Failure</code> prototype.
     * The surface content format, and the surface content format message, 
     * are the default ones. 
     * The surface performative is set to <code>FAILURE</code>.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
    public Failure(SemanticActionTable table) {
        this(table, ACLMessage.FAILURE, null, null);
    }
    
    /**
     * Returns an instance of <code>Failure</code>
     * @return an instance of <code>Failure</code>
     */
    public CommunicativeActionImpl createInstance() {
        return new Failure(table);
    }
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * {@inheritDoc}
     */
    protected Node instantiateInformContentPattern(Content surfaceContent) throws WrongTypeException {
        return SLPatternManip.instantiate(super.instantiateInformContentPattern(surfaceContent),
                "sender", getAuthor());
    }   
}
