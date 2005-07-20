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
 * ManAgentCapabilities.java
 * Created on 16 mai 2005
 * Author : Vincent Pautret
 */
package demo;

import jade.core.AID;
import jade.semantics.actions.OntologicalAction;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.StandardCustomizationAdapter;
import jade.semantics.kbase.KBFilterManagment;
import jade.semantics.kbase.observer.EventCreationObserver;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Capabilities of the Son Agent.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/16 Revision: 1.0
 */
public class ManAgentCapabilities extends SemanticCapabilities {
    
    /**
     * AID of the mother
     */
    AID motherAID  = null;
    
    /**
     * Agent term pattern
     */
    final Term AGENT_TERM = 
        SLPatternManip.fromTerm("(agent-identifier :name ??agent)");
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    /**
     * Creates a new Capabilities
     */
    public ManAgentCapabilities() {
        super();
    } // End of ManAgentCapabilities/0
    
    /*********************************************************************/
    /**                         LOCAL METHODS                           **/
    /*********************************************************************/
    
    /**
     * Sets the AID of the mother agent
     * @param mother the mother agent name
     */
    public void setMotherAIDfromString(String mother) {
        motherAID = new AID(mother,true);
    } // End of ManAgentCapabilities/1
    
    /*********************************************************************/
    /**                         METHODS     **/
    /*********************************************************************/
    
    /**
     * Knowledge base setup.
     * Adds Observer on differents levels of temperature and initial knowledge
     * of the son agent.
     */
    public void setupKbase() {
        // KBase setup
        // -----------
        super.setupKbase();
        try {
            // KBase Observers setup
            // -------------------
            KBFilterManagment kb = ((KBFilterManagment)getMyKBase());
            kb.addFiltersDefinition(new SingleNumValueDefinition("temperature"));
            
            //Adds Observers on temperature levels.
            //If the Son believes that the temperature is greater than (20,15,10,0), 
            //the son adopts the intention to put or take-off clothing
            
            getMyKBase().addObserver(new EventCreationObserver(myAgent,
                    SLPatternManip.fromFormula("(B "+getAgentName()+" (temperature_gt 20))"),
                    SLPatternManip.fromFormula("(and (I "+getAgentName()+" (not (wearing "+getAgentName()+" trousers)))" +
                            "(and (I "+getAgentName()+" (not (wearing "+getAgentName()+" pullover)))" +
                            "(and (I "+getAgentName()+" (not (wearing "+getAgentName()+" coat)))" +
                            "(I "+getAgentName()+" (not (wearing "+getAgentName()+" cap))))))" 
                    )));
            getMyKBase().addObserver(new EventCreationObserver(myAgent,
                    SLPatternManip.fromFormula("(B "+getAgentName()+" (temperature_gt 15.0))"),
                    SLPatternManip.fromFormula("(and (I "+getAgentName()+" (not (wearing "+getAgentName()+" pullover)))" +
                            "(and (I "+getAgentName()+" (not (wearing "+getAgentName()+" coat)))" +
                            "(I "+getAgentName()+" (not (wearing "+getAgentName()+" cap)))))")));
            
            getMyKBase().addObserver(new EventCreationObserver(myAgent,
                    SLPatternManip.fromFormula("(B "+getAgentName()+" (temperature_gt 10))"),
                    SLPatternManip.fromFormula("(and (I "+getAgentName()+" (not (wearing "+getAgentName()+" coat)))" +
                            "(I "+getAgentName()+" (not (wearing "+getAgentName()+" cap))))))")));
            
            getMyKBase().addObserver(new EventCreationObserver(myAgent,
                    SLPatternManip.fromFormula("(B "+getAgentName()+" (temperature_gt 0))"),
                    SLPatternManip.fromFormula("(I "+getAgentName()+" (not (wearing "+getAgentName()+" cap)))")));
            
            //Adds Observers on temperature levels.
            //If the Son believes that the temperature is lower (not greater) than (20,15,10,0), 
            //the son adopts the intention to put or take-off clothing

            getMyKBase().addObserver(new EventCreationObserver(myAgent,
                    SLPatternManip.fromFormula("(B "+getAgentName()+" (not (temperature_gt 20)))"),
                    SLPatternManip.fromFormula("(I "+getAgentName()+" (wearing "+getAgentName()+" trousers))")));
            
            getMyKBase().addObserver(new EventCreationObserver(myAgent,
                    SLPatternManip.fromFormula("(B "+getAgentName()+" (not (temperature_gt 15)))"),
                    SLPatternManip.fromFormula("(and (I "+getAgentName()+" (wearing "+getAgentName()+" pullover))" +
                            "(I "+getAgentName()+" (wearing "+getAgentName()+" trousers)))")));
            
            getMyKBase().addObserver(new EventCreationObserver(myAgent,
                    SLPatternManip.fromFormula("(B "+getAgentName()+" (not (temperature_gt 10)))"),
                    SLPatternManip.fromFormula("(and (I "+getAgentName()+" (wearing "+ getAgentName()+" coat))" +
                            "(and (I "+getAgentName()+" (wearing "+getAgentName()+" pullover))" +
                            "(I "+getAgentName()+" (wearing "+getAgentName()+" trousers))))"
                    )));
            
            getMyKBase().addObserver(new EventCreationObserver(myAgent,
                    SLPatternManip.fromFormula("(B "+getAgentName()+" (not (temperature_gt 0)))"),
                    SLPatternManip.fromFormula("(and (I "+getAgentName()+" (wearing "+getAgentName()+" cap))" +
                            "(and (I "+getAgentName()+" (wearing "+ getAgentName()+" coat))" +
                            "(and (I "+getAgentName()+" (wearing "+getAgentName()+" pullover))" +
                            "(I "+getAgentName()+" (wearing "+getAgentName()+" trousers)))))"
                    )));
            
            // Initial Knowledge setup 
            // -----------------------
            Formula initialKPattern = 
                SLPatternManip.fromFormula("(B "+ getAgentName() +" (not (wearing "+getAgentName()+" ??clothing)))");
            // In the initial state, the son does not wear clothing
            getSemanticInterpreterBehaviour().interpret((Formula)SLPatternManip
                    .instantiate(initialKPattern, "clothing", new WordConstantNode("cap")));
            getSemanticInterpreterBehaviour().interpret((Formula)SLPatternManip
                    .instantiate(initialKPattern,"clothing", new WordConstantNode("coat")));
            getSemanticInterpreterBehaviour().interpret((Formula)SLPatternManip
                    .instantiate(initialKPattern,"clothing", new WordConstantNode("trousers")));
            getSemanticInterpreterBehaviour().interpret((Formula)SLPatternManip
                    .instantiate(initialKPattern, "clothing", new WordConstantNode("pullover")));
        }
        catch(Exception e) {e.printStackTrace();}
    } // End of setupKbase
    
    /**
     * Standard Customization setup.
     * Accepts the intention of his mother only.
     */
    
    public void setupStandardCustomization() {
        setMyStandardCustomization(new StandardCustomizationAdapter() {
            // The son accepts the orders and questions only his mother (intention transfer) 
            public boolean acceptIntentionTransfer(Formula goal, Term agent) {
                try {
                    return agent.equals(SLPatternManip.instantiate(AGENT_TERM, "agent", new WordConstantNode(motherAID.getName())));
                }
                catch (Exception e) {return false;}
            }
        });
    } // End of setupStandardCustomization/0
    
    /**
     * Action table setup
     * Adds ontological actions.
     */
    public void setupSemanticActions() {
        // Adds the semantic actions
        super.setupSemanticActions();
          
        // Ontological actions 
        // -------------------
        //Adds the action to put a clothing.
        getMySemanticActionTable().addSemanticAction(new OntologicalAction(getMySemanticActionTable(),
                "(PUT-ON :clothing ??clothing)",
                SLPatternManip.fromFormula("(wearing ??sender ??clothing)"),
                SLPatternManip.fromFormula("(not (wearing ??sender ??clothing))")) {
            public void perform(OntoActionBehaviour behaviour) {
                ((ManAgent)myAgent).putOn(getActionParameter("clothing").toString());
                behaviour.setState(SemanticBehaviour.SUCCESS);
            }
        });
          //Add the action to take off a clothing.
        getMySemanticActionTable().addSemanticAction(new OntologicalAction(getMySemanticActionTable(),
                "(TAKE-OFF :clothing ??clothing)",
                SLPatternManip.fromFormula("(not (wearing ??sender ??clothing))"),
                SLPatternManip.fromFormula("(wearing ??sender ??clothing)")){
            public void perform(OntoActionBehaviour behaviour) {
                ((ManAgent)myAgent).takeOff(getActionParameter("clothing").toString());
                behaviour.setState(SemanticBehaviour.SUCCESS);
            }
        });
        //Adds the action to wait a given time.
        getMySemanticActionTable().addSemanticAction(new OntologicalAction(getMySemanticActionTable(),
                "(WAIT :time ??time)",
                SLPatternManip.fromFormula("true"),
                SLPatternManip.fromFormula("true")){
            public void perform(OntoActionBehaviour behaviour) {
                if (behaviour.getState() == SemanticBehaviour.START) {
                    behaviour.block(new Long(getActionParameter("time").toString()));
                    behaviour.setState(1000);
                }
                else behaviour.setState(SemanticBehaviour.SUCCESS);
            }
        });
        
    } // End of setupSemanticActions/0
    
} // End of class ManAgentCapabilities
