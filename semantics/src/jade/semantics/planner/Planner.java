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

import jade.core.behaviours.Behaviour;
import jade.semantics.lang.sl.grammar.Formula;

/**
 * Definition of the planner api. 
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public interface Planner {
    
    /**
    * Returns a Behaviour if the planner find a plan to reach the input goal.
    * @param formula the goal to be reached
    **/
    public Behaviour findPlan(Formula formula);


} // End of class Planner
