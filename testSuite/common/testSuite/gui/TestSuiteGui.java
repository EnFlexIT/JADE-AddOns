/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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

package test.common.testSuite.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.StringTokenizer;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.util.leap.ArrayList;

import test.common.TesterAgent;
import test.common.testSuite.TestSuiteAgent;

public class TestSuiteGui extends JFrame {
	// Gui states used to enable/disable buttons
	public static final int IDLE_STATE = 0;
	public static final int READY_STATE = 1;
	public static final int RUNNING_STATE = 2;
	public static final int DEBUGGING_STATE = 3;
	public static final int CONFIGURING_STATE = 4;
	private int status;
	
	private GuiAgent myAgent;
	private String[] testers;
	private String currentTester = null;
	
	private JButton exitB, openB, runB, debugB, stepB, configB;
	private JMenuItem exitI, openI, runI, debugI, stepI, configI;
	private JTextField currentF;
	
	public TestSuiteGui(GuiAgent myAgent, String[] testers) {
		super();
		this.myAgent = myAgent;
		this.testers = testers;
		
		setTitle("JADE Test Suite toolbar");
		
    Icon exitImg = GuiProperties.getIcon("exit");
    Icon openImg = GuiProperties.getIcon("open");
		Icon runImg = GuiProperties.getIcon("run");
		Icon debugImg = GuiProperties.getIcon("debug");
    Icon stepImg = GuiProperties.getIcon("step");
    Icon configImg = GuiProperties.getIcon("config");
    
		/////////////////////////////////////////////////////
		// Add Toolbar to the NORTH part of the border layout 
		JToolBar bar = new JToolBar();

		// TEST
		exitB  = bar.add(new ExitAction(this));
		exitB.setText("");
		exitB.setIcon(exitImg);
		//exitB.setDisabledIcon(exitImg);
		exitB.setToolTipText("Exit the JADE Test Suite");
		
		bar.addSeparator();
		
		openB  = bar.add(new OpenAction(this));
		openB.setText("");
		openB.setIcon(openImg);
		//runB.setDisabledIcon(runImg);
		openB.setToolTipText("Load a tester agent");
		
		bar.addSeparator();
		
		runB  = bar.add(new RunAction(this));
		runB.setText("");
		runB.setIcon(runImg);
		//runB.setDisabledIcon(runImg);
		runB.setToolTipText("Run the current tester agent");
		
		configB  = bar.add(new ConfigAction(this));
		configB.setText("");
		configB.setIcon(configImg);
		//runB.setDisabledIcon(runImg);
		configB.setToolTipText("Set arguments for the current tester agent");
		
		bar.addSeparator();
		
		// DEBUG
		debugB  = bar.add(new DebugAction(this));
		debugB.setText("");
		debugB.setIcon(debugImg);
		//goB.setDisabledIcon(debugImg);
		debugB.setToolTipText("Debug the current tester agent");
		
		stepB  = bar.add(new StepAction(this));
		stepB.setText("");
		stepB.setIcon(stepImg);
		//stepB.setDisabledIcon(stepImg);
		stepB.setToolTipText("Execute next test");
		
		bar.addSeparator();
		
		JLabel l = new JLabel("Current tester agent: "); 
		bar.add(l);
		currentF = new JTextField(30);
		currentF.setEditable(false);
		bar.add(currentF);
		
		getContentPane().add(bar, BorderLayout.NORTH);

		// Execute the EXIT action when the user attempts to close 
		// the JADE Test Suite GUI using the button on the upper right corner	
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ExitAction ac = new ExitAction(TestSuiteGui.this);
				ac.actionPerformed(new ActionEvent((Object) this, 0, "Exit"));
			}
		} );
		
		setResizable(false);
		setStatus(IDLE_STATE);
	}
	
	public void showCorrect() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		show();
	}
	
	// Method called by the OpenAction
	void open() {
		GenericChooser c = new GenericChooser();
		int i = c.showChoiceDlg(this,
						"Select the tester agent to run",
						"OK",
						"Cancel",
						testers);
		if (i >= 0) {
			String currentTester = testers[i];
			currentF.setText(currentTester);
			// If we are in the IDLE state --> the event to be posted to the
			// agent is LOAD. Otherwise (a tester was already active) it is RELOAD
			GuiEvent ev = new GuiEvent(this, (status == IDLE_STATE ? TestSuiteAgent.LOAD_EVENT : TestSuiteAgent.RELOAD_EVENT)); 
			ev.addParameter(currentTester);
			setStatus(READY_STATE);
			myAgent.postGuiEvent(ev);
		}
	}
	
	// Method called by the RunAction
	void run() {
		// If we are in the READY state --> the event to be posted to the
		// agent is RUN. Otherwise (the tester was already executing his 
		// test group in debug mode) it is GO
		GuiEvent ev = new GuiEvent(this, (status == READY_STATE ? TestSuiteAgent.RUN_EVENT : TestSuiteAgent.GO_EVENT)); 
		setStatus(RUNNING_STATE);
		myAgent.postGuiEvent(ev);
	}
	
	// Method called by the DebugAction
	void debug() {
		GuiEvent ev = new GuiEvent(this, TestSuiteAgent.DEBUG_EVENT); 
		setStatus(DEBUGGING_STATE);
		myAgent.postGuiEvent(ev);
	}
	
	// Method called by the ExitAction
	void exit() {
		GuiEvent ev = new GuiEvent(this, (status == IDLE_STATE ? TestSuiteAgent.EXIT_EVENT : TestSuiteAgent.CLOSE_AND_EXIT_EVENT)); 
		myAgent.postGuiEvent(ev); 
	}
	
	// Method called by the StepAction
	void step() {
		GuiEvent ev = new GuiEvent(this, TestSuiteAgent.STEP_EVENT); 
		myAgent.postGuiEvent(ev); 
	}
		
	// Method called by the ConfigureAction
	void config() {
		GuiEvent ev = new GuiEvent(this, TestSuiteAgent.CONFIGURE_EVENT); 
		myAgent.postGuiEvent(ev); 
	}
		
	public void setStatus(int status) {
		this.status = status;
		updateEnabled();
	}
	
	private void updateEnabled() {
		openB.setEnabled(status == IDLE_STATE || status == READY_STATE);
		runB.setEnabled(status == READY_STATE || status == DEBUGGING_STATE);
		debugB.setEnabled(status == READY_STATE);
		stepB.setEnabled(status == DEBUGGING_STATE);
		configB.setEnabled(status == READY_STATE);
	}
	
}
