/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
Lice                                                 nse as published by the Free Software Foundation, 
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
 * TemperatureSensorAgent.java
 * Created on 6 dec. 2004
 * Author : Thierry Martinez & Vincent Louis
 */
package test.demos;

import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.StandardCustomizationAdapter;
import jade.semantics.kbase.KBFilterManagment;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SLPatternManip;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class of the Temperature Sensor Agent
 * @author Thierry Martinez & Vincent Louis
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class TemperatureSensorAgent extends SemanticAgent {

    /*********************************************************************/
    /**                         VARIABLES   **/
    /*********************************************************************/
    TemperatureDefinition temperatureDefinition = new TemperatureDefinition();

    /** ****************************************************************** */
    /** METHODS * */
    /** ****************************************************************** */
    /**
     * Set up of the agent
     */
    public void setup() {
        super.setup();

        ((KBFilterManagment) myKBase).addFiltersDefinition(temperatureDefinition);
        guiSetup();
    } // End of setup

    public void setupStandardCustomization() {
        myStandardCustomization = new StandardCustomizationAdapter() {
            public boolean acceptBeliefTransfer(Formula formula, Term agent) {
                return !(SLPatternManip.match(temperatureDefinition.TEMP_X_PATTERN, formula)!=null)
                || (SLPatternManip.match(temperatureDefinition.NOT_TEMP_X_PATTERN, formula)!=null)
                || (SLPatternManip.match(temperatureDefinition.TEMPSUP_X_PATTERN, formula)!=null)
                || (SLPatternManip.match(temperatureDefinition.NOT_TEMPSUP_X_PATTERN, formula)!=null);
            }
        };
    }
    
    public void setupSemanticInterpretationPrinciples() {
        super.setupSemanticInterpretationPrinciples();
        mySemanticInterpretationTable.addSemanticInterpretationPrinciple(new TempHasChangedSubscribe(this), 10);
    }

    /**
     * Gui set up of the agent
     */
    protected void guiSetup() {
        final JSlider slider = new JSlider(JSlider.VERTICAL, -20, 50, 0);
        final SemanticAgent thisAgent = this;
        JFrame frame = new JFrame(getName());
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.getContentPane().add(mainPanel);
        mainPanel.add(slider);
        mainPanel.add(BorderLayout.SOUTH, new JLabel("Temperature Sensor"));
        java.util.Hashtable labelTable = new java.util.Hashtable();
        labelTable.put(new Integer(50), new JLabel("50"));
        labelTable.put(new Integer(40), new JLabel("40"));
        labelTable.put(new Integer(30), new JLabel("30"));
        labelTable.put(new Integer(20), new JLabel("20"));
        labelTable.put(new Integer(10), new JLabel("10"));
        labelTable.put(new Integer(0), new JLabel("0"));
        labelTable.put(new Integer(-10), new JLabel("-10"));
        labelTable.put(new Integer(-20), new JLabel("-20"));
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                interpret("(B ??agent (temperature " + slider.getValue() + "))");
            }
        });
        slider.setValue(21);
        frame.setSize(100, 400);
        frame.setVisible(true);
    } // End of guiSetup/0
    
} // End of TemperatureSensorAgent