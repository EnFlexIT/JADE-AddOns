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
 * StandardCustomization.java
 * Created on 16 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;


import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.Variable;

/**
 * @author Vincent Pautret
 * @version 
 */
public interface StandardCustomization {

    /**
     * Returns true if the agent accepts the belief transfer, false if not.
     * @param formula the fact to believe
     * @param agent the agent that intends the Jade agent to adopt this belief
     * @return true if the fact to believe can be asserted in the knowledge base. 
     */
    public boolean acceptBeliefTransfer(Formula formula, Term agent);
    
    /**
     * Returns true if the agent accepts the intention transfer, false if not.
     * @param formula the fact to intend
     * @param agent the agent that intends the Jade agent to adopt this intention.
     * @return true if the fact can be asserted in the knowledge base. 
     */
    public boolean acceptIntentionTransfer(Formula formula, Term agent);
    
    /**
     * Returns a list of element that corresponds to the query. One and only one
     * solution is awaited.
     * @param variable the variable that represents the variable in the formula
     * @param formula the incoming formula
     * @param action the action expression 
     * @param agent the agent 
     * @return the list of elements that respond to the query
     */
    public ListOfTerm callForProposalIota(Variable variable, Formula formula, ActionExpression action, Term agent);
    
    /**
     * Returns a list of element that corresponds to the query. One 
     * solution is awaited.
     * @param variable the variable that represents the variable in the formula
     * @param formula the incoming formula
     * @param action the action expression 
     * @param agent the agent 
     * @return the list of elements that respond to the query
     */
    public ListOfTerm callForProposalAny(Variable variable, Formula formula, ActionExpression action, Term agent);

    /**
     * Returns a list of element that corresponds to the query. All the
     * solutions are awaited.
     * @param variable the variable that represents the variable in the formula
     * @param formula the incoming formula
     * @param action the action expression 
     * @param agent the agent 
     * @return the list of elements that respond to the query
     */
    public ListOfTerm callForProposalAll(Variable variable, Formula formula, ActionExpression action, Term agent);
    
    /**
     * Returns true if this method trap the specified formula when an agent <i>agent</i> 
     * is no longer committed to do an action <i>action</i> under the condition <i>formula</i>. 
     * @param agent a semantic agent
     * @param action the action
     * @param formula the incoming formula
     * @return true if the formula is trapped, false if not.
     */
    public boolean trapCancelAction(Term agent, ActionExpression action, Formula formula);
    
    /**
     * Returns true if the specified formula is trapped when an agent <i>agentI</i> 
     * is no longer interested to do an action <i>action</i> under the condition 
     * <i>formula</i>.
     * @param agentI a semantic agent
     * @param action the action
     * @param formula the incoming formula
     * @return true if the formula is trapped, false if not.
     */
    public boolean trapCancelMyAction(Term agentI, ActionExpression action, Formula formula);
    
    /**
     * Returns true if the specified formula is trapped when an agent <i>agent</i> 
     * is commited to do an action <i>action</i> under the condition <i>formula</i>.
     * @param agent a semantic agent
     * @param action the action
     * @param formula the incoming formula
     * @return true if the formula is trapped, false if not.
     */
    public boolean trapDoAction(Term agent, ActionExpression action, Formula formula);
    
    /**
     * Returns true if the specified formula is trapped when an agent <i>agentI</i> 
     * is making a proposal (of doing an action <i>action</i> under the 
     * condition <i>formula</i>) towards the Jade agent.
     * @param agentI a semantic agent
     * @param action the action
     * @param formula the incoming formula
     * @return true if the formula is trapped, false if not.
     */
    public boolean trapProposal(Term agentI, ActionExpression action, Formula formula);
    
}
