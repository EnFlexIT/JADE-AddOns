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
 * SemanticAgentBase.java
 * Created on 13 mai 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.core.Agent;

/**
 * Implementation of a Semantic Agent.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/13 Revision: 1.0 
 */
public class SemanticAgentBase extends Agent implements SemanticAgent {
    
    /**
     * Semantic capabilities associated at the agent
     */
    protected SemanticCapabilities semanticCapabilities;
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    /**
     * Creates a SemanticAgent with a new SemanticCapabilities
     */
    public SemanticAgentBase() {
        semanticCapabilities = new SemanticCapabilities();
    }
    
    /**
     * Creates a SemanticAgent with the given SemanticCapabilities
     * @param capabilities the capabilities of the agent
     */
    public SemanticAgentBase(SemanticCapabilities capabilities) {
        semanticCapabilities = capabilities;
    }
    /*********************************************************************/
    /**                         METHODS                             **/
    /*********************************************************************/
    /**
     * @return Returns the semanticCapabilities.
     */
    public SemanticCapabilities getSemanticCapabilities() {
        return semanticCapabilities;
    } // End of getSemanticCapabilities/0
    
    /**
     * Gets the corresponding JADE agent
     * @return the corresponding JADE agent
     */
    public Agent getAgent() {
        return this;
    } // End of setSemanticCapabilities/0
    
    /**
     * Setup of this agent
     */
    public void setup() {
        super.setup();
        semanticCapabilities.install(this);
    }
    
} // End of class SemanticAgentBase
