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
 * Implementation of the StandardCustomization interface.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/17 Revision: 1.0 
 */
public class StandardCustomizationAdapter implements StandardCustomization {

    /**
     * @inheritDoc
     * @return By default, returns true.
     */
    public boolean acceptBeliefTransfer(Formula formula, Term agent) {
        return true;
    } // End of acceptBeliefTransfer/2
    
    /**
     * @inheritDoc
     * @return By default, returns true.
     */
    public boolean acceptIntentionTransfer(Formula formula, Term agent) {
        return true;
    } // End of acceptIntentionTransfer/2

    /**
     * @inheritDoc
     * @return By default, returns null.
     */
    public ListOfTerm handleCFPIota(Variable variable, Formula formula,
            ActionExpression action, Term agent) {
        return null;
    } // End of handleCFPIota/4

    /**
     * @inheritDoc
     * @return By default, returns null.
     */
    public ListOfTerm handleCFPAny(Variable variable, Formula formula,
            ActionExpression action, Term agent) {
        return null;
    } // End of handleCFPAny/4

    /**
     * @inheritDoc
     * @return By default, returns null.
     */
    public ListOfTerm handleCFPAll(Variable variable, Formula formula,
            ActionExpression action, Term agent) {
        return null;
    } // End of handleCFPAll/4

    /**
     * @inheritDoc
     * @return By default, returns null.
     */
    public ListOfTerm handleCFPSome(Variable variable, Formula formula,
            ActionExpression action, Term agent) {
        return null;
    } // End of handleCFPSome/4
    /**
     * @inheritDoc
     * @return By default, returns true.
     */
    public boolean handleRefuse(Term agent, ActionExpression action,
            Formula formula) {
        return true;
    } // End of trapCancelAction/3

    /**
     * @inheritDoc
     * @return By default, returns true.
     */
    public boolean handleRejectProposal(Term agentI, ActionExpression action,
            Formula formula) {
        return true;
    } // End of trapCancelMyAction/3

    /**
     * @inheritDoc
     * @return By default, returns true.
     */
    public boolean handleAgree(Term agent, ActionExpression action,
            Formula formula) {
        return true;
    } // End of trapDoAction/3

    /**
     * @inheritDoc
     * @return By default, returns true.
     */
    public boolean handleProposal(Term agentI, ActionExpression action,
            Formula formula) {
        return true;
    } // End of trapProposal/3
	
	/**
     * By default, does nothing
     * @inheritDoc 
	 * 
	 */
	public void notifySubscribe(Term subscriber, Formula obsverved, Formula goal)
	{
	}
	
	/**
     * By default, does nothing
     * @inheritDoc 
	 * 
	 */
	public void notifyUnsubscribe(Term subscriber, Formula obsverved, Formula goal)
	{
	}


} // End of class StandardCustomizationAdapter
