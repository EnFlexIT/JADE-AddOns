/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2006 France T�l�com

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
 * created on 21 mars 2007 by Vincent Louis
 */

/**
 * 
 */
package jade.semantics.actions;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.operators.Alternative;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;
import jade.util.leap.ArrayList;

/**
 * @author Vincent Louis - France Telecom
 *
 */
public class Do extends SemanticActionImpl implements Cloneable {
	
	Formula goal;
	
	static Term DO_ACTION_PATTERN = SL.fromTerm("(action ??" + ACTOR + " (DO ??__phi))");

	/**
	 * @param capabilities
	 */
	public Do(SemanticCapabilities capabilities) {
		super(capabilities);
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#newAction(jade.semantics.lang.sl.grammar.ActionExpression)
	 */
	public SemanticAction newAction(ActionExpression actionExpression)
			throws SemanticInterpretationException {
		MatchResult matchResult = DO_ACTION_PATTERN.match(actionExpression);
		if (matchResult != null) {
			Do result;
			try {
				result = (Do)clone();
				result.setAuthor(matchResult.getTerm(ACTOR));
				result.goal = SL.fromFormula(((Constant)matchResult.getTerm("__phi")).stringValue());
		    } catch (Exception e) { // WrongTypeException or CloneNotSupported
		        throw new SemanticInterpretationException("cannot-read-author", new StringConstantNode(actionExpression.toString()));
		    }
		    return result;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#newAction(jade.semantics.lang.sl.grammar.Formula, jade.lang.acl.ACLMessage)
	 */
	public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
		return null;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#toActionExpression()
	 */
	public ActionExpression toActionExpression()
			throws SemanticInterpretationException {
		ActionExpression result = (ActionExpression)DO_ACTION_PATTERN.instantiate(ACTOR, getAuthor());
		result.sm_action(this);
		return result;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computeFeasibilityPrecondition()
	 */
	public Formula computeFeasibilityPrecondition() throws WrongTypeException {
		return new TrueNode();
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computePersistentFeasibilityPreconditon()
	 */
	public Formula computePersistentFeasibilityPreconditon()
			throws WrongTypeException {
		return new TrueNode();
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computeRationalEffect()
	 */
	public Formula computeRationalEffect() throws WrongTypeException {
		return new TrueNode();
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computePostCondition()
	 */
	public Formula computePostCondition() throws WrongTypeException {
		return goal;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computeBehaviour()
	 */
	public Behaviour computeBehaviour() {
		ArrayList actions = new ArrayList();
		getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstance(actions, goal, null);
		return new Alternative(getSemanticCapabilities()).newAction(actions).getBehaviour();		
	}

}
