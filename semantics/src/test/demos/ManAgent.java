/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

GNU Lesser General Public License

This library is custom software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Custom Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Custom Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
/*
 * ManAgent.java
 * Created on 26 nov. 2004
 * Author : Thierry Martinez & Vincent Pautret
 */
package test.demos;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.OntologicalAction;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTableImpl;
import jade.semantics.interpreter.StandardCustomizationAdapter;
import jade.semantics.kbase.KBFilterManagment;
import jade.semantics.kbase.observer.EventCreationObserver;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.Set;
import jade.util.leap.SortedSetImpl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Class of the Man Agent.
 * Used to define the Son Agent.
 * @author Thierry Martinez & Vincent Pautret
 *
 */
public class ManAgent extends SemanticAgent 
{
    /*********************************************************************/
    /**				 			VARIABLES	**/
    /*********************************************************************/
    /**
     * The display of the agent
     */
    DemoAgentGui gui = null;
    
    /**
     * AID of the thermometer
     */
    AID thermometerAID = null;
    
    /**
     * AID of the mother
     */
    AID motherAID  = null;
    
    /**
     * Table of clothing items
     */
    Set clothings;
    
    /***
     * Panel used to display the clothing icons
     */
    JPanel manPanel;

    
    /*********************************************************************/
    /**				 			CONSTANTS	**/
    /*********************************************************************/
    /**
     * RequestWhenever content pattern
     */
    static final Content REQUEST_WHENEVER_SUBSCRIBE_PATTERN = 
		SLPatternManip.fromContent("((action ??thermometer (INFORM :sender ??thermometer :receiver (set ??me) :content ??content)) ??phi)");

    /**
     * Inform unsubscribe pattern
     */
    static final Content INFORM_UNSUBSCRIBE_PATTERN = 
		SLPatternManip.fromContent("((or (not (done (action null before) (not (B ??thermometer ??phi)))) (not (I ??me (done (action ??thermometer (INFORM :sender ??thermometer :receiver (set ??me) :content ??content)) true)))))");
    
    /**
     * All clothing query pattern
     */
    static final IdentifyingExpression ALL_CLOTHING_PATTERN = 
		(IdentifyingExpression)SLPatternManip.fromTerm("(all ?x (wearing ??agent ?x))");
	
    /**
     * Agent term pattern
     */
    static final Term AGENT_TERM = 
		SLPatternManip.fromTerm("(agent-identifier :name ??agent)");

	/*********************************************************************/
    /**				 			METHODS		**/
    /*********************************************************************/

    /**
     * Setup of this agent
     */
    public void setup() {

        // Dealing with args
	// -----------------
	testArgs();
	super.setup();
	thermometerAID = new AID((getArguments()[1]).toString(),true);
	motherAID = new AID((getArguments()[2]).toString(),true);
    clothings = new SortedSetImpl();
   	    
	// KBase setup
	// -----------

	try {
	    // KBase Observers setup
	    // -------------------
	    KBFilterManagment kb = ((KBFilterManagment)myKBase);
	    kb.addFiltersDefinition(new TemperatureDefinition());
	    	
        myKBase.addObserver(new EventCreationObserver(this,
	     (Formula)SLParser.getParser().parseFormula("(B "+getAgentName()+" (tempSup 20))"),
	     (Formula)SLParser.getParser().parseFormula("(I "+getAgentName()+" (not (wearing "+getAgentName()+" trousers)))")));

        myKBase.addObserver(new EventCreationObserver(this,
	     (Formula)SLParser.getParser().parseFormula("(B "+getAgentName()+" (tempSup 15))"),
	     (Formula)SLParser.getParser().parseFormula("(I "+getAgentName()+" (not (wearing "+getAgentName()+" pullover)))")));

        myKBase.addObserver(new EventCreationObserver(this,
	     (Formula)SLParser.getParser().parseFormula("(B "+getAgentName()+" (tempSup 10))"),
	     (Formula)SLParser.getParser().parseFormula("(I "+getAgentName()+" (not (wearing "+getAgentName()+" coat)))")));

        myKBase.addObserver(new EventCreationObserver(this,
	     (Formula)SLParser.getParser().parseFormula("(B "+getAgentName()+" (tempSup 0))"),
	     (Formula)SLParser.getParser().parseFormula("(I "+getAgentName()+" (not (wearing "+getAgentName()+" cap)))")));

        myKBase.addObserver(new EventCreationObserver(this,
	     (Formula)SLParser.getParser().parseFormula("(B "+getAgentName()+" (not (tempSup 20)))"),
	     (Formula)SLParser.getParser().parseFormula("(I "+getAgentName()+" (wearing "+getAgentName()+" trousers))")));

        myKBase.addObserver(new EventCreationObserver(this,
	     (Formula)SLParser.getParser().parseFormula("(B "+getAgentName()+" (not (tempSup 15)))"),
	     (Formula)SLParser.getParser().parseFormula("(I "+getAgentName()+" (wearing "+getAgentName()+" pullover))")));

	    myKBase.addObserver(new EventCreationObserver(this,
	     (Formula)SLParser.getParser().parseFormula("(B "+getAgentName()+" (not (tempSup 10)))"),
	     (Formula)SLParser.getParser().parseFormula("(I "+getAgentName()+" (wearing "+getAgentName()+" coat))")));

	    myKBase.addObserver(new EventCreationObserver(this,
	     (Formula)SLParser.getParser().parseFormula("(B "+getAgentName()+" (not (tempSup 0)))"),
	     (Formula)SLParser.getParser().parseFormula("(I "+getAgentName()+" (wearing "+getAgentName()+" cap))")));

	    
	    // Initial Knowledge setup 
	    // -----------------------
	    Formula initialKPattern = 
			SLPatternManip.fromFormula("(B "+getAgentName()+" (not (wearing "+getAgentName()+" ??clothing)))");

		interpret((Formula)SLPatternManip
				.instantiate(initialKPattern, "??clothing", new WordConstantNode("cap")));
		interpret((Formula)SLPatternManip
				.instantiate(initialKPattern,"??clothing", new WordConstantNode("coat")));
		interpret((Formula)SLPatternManip
				.instantiate(initialKPattern,"??clothing", new WordConstantNode("trousers")));
		interpret((Formula)SLPatternManip
				.instantiate(initialKPattern, "??clothing", new WordConstantNode("pullover")));
	}
	catch(Exception e) {e.printStackTrace();}
	
	setupGui();
    } // End of setup/0
    
    /**
     * Standard Customization setup
     */
    public void setupStandardCustomization() {
        myStandardCustomization = new StandardCustomizationAdapter() {
            public boolean acceptIntentionTransfer(Formula goal, Term agent) {
                try {
                    SLPatternManip.set(AGENT_TERM, "??agent", new WordConstantNode(motherAID.getName()));
                    return agent.equals(SLPatternManip.instantiate(AGENT_TERM));
                }
                catch (Exception e) {return false;}
            }
        };
    } // End of setupStandardCustomization/0
    
    /**
     * Action table setup
     */
    public void setupSemanticActions() {
        super.setupSemanticActions();
        
        // Ontological actions 
        // -------------------
        mySemanticActionTable.addSemanticAction(new OntologicalAction(mySemanticActionTable,
                        SLPatternManip.fromTerm("(PUT-ON :clothing ??clothing)"),
                        SLPatternManip.fromFormula("(wearing ??sender ??clothing)"),
                        SLPatternManip.fromFormula("(not (wearing ??sender ??clothing))")) {
            public void perform(OntoActionBehaviour behaviour) {
                putOn(getParameter("??clothing").toString());
                behaviour.setState(SemanticBehaviour.SUCCESS);
            }
        });
        
        mySemanticActionTable.addSemanticAction(new OntologicalAction(mySemanticActionTable,
                        SLPatternManip.fromTerm("(TAKE-OFF :clothing ??clothing)"),
                        SLPatternManip.fromFormula("(not (wearing ??sender ??clothing))"),
                        SLPatternManip.fromFormula("(wearing ??sender ??clothing)")){
            public void perform(OntoActionBehaviour behaviour) {
                takeOff(getParameter("??clothing").toString());
                behaviour.setState(SemanticBehaviour.SUCCESS);
            }
        });

    } // End of setupSemanticActions/0
    
    
    public void setupSemanticInterpretationPrinciples() {
        super.setupSemanticInterpretationPrinciples();
        mySemanticInterpretationTable.addSemanticInterpretationPrinciple(new TempHasChangedSubscribe(this), 10);
    } // End of setupSemanticInterpretationPrinciples/0
    /*********************************************************************/
    /**				 			INTERNALS	**/
    /*********************************************************************/
    
    /**
     * Repaints the man panel when a clothing is put on
     * @param clothing the clothing to be put on
     */
    public void putOn(String clothing) {
        clothings.add(clothing);
        if (manPanel != null) manPanel.repaint();
    } // End of putOn/1
    
    /**
     * Repaints the man panel when a clothing is taken off
     * @param clothing
     */
    public void takeOff(String clothing) {
        clothings.remove(clothing);
        if (manPanel != null) manPanel.repaint();
    } // End of takeOff/1

    /**
     * Sends messages. Used by the subscribe button.
     * @param formula a formula
     * @param perf the performative to send
     */
    protected void manageTempSubscription(String formulaStr, boolean subscribe) {
	try {
	    ACLMessage msg = new ACLMessage(subscribe ? ACLMessage.REQUEST_WHENEVER : ACLMessage.INFORM);
	    msg.setSender(getAID());
	    msg.addReceiver(thermometerAID);
	
	    Term receiver = (Term)SLParser.getParser().parseTerm(thermometerAID.toString());
	    Formula formula = (Formula)SLParser.getParser().parseFormula(formulaStr);
	    Content content = (Content)SLParser.getParser().parseContent("("+formula+")");
		    
	    if (subscribe) {
			SLPatternManip.set(REQUEST_WHENEVER_SUBSCRIBE_PATTERN, "??me", getAgentName());
			SLPatternManip.set(REQUEST_WHENEVER_SUBSCRIBE_PATTERN, "??thermometer", receiver);
			SLPatternManip.set(REQUEST_WHENEVER_SUBSCRIBE_PATTERN, "??content", new StringConstantNode(content.toString()));
			SLPatternManip.set(REQUEST_WHENEVER_SUBSCRIBE_PATTERN, "??phi", formula);
			msg.setContent(SLPatternManip.instantiate(REQUEST_WHENEVER_SUBSCRIBE_PATTERN).toString());
	    } else {
			SLPatternManip.set(INFORM_UNSUBSCRIBE_PATTERN, "??me", getAgentName());
			SLPatternManip.set(INFORM_UNSUBSCRIBE_PATTERN, "??thermometer", receiver);
			SLPatternManip.set(INFORM_UNSUBSCRIBE_PATTERN, "??content", new StringConstantNode(content.toString()));
			SLPatternManip.set(INFORM_UNSUBSCRIBE_PATTERN, "??phi", formula);
			msg.setContent(SLPatternManip.instantiate(INFORM_UNSUBSCRIBE_PATTERN).toString());
	    }
		    
	    send(msg);
	    System.err.println("["+msg+"] was just sent");
	}
	catch(Exception e) {e.printStackTrace();}
    } // End of sendSubscribeMessage
    
    /**
     * Tests the arguments
     */
    protected void testArgs() {
    	Object[] args = getArguments();
    	if ( args == null || args.length < 3 ) {
    	    System.err.println("ERROR : agent should be run like this "
			       +"<agent name>:DemosAgent\\(<file_name agent_to_subscribe mother_agent_name [showkb]>\\)");
    	    System.err.print("ERROR : current args are :");
    	    for (int i=0; i<args.length; i++) {System.err.print(args[i]+" ");}
    	    System.err.println();
    	    System.exit(1);
    	}
	System.err.print("setup of DemosAgent(");
	for (int i=0; i<args.length; i++) {System.err.print(args[i]+" ");}
	System.err.println(")");
    } // End of testArgs/0

    /**
     * Set the gui within the given panel
     */
    protected void setupGui()
    {
    	Object[] args = getArguments();

	gui = new DemoAgentGui(getName(), args[0].toString(), this, true,  args.length >= 4 && args[3].equals("showkb"));

	try {
		SLPatternManip.set(ALL_CLOTHING_PATTERN, "??agent", getAgentName());
	    final IdentifyingExpression allClothingsIE = (IdentifyingExpression)SLPatternManip.instantiate(ALL_CLOTHING_PATTERN);

	    manPanel = (new JPanel() {
		    public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(gui.getIcon("man").getImage(), 0, 0, null);
            Object[] array = clothings.toArray();
			for (int i = array.length - 1; i >=0 ; i--) {
                String clothing = (String)array[i];
				g.drawImage(gui.getIcon(clothing).getImage(), 0, 0, null);
			}
		    }});
	}
	catch (Exception e) {e.printStackTrace();}

	manPanel.setPreferredSize(new Dimension(250,291));
	gui.getCustomPanel().add(manPanel);

    final JToggleButton toggleButton = new JToggleButton("Subscribe");
	toggleButton.setIcon(gui.getIcon("red"));
	toggleButton.addActionListener(new AbstractAction("Subscribing") {
		public void actionPerformed(ActionEvent evt) {
		    if (toggleButton.isSelected()) {
			toggleButton.setIcon(gui.getIcon("green"));
			toggleButton.setText("UnSubscribe");
			manageTempSubscription("(tempSup 0)", true);
			manageTempSubscription("(tempSup 10)", true);
			manageTempSubscription("(tempSup 15)", true);
			manageTempSubscription("(tempSup 20)", true);
			manageTempSubscription("(not (tempSup 0))", true);
			manageTempSubscription("(not (tempSup 10))", true);
			manageTempSubscription("(not (tempSup 15))", true);
			manageTempSubscription("(not (tempSup 20))", true);
		    } 
		    else {
			toggleButton.setIcon(gui.getIcon("red"));
			toggleButton.setText("Subscribe");
			manageTempSubscription("(tempSup 0)", false);
			manageTempSubscription("(tempSup 10)", false);
			manageTempSubscription("(tempSup 15)", false);
			manageTempSubscription("(tempSup 20)", false);
			manageTempSubscription("(not (tempSup 0))", false);
			manageTempSubscription("(not (tempSup 10))", false);
			manageTempSubscription("(not (tempSup 15))", false);
			manageTempSubscription("(not (tempSup 20))", false);
		    }
		}});
	gui.getCustomPanel().add(BorderLayout.SOUTH, toggleButton);
	gui.pack();
    } // End of setupGui/0
} // End of class ManAgent