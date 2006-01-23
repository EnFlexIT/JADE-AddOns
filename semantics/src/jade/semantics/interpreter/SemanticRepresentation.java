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
 * SemanticRepresentation.java
 * Created on 29 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.lang.acl.ACLMessage;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * Class that represents a Semantic Representation.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/10/29 Revision: 1.0
 */
public class SemanticRepresentation {

	/**
	 * Index of the initial semantic interpretation principle for this semantic representation
	 */
	public static final int INITIAL_DEDUCTIVE_STEP = 0;

	/**
	 * An ACL message
	 */
	private ACLMessage aclMessage;

	/**
	 * A SL Formula
	 */
	private Formula slFormula;

	/**
	 * A semantic interpretation principle index
	 */
	private int semanticInterpretationPrincipleIndex;
    
    /**
     * Data to feed back
     */
    private FeedBackData dataToFeedback;

	/** ****************************************************************** */
	/** CONSTRUCTOR * */
	/** ****************************************************************** */

	/**
	 * Creates a new Semantic Representation.
	 * @param msg an ACL message
	 * @param sl a SL Formula
	 * @param index the index of the first semantic interpretation principle it 
     * is possible to apply on this <code>SemanticRepresentation</code>.
     * @param feedbackData data to feed back
	 */
	public SemanticRepresentation(ACLMessage msg, Formula sl, int index, FeedBackData feedbackData) {
		aclMessage = msg;
		slFormula = sl;
		semanticInterpretationPrincipleIndex = index;
        dataToFeedback = feedbackData;
	} // End of constructor/3

    /**
     * Creates a new Semantic Representation. Data to feed back is set to 
     * <code>null</code>.
     * @param msg an ACL message
     * @param sl a SL Formula
     * @param index the index of the first semantic interpretation principle it 
     */
    public SemanticRepresentation(ACLMessage msg, Formula sl, int index) {
        this(msg, sl, index, null);
    } // End of constructor/3

    /**
	 * Creates a new Semantic Representation. The message is <code>null</code>. 
     * The formula is <code>null</code>. The feed back is <code>null</code>
	 */
	public SemanticRepresentation() {
		this(null, null, INITIAL_DEDUCTIVE_STEP, null);
	} // End of constructor/0

	/**
	 * Constructor The message is null.Data to feed back is set to 
     * <code>null</code>.
	 * @param formula a SL Formula
	 */
	public SemanticRepresentation(Formula formula) {
		this(null, formula, INITIAL_DEDUCTIVE_STEP, null);
	} // End of constructor/0

	/**
	 * Creates a semantic representation from a given SL Formula. WARNING: the
	 * formula is given by a String, in which the agent is identified by the
	 * "??agent" meta-variable.
	 * 
	 * @param formula the formula defining the semantic representation
	 * @param agent the agent to be substituted in formula
	 * @throws WrongTypeException if any problem occurs
	 */
	public SemanticRepresentation(String formula, Term agent)
			throws WrongTypeException {
		aclMessage = null;
		Formula f = SLPatternManip.fromFormula(formula);
		slFormula = (Formula) SLPatternManip.instantiate(f, "agent", agent);
		semanticInterpretationPrincipleIndex = INITIAL_DEDUCTIVE_STEP;
	} // End of SemanticRepresentationImpl/2

	/** ****************************************************************** */
	/** METHOD * */
	/** ****************************************************************** */

	/**
	 * @return Returns the ACL message
	 */
	public ACLMessage getMessage() {
		return aclMessage;
	} // End of getMessage/0

	/**
	 * Sets the ACL message
	 * @param msg the message to set
	 */
	public void setMessage(ACLMessage msg) {
		aclMessage = msg;
	} // End of setMessage/1

	/**
     * Returns the SL representation
	 * @return the SL representation
	 */
	public Formula getSLRepresentation() {
		return slFormula;
	} // End of getSLRepresentation/0

	/**
	 * Sets the SL representation
	 * @param formula the formula to set
	 */
	public void setSLRepresentation(Formula formula) {
		slFormula = formula;
	} // End of setSLRepresentation/1

	/**
     * Returns the index of the semantic interpretation principle
	 * @return the index of the semantic interpretation principle
	 */
	public int getSemanticInterpretationPrincipleIndex() {
		return semanticInterpretationPrincipleIndex;
	} // End of getSemanticInterpretationPrincipleIndex/0

	/**
	 * Sets the semantic interpretation principle index
	 * @param i the index to set
	 */
	public void setSemanticInterpretationPrincipleIndex(int i) {
		semanticInterpretationPrincipleIndex = i;
	} // End of setSemanticInterpretationPrincipleIndex/1

    
    /**
     * Returns the dataToFeedback.
     * @return the dataToFeedback.
     */
    public FeedBackData getDataToFeedback() {
        return dataToFeedback;
    }
    /**
     * Sets the data to feed back
     * @param dataToFeedback The dataToFeedback to set.
     */
    public void setDataToFeedback(FeedBackData dataToFeedback) {
        this.dataToFeedback = dataToFeedback;
    }
	/**
	 * Returns the SL formula that represents the semantic representation
	 * @return the SL formula that represents the semantic representation
	 */
	public String toString() {
		return slFormula.toString();
	} // End of ToString/0
    
    /**
     * Two SR are equals if their SL representations are equal.
     * @see Object#equals(java.lang.Object) 
     */
    public boolean equals(Object o) {
        if (o instanceof SemanticRepresentation) {
            return ((SemanticRepresentation)o).getSLRepresentation().equals(slFormula);
        } 
        return false;
    } // End of equals/1
    
    
    /**
     * Represents data to feed back. 
     * @author Vincent Pautret - France Telecom
     * @version Date: 2005/06/10 Revision: 1.0
     */
    public static class FeedBackData {
        
        /**
         * Agent that receives the feed back
         */
        private Term receiver;
        
        /**
         * Goal to reach
         */
        private Formula goal;
        
        /**
         * Constructor
         * @param receiver the reciver
         * @param goal the goal to reach
         */
        public FeedBackData(Term receiver, Formula goal) {
            this.receiver = receiver;
            this.goal = goal;
        }
        
        /**
         * Returns the goal.
         * @return the goal.
         */
        public Formula getGoal() {
            return goal;
        }
        /**
         * Sets the goal.
         * @param goal The goal to set.
         */
        public void setGoal(Formula goal) {
            this.goal = goal;
        }
        /**
         * Returns the receiver.
         * @return the receiver.
         */
        public Term getReceiver() {
            return receiver;
        }
        /**
         * Sets the receiver.
         * @param receiver The receiver to set.
         */
        public void setReceiver(Term receiver) {
            this.receiver = receiver;
        }
    } // End of class FeedBackData
    
} // End of class SemanticRepresentation
