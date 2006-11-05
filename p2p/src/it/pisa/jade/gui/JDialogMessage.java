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
package it.pisa.jade.gui;

import jade.lang.acl.ACLMessage;
import jade.tools.sl.SLFormatter;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class JDialogMessage extends JDialog {

	private JPanel jContentPane = null;
	private JTextPane messageArea = null;
	private JScrollPane messageAreajScrollPane = null;
	private JPanel buttonjPanel = null;
	private JButton okjButton = null;
	private JPanel iconjPanel = null;

	/**
	 * @throws HeadlessException
	 */
	public JDialogMessage() throws HeadlessException {
		super();
		initialize();
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public JDialogMessage(Frame owner) throws HeadlessException {
		super(owner);
		initialize();
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public JDialogMessage(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		initialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public JDialogMessage(Frame owner, String title) throws HeadlessException {
		super(owner, title);
		initialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public JDialogMessage(Frame owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		initialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public JDialogMessage(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		initialize();
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public JDialogMessage(Dialog owner) throws HeadlessException {
		super(owner);
		initialize();
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public JDialogMessage(Dialog owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		initialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public JDialogMessage(Dialog owner, String title) throws HeadlessException {
		super(owner, title);
		initialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public JDialogMessage(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		initialize();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @throws HeadlessException
	 */
	public JDialogMessage(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(517, 242);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getMessageAreajScrollPane(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getButtonjPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getIconjPanel(), java.awt.BorderLayout.WEST);
		}
		return jContentPane;
	}

	/**
	 * This method initializes messageArea	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getMessageArea() {
		if (messageArea == null) {
			messageArea = new JTextPane();
			messageArea.setAutoscrolls(true);
			messageArea.setEditable(false);
			messageArea.setFont(new java.awt.Font("Comic Sans MS", java.awt.Font.PLAIN, 12));
		}
		return messageArea;
	}

	/**
	 * This method initializes messageAreajScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getMessageAreajScrollPane() {
		if (messageAreajScrollPane == null) {
			messageAreajScrollPane = new JScrollPane();
			messageAreajScrollPane.setViewportView(getMessageArea());
		}
		return messageAreajScrollPane;
	}

	/**
	 * This method initializes buttonjPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonjPanel() {
		if (buttonjPanel == null) {
			buttonjPanel = new JPanel();
			buttonjPanel.add(getOkjButton(), null);
		}
		return buttonjPanel;
	}

	/**
	 * This method initializes okjButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkjButton() {
		if (okjButton == null) {
			okjButton = new JButton();
			okjButton.setText("Ok");
			okjButton.setFont(new java.awt.Font("Comic Sans MS", java.awt.Font.BOLD, 12));
			okjButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
					dispose();
					notifyOk();
				}
			});
		}
		return okjButton;
	}

	/**
	 * This method initializes iconjPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getIconjPanel() {
		if (iconjPanel == null) {
			iconjPanel = new JPanel();
		}
		return iconjPanel;
	}
	
	public static void showMessage(JFrame parent,String message,String title){
		JDialogMessage instance=new JDialogMessage(parent);
		instance.setTitle(title);
		instance.messageArea.setText(message);
		instance.setVisible(true);
		instance.waitOk();
	}
	public static void showMessage(JFrame parent,ACLMessage message,String title){
		JDialogMessage instance=new JDialogMessage(parent);
		instance.setTitle(title);
		instance.messageArea.setText(SLFormatter.format(message.toString()));
		instance.setVisible(true);
		instance.waitOk();
	}
	private boolean ok=false;
	private synchronized void waitOk(){
		if(!ok)
			try {
				wait();
			} catch (InterruptedException e) {
				//WrapperErrori.wrap("Wait ok action failed",e);
			}
	}
	private synchronized void notifyOk(){
		notifyAll();
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
