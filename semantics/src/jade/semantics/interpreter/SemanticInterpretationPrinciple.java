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
 * SemanticInterpretationPrinciple.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.core.behaviours.Behaviour;
import jade.semantics.interpreter.SemanticInterpretationPrincipleImpl.SemanticInterpretationPrincipleException;
import jade.semantics.lang.sl.grammar.Formula;
import jade.util.leap.ArrayList;

/**
 * Interface that represents a semantic interpretation principle.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public interface SemanticInterpretationPrinciple {
    
    /**
    * Try to apply the semantic interpretation principle. 
    * It returns an ArrayList which contains the SR produced by the semantic interpretation principle.  
    * @param sr a semantic representation
    * @return the list of SR produced by the deductive step
    * @throws SemanticInterpretationPrincipleException if an exception occurs
    **/
    public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException; 
   
    /**
     * Adds a behaviour to the agent list
     * @param b a behaviour
     */
    public void potentiallyAddBehaviour(Behaviour b);
    
    /**
     * Removes a behaviour from the list of behaviour
     * @param b a behaviour
     */
    public void potentiallyRemoveBehaviour(Behaviour b);
    
    public void potentiallyAssertFormula(Formula formula);

} // End of interface SemanticInterpretationPrinciple
