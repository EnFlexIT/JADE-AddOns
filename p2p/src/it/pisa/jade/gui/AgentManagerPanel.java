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

import it.pisa.jade.agents.guiAgent.GuiAgent;
import it.pisa.jade.agents.guiAgent.action.KillAction;
import it.pisa.jade.agents.guiAgent.behaviours.ConstantBehaviourAction;
import it.pisa.jade.util.AgentName;
import it.pisa.jade.util.FactoryAgent;
import it.pisa.jade.util.JadeObserver;
import it.pisa.jade.util.WrapperErrori;
import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
/**
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class AgentManagerPanel extends JPanel implements JadeObserver{

	
	
	private JPanel actionPanel = null;
	private JButton createAgentJButton = null;
	private JList agentJList = null;
	private JPanel AgentListjPanel = null;
	private JPanel panelStatusBarAgentManager = null;
	private ListModel<AgentName> agentListModel = null;  //  @jve:decl-index=0:visual-constraint=""
	private JLabel statusBar = null;
	private String tempString="";
	private GuiAgent guiAgent=null;

	private JButton jButton = null;
	public AgentManagerPanel(GuiAgent guiAgent) {
		super();
		this.guiAgent=guiAgent;
		initialize();
	}
	/**
	 * This is the default constructor
	 */
	public AgentManagerPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(513, 334);
		this.setFont(new java.awt.Font("Comic Sans MS", java.awt.Font.PLAIN, 12));
		this.setName("AgentManager");
		this.add(getActionPanel(), java.awt.BorderLayout.EAST);
		this.add(getAgentListjPanel(), java.awt.BorderLayout.CENTER);
		this.add(getPanelStatusBarAgentManager(), java.awt.BorderLayout.SOUTH);
	}

	/**
	 * This method initializes actionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getActionPanel() {
		if (actionPanel == null) {
			GridLayout gridLayout5 = new GridLayout();
			actionPanel = new JPanel();
			actionPanel.setLayout(gridLayout5);
			actionPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Operation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Comic Sans MS", java.awt.Font.BOLD, 14), new java.awt.Color(51,51,51)), javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.activeCaption,1)));
			gridLayout5.setRows(5);
			gridLayout5.setColumns(1);
			gridLayout5.setHgap(5);
			gridLayout5.setVgap(5);
			actionPanel.add(getCreateAgentJButton(), null);
			actionPanel.add(getJButton(), null);
		}
		return actionPanel;
	}

	/**
	 * This method initializes createAgentJButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCreateAgentJButton() {
		if (createAgentJButton == null) {
			createAgentJButton = new JButton();
			createAgentJButton.setText("Create agent");
			createAgentJButton.setFont(new java.awt.Font("Comic Sans MS", java.awt.Font.PLAIN, 12));
			createAgentJButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(guiAgent==null)return;
					int indexSelected=agentJList.getSelectedIndex();
					if(indexSelected!=-1){
						AgentName name= agentListModel.get(indexSelected);
						try {
							statusBar.setText("Creating agent");
							AgentContainer agentContainer=guiAgent.getContainerController();
							AgentController agent=agentContainer.createNewAgent(name.name(),FactoryAgent.getAgent(name).getName(),new Object[0]);
							agent.start();
							statusBar.setText("agent created");
						} catch (StaleProxyException e1) {
							WrapperErrori.wrap("",e1);
							statusBar.setText("Creating failure");
						}
					}
				}
			});
		}
		return createAgentJButton;
	}

	/**
	 * This method initializes agentJList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getAgentJList() {
		if (agentJList == null) {
			agentJList = new JList();
			agentJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			agentJList.setModel(getAgentListModel());
			agentJList.addMouseListener(new java.awt.event.MouseAdapter() {   
				public void mouseExited(java.awt.event.MouseEvent e) {    
					statusBar.setText(tempString);
				}
				public void mouseEntered(java.awt.event.MouseEvent e) {
					tempString=statusBar.getText();
				}
			});
			agentJList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
				public void mouseMoved(java.awt.event.MouseEvent e) {
					int indexSelected=agentJList.locationToIndex(e.getPoint());
					if(indexSelected!=-1){
						statusBar.setText(agentListModel.get(indexSelected).getDescription());
					}else{
						statusBar.setText("Staus bar");
					}
				}
			});
		}
		return agentJList;
	}

	/**
	 * This method initializes AgentListjPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAgentListjPanel() {
		if (AgentListjPanel == null) {
			AgentListjPanel = new JPanel();
			AgentListjPanel.setLayout(new BorderLayout());
			AgentListjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Agent list", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Comic Sans MS", java.awt.Font.BOLD, 14), new java.awt.Color(51,51,51)));
			AgentListjPanel.setFont(new java.awt.Font("Comic Sans MS", java.awt.Font.BOLD, 14));
			AgentListjPanel.add(getAgentJList(), java.awt.BorderLayout.CENTER);
		}
		return AgentListjPanel;
	}

	/**
	 * This method initializes statusBarAgentManager	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPanelStatusBarAgentManager() {
		if (panelStatusBarAgentManager == null) {
			statusBar = new JLabel();
			statusBar.setText("statusBar");
			statusBar.setFont(new java.awt.Font("Comic Sans MS", java.awt.Font.PLAIN, 12));
			panelStatusBarAgentManager = new JPanel();
			panelStatusBarAgentManager.setLayout(new BorderLayout());
			panelStatusBarAgentManager.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.desktop,1));
			panelStatusBarAgentManager.add(statusBar, java.awt.BorderLayout.CENTER);
		}
		return panelStatusBarAgentManager;
	}

	/**
	 * This method initializes agentListModel	
	 * 	
	 * @return it.pisa.jade.gui.ListModel	
	 */
	private ListModel getAgentListModel() {
		if (agentListModel == null) {
			agentListModel = new ListModel<AgentName>();
			AgentName[]agents= AgentName.values();
			for(int i=0;i<agents.length;i++){
				agentListModel.add(agents[i]);
			}
		}
		return agentListModel;
	}
	public GuiAgent getGuiAgent() {
		return guiAgent;
	}
	public void setGuiAgent(GuiAgent guiAgent) {
		this.guiAgent = guiAgent;
		guiAgent.addObserver(this);
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setFont(new java.awt.Font("Comic Sans MS", java.awt.Font.PLAIN, 12));
			jButton.setText("kill agent");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(guiAgent==null)return;
					int indexSelected=agentJList.getSelectedIndex();
					if(indexSelected!=-1){
						guiAgent.doAction(ConstantBehaviourAction.KILLAGENT,new AID(agentListModel.get(indexSelected).name(),AID.ISLOCALNAME));
					}
				}
			});
		}
		return jButton;
	}
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if(arg instanceof KillAction){
			if(((KillAction)arg).isOK())
				statusBar.setText("Kill ok");
			else{
				statusBar.setText("Killing error");
			}
		}
		
	}
 }  //  @jve:decl-index=0:visual-constraint="10,10"
