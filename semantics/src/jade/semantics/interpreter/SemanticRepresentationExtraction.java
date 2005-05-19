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
 * SemanticRepresentationExtraction.java
 * Created on 29 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.lang.acl.ACLMessage;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;

/**
 * Static class that offers a method for the construction of semantic 
 * representation from ACL message 
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class SemanticRepresentationExtraction {
    
    /**
     * Returns a semantic representation that represents the ACL message passed 
     * in parameter. Returns null if an exception occurs.
     * @param msg an ACL message
     * @param agentName the agent name
     * @return semantic representation that represents the ACL message passed 
     * in parameter.
     */
    public static SemanticRepresentation extract(ACLMessage msg, String agentName) {
        try {
	        SemanticRepresentation sr = new SemanticRepresentationImpl();
	        sr.setMessage(msg);
	        sr.setSLRepresentation(
	                SLParser.getParser()
	                .parseFormula("(B (agent-identifier :name " + agentName + ") (done (action (agent-identifier :name " + msg.getSender().getName() + ") " + msg.toString().replaceAll("\n","") + ") true))")
	                .getSimplifiedFormula());
	        return sr;
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }
    } // End of extract/2
    
} // End of class SemanticRepresentationExtraction
