/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is WebService Integration Gateway (WSIG).
 *
 * The Initial Developer of the Original Code is
 * Whitestein Technologies AG.
 * Portions created by the Initial Developer are Copyright (C) 2004, 2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Jozef Nagy (jna at whitestein.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.whitestein.wsig.fipa;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import javax.swing.JFileChooser;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

// import jade.core.*;
import jade.gui.GuiEvent;
import com.whitestein.wsig.struct.ServedOperation;


/**
 * @author jna
 *
 * is a GUI for gateway agent.
 */
public class GatewayAgentGui extends JFrame {

	private GatewayAgent myAgent;
	private Logger log = Logger.getLogger( GatewayAgentGui.class.getName());

	//GUI
	private JTextArea agentsLog;
	private JComboBox agentsAll; 
	private JTextArea wsLog;
	private JComboBox wsAll; 
	private JTextField statusText;
	private JCheckBoxMenuItem menuFileLogging;
	//Create a file chooser
	final JFileChooser fileChooser = new JFileChooser();

	JFrame thisGui;

	/**
	 * constructs a GUI for Gateway Agent
	 *
	 * @param ga a gateway agent
	 */
	GatewayAgentGui( GatewayAgent ga ) {
		super();
		myAgent = ga;
		setTitle("GUI of " + ga.getLocalName());
		thisGui = this;

		//
		// Construct a GUI
		//
		JLabel label;
		JPanel panel;

		JPanel main = new JPanel();
		main.setLayout( new BorderLayout());
		getContentPane().add( main, BorderLayout.CENTER );


		//
		// a menu bar
		//
		JMenuItem menuFileLogFile = new JMenuItem( "Set Log File" );
		menuFileLogFile.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				File file = null;
				int returnVal = fileChooser.showOpenDialog(
					(JMenuItem) e.getSource() );
				if ( returnVal == JFileChooser.APPROVE_OPTION ){
					file = fileChooser.getSelectedFile();
				}
				if ( null == file ) {
					return;
				}
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.SET_LOG_FILE_EVENT );
				event.addParameter( file );
				menuFileLogging.setToolTipText("Logging to: " + file.getAbsolutePath() );
				myAgent.postGuiEvent( event );
			}});
		menuFileLogging = new JCheckBoxMenuItem( "Logging" );
		menuFileLogging.setToolTipText("Logging to: (none)");
		menuFileLogging.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				JCheckBoxMenuItem item =
					(JCheckBoxMenuItem) e.getSource();
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.SET_LOGGING_EVENT );
				event.addParameter( new Boolean(
					item.isSelected() ));
				myAgent.postGuiEvent( event );
			}});
		JMenuItem menuFileReset = new JMenuItem( "Reset" );
		menuFileReset.setEnabled( false );
		menuFileReset.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				myAgent.postGuiEvent( new GuiEvent(
					(Object) thisGui,
					GatewayAgent.RESET_EVENT ));
			}});
		JMenuItem menuFileCloseGui = new JMenuItem( "Close GUI" );
		menuFileCloseGui.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				thisGui.setVisible( false ); // a quick response
				myAgent.postGuiEvent( new GuiEvent(
					(Object) thisGui,
					GatewayAgent.CLOSE_GUI_EVENT ));
			}});
		JMenuItem menuFileExit = new JMenuItem( "Exit WSIG" );
		menuFileExit.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				thisGui.setVisible( false ); // a quick response
				myAgent.postGuiEvent( new GuiEvent(
					(Object) thisGui,
					GatewayAgent.EXIT_EVENT ));
			}});
	

		JMenu menuFile = new JMenu( "File" );
		menuFile.setMnemonic( KeyEvent.VK_F );
		menuFile.add(menuFileLogFile);
		menuFile.add(menuFileLogging);
		menuFile.addSeparator();
		menuFile.add(menuFileReset);
		menuFile.addSeparator();
		menuFile.add(menuFileCloseGui);
		menuFile.add(menuFileExit);

		JCheckBoxMenuItem menuControlViewDF = new JCheckBoxMenuItem( "View DF" );
		menuControlViewDF.setEnabled( false );
		JCheckBoxMenuItem menuControlViewUDDI = new JCheckBoxMenuItem( "View UDDI" );
		menuControlViewUDDI.setEnabled( false );
		JMenuItem menuControlInitAgent = new JMenuItem( "Initiate Agent" );
		menuControlInitAgent.addActionListener(
			new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.START_AGENT_SERVER001_EVENT );
				myAgent.postGuiEvent( event );
			}});
		JMenuItem menuControlInvokeWS = new JMenuItem( "Invoke Web Service" );
		menuControlInvokeWS.addActionListener(
			new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.START_WS_CLIENT01_EVENT );
				myAgent.postGuiEvent( event );
			}});
		JMenuItem menuControlInitWS = new JMenuItem( "Initiate Web Service" );
		menuControlInitWS.addActionListener(
			new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.START_WS_SERVER01_EVENT );
				myAgent.postGuiEvent( event );
			}});
		JMenuItem menuControlInvokeAgentService = new JMenuItem( "Invoke Agent service" );
		menuControlInvokeAgentService.addActionListener(
			new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.START_AGENT_CLIENT033_EVENT );
				myAgent.postGuiEvent( event );
			}});
		JMenuItem menuCFindPlaceReg = new JMenuItem(
			"Register FindPlace Version" );
		menuCFindPlaceReg.setToolTipText( "A remote WS" );
		menuCFindPlaceReg.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.START_WS_REGISTRATION_FOR_FIND_PLACE_EVENT );
				myAgent.postGuiEvent( event );
			}});
		JMenuItem menuCGetVersion = new JMenuItem(
			"Invoke getVersion" );
		menuCGetVersion.setToolTipText( "A remote WS" );
		menuCGetVersion.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				Object[] args = new Object[2];
				args[0] = GatewayAgent.SERVICE_GET_VERSION;
				args[1] = GatewayAgent.SERVICE_EMPTY_ARGS;
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.START_AGENT_CLIENT_WITH_ARGUMENTS_EVENT );
				event.addParameter( GatewayAgent.AGENT_NAME_GET_VERSION );
				event.addParameter( args );
				myAgent.postGuiEvent( event );
			}});
		JMenuItem menuCGoogleReg = new JMenuItem(
			"Register Google Searching" );
		menuCGoogleReg.setToolTipText( "A remote WS" );
		menuCGoogleReg.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.START_WS_REGISTRATION_FOR_GOOGLE_EVENT );
				myAgent.postGuiEvent( event );
			}});
		JMenuItem menuCGoogleSearch = new JMenuItem(
			"Invoke doGoogleSearch" );
		menuCGoogleSearch.setToolTipText( "A remote WS" );
		menuCGoogleSearch.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				Object[] args = new Object[2];
				args[0] = GatewayAgent.SERVICE_GOOGLE_SEARCH;
				args[1] = GatewayAgent.SERVICE_GOOGLE_S_ARGS;
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.START_AGENT_CLIENT_WITH_ARGUMENTS_EVENT );
				event.addParameter( GatewayAgent.AGENT_NAME_GOOGLE_SEARCH );
				event.addParameter( args );
				myAgent.postGuiEvent( event );
			}});

		JMenu menuControl = new JMenu( "Control" );
		menuControl.setMnemonic( KeyEvent.VK_R );
		menuControl.add( menuControlViewDF );
		menuControl.add( menuControlViewUDDI );
		menuControl.addSeparator();
		menuControl.add( menuControlInitAgent );
		menuControl.add( menuControlInvokeWS );
		menuControl.addSeparator();
		menuControl.add( menuControlInitWS );
		menuControl.add( menuControlInvokeAgentService );
		menuControl.addSeparator();
		menuControl.add( menuCFindPlaceReg );
		menuControl.add( menuCGetVersion );
		menuControl.addSeparator();
		menuControl.add( menuCGoogleReg );
		menuControl.add( menuCGoogleSearch );

		JMenuBar menu = new JMenuBar();
		menu.add( menuFile );
		menu.add( menuControl );

		this.setJMenuBar( menu );
		//
		//

		//
		// logs for agents and web-services
		//
		label = new JLabel( "Agents' services provided:" );
		agentsAll = new JComboBox();
		agentsAll.addItem( new ComboBoxItem( "all", null ));
		agentsAll.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				JComboBox box =
					(JComboBox) e.getSource();
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.AGENT_SELECTION_EVENT );
				event.addParameter( box.getSelectedObjects() );
				myAgent.postGuiEvent( event );
			}});
		agentsLog = new JTextArea();
		agentsLog.setEditable( false );
		JScrollPane agentsScroll = new JScrollPane( agentsLog );

		panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.PAGE_AXIS));
		panel.add( label );
		panel.add( agentsAll );

		JPanel agentPanel = new JPanel();
		agentPanel.setLayout( new BorderLayout());
		agentPanel.add( panel, BorderLayout.NORTH );
		agentPanel.add( agentsScroll, BorderLayout.CENTER );


		label = new JLabel( "Web Services' operations provided:" );
		wsAll = new JComboBox();
		wsAll.addItem( new ComboBoxItem( "all", null ));
		wsAll.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				JComboBox box =
					(JComboBox) e.getSource();
				GuiEvent event = new GuiEvent(
					(Object) thisGui,
					GatewayAgent.WS_SELECTION_EVENT );
				event.addParameter( box.getSelectedObjects() );
				myAgent.postGuiEvent( event );
			}});
		wsLog = new JTextArea();
		wsLog.setEditable( false );
		JScrollPane wsScroll = new JScrollPane( wsLog );

		panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.PAGE_AXIS));
		panel.add( label );
		panel.add( wsAll );

		JPanel wsPanel = new JPanel();
		wsPanel.setLayout( new BorderLayout() );
		wsPanel.add( panel, BorderLayout.NORTH );
		wsPanel.add( wsScroll, BorderLayout.CENTER );


		JSplitPane split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, agentPanel, wsPanel );
		split.setResizeWeight(0.5);
		split.setOneTouchExpandable( true );

		main.add( split, BorderLayout.CENTER );
		//
		//


		//
		// a status panel
		//
		label = new JLabel("Status:");
		statusText = new JTextField();
		statusText.setEditable( false );

		JPanel statusPanel = new JPanel();
		statusPanel.setLayout( new BorderLayout() );
		statusPanel.add( label, BorderLayout.WEST );
		statusPanel.add( statusText, BorderLayout.CENTER );

		main.add( statusPanel, BorderLayout.NORTH );
		//
		//

	}

	/**
	 * wraps a string for JComboBox.
	 * A served operation data structure is stored too.
	 *
	 * @param item a string
	 * @param so an operation
	 */
	//private Object makeObject( String item, ServedOperation so )  {
	//	return new ComboBoxItem( item, so );
	//}

	/**
	 * shows a GUI at the first time.
	 *  This method is threadsafe, although most Swing methods are not.
	 */
	public void showMeTheFirstTime() {
		SwingUtilities.invokeLater( new Runnable () {
			public void run() {
				setSize(500,400);

				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				int centerX = (int)screenSize.getWidth() / 2;
				int centerY = (int)screenSize.getHeight() / 2;
				setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);

				setVisible( true );
		}});
	}

	/**
	 * shows a GUI.
	 *  This method is threadsafe, although most Swing methods are not.
	 */
	public void showMe() {
		SwingUtilities.invokeLater( new Runnable () {
			public void run() {
				setVisible( true );
		}});
	}

	/**
	 * disposes a GUI.
	 *  This method is threadsafe, although most Swing methods are not.
	 */
	public void exit() {
		SwingUtilities.invokeLater( new Runnable () {
			public void run() {
				dispose();
		}});
	}

	/**
	 * appends a text to agents' log.
	 *  This method is threadsafe, although most Swing methods are not.
	 * @param text a text
	 */
	public void addAgentLog( String text ) {
		agentsLog.append( text );
		agentsLog.append( "\n-------------------------------\n" );
	}

	/**
	 * appends a text to web services' log.
	 *  This method is threadsafe, although most Swing methods are not.
	 * @param text a text
	 */
	public void addWsLog( String text ) {
		wsLog.append( text );
		wsLog.append( "\n-------------------------------\n" );
	}

	/**
	 * adds an operation to a WS list
	 * @param so an operation to be added
	 */
	public void addOperationToWsLog( final ComboBoxItem item ) {
		SwingUtilities.invokeLater( new Runnable () {
			public void run() {
				wsAll.addItem( item );
		}});
	}

	/**
	 * adds an operation to an agents' list
	 * @param so an operation to be added
	 */
	public void addOperationToAgentsLog( final ComboBoxItem item ) {
		SwingUtilities.invokeLater( new Runnable () {
			public void run() {
				agentsAll.addItem( item );
		}});
	}

	/**
	 * deletes an operation from a WS list
	 * @param so an operation to be deleted
	 */
	public void removeOperationFromWsLog( final ServedOperation so ) {
		SwingUtilities.invokeLater( new Runnable () {
			public void run() {
				ComboBoxItem item;
				// count down for a good removing
				for ( int k = wsAll.getItemCount() - 1 ; k > 0; k -- ) {
					item = (ComboBoxItem) wsAll.getItemAt( k );
					if ( item.getServedOperation() == so ){
						wsAll.removeItemAt( k );
					}
				}
		}});
	}

	/**
	 * deletes an operation from an agents' list
	 * @param so an operation to be deleted
	 */
	public void removeOperationFromAgentsLog( final ServedOperation so ) {
		SwingUtilities.invokeLater( new Runnable () {
			public void run() {
				ComboBoxItem item;
				// count down for a good removing
				for ( int k = agentsAll.getItemCount() - 1 ; k > 0; k -- ) {
					item = (ComboBoxItem) agentsAll.getItemAt( k );
					if ( item.getServedOperation() == so ){
						agentsAll.removeItemAt( k );
					}
				}
		}});
	}

}
