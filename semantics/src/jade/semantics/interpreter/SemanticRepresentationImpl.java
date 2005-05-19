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
 * SemanticRepresentationImpl.java
 * Created on 29 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.lang.acl.ACLMessage;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * Class that implements the interface SemanticRepresentation.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class SemanticRepresentationImpl implements SemanticRepresentation {
    
    /**
     * Index of the initial deductive step for this semantic representation
     */
    public static final int INITIAL_DEDUCTIVE_STEP = 1;
    
    /**
     * An ACL message
     */
    private ACLMessage aclMessage;

    /**
     * A SL Formula
     */
    private Formula slFormula;

    /**
     * A Deductive Step index
     */
    private int semanticInterpretationPrincipleIndex;

    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor
     * @param msg an ACL message
     * @param sl a SL Formula
     * @param index the index of the first deductive step it is possible to apply
     * on this <code>SemanticRepresentation</code>. 
     */
    public SemanticRepresentationImpl(ACLMessage msg, Formula sl, int index) {
        aclMessage = msg;
        slFormula = sl;
        semanticInterpretationPrincipleIndex = index;
    } // End of constructor/3
    
    /**
     * Constructor
     * The message is null.
     * The formula is null.
     */
    public SemanticRepresentationImpl() {
        this(null, null, INITIAL_DEDUCTIVE_STEP);
    } // End of constructor/0

    /**
     * Constructor
     * The message is null.
     * @param formula a SL Formula 
     */
    public SemanticRepresentationImpl(Formula formula) {
        this(null, formula, INITIAL_DEDUCTIVE_STEP);
    } // End of constructor/0

    /**
     * Creates a semantic representation from a given SL Formula.
     * WARNING: the formula is given by a String, in which the agent is
     * identified by the "??agent" meta-variable.
     * @param formula the formula defining the semantic representation
     * @param agent the agent to be substituted in formula
     * @throws PatternCannotBeinstantiated
     * @throws WrongVariableType
     */
    public SemanticRepresentationImpl(String formula, Term agent) throws WrongTypeException {
        aclMessage = null;
		try {
			Formula f = SLParser.getParser().parseFormula(formula, true);
			SLPatternManip.set(f, "??agent", agent);
			slFormula = (Formula)SLPatternManip.instantiate(f);
		}
		catch(ParseException pe) {pe.printStackTrace();}
        semanticInterpretationPrincipleIndex = INITIAL_DEDUCTIVE_STEP;
    } // End of SemanticRepresentationImpl/2
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * @return Returns the ACL message
     * @see jade.semantics.interpreter.SemanticRepresentation#getMessage()
     */
    public ACLMessage getMessage() {
        return aclMessage;
    } // End of getMessage/0

    /**
     * Sets the ACL message
     * @param msg the message to set
     * @see jade.semantics.interpreter.SemanticRepresentation#setMessage(jade.lang.acl.ACLMessage)
     */
    public void setMessage(ACLMessage msg) {
        aclMessage = msg;
    } // End of setMessage/1

    /**
     * @return Returns the SL representation
     * @see jade.semantics.interpreter.SemanticRepresentation#getSLRepresentation()
     */
    public Formula getSLRepresentation() {
        return slFormula;
    } // End of getSLRepresentation/0

    /**
     * Sets teh SL representation
     * @param formula the formula to set
     * @see jade.semantics.interpreter.SemanticRepresentation#setSLRepresentation(jade.lang.sl.grammar.Formula)
     */
    public void setSLRepresentation(Formula formula) {
        slFormula = formula;
    } // End of setSLRepresentation/1

    /**
     * @return Returns the index of the deductive step
     * @see jade.semantics.interpreter.SemanticRepresentation#getDeductiveStepIndex()
     */
    public int getSemanticInterpretationPrincipleIndex() {
        return semanticInterpretationPrincipleIndex;
    } // End of getSemanticInterpretationPrincipleIndex/0

    /**
     * Sets the deductive step index
     * @param i the index to set
     * @see jade.semantics.interpreter.SemanticRepresentation#setDeductiveStepIndex(int)
     */
    public void setSemanticInterpretationPrincipleIndex(int i) {
        semanticInterpretationPrincipleIndex = i;
    } // End of setSemanticInterpretationPrincipleIndex/1
    
    /**
     * Returns the SL formula that represents the semantic representation
     * @return the SL formula that represents the semantic representation
     */
    public String toString() {
       return slFormula.toString();  
    } // End of ToString/0
    
} // End of class SemanticRepresentationImpl
