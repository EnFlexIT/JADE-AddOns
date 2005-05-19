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
 * CommunicativeAction.java
 * Created on 9 dec. 2004
 * Author : Vincent Louis
 */
package jade.semantics.actions;

import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * This class is an implementation of the <code>SemanticAction</code> interface specific
 * to the use of FIPA performatives. It is the super class of the semantic implementation
 * of all FIPA performatives.
 * @author Vincent LOUIS
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public abstract class CommunicativeAction extends SemanticActionImpl {

    /**
     * The content of the action
     */
    private Content content;

    /**
     * The code of the ACL performative
     */
    private int ACLMessageCode;

    /**
     * The list of receivers of the action
     */
    private ListOfTerm receiverList;

    /********************************************************************/
    /** 			CONSTRUCTOR
    /********************************************************************/

    /**
     * @see jade.semantics.actions.SemanticActionImpl#SemanticActionImpl(jade.core.semantics.actions.SemanticActionTable)
     */
    public CommunicativeAction(SemanticActionTable table) {
    	super(table);
    } // End of CommunicativeAction/1

    /********************************************************************/
    /** 			PUBLIC METHODS
    /********************************************************************/

    /**
	 * @return Returns the receiverList.
	 */
	public ListOfTerm getReceiverList() {
		return receiverList;
	} // End of getReceiverList/0

	/**
	 * @return Returns the fisrt receiver of the receiverList.
	 */
	public Term getReceiver() {
		return receiverList.first();
	} // End of getReceiver/0

	/**
	 * @param receiverList The receiverList to set.
	 */
	public void setReceiverList(ListOfTerm receiverList) {
		this.receiverList = receiverList;
	} // End of setReceiverList/1

	/**
	 * @return Returns the aCLMessageCode.
	 */
	public int getACLMessageCode() {
		return ACLMessageCode;
	} // End of getACLMessageCode/0
	
	/**
	 * @param messageCode The aCLMessageCode to set.
	 */
	public void setACLMessageCode(int messageCode) {
		ACLMessageCode = messageCode;
	} // End of setACLMessageCode/1

	/**
	 * @return Returns the content.
	 */
	public Content getContent() {
		return content;
	} // End of getContent/0
	
	/**
	 * @param content The content to set.
	 */
	public void setContent(Content content) {
		this.content = content;
	} // End of setContent/1
    
    public Formula feasibilityPreconditionCalculation() throws WrongTypeException {
        return null;
    }
    public Formula persistentFeasibilityPreconditonCalculation() throws WrongTypeException {
        return null;
    }
    public Formula rationalEffectCalculation() throws WrongTypeException {
        return null;
    }
    public Formula postConditionCalculation() throws WrongTypeException {
        return null;
    }

} // End of class CommunicativeAction
