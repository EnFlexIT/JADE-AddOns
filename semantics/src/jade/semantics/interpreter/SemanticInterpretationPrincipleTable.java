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
 * SemanticInterpretationPrincipleTable.java
 * Created on 18 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;


/**
 * Interface of the Semantic Interpretation Principle Table. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/18 Revision: 1.0
 */
public interface SemanticInterpretationPrincipleTable {
    
    /**
     * Index of the frist SIP in the table. 
     */
    public final static int FRONT = 0;

    /**
     * Index of the last SIP in the table.
     */
    public final static int END = Integer.MAX_VALUE;

    /**
     * Adds a new semantic interpretation principle at the end of the table
     * @param semanticInterpretationPrinciple the semantic interpretation 
     * principle to add 
     */
    public void addSemanticInterpretationPrinciple(SemanticInterpretationPrinciple semanticInterpretationPrinciple);
    
    /**
     * Adds a semantic interpretation principle in the table at the specified 
     * index
     * @param sip the sip to add
     * @param index the index in the table
     */
    public void addSemanticInterpretationPrinciple(SemanticInterpretationPrinciple sip, int index);
    
    /**
     * Removes all the semantic interpretation principles that correspond to the
     * finder.
     * @param finder the finder that permits the recognition of semantic 
     * interpretation principles
     */
    public void removeSemanticInterpretationPrinciple(Finder finder);
    
    /**
     * Returns the semantic interpretation principle at the specified index 
     * in the table
     * @param index an index
     * @return a semantic interpretation principle
     */
    public SemanticInterpretationPrinciple getSemanticInterpretationPrinciple(int index);
    
    /**
     * Loads all the semantic interpretation principles in the table for the specified agent
     * @param capabilities the semantic capabilities of the agent
     */
    public void loadTable(SemanticCapabilities capabilities);
    
    /**
     * Returns the size of the table
     * @return the size of the table 
     */
    public int size();
    
    /**
     * Removes the semantic interpretation principle at the specified index
     * @param index the index in the table
     */
    public void removeSemanticInterpretationPrinciple(int index);
} // End of interface SemanticInterpretationPrincipleTable
