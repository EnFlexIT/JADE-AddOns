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
package it.pisa.jade.agents.chatAgent;

import it.pisa.jade.gui.ListModel;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiEvent;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 * Gui for the chat agent
 * 
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 * 
 */
@SuppressWarnings("serial")
public class GuiAgentChat extends JFrame {
	
	@SuppressWarnings("serial")
	class PeerItem extends JComponent {
		private AID peer;

		public PeerItem(AID peer) {
			super();
			this.peer = peer;
		}

		@Override
		public boolean equals(Object obj) {
			PeerItem p = (PeerItem) obj;
			return peer.getName().equals(p.peer.getName());
		}

		public AID getPeerAID() {
			return peer;
		}

		public String toString() {
			return peer.getName();
		}
	}

	protected PeerItem currentPeer;

	private JTextArea editorCorrente;

	private JButton invia = null;

	private JPanel jContentPane = null;

	private JList jListUtentiConnessi = null;

	private JMenuItem jMenuItemClose;

	private JPanel jPanelListaPeerConnessi = null;

	private JPanel jPanelWriteChat = null;

	private JPopupMenu jPopupMenu;

	private JProgressBar jProgressBar = null;

	private JTextField jTextFieldWrite = null;

	private ListModel<PeerItem> listModel=new ListModel<PeerItem>();

	private JTabbedPane mainChat = null;

	private ChatAgent myAgent;

	private JPanel pannelloCorrente;

	private JButton scanPeer = null;
	private boolean selectItem = false;
	private JPanel statusBarjPanel = null;

	/**
	 * This is the default constructor
	 */
	public GuiAgentChat(ChatAgent agent) {
		super();
		myAgent = agent;
		initialize();
	}

	public void arrivedMessage(AID sender, String content) {
		int index = -1;
		for (int i = 0; i < mainChat.getTabCount(); i++) {
			String title = mainChat.getTitleAt(i);
			if (title.equals(sender.getName())) {
				index = i;
			}
		}
		if (index != -1) {
			pannelloCorrente = (JPanel) mainChat.getComponentAt(index);
			JScrollPane scroll = (JScrollPane) pannelloCorrente.getComponent(0);
			editorCorrente = (JTextArea) scroll.getViewport().getComponent(0);
			editorCorrente.append(sender.getName() + ">" + content + "\n");
			//mainChat.setSelectedComponent(scroll);
		} else {
			currentPeer = new PeerItem(sender);
			if (!listModel.contains(currentPeer)) {
				listModel.add(currentPeer);
			}
			final JPanel pannelloCorrente1 = new JPanel(new BorderLayout());
			pannelloCorrente1.setName(currentPeer.getPeerAID().getName());
			final JTextArea editorCorrente1 = new JTextArea();
			JScrollPane scroll = new JScrollPane(editorCorrente1);
			pannelloCorrente1.add(scroll, BorderLayout.CENTER);
			pannelloCorrente = pannelloCorrente1;
			editorCorrente = editorCorrente1;
			mainChat.addTab(currentPeer.getPeerAID().getName(),
					pannelloCorrente1);
			editorCorrente.append(sender.getName() + ">" + content + "\n");
			//mainChat.setSelectedComponent(scroll);
		}

	}

	public void errorMessage(AID sender, String content) {
		JOptionPane.showMessageDialog(this, sender.getName() + ">" + content);
	}

	/**
	 * This method initializes invia
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getInvia() {
		if (invia == null) {
			invia = new JButton();
			invia.setText("invia");
			invia.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (editorCorrente != null
							&& jTextFieldWrite.getText() != null
							&& !jTextFieldWrite.getText().equals("")) {
						String msg = jTextFieldWrite.getText();
						jTextFieldWrite.setText("");
						editorCorrente.append("my>" + msg + "\n");
						GuiEvent event = new GuiEvent(this, ChatAgent.SEND);
						event.addParameter(msg);
						event.addParameter(currentPeer);
						myAgent.postGuiEvent(event);
					}
				}
			});
		}
		return invia;
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
			jContentPane.add(getJPanelListaPeerConnessi(),
					java.awt.BorderLayout.EAST);
			jContentPane.add(getJPanelWriteChat(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getMainChat(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jListUtentiConnessi1
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJListUtentiConnessi() {
		if (jListUtentiConnessi == null) {
			jListUtentiConnessi = new JList(listModel);
			jListUtentiConnessi
					.addMouseListener(new java.awt.event.MouseAdapter() {

						public void mouseClicked(java.awt.event.MouseEvent e) {
							if (e.getClickCount() == 2) {
								int index = jListUtentiConnessi
										.locationToIndex(e.getPoint());
								if (index != -1) {
									selectItem = true;
									boolean trovato = false;
									currentPeer = (PeerItem) jListUtentiConnessi
											.getModel().getElementAt(index);
									for (int i = 0; i < mainChat.getTabCount(); i++) {
										String t = mainChat.getTitleAt(i);
										if (t.equals(currentPeer.getPeerAID()
												.getName())) {
											trovato = true;
										}
									}

									if (!trovato) {
										final JPanel pannelloCorrente1 = new JPanel(
												new BorderLayout());
										pannelloCorrente1.setName(currentPeer
												.getPeerAID().getName());
										final JTextArea editorCorrente1 = new JTextArea();
										JScrollPane scroll = new JScrollPane(
												editorCorrente1);
										pannelloCorrente1.add(scroll,
												BorderLayout.CENTER);
										pannelloCorrente = pannelloCorrente1;
										editorCorrente = editorCorrente1;
										mainChat.addTab(currentPeer
												.getPeerAID().getName(),
												pannelloCorrente1);
									}
									selectItem = false;
								}
							}
						}
					});
		}
		return jListUtentiConnessi;
	}

	private JMenuItem getJMenuItemClose() {
		if (jMenuItemClose == null) {
			jMenuItemClose = new JMenuItem("close");
			jMenuItemClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currentPeer = null;
					pannelloCorrente = null;
					editorCorrente = null;
					JPanel r = (JPanel) mainChat.getSelectedComponent();
					mainChat.remove(r);
					r.setVisible(false);
				}
			});
		}
		return jMenuItemClose;
	}

	/**
	 * This method initializes JPanelListaPeerConnessi
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelListaPeerConnessi() {
		if (jPanelListaPeerConnessi == null) {
			jPanelListaPeerConnessi = new JPanel(new BorderLayout());
			jPanelListaPeerConnessi.add(getJListUtentiConnessi(),
					BorderLayout.CENTER);
			jPanelListaPeerConnessi.add(getScanPeer(), BorderLayout.SOUTH);
		}
		return jPanelListaPeerConnessi;
	}

	/**
	 * This method initializes jPanelWriteChat
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelWriteChat() {
		if (jPanelWriteChat == null) {
			jPanelWriteChat = new JPanel(new BorderLayout());
			jPanelWriteChat.add(getJTextFieldWrite(),
					java.awt.BorderLayout.CENTER);
			jPanelWriteChat.add(getInvia(), java.awt.BorderLayout.EAST);

			jPanelWriteChat.add(getStatusBarjPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jPanelWriteChat;
	}

	private JPopupMenu getJPopupMenu() {
		if (jPopupMenu == null) {
			jPopupMenu = new JPopupMenu("menu");
			jPopupMenu.add(getJMenuItemClose());
		}
		return jPopupMenu;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */    
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jTextFieldWrite
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldWrite() {
		if (jTextFieldWrite == null) {
			jTextFieldWrite = new JTextField();
			jTextFieldWrite.setText("");
			jTextFieldWrite
					.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			jTextFieldWrite.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						invia.doClick();
					}
				}
			});
		}
		return jTextFieldWrite;
	}

	/**
	 * This method initializes mainChat
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getMainChat() {
		if (mainChat == null) {
			mainChat = new JTabbedPane();
			mainChat.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if (!selectItem && !listModel.isEmpty()
							&& currentPeer != null) {
						pannelloCorrente = (JPanel) mainChat
								.getSelectedComponent();
						JScrollPane scroll = (JScrollPane) pannelloCorrente
								.getComponent(0);
						editorCorrente = (JTextArea) scroll.getViewport()
								.getComponent(0);
						AID p = new AID(pannelloCorrente.getName(), false);
						if (listModel.indexOf(new PeerItem(p)) != -1)
							currentPeer = listModel.get(listModel
									.indexOf(new PeerItem(p)));
					}
				}
			});
			mainChat.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger())
						getJPopupMenu().show(e.getComponent(), e.getX(),
								e.getY());
				}

			});
		}
		return mainChat;
	}

	/**
	 * This method initializes scanPeer
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getScanPeer() {
		if (scanPeer == null) {
			scanPeer = new JButton();
			scanPeer.setText("scanPeer");
			scanPeer.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent e) {
					scanPeer.setEnabled(false);
					DFAgentDescription dfad = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType(ConstantChat.typeService.getValue());
					dfad.addServices(sd);
					jProgressBar.setIndeterminate(true);
					myAgent.addBehaviour(new SearchPeerChatBehaviour(myAgent,
							GuiAgentChat.this));
				}
			});
		}
		return scanPeer;
	}

	/**
	 * This method initializes statusBarjPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getStatusBarjPanel() {
		if (statusBarjPanel == null) {
			statusBarjPanel = new JPanel();
			statusBarjPanel.setLayout(new BorderLayout());
			statusBarjPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.activeCaption,1));
			statusBarjPanel.add(getJProgressBar(), java.awt.BorderLayout.NORTH);
		}
		return statusBarjPanel;
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(593, 229);
		this.setContentPane(getJContentPane());
		this.setTitle("Chat Agent : " + myAgent.getName());
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				dispose();
				myAgent.doDelete();
			}
		});
	}
	public void setPeers(DFAgentDescription[] peers) {
		listModel.clear();
		for (int i = 0; i < peers.length; i++) {
			DFAgentDescription peer = peers[i];
			PeerItem peerItem = new PeerItem(peer.getName());
			listModel.add(peerItem);
		}
		jProgressBar.setIndeterminate(false);
		jProgressBar.setValue(0);
		scanPeer.setEnabled(true);
	}
  } // @jve:decl-index=0:visual-constraint="10,10"
