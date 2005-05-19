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
 * SemanticActionImpl.java
 * Created on 28 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions;

import jade.core.behaviours.Behaviour;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;


/**
 * This class is an implementation of the <code>SemanticAction</code> interface.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public abstract class SemanticActionImpl implements SemanticAction {
    
    /**
     * The author (sender) of the action
     */
    private Term sender;

    /**
     * Precondition of feasibility
     */
    protected Formula feasibilityPrecondition;

    /**
     * Persistent precdondition of feasibility
     */
    protected Formula persistentFeasibilityPrecondition;

    /**
     * Rational effect
     */
    protected Formula rationalEffect;

    /**
     * Post-condition of the action
     */
    protected Formula postCondition;

    /**
     * The behaviour of the action
     */
    private Behaviour behaviour;
    
    /**
     * The semantic action table that contains this semantic action
     */
    protected SemanticActionTable table;
    
    /********************************************************************/
    /** 			CONSTRUCTOR
    /********************************************************************/

    /**
     * Constructor
     * @param table the semantic action table that contains this semantic action
     */
    public SemanticActionImpl(SemanticActionTable table) {
    	this.table = table;
//    	feasibilityPrecondition = new TrueNode();
//    	persistentFeasibilityPrecondition = new TrueNode();
//    	rationalEffect = new TrueNode();
//    	postCondition = new TrueNode();
        feasibilityPrecondition = null;
        persistentFeasibilityPrecondition = null;
        rationalEffect = null;
        postCondition = null;

    } // End of SemanticActionImpl/1
    
    /********************************************************************/
    /** 			PUBLIC METHODS
    /********************************************************************/

    /**
     * @return Returns the author (sender) of this action
     */
    public Term getSender() {
        return sender;
    } // End of getAuthor/0
    
    /**
     * @param author The author to set.
     */
    public void setSender(Term author) {
        this.sender = author;
    } // End of setAuthor/1

     /**
     * @return Returns the feasibility precondition.
     **/
     public Formula getFeasibilityPrecondition() {
         try {
             if (feasibilityPrecondition == null) {
                 feasibilityPrecondition = feasibilityPreconditionCalculation();
             }
         } catch (WrongTypeException e) {
             e.printStackTrace();
             feasibilityPrecondition = null;
         }
         return feasibilityPrecondition;
     } // End of getFeasibilityPrecondition/0

     /**
     * Set the feasibility precondition of the action
     * @param formula the feasibility precondition to set
     **/
     public void setFeasibilityPrecondition (Formula formula) {
         feasibilityPrecondition = formula.getSimplifiedFormula();
     } // End of setFeasibilityPrecondition/1

     /**
      * @return Returns the persistentFeasibilityPrecondition.
      */
     public Formula getPersistentFeasibilityPrecondition() {
         try {
             if (persistentFeasibilityPrecondition == null) {
                 persistentFeasibilityPrecondition = persistentFeasibilityPreconditonCalculation();
             }

         } catch (WrongTypeException e) {
            e.printStackTrace();
            persistentFeasibilityPrecondition = null;
         }
         return persistentFeasibilityPrecondition;
     } // End of getPersitentFeasibilityPrecondition/0
     
     /**
      * @param persistentFeasibilityPrecondition The persistentFeasibilityPrecondition to set.
      */
     public void setPersistentFeasibilityPrecondition(Formula persistentFeasibilityPrecondition) {
         this.persistentFeasibilityPrecondition = persistentFeasibilityPrecondition;
     } // End of setPersitentFeasibilityPrecondition/1

     /**
     * @return Returns the rational effect of the action
     **/
     public Formula getRationalEffect() {
         try {
             if (rationalEffect == null) {
                 rationalEffect = rationalEffectCalculation();
             }
         } catch (WrongTypeException e) {
            e.printStackTrace();
            rationalEffect = null;
         }
         return rationalEffect;
     } // End of getRationalEffect/0

     /**
     * Sets the rational effect of the action
     * @param formula the rational effect to set
     **/
     public void setRationalEffect (Formula formula) {
         rationalEffect = formula;
     } // End of setRationalEffect/1

 	/**
 	 * @return Returns the postCondition.
 	 */
 	public Formula getPostCondition() {
        try {
            if (postCondition == null) {
                postCondition = postConditionCalculation();
            }
        } catch (WrongTypeException e) {
           e.printStackTrace();
           postCondition = null;
        }
        return postCondition;
 	} // End of getPostCondition/0
 	
 	/**
 	 * @param postCondition The postCondition to set.
 	 */
 	public void setPostCondition(Formula postCondition) {
 		this.postCondition = postCondition;
 	} // End of setPostCondition/1
 	
     /**
     * @return Return the body of the action
     **/
     public Behaviour getBehaviour() {
         return behaviour;
     } // End of getBehaviour/0

     /**
 	 * @param behaviour The behaviour to set.
 	 */
 	public void setBehaviour(Behaviour behaviour) {
 		this.behaviour = behaviour;
 	} // End of setBehaviour/1

	/**
	 * Creates a new instance of this prototype of semantic action from
	 * the specified rational effect.
	 * Should be overriden when using the rational effect of the action
	 * (returns null by default).
	 * 
	 * @param rationalEffect
	 * 				a formula that specifies the rational effet of the instance to create
	 * @param agentName the name of the agent
	 * @return a new instance of the semantic action, the rational effect of
	 * which is specified, or null if no instance of the semantic action with
	 * the specified rational effect can be created
	 */
	public SemanticAction newAction(Formula rationalEffect,	Term agentName) {
	    return null;
	} // End of newAction/2

 	/**************************************************************************/
 	/**								ABSTRACT METHODS						 **/
 	/**************************************************************************/

 	/**
	  * Creates a new instance of this prototype of semantic action from
	  * the specified action expression.
	  * 
	  * @param actionExpression
	  * 			an expression of action that specifies the instance to create
	 * @return a new instance of the semantic action, the action expression of
	 * which is specified, or null if no instance of the semantic action with
	 * the specified action expression can be created
	  */
	public abstract SemanticAction newAction(ActionExpression actionExpression);

    public abstract Formula feasibilityPreconditionCalculation() throws WrongTypeException;
    public abstract Formula persistentFeasibilityPreconditonCalculation() throws WrongTypeException;
    public abstract Formula rationalEffectCalculation() throws WrongTypeException;
    public abstract Formula postConditionCalculation() throws WrongTypeException;
    
} // End of class SemanticActionImpl
