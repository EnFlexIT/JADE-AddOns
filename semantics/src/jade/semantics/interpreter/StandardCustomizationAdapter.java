/*
 * StandardCustomizationAdapter.java
 * Created on 17 mars 2005
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
public class StandardCustomizationAdapter implements StandardCustomization {

    /**
     * By default, returns true.
     * @see jade.semantics.interpreter.StandardCustomization#acceptBeliefTransfer(jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.Term)
     */
    public boolean acceptBeliefTransfer(Formula formula, Term agent) {
        return true;
    }

    /**
     * By default, returns true.
     * @see jade.semantics.interpreter.StandardCustomization#acceptIntentionTransfer(jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.Term)
     */
    public boolean acceptIntentionTransfer(Formula formula, Term agent) {
        return true;
    }

    /**
     * By default, returns null.
     * @see jade.semantics.interpreter.StandardCustomization#callForProposalIota(jade.lang.sl.grammar.Variable, jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.ActionExpression, jade.lang.sl.grammar.Term)
     */
    public ListOfTerm callForProposalIota(Variable variable, Formula formula,
            ActionExpression action, Term agent) {
        return null;
    }

    /**
     * By default, returns null.
     * @see jade.semantics.interpreter.StandardCustomization#callForProposalAny(jade.lang.sl.grammar.Variable, jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.ActionExpression, jade.lang.sl.grammar.Term)
     */
    public ListOfTerm callForProposalAny(Variable variable, Formula formula,
            ActionExpression action, Term agent) {
        return null;
    }

    /**
     * By default, returns null.
     * @see jade.semantics.interpreter.StandardCustomization#callForProposalAll(jade.lang.sl.grammar.Variable, jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.ActionExpression, jade.lang.sl.grammar.Term)
     */
    public ListOfTerm callForProposalAll(Variable variable, Formula formula,
            ActionExpression action, Term agent) {
        return null;
    }

    /**
     * By default, returns true.
     * @see jade.semantics.interpreter.StandardCustomization#trapCancelAction(jade.lang.sl.grammar.Term, jade.lang.sl.grammar.ActionExpression, jade.lang.sl.grammar.Formula)
     */
    public boolean trapCancelAction(Term agent, ActionExpression action,
            Formula formula) {

        return true;
    }

    /**
     * By default, returns true.
     * @see jade.semantics.interpreter.StandardCustomization#trapCancelMyAction(jade.lang.sl.grammar.Term, jade.lang.sl.grammar.ActionExpression, jade.lang.sl.grammar.Formula)
     */
    public boolean trapCancelMyAction(Term agentI, ActionExpression action,
            Formula formula) {
        return true;
    }

    /**
     * By default, returns true.
     * @see jade.semantics.interpreter.StandardCustomization#trapDoAction(jade.lang.sl.grammar.Term, jade.lang.sl.grammar.ActionExpression, jade.lang.sl.grammar.Formula)
     */
    public boolean trapDoAction(Term agent, ActionExpression action,
            Formula formula) {
        return true;
    }

    /**
     * By default, returns true.
     * @see jade.semantics.interpreter.StandardCustomization#trapProposal(jade.lang.sl.grammar.Term, jade.lang.sl.grammar.ActionExpression, jade.lang.sl.grammar.Formula)
     */
    public boolean trapProposal(Term agentI, ActionExpression action,
            Formula formula) {
        return true;
    }

}
