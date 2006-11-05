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
package it.pisa.jade.agents.peerAgent.gui;

import it.pisa.jade.agents.peerAgent.Command;
import it.pisa.jade.agents.peerAgent.data.SearchElements;
import it.pisa.jade.agents.peerAgent.ontologies.LookFor;
import it.pisa.jade.agents.peerAgent.ontologies.PeerVocabulary;
import jade.core.AID;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
/**
 * 
 * @author Fabrizio Marozzo
 * @author Domenico Trimboli
 *
 */
@SuppressWarnings("serial")
public class FramePeerAgent extends JFrame implements Observer,PeerVocabulary {

	private javax.swing.JPanel jContentPane = null;

	private JTextField jTextField = null;

	private JButton jButton = null;

	private JTabbedPane jTabbedPane = null;

	private Hashtable tablePanel = null;

	private JPopupMenu jPopupMenu = null;

	private JMenuItem jMenuItem = null;

	private Command command=null;

	private JScrollPane jScrollPane = null;

	private JTree jTree = null;

	private JPanel jPanel1 = null;

	private JSplitPane jSplitPane = null;

	private JPanel jPanelCenter = null;

	private JPanel jPanelRicerca = null;

	private JPanel jPanel = null;

	private DefaultMutableTreeNode rootMutableTreeNode = null;  //  @jve:decl-index=0:visual-constraint=""

	private HashMap<AID,DefaultMutableTreeNode> childs=new HashMap<AID,DefaultMutableTreeNode>();

	private DefaultTreeModel defaultTreeModel;

	/**
	 * This is the default constructor
	 */
	public FramePeerAgent(Command c) {
		super();
		this.command = c;
		initialize();
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.tablePanel = new Hashtable();
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				try {
					System.out.println("Sto uscendo");
					command.exitFromGroup();
					System.out.println("Sono uscito");
					dispose();
					//System.exit(0);
				} catch (Exception e2) {
					System.exit(0);
				}

			}

		});
		this.setTitle("PeerAgent");
		this.setSize(791, 553);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		this.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);

		this.setContentPane(getJContentPane());

		System.out.println("STOP");

	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJPanelCenter(), java.awt.BorderLayout.CENTER);

		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBackground(new java.awt.Color(205, 232, 242));
			jTextField.setBounds(200, 19, 305, 23);
			jTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
					int keyCode = e.getKeyCode();
					if (keyCode == KeyEvent.VK_ENTER) {
						startNewSearch();
					}
				}
			});
		}
		return jTextField;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Start a new search");
			jButton.setBounds(27, 10, 166, 39);
			jButton
					.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					startNewSearch();

				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseReleased(java.awt.event.MouseEvent e) {
					if (e.isPopupTrigger()) {
						FramePeerAgent.this.getJPopupMenu().show(
								e.getComponent(), e.getX(), e.getY());
					}

				}
			});
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jPopupMenu
	 * 
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if (jPopupMenu == null) {
			jPopupMenu = new JPopupMenu();
			jPopupMenu.add(getJMenuItem());
		}
		return jPopupMenu;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItem() {
		if (jMenuItem == null) {
			jMenuItem = new JMenuItem("Delete");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ResearchPane r = (ResearchPane) FramePeerAgent.this.jTabbedPane
							.getSelectedComponent();
					FramePeerAgent.this.tablePanel.remove(r.id);
					FramePeerAgent.this.jTabbedPane.remove(r);
					r.setVisible(false);
					command.removeSearch(r.id);
				}
			});
		}
		return jMenuItem;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new java.awt.Dimension(200, 10));
			jScrollPane.setViewportView(getJTree());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTree
	 * 
	 * @return javax.swing.JTree
	 */
	private JTree getJTree() {
		if (jTree == null) {
			jTree = new JTree(getDefaultTreeModel());
			jTree.setEditable(true);
			jTree.setShowsRootHandles(true);
			//jTree.setModel(defaultTreeModel);
			//jTree.expandRow(0);
			repaint();

		}
		return jTree;
	}
	private DefaultTreeModel getDefaultTreeModel(){
		if(defaultTreeModel==null){
			defaultTreeModel = new DefaultTreeModel(getRootMutableTreeNode());
		}
		return defaultTreeModel;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jPanel1.setBackground(new java.awt.Color(184, 207, 229));
			jPanel1.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getJScrollPane());
			jSplitPane.setRightComponent(getJPanel1());
			jSplitPane.setName("jSplitPane");
		}
		return jSplitPane;
	}

	Command getCommand() {
		return command;
	}

	/**
	 * This method initializes jPanelCenter
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelCenter() {
		if (jPanelCenter == null) {
			jPanelCenter = new JPanel();
			jPanelCenter.setLayout(new CardLayout());
			jPanelCenter.add(getJPanelRicerca(), "ricerca");
		}
		return jPanelCenter;
	}

	/**
	 * This method initializes jPanelRicerca
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRicerca() {
		if (jPanelRicerca == null) {
			jPanelRicerca = new JPanel();
			jPanelRicerca.setLayout(new BorderLayout());
			jPanelRicerca.setName("jPanelRicerca");
			jPanelRicerca.add(getJSplitPane(), java.awt.BorderLayout.CENTER);
			jPanelRicerca.add(getJPanel(), java.awt.BorderLayout.NORTH);
		}
		return jPanelRicerca;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.setPreferredSize(new java.awt.Dimension(60, 60));
			jPanel.setBackground(java.awt.Color.white);
			jPanel.add(getJTextField(), null);
			jPanel.add(getJButton(), null);
		}
		return jPanel;
	}

	private void startNewSearch() {
		if (jTextField.getText() != null && !(jTextField.getText().equals(""))) {
			LookFor lookFor = new LookFor();
			lookFor.setSearchString(jTextField.getText());
			lookFor.setSearchKey(jTextField.getText()+System.currentTimeMillis());
			jTextField.setText("");
			
			// TODO
			command.startNewSearch(lookFor);
		}
	}

	@SuppressWarnings("unchecked")
	public void update(Observable o, Object arg) {

		if (o instanceof SearchElements) {
			SearchElements table = (SearchElements) o;
			String s = (String) arg;
			LinkedList l = table.getSearchElements(s);
			if (l.isEmpty()) {
				ResearchPane p = new ResearchPane(s, this);
				jTabbedPane.addTab(s, p);
				tablePanel.put(s, p);
				return;
			}
			System.out.println(s + " l non è vuota");
			if (tablePanel.containsKey(s)) {
				ResearchPane p = (ResearchPane) tablePanel.get(s);
				p.setListData(l);
				p.repaint();
			} else {
				System.out.println("table non contiene");
			}

		}// SearchElements
		
		if(arg  instanceof HashSet){
			HashSet<AID> set=(HashSet<AID>)(arg);
			DefaultMutableTreeNode child=null;
			for(AID peerInfo: set){
				if(!childs.containsKey(peerInfo)){
					child =new DefaultMutableTreeNode(peerInfo.getName());
					childs.put(peerInfo,child);				
					getDefaultTreeModel().insertNodeInto(child,getRootMutableTreeNode(),getRootMutableTreeNode().getChildCount());
				}
			}
			for(Iterator<AID> it=childs.keySet().iterator();it.hasNext();){
				AID aid=it.next();
				if(!set.contains(aid)){
					DefaultMutableTreeNode c=childs.get(aid);
					getDefaultTreeModel().removeNodeFromParent(c);
					it.remove();
				}
			}
			if(child!=null){
				jTree.scrollPathToVisible(new TreePath(child));
			}		
		}
		

		// TODO

	}// PeerTable

	public void chooseFile(String searchKey, int pos) {
		command.chooseFile(searchKey, pos);
		

	}

	/**
	 * This method initializes rootMutableTreeNode	
	 * 	
	 * @return javax.swing.tree.DefaultMutableTreeNode	
	 */
	private DefaultMutableTreeNode getRootMutableTreeNode() {
		if (rootMutableTreeNode == null) {
			rootMutableTreeNode = new DefaultMutableTreeNode("Peers");
			
		}
		return rootMutableTreeNode;
	}

	

}
// @jve:decl-index=0:visual-constraint="10,17"
