/*
 * SemanticInterpretationPrincipleTable.java
 * Created on 18 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.util.leap.ArrayList;

/**
 * @author Vincent Pautret
 * @version 
 */
public interface SemanticInterpretationPrincipleTable {
    
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
     * @param finder the finder that permits the recognition of a semantic 
     * interpretation principle
     */
    public void removeSemanticInterpretationPrinciple(Finder finder);
    
    /**
     * Returns the semantic interpretation principle at the specified index 
     * in the table
     * @param index 
     * @return a semantic interpretation principle
     */
    public SemanticInterpretationPrinciple getSemanticInterpretationPrinciple(int index);
    
    /**
     * Loads all the semantic interpretation principle in the table for the specified agent
     * @param agent a semantic agent
     */
    public void loadTable(SemanticAgent agent);
    
    /**
     * 
     * @return the list of the semantic interpretation principles 
     */
    public ArrayList getSemanticInterpretationPrincipleList();
    
    /**
     * Removes the semantic interpretation principle at the specified index
     * @param index the index in the table
     */
    public void removeSemanticInterpretationPrinciple(int index);
} // End of interface SemanticInterpretationPrincipleTable
