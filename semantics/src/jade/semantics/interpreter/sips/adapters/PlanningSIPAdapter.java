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
 * Planning.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips.adapters;

import jade.semantics.behaviours.IntentionalBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This semantic interpretation principle provides the Jade agent with a general 
 * mean of planning.
 * It calls an external component that returns a Jade Behaviour that implements 
 * a way to reach an input goal <i>phi</i>, and adds this Behaviour to the 
 * agent.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public abstract class PlanningSIPAdapter extends SemanticInterpretationPrinciple {
        	
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * @param goalPattern the goal to reach 
     * @param addIt if true the SIP is automatically added t the sip table of the agent 
     * semantic interpretation principle
     */
    public PlanningSIPAdapter(SemanticCapabilities capabilities, Formula goalPattern) {
        this(capabilities, goalPattern.toString());
    } 
	
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * @param goalPattern the goal to reach 
     * @param addIt if true the SIP is automatically added t the sip table of the agent 
     * semantic interpretation principle
     */
    public PlanningSIPAdapter(SemanticCapabilities capabilities, String goalPattern) {
        super(capabilities,
        	  "(I ??myself " + goalPattern + ")",
        	  SemanticInterpretationPrincipleTable.PLANNING);
    } 

    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Adds a new intentional behaviour ({@link IntentionalBehaviour}) on the behaviour
     * found by the method <code>finPlan</code> of the <code>Planner</code> interface.
     * @param sr a semantic representation
     * @return if the pattern "(I ??agent ??phi)"
     * matches, and the current agent believes ??phi and the agent find 
     * an action ??act, this method returns an ArrayLIst with the same SR which
     * SIP index is increased by one. Returns null in other cases. 
     * @throws SemanticInterpretationPrincipleException if any exception occurs
     */
    final public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
        try {
            MatchResult matchResult = pattern.match(sr.getSLRepresentation());
            if (matchResult != null) {
                ActionExpression plan = doApply(matchResult, sr);
                if ( plan != null ) {
                    potentiallyAddBehaviour(new IntentionalBehaviour(
                            plan,
							myCapabilities,
                            new SemanticRepresentation(sr, getIndex()+1)));
                    ArrayList result = new ArrayList();
                    sr.setSemanticInterpretationPrincipleIndex(SemanticRepresentation.NO_LONGER_APPLICABLE);
                    result.add(sr);
                    return result;
                }                 
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } 
	
	/**
	 * @param matchResult
	 * @param sr the semantic representation to which applies SIP 
	 * @return
	 */
	public abstract ActionExpression doApply(MatchResult matchResult, SemanticRepresentation sr);
} // End of class Planning
