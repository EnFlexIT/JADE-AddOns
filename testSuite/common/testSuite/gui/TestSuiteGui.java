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
	private int status;
	
	private GuiAgent myAgent;
	private String[] testers;
	private String currentTester = null;
	
	private JButton exitB, openB, runB, debugB, stepB;
	private JMenuItem exitI, openI, runI, debugI, stepI;
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
	
	void runTester() {
		TesterAgent tester = loadTester();
		if (tester != null) {
			setStatus(RUNNING_STATE);
			tester.setDebugMode(false);
			GuiEvent ev = new GuiEvent(this, TestSuiteAgent.START_TESTER_EVENT); 
			ev.addParameter(tester);
			myAgent.postGuiEvent(ev);
		}
	}
	
	void debugTester() {
		TesterAgent tester = loadTester();
		if (tester != null) {
			setStatus(DEBUGGING_STATE);
			tester.setDebugMode(true);
			GuiEvent ev = new GuiEvent(this, TestSuiteAgent.START_TESTER_EVENT); 
			ev.addParameter(tester);
			myAgent.postGuiEvent(ev);
		}
	}
	
	void exit() {
		myAgent.postGuiEvent(new GuiEvent(this, TestSuiteAgent.EXIT_EVENT)); 
	}
	
	void step() {
		myAgent.postGuiEvent(new GuiEvent(this, TestSuiteAgent.STEP_EVENT)); 
	}
		
	void selectTester() {
		GenericChooser c = new GenericChooser();
		int i = c.showChoiceDlg(this,
						"Select the tester agent to run",
						"OK",
						"Cancel",
						testers);
		if (i >= 0) {
			currentTester = testers[i];
			currentF.setText(currentTester);
		}
		
		if (currentTester != null) {
			setStatus(READY_STATE);
		}
		else {
			setStatus(IDLE_STATE);
		}
	}
	
	/*private Object[] getArguments() {
		ArrayList l = new ArrayList();
		String args = argsF.getText().trim();
		if (args != null && (!args.equals(""))) {
			StringTokenizer st = new StringTokenizer(args, " ");
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
 				l.add(s);
			}
		}
		return l.toArray();
	}*/
	
	private TesterAgent loadTester() {
		try {
			TesterAgent ta = (TesterAgent) Class.forName(currentTester).newInstance();
			//ta.setArguments(getArguments());
			ta.setTestSuiteExecution(myAgent.getLocalName());
			return ta;
		}
		catch (Exception e) {
			System.out.println("Error loading tester agent. "+e);
			return null;
		}
	}
		
	public void setStatus(int status) {
		this.status = status;
		updateEnabled();
	}
	
	private void updateEnabled() {
		openB.setEnabled(status == IDLE_STATE || status == READY_STATE);
		runB.setEnabled(status == READY_STATE);
		debugB.setEnabled(status == READY_STATE);
		stepB.setEnabled(status == DEBUGGING_STATE);
		//argsF.setEnabled(status == READY_STATE);
	}
	
}
