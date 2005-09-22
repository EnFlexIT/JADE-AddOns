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

import jade.semantics.interpreter.sips.RequestWhen;
import jade.semantics.interpreter.sips.RequestWhenever;
import jade.semantics.interpreter.sips.UnreachableGoal;
import jade.semantics.interpreter.sips.ActionFeatures;
import jade.semantics.interpreter.sips.ActionPerformance;
import jade.semantics.interpreter.sips.And;
import jade.semantics.interpreter.sips.BeliefTransfer;
import jade.semantics.interpreter.sips.Refuse;
import jade.semantics.interpreter.sips.RejectProposal;
import jade.semantics.interpreter.sips.Agree;
import jade.semantics.interpreter.sips.IntentionTransfer;
import jade.semantics.interpreter.sips.Planning;
import jade.semantics.interpreter.sips.Propose;
import jade.semantics.interpreter.sips.RationalityPrinciple;
import jade.semantics.interpreter.sips.Subscribe;
import jade.semantics.interpreter.sips.AlreadyReachedGoal;
import jade.semantics.interpreter.sips.Unsubscribe;
import jade.util.leap.ArrayList;

/**
 * Class that represents a table which contains all the semantic interpretation 
 * principles known by the agent.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/02 Revision: 1.0 
 */
public class SemanticInterpretationPrincipleTableImpl extends ArrayList implements SemanticInterpretationPrincipleTable {
    
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a new table
     */
    public SemanticInterpretationPrincipleTableImpl() {
        super();
    } // End of SemanticInterpretationPrincipleTableImpl/0
    
    /**************************************************************************/
    /**                                 PUBLIC METHODS                       **/
    /**************************************************************************/
    
    /**
     * Adds a semantic interpretation principle in the table and updates the 
     * semantic interpretation principle index of the added sip.
     * @param sip the semantic interpretation principle
     */
    public void addSemanticInterpretationPrinciple(SemanticInterpretationPrinciple sip) {
        this.add(sip);
        sip.setOrderIndex(this.size()-1);
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
     * Removes the semantic interpretation principle corresponding to a given 
     * specification
     * @param sipFinder specifies the semantic interpretation principle to remove
     */
    public void removeSemanticInterpretationPrinciple(Finder sipFinder) {
        sipFinder.removeFromList(this);
        for (int i = 0; i < this.size(); i++) {
            ((SemanticInterpretationPrinciple)this.get(i)).setOrderIndex(i);
        }
    } // End of removeSemanticInterpretationPrinciple/1
    
    /**
     * Loads all the semantic interpretation principles defined in the 
     * sip package. Do not change the load order.
     * @param capabilities the semantic capabilities of the agent
     */
    public  void loadTable(SemanticCapabilities capabilities) {
        this.addSemanticInterpretationPrinciple(new ActionFeatures(capabilities));
        this.addSemanticInterpretationPrinciple(new AlreadyReachedGoal(capabilities));
        this.addSemanticInterpretationPrinciple(new BeliefTransfer(capabilities));
        this.addSemanticInterpretationPrinciple(new RequestWhen(capabilities));
        this.addSemanticInterpretationPrinciple(new IntentionTransfer(capabilities));
        this.addSemanticInterpretationPrinciple(new ActionPerformance(capabilities));
        this.addSemanticInterpretationPrinciple(new RationalityPrinciple(capabilities));
        this.addSemanticInterpretationPrinciple(new Planning(capabilities));
        this.addSemanticInterpretationPrinciple(new Refuse(capabilities));
        this.addSemanticInterpretationPrinciple(new RejectProposal(capabilities));
        this.addSemanticInterpretationPrinciple(new Agree(capabilities));
        this.addSemanticInterpretationPrinciple(new Propose(capabilities));
        this.addSemanticInterpretationPrinciple(new RequestWhenever(capabilities)); 
        this.addSemanticInterpretationPrinciple(new Subscribe(capabilities));
        this.addSemanticInterpretationPrinciple(new Unsubscribe(capabilities));
        this.addSemanticInterpretationPrinciple(new And(capabilities));
        this.addSemanticInterpretationPrinciple(new UnreachableGoal(capabilities));        
    } // End of loadTable/0
    
    /**
     * Adds a semantic interpretation principle in the table at the specified 
     * index
     * @param sip the sip to add
     * @param index the index in the table
     */
    public void addSemanticInterpretationPrinciple(SemanticInterpretationPrinciple sip, int index) {
        this.add(index, sip);
        sip.setOrderIndex(index);
        for (int i = index + 1; i < this.size(); i++) {
            ((SemanticInterpretationPrinciple)this.get(i)).setOrderIndex(i);
        }
    } // End of addSemanticInterpretationPrinciple/2
    
    /**
     * Removes the semantic interpretation principle corresponding to the 
     * specified index. 
     * @param index index int the table of the semantic interpretation principle
     *  to remove
     */
    public void removeSemanticInterpretationPrinciple(int index) {
        this.remove(index);
        for (int i = index; i < this.size(); i++) {
            ((SemanticInterpretationPrinciple)this.get(i)).setOrderIndex(i);
        }
    } // End of removeSemanticInterpretationPrinciple/1
    
} // End of class semanticInterpretationPrincipleTableImpl
