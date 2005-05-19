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
 * OntologicalAction.java
 * Created on 15 déc. 2004
 * Author : louisvi
 */
package jade.semantics.actions;

import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;
import jade.util.leap.HashMap;

/**
 * Defines a prototype for ontological actions.
 * 
 * @author louisvi
 * @version Date: 2004/11/30 17:00:00 Revision: 1.0
 */
public class OntologicalAction extends SemanticActionImpl implements Cloneable {

	/**
	 * Pattern that recognizes an action expression involving this ontologic
	 * action
	 */
	ActionExpression actionPattern;

	/**
	 * Pattern that recognizes the rational effect of this ontologic action, and
	 * that enables to build its postcondition
	 */
	Formula postconditionPattern;

	/**
	 * Pattern that enables to build the feasibility precondition of this
	 * ontologic action
	 */
	Formula preconditionPattern;

	/**
	 * Table that stores the values of the parameters of an instance of this
	 * ontologic action
	 */
	HashMap actionParameters;

	/**
	 * Standard name for metaVariables refering to the agent of the ontologic
	 * action
	 */
	private static final String SENDER_REFERENCE = "??sender";

	/**
	 * Creates a new Ontologic Action prototype defined by an action pattern, a
	 * postcondition pattern and a precondition pattern. All the metaVariables
	 * of these patterns must refere to SL terms representing one of the
	 * arguments of the action and must use the same names for these
	 * metaVariables. These patterns may refere to the reserved metaReference "<code>??sender</code>",
	 * which denotes the agent of the action. A call to one of the
	 * <code>newAction</code> methods creates instances of this Ontologic
	 * Action prototype such that :
	 * <ul>
	 * <li> the <code>getFeasibilityPrecondition</code> and
	 * <code>getPostCondition</code> methods return formulae that comply with
	 * the corresponding patterns given to this constructor,</li>
	 * <li> the <code>getRationalEffect</code> method returns the same as the
	 * <code>
	 *           getPostCondition</code> method,</li>
	 * <li> the <code>getPersistentFeasibilityPrecondition</code> method
	 * returns the true formula,</li>
	 * <li> the <code>getBehaviour</code> method returns a
	 * <code>OntoActionBehaviour</code> that automatically manages the
	 * specified preconditions and postconditions for this OntologicAction.</li>
	 * </ul>
	 * 
	 * @param table
	 *            the SemanticActionTable, which this action prototype belongs
	 *            to
	 * @param actionPattern
	 *            pattern used to recognize the SL functional term representing
	 *            this action. For example, "<code>(CLOSE :what ??what)</code>"
	 * @param postconditionPattern
	 *            pattern used to both recognize SL formulae representing the
	 *            rational effect of this action and instantiate the SL formula
	 *            representing the postcondition of this action.
	 * @param preconditionPattern
	 *            pattern used to instantiate the SL formula representing the
	 *            precondition of this action.
	 * @see SemanticAction#newAction(ActionExpression)
	 * @see SemanticAction#newAction(Formula, Term)
	 */
	public OntologicalAction(SemanticActionTable table,
							 Term actionPattern, 
							 Formula postconditionPattern,
							 Formula preconditionPattern) {
		super(table);
		this.actionPattern = (ActionExpression) SLPatternManip.fromTerm("(action "+SENDER_REFERENCE+" "+actionPattern+")");
		this.postconditionPattern = postconditionPattern;
		this.preconditionPattern = preconditionPattern;
	} // End of OntologicalAction/4

	/**
	 * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
	 */
	public SemanticAction newAction(ActionExpression actionExpression) {
		return newAction(actionPattern, actionExpression, null);
	} // End of newAction/1

	/**
	 * @see jade.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.Formula,
	 *      jade.lang.sl.grammar.Term)
	 */
	public SemanticAction newAction(Formula rationalEffect, Term agentName) {
		return newAction(postconditionPattern, rationalEffect, agentName);
	} // End of newAction/2

	/**
	 * Internal implementation for building a new instance of this Ontologic
	 * Action prototype
	 * 
	 * @param pattern
	 *            pattern to match
	 * @param node
	 *            instantiated formula or term that identifies the parameters of
	 *            the instance of action to create (when matching the pattern)
	 * @param agentName
	 *            agent of the instance of action to create (null if it can be
	 *            retrieved from node)
	 * @return the new instance of the action prototype that has been created
	 */
	private SemanticAction newAction(Node pattern, Node node, Term agentName) {
		MatchResult matchResult = SLPatternManip.match(pattern, node);
		if (matchResult != null) {
			try {
				OntologicalAction result = (OntologicalAction) clone();
				result.setSemanticFeatures(matchResult,
						(agentName == null ? matchResult.getTerm(SENDER_REFERENCE) : agentName));
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	} // End of newAction/3

	/**
	 * Internal method that computes all the features (action specific
	 * parameters, preconditions, effects and behaviour) of a new created
	 * instance of this action prototype
	 * 
	 * @param metaReferences
	 *            list of <code>MetaTermReferenceNode</code>s containing the
	 *            identified values of each parameter of the instance of action
	 *            being created
	 * @param sender
	 *            term representing the agent of the instance of action action
	 *            being created
	 * @throws Exception
	 *             sent if a parameter mismatch
	 */
	private void setSemanticFeatures(ListOfNodes metaReferences, Term sender)
			throws Exception {
		setSender(sender);
		setActionParameters(new HashMap());
		for (int i = 0; i < metaReferences.size(); i++) {
			Node node = metaReferences.get(i);
			if (node instanceof MetaTermReferenceNode) {
				if (((MetaTermReferenceNode) node).lx_name().equals(SENDER_REFERENCE)) {
					if (!((MetaTermReferenceNode) node).sm_value().equals(sender)) {
						throw new Exception(SENDER_REFERENCE+ " parameter mismatch");
					}
				} else {
					setParameter(((MetaTermReferenceNode) node).lx_name(),
							((MetaTermReferenceNode) node).sm_value());
				}
			}
		}
		setBehaviour(new OntoActionBehaviour(this));
		//setFeasibilityPrecondition((Formula) instantiateFeatures(preconditionPattern));
		//setPostCondition((Formula) instantiateFeatures(postconditionPattern));
		//setRationalEffect(getPostCondition());
	} // End of setSemanticFeatures/2

    public Formula feasibilityPreconditionCalculation() throws WrongTypeException {
        return (Formula) instantiateFeatures(preconditionPattern);
    }
    public Formula persistentFeasibilityPreconditonCalculation() throws WrongTypeException {
        return new TrueNode();
    }
    public Formula rationalEffectCalculation() throws WrongTypeException  {
        return (Formula) instantiateFeatures(postconditionPattern);
    }
    public Formula postConditionCalculation() throws WrongTypeException {
        return (Formula) instantiateFeatures(postconditionPattern);
    }

	/**
	 * Internal method that helps instantiated some semantic features of an
	 * instance of this action being created from the values of the parameters
	 * of the action (which should have been set)
	 * 
	 * @param pattern
	 *            pattern to instantiate
	 * @return instantiated pattern with the values of the parameters of the
	 *         action
	 * @throws PatternCannotBeinstantiated
	 */
	private Node instantiateFeatures(Node pattern) {
		ListOfNodes metaReferences = new ListOfNodes();
		if (pattern.childrenOfKind(MetaTermReferenceNode.class, metaReferences)) {
			for (int i = 0; i < metaReferences.size(); i++) {
				String parameterName = ((MetaTermReferenceNode) metaReferences
						.get(i)).lx_name();
				try {
					SLPatternManip.set(pattern, parameterName, (parameterName
							.equals(SENDER_REFERENCE) ? getSender()
							: getParameter(parameterName)));
				} catch (WrongTypeException e) {
					e.printStackTrace();
				}
			}
		}
		return SLPatternManip.instantiate(pattern);
	} // End of instantiateFeatures/1

	/**
	 * Implementation of the behaviour of the ontologic action. This method must
	 * be developped along the same way as the <code>action</code> method of
	 * the <code>Behaviour</code>. This method must be overriden in all the
	 * subclasses (by default, does nothing but setting the internal state to
	 * the <code>SUCCESS</code> constant).
	 * 
	 * @param behaviour
	 *            Nesting SemanticBehaviour (useful for setting the internal
	 *            state of the SemanticBehaviour with the
	 *            <code>setState(int)</code> method).
	 * @see jade.core.behaviours.Behaviour#action()
	 * @see SemanticBehaviour#action()
	 */
	public void perform(OntoActionBehaviour behaviour) {
		behaviour.setState(SemanticBehaviour.SUCCESS);
	} // End of perform/1

	/**
	 * This method is called just before running the behaviour of the ontologic
	 * action. Does nothing by default. May be usefull to override it when
	 * programming a GUI for example.
	 * 
	 * @param behaviour
	 *            Nesting SemanticBehaviour.
	 * @see jade.semantics.behaviours.OntoActionBehaviour#action()
	 */
	public void beforePerform(OntoActionBehaviour behaviour) {
	}

	/**
	 * This method is called just after running the behaviour of the ontologic
	 * action if it has succeeded. Does nothing by default. May be usefull to
	 * override it when programming a GUI for example.
	 * 
	 * @param behaviour
	 *            Nesting SemanticBehaviour.
	 * @see jade.semantics.behaviours.OntoActionBehaviour#action()
	 */
	public void afterPerform(OntoActionBehaviour behaviour) {
	}

	/**
	 * Gets the value of a parameter of the ontologic action
	 * 
	 * @param name
	 *            name of the parameter to get
	 * @return the term representing the value of the parameter
	 */
	public Term getParameter(String name) {
		return (Term) actionParameters.get(name);
	} // End of getParameter/1

	/**
	 * Sets a new parameter for the ontologic action
	 * 
	 * @param name
	 *            name of the parameter to set
	 * @param value
	 *            term representing the value of the parameter to set
	 */
	public void setParameter(String name, Term value) {
		actionParameters.put(name, value);
	} // End of setParameter/2

	/**
	 * @return Returns the actionParameters.
	 */
	public HashMap getActionParameters() {
		return actionParameters;
	} // End of getActionParameters/0

	/**
	 * @param actionParameters
	 *            The actionParameters to set.
	 */
	public void setActionParameters(HashMap actionParameters) {
		this.actionParameters = actionParameters;
	} // End of setActionParameters/1

} // End of OntologicalAction
