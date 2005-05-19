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
 * SemanticRepresentation.java
 * Created on 29 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.lang.acl.ACLMessage;
import jade.semantics.lang.sl.grammar.Formula;

/**
 * Interface that represents a semantic representation. It must contains: 
 * <ul>
 * <li> an ACL message;
 * <li> a SL representation;
 * <li> a deductive step index.
 * </ul>
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public interface SemanticRepresentation {

    /**
    * @return Returns the ACL message
    **/
    public ACLMessage getMessage();

    /**
    * Sets the ACL message
    * @param msg the message to set
    **/
    public void setMessage(ACLMessage msg);

    /**
    * @return Returns the SL representation
    **/
    public Formula getSLRepresentation();

    /**
    * Sets the SL representation
    * @param formula the formula to set
    **/
    public void setSLRepresentation(Formula formula);

    /**
    * @return Returns semantic interpretation principle index
    **/
    public int getSemanticInterpretationPrincipleIndex();

    /**
    * Sets the semantic interpretation principle index
    *@param i the index to set
    **/
    public void setSemanticInterpretationPrincipleIndex(int i);

} // End of interface SemanticRepresentation