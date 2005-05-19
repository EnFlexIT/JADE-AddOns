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
 * SemanticAgent.java
 * Created on 29 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;


import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.actions.SemanticActionTableImpl;
import jade.semantics.behaviours.SemanticInterpreterBehaviour;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.KbaseImpl_List;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.grammar.operations.DefaultNodeOperations;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.planner.Planner;
import jade.util.leap.ArrayList;


/**
 * Class that represents a semantic agent.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class SemanticAgent extends Agent {
    
    /**
     * Knowledge base for this agent
     */
    protected KBase myKBase;
    
    /**
     * Semantic interpretation principles table of this agent
     */
    protected SemanticInterpretationPrincipleTable mySemanticInterpretationTable;
    
    /**
     * Semantic action table of this agent
     */
    protected SemanticActionTable mySemanticActionTable;
 
    /**
     * Semantic interpreter behaviour
     */
    protected Behaviour myBehaviour;
 
    /**
     * 
     */
    protected Planner myPlanner;
    
    /**
     * 
     */
    protected StandardCustomization myStandardCustomization;
    
    
    /**
     * List of events (internal or external like a incoming message)
     */
    ArrayList eventList;
   
    /**
     * The SL Formula representation of this agent
     */
    Term agentName;
    
   
    static {
    	DefaultNodeOperations.installOperations();
    }
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor. Initializes all the tables.
     */
    public SemanticAgent() {
        super();
        
        eventList = new ArrayList();
    } // End of SemanticAgent/0

    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
  
    /**
     * Startup code :
     * <ul>
     * <li> creates the knowledge base and loads the filters and observers,
     * <li> creates and loads the semantic interpretaiton principle table,
     * <li> creates and loads the semantic action table,
     * <li> add the semantic behaviour.
     * </ul> 
     */
    public void setup() {
        super.setup();
        agentName = createAgentNameSLRepresentation(this.getName());
        setupStandardCustomization();
        setupKbase();
        setupSemanticInterpretationPrinciples();
        setupPlanner();
        setupSemanticActions();
        addBehaviour(myBehaviour = new SemanticInterpreterBehaviour());
    } // End of setup/0

    
    public void setupSemanticActions() {
        mySemanticActionTable = new SemanticActionTableImpl();
        mySemanticActionTable.loadTable();
    }
    
    public void setupSemanticInterpretationPrinciples() {
        mySemanticInterpretationTable = new SemanticInterpretationPrincipleTableImpl();
        mySemanticInterpretationTable.loadTable(this);
    }

    public void setupKbase() {
        myKBase = new KbaseImpl_List(this);
    }

    public void setupPlanner() {
        myPlanner = null;
    }
    
    public void setupStandardCustomization() {
        myStandardCustomization = new StandardCustomizationAdapter();
    }

    //////////////////////////////////////////////////////////////////
    
    
    /**
     * Adds an event (a SemanticRepresentation) in the internal event list
     * @param sr a SemanticRepresentation that represents the event
     */
    public void interpret(SemanticRepresentation sr) {
        eventList.add(sr);
        myBehaviour.restart();
    } // End of interprete/1
    
    /**
     * 
     * @param formula
     */
    public void interpret(Formula formula) {
        interpret(new SemanticRepresentationImpl(formula));
    } // End of interprete/1

    
    /**
     * Creates an event (a SemanticRepresentation) in the internal event list from
     * the string that represents an SL formula.
     * @param s the string representation of an SL formula.
     */
    public void interpret(String s) {
    	try {
	   		Formula f = SLParser.getParser().parseFormula(s, true);
			SLPatternManip.set(f, "??agent", getAgentName());
	   		interpret(new SemanticRepresentationImpl((Formula)SLPatternManip.instantiate(f)));
		} catch (Exception e) {e.printStackTrace();}
    } // End of interprete/1
    
    /**
     * @return Returns the eventList.
     */
    public ArrayList getEventList() {
        return eventList;
    } // End of getEventList
    
    /**
     * Creates the agent SL representation of this agent
     * @param agentName the name of this agent
     * @return the Term that represents the agent in a SL representation
     */
    private Term createAgentNameSLRepresentation(String agentName) {
        try {
            WordConstantNode agentNameNode = new WordConstantNode();
            agentNameNode.lx_value(agentName);
			Term a = SLParser.getParser().parseTerm("(agent-identifier :name ??name)", true);
			SLPatternManip.set(a, "??name", agentNameNode);
           return (Term)SLPatternManip.instantiate(a);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } // End of createAgentNameSLRepresentation/1
    
    /**
     * Returns the SL representation of this agent
     * @return Returns the agentName.
     */
    public Term getAgentName() {
        return agentName;
    } // End of getAgentName/0
    
    public void setAgentName(Term name) {
        agentName = name;
    }
    /**
     * @return Returns the myBehaviour.
     */
    public Behaviour getInterpretationAlgorithm() {
        return myBehaviour;
    }
    /**
     * @param myBehaviour The myBehaviour to set.
     */
    public void setMyBehaviour(Behaviour myBehaviour) {
        this.myBehaviour = myBehaviour;
    }
    
    /**
     * Gets the knowledge base of this agent.
     * @return Returns the knowledge base.
     */
    public KBase getMyKBase() {
        return myKBase;
    } // End of getMyKBase/0
 
    /**
     * @return Returns the myPlanner.
     */
    public Planner getMyPlanner() {
        return myPlanner;
    }
    /**
     * @param myPlanner The myPlanner to set.
     */
    public void setMyPlanner(Planner myPlanner) {
        this.myPlanner = myPlanner;
    }
    /**
     * @return Returns the mySemanticActionTable.
     */
    public SemanticActionTable getMySemanticActionTable() {
        return mySemanticActionTable;
    }
    /**
     * @param mySemanticActionTable The mySemanticActionTable to set.
     */
    public void setMySemanticActionTable(
            SemanticActionTable mySemanticActionTable) {
        this.mySemanticActionTable = mySemanticActionTable;
    }
    /**
     * @return Returns the mySemanticInterpretationTable.
     */
    public SemanticInterpretationPrincipleTable getMySemanticInterpretationTable() {
        return mySemanticInterpretationTable;
    }
    /**
     * @param mySemanticInterpretationTable The mySemanticInterpretationTable to set.
     */
    public void setMySemanticInterpretationTable(
            SemanticInterpretationPrincipleTable mySemanticInterpretationTable) {
        this.mySemanticInterpretationTable = mySemanticInterpretationTable;
    }
    /**
     * @return Returns the myStandardCustomization.
     */
    public StandardCustomization getMyStandardCustomization() {
        return myStandardCustomization;
    }
    /**
     * @param myStandardCustomization The myStandardCustomization to set.
     */
    public void setMyStandardCustomization(
            StandardCustomization myStandardCustomization) {
        this.myStandardCustomization = myStandardCustomization;
    }
    /**
     * @param myKBase The myKBase to set.
     */
    public void setMyKBase(KBase myKBase) {
        this.myKBase = myKBase;
    }
} // Fin de la classe SemanticAgent
