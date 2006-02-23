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
/*#DOTNET_INCLUDE_BEGIN
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.interpreter.SemanticAgent;
#DOTNET_INCLUDE_END*/
import jade.semantics.actions.OntologicalAction;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.StandardCustomizationAdapter;
import jade.semantics.kbase.FilterKBase;
import jade.semantics.kbase.FilterKBaseImpl;
import jade.semantics.kbase.observer.EventCreationObserver;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

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
    * Belief base setup.
    * Adds Observer on differents levels of temperature and initial belief
    * of the son agent.
    */
   public void setupKbase() {
       // KBase setup
       // -----------
       super.setupKbase();
       try {
           // KBase Observers setup
           // -------------------
           FilterKBase kb = ((FilterKBase)getMyKBase());
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
           
           // Initial belief setup 
           // -----------------------
           ((FilterKBaseImpl)getMyKBase()).addClosedPredicate(SLPatternManip.fromFormula("(wearing "+getAgentName()+" ??c)"));
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
               catch (WrongTypeException wte) {return false;}
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
        //#DOTNET_EXCLUDE_BEGIN
       getMySemanticActionTable().addSemanticAction(new OntologicalAction(getMySemanticActionTable(),
                "(PUT-ON :clothing ??clothing)",
               SLPatternManip.fromFormula("(wearing ??sender ??clothing)"),
               SLPatternManip.fromFormula("(not (wearing ??sender ??clothing))")) {
           public void perform(OntoActionBehaviour behaviour) {
               ((ManAgent)myAgent).putOn(getActionParameter("clothing").toString());
               behaviour.setState(SemanticBehaviour.SUCCESS);
           }
       });
        //#DOTNET_EXCLUDE_END
        /*#DOTNET_INCLUDE_BEGIN
        getMySemanticActionTable().addSemanticAction( new OntologicalAction1(getMySemanticActionTable(),
            "(PUT-ON :clothing ??clothing)",
            SLPatternManip.fromFormula("(wearing ??sender ??clothing)"),
            SLPatternManip.fromFormula("(not (wearing ??sender ??clothing))"), myAgent) );//Add the action to take off a clothing.
        #DOTNET_INCLUDE_END*/
       //Add the action to take off a clothing.
       
        //#DOTNET_EXCLUDE_BEGIN
        getMySemanticActionTable().addSemanticAction(new OntologicalAction(getMySemanticActionTable(),
               "(TAKE-OFF :clothing ??clothing)",
               SLPatternManip.fromFormula("(not (wearing ??sender ??clothing))"),
               SLPatternManip.fromFormula("(wearing ??sender ??clothing)")){
           public void perform(OntoActionBehaviour behaviour) {
               ((ManAgent)myAgent).takeOff(getActionParameter("clothing").toString());
               behaviour.setState(SemanticBehaviour.SUCCESS);
           }
       });
        //#DOTNET_EXCLUDE_END
        /*#DOTNET_INCLUDE_BEGIN
       getMySemanticActionTable().addSemanticAction(new OntologicalAction2(getMySemanticActionTable(),
               "(TAKE-OFF :clothing ??clothing)",
               SLPatternManip.fromFormula("(not (wearing ??sender ??clothing))"),
               SLPatternManip.fromFormula("(wearing ??sender ??clothing)"), myAgent));
        #DOTNET_INCLUDE_END*/
       //Adds the action to wait a given time.

        //#DOTNET_EXCLUDE_BEGIN
       getMySemanticActionTable().addSemanticAction(new OntologicalAction(getMySemanticActionTable(),
               "(WAIT :time ??time)",
               SLPatternManip.fromFormula("true"),
               SLPatternManip.fromFormula("true")){
           private long wakeupTime = -1, blockTime;

           public void perform(OntoActionBehaviour behaviour) {
               switch (behaviour.getState()) {
               case SemanticBehaviour.START: {
                    if (wakeupTime == -1) {
                        wakeupTime = System.currentTimeMillis()+Long.parseLong((getActionParameter("time").toString()));
                    }
                 // in this state the behaviour blocks itself
                 blockTime = wakeupTime - System.currentTimeMillis();
                 if (blockTime > 0) 
                   behaviour.block(blockTime);
                 behaviour.setState(1000);
                 break;
               }
               case 1000: {
                 // in this state the behaviour can be restarted for two reasons
                 // 1. the timeout is elapsed (then the handler method is called 
                 //                            and the behaviour is definitively finished) 
                 // 2. a message has arrived for this agent (then it blocks again and
                 //                            the FSM remains in this state)
                 blockTime = wakeupTime - System.currentTimeMillis();
                 if (blockTime <= 0) {
                   // timeout is expired
                   behaviour.setState(SemanticBehaviour.SUCCESS);
                 } else 
                   behaviour.block(blockTime);
                 break;
               }
               default : {
                 behaviour.setState(SemanticBehaviour.EXECUTION_FAILURE);
                 break;
               }
               } // end of switch
           }
       });
        //#DOTNET_EXCLUDE_END
        /*#DOTNET_INCLUDE_BEGIN
       getMySemanticActionTable().addSemanticAction(new OntologicalAction3(getMySemanticActionTable(),
               "(WAIT :time ??time)",
               SLPatternManip.fromFormula("true"),
               SLPatternManip.fromFormula("true"), myAgent));
        #DOTNET_INCLUDE_END*/
       
   } // End of setupSemanticActions/0
   
} // End of class ManAgentCapabilities

/*#DOTNET_INCLUDE_BEGIN
public class OntologicalAction1 extends OntologicalAction
{
    private SemanticAgent myAgent;

    public OntologicalAction1(SemanticActionTable table,
        String actionPattern, 
        Formula postconditionPattern,
        Formula preconditionPattern, SemanticAgent ag) 
    {
        super(table, actionPattern, postconditionPattern, preconditionPattern);
        myAgent = ag;
    }

    public void perform(OntoActionBehaviour behaviour) 
    {
        ((ManAgent)myAgent).putOn(getActionParameter("clothing").toString());
        behaviour.setState(SemanticBehaviour.SUCCESS);
    }
}

public class OntologicalAction2 extends OntologicalAction
{
    private SemanticAgent myAgent;

    public OntologicalAction1(SemanticActionTable table,
        String actionPattern, 
        Formula postconditionPattern,
        Formula preconditionPattern, SemanticAgent ag) 
    {
        super(table, actionPattern, postconditionPattern, preconditionPattern);
        myAgent = ag;
    }

    public void perform(OntoActionBehaviour behaviour) 
    {
        ((ManAgent)myAgent).takeOff(getActionParameter("clothing").toString());
        behaviour.setState(SemanticBehaviour.SUCCESS);
    }
}

public class OntologicalAction2 extends OntologicalAction
{
    private SemanticAgent myAgent;

    private long wakeupTime = -1, blockTime;
    
    public OntologicalAction2(SemanticActionTable table,
        String actionPattern, 
        Formula postconditionPattern,
        Formula preconditionPattern, SemanticAgent ag) 
    {
        super(table, actionPattern, postconditionPattern, preconditionPattern);
        myAgent = ag;
    }

    public void perform(OntoActionBehaviour behaviour) 
    {
           
               switch (behaviour.getState()) {
               case SemanticBehaviour.START: {
                    if (wakeupTime == -1) {
                        wakeupTime = System.currentTimeMillis()+Long.parseLong((getActionParameter("time").toString()));
                    }
                 // in this state the behaviour blocks itself
                 blockTime = wakeupTime - System.currentTimeMillis();
                 if (blockTime > 0) 
                   behaviour.block(blockTime);
                 behaviour.setState(1000);
                 break;
               }
               case 1000: {
                 // in this state the behaviour can be restarted for two reasons
                 // 1. the timeout is elapsed (then the handler method is called 
                 //                            and the behaviour is definitively finished) 
                 // 2. a message has arrived for this agent (then it blocks again and
                 //                            the FSM remains in this state)
                 blockTime = wakeupTime - System.currentTimeMillis();
                 if (blockTime <= 0) {
                   // timeout is expired
                   behaviour.setState(SemanticBehaviour.SUCCESS);
                 } else 
                   behaviour.block(blockTime);
                 break;
               }
               default : {
                 behaviour.setState(SemanticBehaviour.EXECUTION_FAILURE);
                 break;
               }
               } // end of switch
    }
}
#DOTNET_INCLUDE_END*/
