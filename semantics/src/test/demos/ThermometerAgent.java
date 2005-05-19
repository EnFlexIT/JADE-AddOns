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
 * ThermometerAgent.java
 * Created on 15 déc. 2004
 * Author : Vincent Pautret
 */
package test.demos;


import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.StandardCustomizationAdapter;
import jade.semantics.kbase.KBFilterManagment;
import jade.semantics.kbase.filter.KBAssertFilterAdapter;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.SLPatternManip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Class that defines the thermometer
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */

public class ThermometerAgent extends SemanticAgent {
    /*********************************************************************/
    /**                         VARIABLES   **/
    /*********************************************************************/
    TemperatureDefinition temperatureDefinition = new TemperatureDefinition();
    
    /**
     * Display of the thermometer
     */
    ThermometerDisplay display;
    
    /**
     * Temperature Sensor Agent term 
     */
    Term temperatureSensorAgent;

    /*********************************************************************/
    /**                         METHODS     **/
    /*********************************************************************/
    /**
     * Sends a requestWhenever.
     * Used a the initialisation to be inform of temperature variations by 
     * the temperature sensor.
     * @param content the content of the requestwhenever
     */
    protected void sendRequestWhenever(String content) {
        try {
            Thread.sleep(500);
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST_WHENEVER);
            msg.setSender(getAID());
            msg.addReceiver(new AID((getArguments()[0]).toString()));
            Content f = SLParser.getParser().parseContent(content, true);
            SLPatternManip.set(f, "??agent", getAgentName());
            SLPatternManip.set(f, "??receiver", temperatureSensorAgent);
            msg.setContent(SLPatternManip.instantiate(f).toString());
            send(msg);
            System.err.println("["+msg+"] was just sent");
        }
        catch(Exception e) {e.printStackTrace();}
    } // End of sendRequestWhenever/1
    
    /**
     * Creates the display for this agent
     */
    protected void createDisplay() {
        display = new ThermometerDisplay();
    } // End of createDisplay/0
    
    /**
     * Setup of this agent
     */
    public void setup() {
        super.setup();
        try {
            temperatureSensorAgent = (Term)SLParser.getParser().parseTerm("(agent-identifier :name " + getArguments()[0].toString() + ")"); 
        }
        catch(ParseException pe) {pe.printStackTrace();}

        ((KBFilterManagment)myKBase).addFiltersDefinition(temperatureDefinition);
        ((KBFilterManagment)myKBase).addKBAssertFilter(
                new KBAssertFilterAdapter("(B ??agent " + temperatureDefinition.TEMP_X_PATTERN + ")") {
                    public void afterAssert(Formula formula) {
                        try {
                            display.setTemperature(((IntegerConstantNode)applyResult.getTerm("??X")).lx_value());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        createDisplay();
        
        sendRequestWhenever("(( action ??receiver (INFORM-REF :sender ??receiver :receiver (set ??agent) :content \"((any ?x (temperature ?x)))\")) (tempHasChanged))");
    } // End of setup/0
    
    public void setupStandardCustomization() {
        myStandardCustomization = new StandardCustomizationAdapter() {
            public boolean acceptBeliefTransfer(Formula formula, Term agent) {
                return agent.equals(temperatureSensorAgent) ||
                !(SLPatternManip.match(temperatureDefinition.TEMP_X_PATTERN, formula) != null)
                || (SLPatternManip.match(temperatureDefinition.NOT_TEMP_X_PATTERN, formula) != null)
                || (SLPatternManip.match(temperatureDefinition.TEMPSUP_X_PATTERN, formula) != null)
                || (SLPatternManip.match(temperatureDefinition.NOT_TEMPSUP_X_PATTERN, formula) != null);
            }
        };
    }
    
    public void setupSemanticInterpretationPrinciples() {
        super.setupSemanticInterpretationPrinciples();
        mySemanticInterpretationTable.addSemanticInterpretationPrinciple(new TempHasChangedSubscribe(this), 10);
    }

    /*********************************************************************/
    /**                         INNER CLASSES   **/
    /*********************************************************************/
    /**
     * The display of this agent
     */
    public class ThermometerDisplay extends JFrame {
        
        TemperaturePanel panel;
        
        /**
         * Constructor
         */
        public ThermometerDisplay() {
            super(ThermometerAgent.this.getName());
            panel = new TemperaturePanel();
            add(panel);
            setBackground(Color.black);
            setSize(450,250);
            setVisible(true);
            setLocation(100,100);
        } // End of JaugeDisplay    
        
        /**
         * @param temp the temperature to set
         */
        public void setTemperature(Long temp) {
            panel.setTemperature(temp);
        } // End of setTemperature/1
        
    } // End of class ThermometerDisplay
    
    /**
     * Panel that contains the temperature
     */
    public class TemperaturePanel extends JPanel {
        Color dk;
        String temperature;
        Digit[] digits;
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        /**
         * Temperature Panel
         */
        public TemperaturePanel() {
            this.setFont(new Font("TimesRoman",Font.BOLD,40));
            dk = Color.darkGray;
            this.setBackground(Color.black);
            temperature = "+00";
            digits = setDigit(Color.green);
        } // End of TemperaturePanel/0
        
        /**
         * Sets all the digits for the current temperature.
         * @param color the color of the digits
         * @return an array of digits
         */
        public Digit[] setDigit(Color color){
            Digit[] lc = new Digit[5];
            if (temperature.startsWith("-")) {
                lc[0] = new Digit(11,color,dk);
            } else {
                lc[0] = new Digit(11,dk,dk);
            }
            lc[1] = new Digit(Integer.parseInt(temperature.substring(1,2)),color,dk);
            lc[2] = new Digit(Integer.parseInt(temperature.substring(2,3)),color,dk);
            lc[3] = new Digit(10,color,dk);
            lc[4] = new Digit(12,color,dk);
            return lc;
        } // End of setLCD/1
        
        /**
         * Draws the temperature by painting the digits.
         * @param g graphical area
         */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        digits[0].draw(g,20,40);
        digits[1].draw(g,100,40);
        digits[2].draw(g,180,40);
        digits[3].draw(g,260,40);
        digits[4].draw(g,340,40);
        } // End of paint/1
        
        /**
         * Sets the temperature
         * @param temp the temperature to set
         */
        public void setTemperature(Long temp) {
            if (temp.intValue() > 20) {
                temperature = "+" + new Integer(temp.intValue()).toString();
                digits = setDigit(Color.red);
            } else if (temp.intValue() >= 10) {
                temperature = "+" + new Integer(temp.intValue()).toString();
                digits = setDigit(Color.green);
            } else if (temp.intValue() >= 0 ) {
                temperature = "+0" + new Integer(temp.intValue()).toString();
                digits = setDigit(Color.blue);
            } else if (temp.intValue() >= -9){
                temperature = "-0" + new Integer(temp.intValue()).toString().substring(1);
                digits = setDigit(Color.white);
            } else {
                temperature = new Integer(temp.intValue()).toString();
                digits = setDigit(Color.white);
            }
            repaint();
        } // End of setTemperature/1
    } // End of TemperaturePanel
} // End of class ThermometerAgent