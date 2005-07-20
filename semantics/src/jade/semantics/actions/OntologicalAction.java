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

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviourBase;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * Defines a prototype for ontological actions.
 * 
 * @author Vincent Louis - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0
 */
public class OntologicalAction extends SemanticActionImpl implements Cloneable {

	/**
	 * Pattern to recognize an action expression corresponding to this ontologic
	 * action
	 */
    private ActionExpression actionPattern;

	/**
	 * Pattern to recognize and build the rational effect and the postcondition
     * of this ontologic action
	 */
    private Formula postconditionPattern;

	/**
	 * Pattern to build the feasibility precondition of this ontologic action
	 */
    private Formula preconditionPattern;

	/**
	 * Table that stores the values of the parameters of an instance of this
	 * ontologic action
	 */
    private MatchResult actionParameters;

	/**
	 * Standard name for metaVariables refering to the agent of the ontologic
	 * action
	 */
	private static final String SENDER_REFERENCE = "sender";

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
	 * <li> the <code>computeBehaviour</code> method returns a
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
	 */
	public OntologicalAction(SemanticActionTable table,
							 String actionPattern, 
							 Formula postconditionPattern,
							 Formula preconditionPattern) {
		super(table);
		this.actionPattern = (ActionExpression) SLPatternManip.fromTerm("(action ??" + SENDER_REFERENCE + " " + actionPattern + ")");
		this.postconditionPattern = postconditionPattern;
		this.preconditionPattern = preconditionPattern;
	} // End of OntologicalAction/4

	/**
	 * @inheritDoc
	 */
	public SemanticAction newAction(ActionExpression actionExpression) throws SemanticInterpretationException {
		return newAction(actionPattern, actionExpression, false);
	} // End of newAction/1

	/**
	 * @inheritDoc
	 */
	public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
		try {
            return newAction(postconditionPattern, rationalEffect, true);
        }
        catch (SemanticInterpretationException e) {
            return null;
        }
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
     * @param isAuthorToBeSet
	 * @return the new instance of the action prototype that has been created
     * @throws SemanticInterpretationException if any exception occurs
	 */
	private SemanticAction newAction(Node pattern, Node node, boolean isAuthorToBeSet) throws SemanticInterpretationException {
		MatchResult matchResult = SLPatternManip.match(pattern, node);
		if (matchResult != null) {
		    try {
		        OntologicalAction result = (OntologicalAction)clone();
		        if (isAuthorToBeSet) {
		            result.setAuthor(table.getSemanticCapabilities().getAgentName());
		            if (matchResult.getTerm(SENDER_REFERENCE) != null
		                    && !matchResult.getTerm(SENDER_REFERENCE).equals(result.getAuthor())) {
		                throw new SemanticInterpretationException("inconsistent-author", new WordConstantNode(""));
		            }
		        }
		        else {
		            result.setAuthor(matchResult.getTerm(SENDER_REFERENCE));
		        }
		        result.actionParameters = matchResult;
		        return result;
		    } catch (Exception e) { // WrongTypeException or CloneNotSupported
		        throw new SemanticInterpretationException("cannot-read-author", new StringConstantNode(node.toString()));
		    }
		}
		return null;
	} // End of newAction/3

	/**
     * @inheritDoc
	 */
    public ActionExpression toActionExpression() throws SemanticInterpretationException {
	    try {
            return (ActionExpression)instantiateFeatures(actionPattern);
        }
        catch (WrongTypeException e) {
            throw new SemanticInterpretationException("cannot-expand-action", new StringConstantNode("missing or bad parameter"));
        }
    }
    
    /**
     * @inheritDoc
     */
    public Behaviour computeBehaviour() {
        return new OntoActionBehaviour(this);
    }
    /**
     * @inheritDoc
     */
    public Formula computeFeasibilityPrecondition() throws WrongTypeException {
        return (Formula)instantiateFeatures(preconditionPattern);
    }
    
    /**
     * @inheritDoc
     */
    public Formula computePersistentFeasibilityPreconditon() throws WrongTypeException {
        return new TrueNode();
    }
    
    /**
     * @inheritDoc
     */
    public Formula computeRationalEffect() throws WrongTypeException  {
        return (Formula)instantiateFeatures(postconditionPattern);
    }
    
    /**
     * @inheritDoc
     */
    public Formula computePostCondition() throws WrongTypeException {
        return (Formula)instantiateFeatures(postconditionPattern);
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
	 * @throws WrongTypeException 
	 */
	private Node instantiateFeatures(Node pattern) throws WrongTypeException {
		ListOfNodes metaReferences = new ListOfNodes();
		if (pattern.childrenOfKind(MetaTermReferenceNode.class, metaReferences)) {
			for (int i = 0; i < metaReferences.size(); i++) {
				String parameterName = ((MetaTermReferenceNode)metaReferences.get(i)).lx_name();
				SLPatternManip.set(pattern, parameterName,
				        (parameterName.equals(SENDER_REFERENCE) ? getAuthor() : actionParameters.getTerm(parameterName)));
			}
		}
        Node result = SLPatternManip.instantiate(pattern);
        SLPatternManip.clearMetaReferences(pattern);
        return result;
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
	 * @see SemanticBehaviourBase#action()
	 */
	public void perform(OntoActionBehaviour behaviour) {
		behaviour.setState(SemanticBehaviourBase.SUCCESS);
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
	} // End of beforePerform/1

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
	} // End of afterPerform/1


	/**
     * Returns a Term representing a parameter from the given parameter name.
     * @param parameterName a name of parameter
	 * @return an action parameter
	 */
	public Term getActionParameter(String parameterName) {
		try {
            return actionParameters.getTerm(parameterName);
        }
        catch (WrongTypeException e) {
            return null;
        }
	} // End of getActionParameter/1
} // End of OntologicalAction
