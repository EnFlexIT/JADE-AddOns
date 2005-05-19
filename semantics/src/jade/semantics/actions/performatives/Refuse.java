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
 * Refuse.java
 * Created on 25 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.actions.operators.Sequence;
import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * @author Vincent Pautret
 * @version 
 */
public class Refuse extends CommunicativeAction {

    /**
     * 
     */
    ActionExpression refuseMainPattern;
    
    /**
     * 
     */
    Content contentPattern;
    
    /**
     * 
     */
    Term actionPattern;
    
    /**
     * Pattern used to build the rational effect
     */
    Formula rationalEffectPattern;
    
    
    Formula alphaPattern;
    /** ****************************************************************** */
    /** CONSTRUCTOR * */
    /** ****************************************************************** */

    /**
     * 
     */
    public Refuse(SemanticActionTable table) {
        super(table);
        refuseMainPattern = (ActionExpression) SLPatternManip.fromTerm("(action ??sender (REFUSE :sender ??sender :receiver ??receiver :content ??content))");
        contentPattern = SLPatternManip.fromContent("((Feasible ??act)))");
        actionPattern = SLPatternManip.fromTerm("(action ??sender (DISCONFIRM :sender ??sender :receiver ??receiverList :content ??content))");
        alphaPattern = SLPatternManip.fromFormula("(and ??phi (and (not (done ??act)) (not (I ??sender (done ??act)))))");
        rationalEffectPattern = SLPatternManip.fromFormula("(and (B ??receiver (not (feasible ??act))) (B ??receiver ??alpha))");
        setACLMessageCode(ACLMessage.REFUSE);
    } // End of constructor Refuse/1
    
    /** ****************************************************************** */
    /** METHODS * */
    /** ****************************************************************** */

    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.semantics.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
        try {
            MatchResult matchResult = SLPatternManip.match(refuseMainPattern, actionExpression);
            if (matchResult != null) {
                Content content = (SLParser.getParser().parseContent(((StringConstantNode)matchResult.getTerm("??content")).lx_value()));
                ListOfContentExpression listOfContentExpr = ((ContentNode)content).as_expressions();
                if (listOfContentExpr.size() == 3 && listOfContentExpr.element(0) instanceof ActionContentExpressionNode 
                        && listOfContentExpr.element(1) instanceof FormulaContentExpressionNode 
                        && listOfContentExpr.element(2) instanceof FormulaContentExpressionNode) {
                    if (((ActionExpressionNode)((ActionContentExpressionNode)listOfContentExpr.element(0)).as_action_expression()).as_agent().equals(((TermSetNode) matchResult.getTerm("??receiver")).as_terms().first())) {
                        SLPatternManip.set(contentPattern, "??agent", matchResult.getTerm("??sender"));
                        Content cont = (Content)SLPatternManip.instantiate(contentPattern, 
                            "??act", ((ActionContentExpressionNode)listOfContentExpr.element(0)).as_action_expression(),
                            "??phi", listOfContentExpr.element(1),
                            "??psi", listOfContentExpr.element(2));
                        ActionExpression actionExpr = (ActionExpression)SLPatternManip.instantiate(actionPattern,
                            "??sender", matchResult.getTerm("??sender"),
                            "??receiverList", matchResult.getTerm("??receiver"),
                            "??content", new StringConstantNode(cont.toString()));
                        return new Inform(table).newAction(actionExpr);
                    }
                }
            }
        } catch (SLPatternManip.WrongTypeException wte) {
            wte.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return null;
    } // End of newAction/1

    /**
     * @see jade.semantics.actions.SemanticAction#newAction(jade.semantics.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(Formula rationalEffect, Term agentName) {
        try {
            MatchResult matchResult = SLPatternManip.match(rationalEffectPattern, rationalEffect);
            if (matchResult != null) {
                return new Sequence(table).newAction(rationalEffect.getSimplifiedFormula(), agentName);
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of newAction/2

} // End of class Refuse
