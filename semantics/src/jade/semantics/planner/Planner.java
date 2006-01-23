/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France T�l�com

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
 * Planner.java
 * Created on 28 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.planner;

import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.Formula;

/**
 * Definition of the planner api. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public interface Planner {
    
    /**
    * Returns a Behaviour if the planner finds a plan to reach the input goal.
    * @param formula the goal to be reached
    * @param sr the incoming SemanticRepresentation
    * @return a behaviour
    **/
    public SemanticBehaviour findPlan(Formula formula, SemanticRepresentation sr);

} // End of class Planner
