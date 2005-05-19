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
 * ActionPlan.java
 * Created on 5 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions;

import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * @author Vincent Pautret
 * @version 
 */
public class ActionPlan extends SemanticActionImpl {

    /********************************************************************/
    /**             CONSTRUCTOR
    /********************************************************************/

    public ActionPlan(SemanticActionTable table) {
        super(table);
    } // End of ActionPlan/1

    /********************************************************************/
    /**            METHODS
    /********************************************************************/

    /* (non-Javadoc)
     * @see jade.core.semantics.actions.SemanticAction#newAction(jade.lang.sl.grammar.ActionExpression)
     */
    public SemanticAction newAction(ActionExpression actionExpression) {
        return null;
    } // End of newAction/1

    public Formula feasibilityPreconditionCalculation() throws WrongTypeException {
        return null;
    }
    public Formula persistentFeasibilityPreconditonCalculation() throws WrongTypeException {
        return null;
    }
    public Formula rationalEffectCalculation() throws WrongTypeException  {
        return null;
    }
    public Formula postConditionCalculation() throws WrongTypeException {
        return null;
    }

} // End of class ActionPlan
