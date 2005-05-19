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
 * SemanticInterpretationPrincipleTableImpl.java
 * Created on 2 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.semantics.interpreter.semanticinterpretationprinciple.ActionFeatures;
import jade.semantics.interpreter.semanticinterpretationprinciple.ActionPerformance;
import jade.semantics.interpreter.semanticinterpretationprinciple.AndFilter;
import jade.semantics.interpreter.semanticinterpretationprinciple.BeliefTransfer;
import jade.semantics.interpreter.semanticinterpretationprinciple.CancelAction;
import jade.semantics.interpreter.semanticinterpretationprinciple.CancelMyAction;
import jade.semantics.interpreter.semanticinterpretationprinciple.DoAction;
import jade.semantics.interpreter.semanticinterpretationprinciple.IntentionTransfer;
import jade.semantics.interpreter.semanticinterpretationprinciple.Planning;
import jade.semantics.interpreter.semanticinterpretationprinciple.Propose;
import jade.semantics.interpreter.semanticinterpretationprinciple.RationalityPrinciple;
import jade.semantics.interpreter.semanticinterpretationprinciple.Subscribe;
import jade.semantics.interpreter.semanticinterpretationprinciple.Unsubscribe;
import jade.util.leap.ArrayList;



/**
 * Class that represents a table wihch contains all the semantic interpretation principles 
 * known by the agent.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class SemanticInterpretationPrincipleTableImpl extends ArrayList implements SemanticInterpretationPrincipleTable {

    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Constructor
     */
    public SemanticInterpretationPrincipleTableImpl() {
        super();
    } // End of SemanticInterpretationPrincipleTableImpl/0
    
    /**************************************************************************/
    /**                                 PUBLIC METHODS                       **/
    /**************************************************************************/

    /**
     * Adds a semantic interpretation principle in the table
     * @param sip the semantic interpretation principle
     */
    public void addSemanticInterpretationPrinciple(SemanticInterpretationPrinciple sip) {
        this.add(sip);
    } // End of addSemanticInterpretationPrinciple/1


    /**
     * Returns the semantic interpretation principle at the specified index
     * @param index an index
     * @return the semantic interpretation principle at the specified index
     */
    public SemanticInterpretationPrinciple getSemanticInterpretationPrinciple(int index) {
        return (SemanticInterpretationPrinciple)get(index);
    } // End of getSemanticInterpretationPrinciple/1
    
    /**
     * Removes the semantic interpretation principle corresponding to a given specification
     * @param sipFinder specifies the semantic interpretation principle to remove
     */
    public void removeSemanticInterpretationPrinciple(Finder sipFinder) {
        sipFinder.removeFromList(this);
    } // End of removeSemanticInterpretationPrinciple/1

    /**
     * Load all the semantic interpretation principles defined in the dedcutivestep package.
     * @param agent agent that loads the table
     */
    public  void loadTable(SemanticAgent agent) {
        this.add(new ActionFeatures(agent));
        this.add(new BeliefTransfer(agent)); 
        this.add(new IntentionTransfer(agent));
        this.add(new RationalityPrinciple(agent));
        this.add(new ActionPerformance(agent));
        this.add(new Planning(agent));
        this.add(new CancelAction(agent));
        this.add(new CancelMyAction(agent));
        this.add(new DoAction(agent));
        this.add(new Propose(agent));
        this.add(new Subscribe(agent)); 
        this.add(new Unsubscribe(agent));
        this.add(new AndFilter(agent));
                
//      TODO Use a classloader to load application-specific deductive steps
    } // End of loadTable/0
    
    /**
     * Returns the list of semantic interpretation principles
     * @return the sorted list of semantic interpretation principles
     */
    public  ArrayList getSemanticInterpretationPrincipleList() {
        return this;
    } // End of getsemanticInterpretationPrincipleList/0
    
    /**
     * Adds a semantic interpretation principle in the table at the specified 
     * index
     * @param sip the sip to add
     * @param index the index in the table
     */
    public void addSemanticInterpretationPrinciple(SemanticInterpretationPrinciple sip, int index) {
        this.add(index, sip);
    } // End of addSemanticInterpretationPrinciple/2
    
    /**
     * Removes the semantic interpretation principle corresponding to the 
     * specified index. 
     * @param index index int the table of the semantic interpretation principle
     *  to remove
     */
    public void removeSemanticInterpretationPrinciple(int index) {
        this.remove(index);
    } // End of removeSemanticInterpretationPrinciple/1
    
} // End of class semanticInterpretationPrincipleTableImpl
