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

import test.common.testSuite.TestSuiteAgent;
import test.common.TestUtility;
import test.common.TestException;
import test.common.remote.TSDaemon;
import test.common.remote.RemoteManager;
import test.common.xml.*;

/**
 * @author Giovanni Caire - TiLab
 * @author Elisabetta Cortese - TiLab
*/
public class TestSuiteGui extends JFrame {
	// Gui states used to enable/disable buttons
	public static final int IDLE_STATE = 0;
	public static final int READY_STATE = 1;
	public static final int RUNNING_STATE = 2;
	public static final int DEBUGGING_STATE = 3;
	public static final int CONFIGURING_STATE = 4;
	private int status;
	
	private GuiAgent myAgent;
	private String xmlFileName;
	//private String currentTester = null;
	private TSDaemonConnectionConfiguration daemonConf = new TSDaemonConnectionConfiguration();
		
	private JButton exitB, openB, runB, debugB, stepB, configB, connectB, runAllB;
	private JMenuItem exitI, openI, runI, debugI, stepI, configI, connectI, runAllI;
	private JTextField currentF;
	
	public TestSuiteGui(GuiAgent myAgent, String xmlFileName) {
		super();
		this.myAgent = myAgent;
		this.xmlFileName = xmlFileName;
		
		setTitle("JADE Test Suite toolbar");
		
    Icon exitImg = GuiProperties.getIcon("exit");
    Icon openImg = GuiProperties.getIcon("open");
		Icon runImg = GuiProperties.getIcon("run");
		Icon runAllImg = GuiProperties.getIcon("runAll");
		Icon debugImg = GuiProperties.getIcon("debug");
    Icon stepImg = GuiProperties.getIcon("step");
    Icon configImg = GuiProperties.getIcon("config");
    Icon connectImg = GuiProperties.getIcon("connect");
    
		/////////////////////////////////////////////////////
		// Add Toolbar to the NORTH part of the border layout 
		JToolBar bar = new JToolBar();

		// EXIT button
		exitB  = bar.add(new ExitAction(this));
		exitB.setText("");
		exitB.setIcon(exitImg);
		//exitB.setDisabledIcon(exitImg);
		exitB.setToolTipText("Exit the JADE Test Suite");
		
		// CONNECT button
		connectB  = bar.add(new ConnectAction(this));
		connectB.setText("");
		connectB.setIcon(connectImg);
		//connectB.setDisabledIcon(connectImg);
		connectB.setToolTipText("Use the Test Suite Daemon to launch other JADE instances remotely");
		
		bar.addSeparator();
		
		// OPEN button
		openB  = bar.add(new OpenAction(this));
		openB.setText("");
		openB.setIcon(openImg);
		//runB.setDisabledIcon(runImg);
		openB.setToolTipText("Load a tester agent");
		
		// CONFIG button
		configB  = bar.add(new ConfigAction(this));
		configB.setText("");
		configB.setIcon(configImg);
		//runB.setDisabledIcon(runImg);
		configB.setToolTipText("Set arguments for the current tester agent");
		
		// RUN button
		runB  = bar.add(new RunAction(this));
		runB.setText("");
		runB.setIcon(runImg);
		//runB.setDisabledIcon(runImg);
		runB.setToolTipText("Run the current tester agent");
	
		// RUN-ALL button
		runAllB = bar.add(new RunAllAction(this));
		runAllB.setText("");
		runAllB.setIcon(runAllImg);
		runAllB.setToolTipText("Run all tester agents");
		
		bar.addSeparator();
		
		// DEBUG button
		debugB  = bar.add(new DebugAction(this));
		debugB.setText("");
		debugB.setIcon(debugImg);
		//goB.setDisabledIcon(debugImg);
		debugB.setToolTipText("Debug the current tester agent");
		
		// STEP button
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
		// Initially status must be in IDLE state
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
		FunctionalityDescriptor func = TesterListVisualizer.showSelectionDlg(this, xmlFileName);
		if (func != null) {
			System.out.println("Functionality selected: "+func.getName());
			currentF.setText(func.getName());
			GuiEvent ev = new GuiEvent(this, TestSuiteAgent.LOAD_EVENT); 
			ev.addParameter(func.getTesterClassName());
			myAgent.postGuiEvent(ev);
		}
	}
	
	// Method called by the ConnectAction
	void connect() {
		TSDaemonConnectionDlg.configure(daemonConf);
		if (daemonConf.getChanged()) {
			if (daemonConf.getConnect()) {
				try {
					RemoteManager rm = TestUtility.createRemoteManager(daemonConf.getHostName(), TSDaemon.DEFAULT_PORT, TSDaemon.DEFAULT_NAME);
					TestUtility.setDefaultRemoteManager(rm);
				}
				catch (TestException te) {
					System.out.println("Error connecting to the Test Suite Daemon. "+te.getMessage());
				}
			}
			else {
				TestUtility.setDefaultRemoteManager(null);
			}
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

	// Method called by the RunAllAction
	void runAll() {
		FunctionalityDescriptor[] allFunc = XMLManager.getFunctionalities(xmlFileName);
		GuiEvent ev = new GuiEvent(this, ((status == IDLE_STATE || status == READY_STATE) ? TestSuiteAgent.RUNALL_EVENT : TestSuiteAgent.GO_EVENT)); 
		ev.addParameter(allFunc);
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
		GuiEvent ev = new GuiEvent(this, TestSuiteAgent.EXIT_EVENT); 
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
	//Elisabetta 
	public int getStatus() {
		return this.status;
	}
	public void setCurrentF(String c){
		currentF.setText(c);
	}
	
	private void updateEnabled() {
		openB.setEnabled(status == IDLE_STATE || status == READY_STATE);
		runB.setEnabled(status == READY_STATE || status == DEBUGGING_STATE);
		runAllB.setEnabled(status == IDLE_STATE || status == READY_STATE);
		debugB.setEnabled(status == READY_STATE);
		stepB.setEnabled(status == DEBUGGING_STATE);
		configB.setEnabled(status == READY_STATE);
	}
	
}
