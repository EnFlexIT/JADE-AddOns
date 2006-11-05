/**
 * 
 */
package it.pisa.jade.gui;

import it.pisa.jade.agents.guiAgent.GuiAgent;
import it.pisa.jade.agents.guiAgent.action.AddAction;
import it.pisa.jade.agents.guiAgent.action.DefederationAction;
import it.pisa.jade.agents.guiAgent.action.DesubscribeAction;
import it.pisa.jade.agents.guiAgent.action.FederationAction;
import it.pisa.jade.agents.guiAgent.action.LoadPlatformAction;
import it.pisa.jade.agents.guiAgent.action.RemoveAction;
import it.pisa.jade.agents.guiAgent.action.SubscribeAction;
import it.pisa.jade.agents.guiAgent.behaviours.ConstantBehaviourAction;
import it.pisa.jade.data.activePlatform.ActivePlatform;
import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.FactoryUtil;
import it.pisa.jade.util.JadeObserver;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;

/**
 * Jpanel for the active platform and faderate the DF
 * @author Domenico Trimboli
 * 
 */
@SuppressWarnings("serial") 
public class InterPlatformManagerPanel extends
		JPanel implements JadeObserver {

	private enum AutomaticState {
		AUTOREFRESHOFF, AUTOREFRESHON;
	}

	private JPanel activePlatformPanel = null;

	private JPanel activityPlatformControlJPanel = null;

	private JList activityPlatformJList = null;

	private ListModel<RecordPlatform> activityPlatformJlistModel = null; // @jve:decl-index=0:visual-constraint=""

	private JButton addjButton = null;

	private JToggleButton autoRefreshJToggleButton = null;

	private JSplitPane centerJSplitPane = null;

	private JButton defederateJButton = null;

	private JButton federatejButton = null;

	private JSplitPane federatejSplitPane = null;

	private JPanel formButtonjPanel = null;

	private JPanel formjPanel = null;

	private GuiAgent guiAgent;

	private boolean inProgress = false;

	private JScrollPane jScrollPane = null;

	private JPanel knownPlatformPanel = null;

	private JPanel knowPlatformControljPanel = null;

	private JScrollPane knowPlatformJScrollPane = null;

	private JTable knowPlatformJTable = null;

	private DefaultTableModel knowPlatformTableModel = null; // @jve:decl-index=0:visual-constraint=""

	private JScrollPane listActivityPlatformJScrollPane = null;

	private JLabel platformAddressjLabel = null;

	private JTextField platformAddressjTextField = null;

	private JLabel platformNamejLabel = null;

	private JTextField platformNamejTextField = null;

	private JButton refreshJButton = null;

	private JButton removejButton = null;

	private AutomaticState stateAutoRefresh = AutomaticState.AUTOREFRESHOFF;

	private JLabel statusBar = null;

	private JPanel statusBarJPanel = null;

	/**
	 * This is the default constructor
	 */
	public InterPlatformManagerPanel() {
		super();
		initialize();
	}

	public InterPlatformManagerPanel(GuiAgent guiAgent) {
		super();
		setGuiAgent(guiAgent);
		initialize();
	}

	/**
	 * @param record
	 */
	private void addToKnowPlatform(RecordPlatform record, String state) {
		boolean found = false;
		Vector table = knowPlatformTableModel.getDataVector();
		String platformName = record.getPlatformName();
		int i;
		for (i = 0; i < table.size() && !found; i++)
			if (((Vector) table.elementAt(i)).elementAt(0).equals(platformName)
					&& ((Vector) table.elementAt(i)).elementAt(0).equals(
							record.getPlatformAddress())) {
				found = true;
			}
		i--;
		if (!found) {
			knowPlatformTableModel.addRow(new String[] {
					record.getPlatformName(), record.getPlatformAddress(),
					state });
		} else {
			knowPlatformJTable.setRowSelectionInterval(i, i);
		}
	}

	/**
	 * This method initializes activePlatformPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getActivePlatformPanel() {
		if (activePlatformPanel == null) {
			activePlatformPanel = new JPanel();
			activePlatformPanel.setLayout(new BorderLayout());
			activePlatformPanel.setPreferredSize(new java.awt.Dimension(300,
					150));
			activePlatformPanel.add(getListActivityPlatformJScrollPane(),
					java.awt.BorderLayout.CENTER);
			activePlatformPanel.add(getActivityPlatformControlJPanel(),
					java.awt.BorderLayout.SOUTH);
		}
		return activePlatformPanel;
	}

	/**
	 * This method initializes activityPlatformControlJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getActivityPlatformControlJPanel() {
		if (activityPlatformControlJPanel == null) {
			activityPlatformControlJPanel = new JPanel();
			activityPlatformControlJPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"activity platformControl",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									new java.awt.Font("Comic Sans MS",
											java.awt.Font.PLAIN, 10), null));
			activityPlatformControlJPanel.add(getRefreshJButton(), null);
			activityPlatformControlJPanel.add(getAutoRefreshJToggleButton(),
					null);
		}
		return activityPlatformControlJPanel;
	}

	/**
	 * This method initializes activityPlatformJList
	 * 
	 * @return javax.swing.JList
	 */
	private JList getActivityPlatformJList() {
		if (activityPlatformJList == null) {
			activityPlatformJList = new JList();
			activityPlatformJList
					.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			activityPlatformJList.setModel(getActivityPlatformJlistModel());
		}
		return activityPlatformJList;
	}

	/**
	 * This method initializes activityPlatformJlistModel
	 * 
	 * @return it.pisa.jade.gui.ListModel
	 */
	private ListModel getActivityPlatformJlistModel() {
		if (activityPlatformJlistModel == null) {
			activityPlatformJlistModel = new ListModel<RecordPlatform>();
		}
		return activityPlatformJlistModel;
	}

	/**
	 * This method initializes addjButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddjButton() {
		if (addjButton == null) {
			addjButton = new JButton();
			addjButton.setText("Add");
			addjButton.setFont(new java.awt.Font("Comic Sans MS",
					java.awt.Font.BOLD, 10));
			addjButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(guiAgent==null)return;
					String platformName = platformNamejTextField.getText();
					String platformAddress = platformAddressjTextField
							.getText();
					if (platformName != null && platformName != null) {
						boolean ris = FactoryUtil.checkName(platformName);
						if (ris) {
							ris = FactoryUtil.checkAddress(platformAddress);
							if (ris) {
								RecordPlatform record = new RecordPlatform(
										platformName, platformAddress);
								addToKnowPlatform(record, "not federate");
							} else {
								JOptionPane.showMessageDialog(
										InterPlatformManagerPanel.this,
										"error to Platform address ", "error",
										JOptionPane.ERROR_MESSAGE);
							}
						} else {
							JOptionPane.showMessageDialog(
									InterPlatformManagerPanel.this,
									"error to Platform name", "error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
		}
		return addjButton;
	}

	/**
	 * This method initializes autoRefreshJToggleButton
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getAutoRefreshJToggleButton() {
		if (autoRefreshJToggleButton == null) {
			autoRefreshJToggleButton = new JToggleButton();
			autoRefreshJToggleButton.setText("Auto refresh");
			autoRefreshJToggleButton.setFont(new java.awt.Font("Comic Sans MS",
					java.awt.Font.BOLD, 10));
			/*
			 * autoRefreshJToggleButton .addChangeListener(new
			 * javax.swing.event.ChangeListener() { public void
			 * stateChanged(javax.swing.event.ChangeEvent e) { } });
			 */
			autoRefreshJToggleButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if(guiAgent==null)return;
							if (!inProgress) {
								inProgress = true;
								if (stateAutoRefresh
										.equals(AutomaticState.AUTOREFRESHOFF)) {
									guiAgent
											.doAction(ConstantBehaviourAction.SUBSCRIBE);
								} else {
									// guiAgent.stopAction();
									guiAgent
											.doAction(ConstantBehaviourAction.DESUBSCRIBE);
								}
							}
						}
					});
		}
		return autoRefreshJToggleButton;
	}

	/**
	 * This method initializes centerJSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getCenterJSplitPane() {
		if (centerJSplitPane == null) {
			centerJSplitPane = new JSplitPane();
			centerJSplitPane.setContinuousLayout(true);
			centerJSplitPane.setOneTouchExpandable(true);
			centerJSplitPane.setDividerLocation(250);
			centerJSplitPane.setLeftComponent(getActivePlatformPanel());
			centerJSplitPane.setRightComponent(getKnownPlatformPanel());
		}
		return centerJSplitPane;
	}

	/**
	 * This method initializes defederateJButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDefederateJButton() {
		if (defederateJButton == null) {
			defederateJButton = new JButton();
			defederateJButton.setText("Defederate");
			defederateJButton.setFont(new java.awt.Font("Comic Sans MS",
					java.awt.Font.BOLD, 10));
			defederateJButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if(guiAgent==null)return;
							int index = knowPlatformJTable.getSelectedRow();
							if (index != -1) {
								Vector row = (Vector) knowPlatformTableModel
										.getDataVector().elementAt(index);
								String platformName = (String) row.elementAt(0);
								String platformAddress = (String) row
										.elementAt(1);
								RecordPlatform record = new RecordPlatform(
										platformName, platformAddress);
								guiAgent.doAction(
										ConstantBehaviourAction.DEFEDERATE,
										record);
							}
						}
					});
		}
		return defederateJButton;
	}

	/**
	 * This method initializes federatejButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFederatejButton() {
		if (federatejButton == null) {
			federatejButton = new JButton();
			federatejButton.setText("Federate");
			federatejButton.setFont(new java.awt.Font("Comic Sans MS",
					java.awt.Font.BOLD, 10));
			federatejButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if(guiAgent==null)return;
							RecordPlatform record = getRecordSelect();
							if (record != null) {
								guiAgent.doAction(
										ConstantBehaviourAction.FEDERATE,
										record);
							} else {
								statusBar.setText("Select row first!");
							}
						}
					});
		}
		return federatejButton;
	}

	/**
	 * This method initializes federatejSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getFederatejSplitPane() {
		if (federatejSplitPane == null) {
			federatejSplitPane = new JSplitPane();
			federatejSplitPane
					.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
			federatejSplitPane.setDividerLocation(150);
			federatejSplitPane.setOneTouchExpandable(true);
			federatejSplitPane.setBottomComponent(getJScrollPane());
			federatejSplitPane.setTopComponent(getKnowPlatformJScrollPane());
		}
		return federatejSplitPane;
	}

	/**
	 * This method initializes formButtonjPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFormButtonjPanel() {
		if (formButtonjPanel == null) {
			formButtonjPanel = new JPanel();
			formButtonjPanel.add(getAddjButton(), null);
			formButtonjPanel.add(getRemovejButton(), null);
		}
		return formButtonjPanel;
	}

	/**
	 * This method initializes formjPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFormjPanel() {
		if (formjPanel == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 3;
			gridBagConstraints12.gridy = 4;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 3;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridx = 3;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 3;
			platformAddressjLabel = new JLabel();
			platformAddressjLabel.setText("Platform Address");
			platformAddressjLabel.setFont(new java.awt.Font("Comic Sans MS",
					java.awt.Font.BOLD, 12));
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.gridx = 2;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.weightx = 1.0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridx = 1;
			platformNamejLabel = new JLabel();
			platformNamejLabel.setText("Platform name");
			platformNamejLabel.setFont(new java.awt.Font("Comic Sans MS",
					java.awt.Font.BOLD, 12));
			formjPanel = new JPanel();
			formjPanel.setLayout(new GridBagLayout());
			formjPanel.add(platformNamejLabel, gridBagConstraints);
			formjPanel.add(getPlatformNamejTextField(), gridBagConstraints1);
			formjPanel.add(platformAddressjLabel, gridBagConstraints11);
			formjPanel.add(getPlatformAddressjTextField(), gridBagConstraints2);
			formjPanel.add(getFormButtonjPanel(), gridBagConstraints12);
		}
		return formjPanel;
	}

	public GuiAgent getGuiAgent() {
		return guiAgent;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane
					.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane.setViewportView(getFormjPanel());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes knownPlatformPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getKnownPlatformPanel() {
		if (knownPlatformPanel == null) {
			knownPlatformPanel = new JPanel();
			knownPlatformPanel.setLayout(new BorderLayout());
			knownPlatformPanel.add(getKnowPlatformControljPanel(),
					java.awt.BorderLayout.SOUTH);
			knownPlatformPanel.add(getFederatejSplitPane(),
					java.awt.BorderLayout.CENTER);
		}
		return knownPlatformPanel;
	}

	/**
	 * This method initializes knowPlatformControljPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getKnowPlatformControljPanel() {
		if (knowPlatformControljPanel == null) {
			knowPlatformControljPanel = new JPanel();
			knowPlatformControljPanel.setBorder(javax.swing.BorderFactory
					.createTitledBorder(null, "Know platform Control",
							javax.swing.border.TitledBorder.LEFT,
							javax.swing.border.TitledBorder.DEFAULT_POSITION,
							new java.awt.Font("Comic Sans MS",
									java.awt.Font.PLAIN, 10),
							new java.awt.Color(51, 51, 51)));
			knowPlatformControljPanel.add(getFederatejButton(), null);
			knowPlatformControljPanel.add(getDefederateJButton(), null);
		}
		return knowPlatformControljPanel;
	}

	/**
	 * This method initializes knowPlatformJScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getKnowPlatformJScrollPane() {
		if (knowPlatformJScrollPane == null) {
			knowPlatformJScrollPane = new JScrollPane();
			knowPlatformJScrollPane.setViewportView(getKnowPlatformJTable());
		}
		return knowPlatformJScrollPane;
	}

	/**
	 * This method initializes kownPlatformJTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getKnowPlatformJTable() {
		if (knowPlatformJTable == null) {
			knowPlatformJTable = new JTable();
			knowPlatformJTable.setModel(getKnowPlatformTableModel());
			knowPlatformJTable
					.addMouseListener(new java.awt.event.MouseAdapter() {
						public void mouseClicked(java.awt.event.MouseEvent e) {
							RecordPlatform record = getRecordSelect();
							platformAddressjTextField.setText(record
									.getPlatformAddress());
							platformNamejTextField.setText(record
									.getPlatformName());
						}
					});
		}
		return knowPlatformJTable;
	}

	/**
	 * This method initializes knowPlatformTableModel
	 * 
	 * @return javax.swing.table.DefaultTableModel
	 */
	private DefaultTableModel getKnowPlatformTableModel() {
		if (knowPlatformTableModel == null) {
			knowPlatformTableModel = new DefaultTableModel();
			knowPlatformTableModel.addColumn("Name");
			knowPlatformTableModel.addColumn("Address");
			knowPlatformTableModel.addColumn("state");
		}
		return knowPlatformTableModel;
	}

	/**
	 * This method initializes listActivityPlatformJScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getListActivityPlatformJScrollPane() {
		if (listActivityPlatformJScrollPane == null) {
			listActivityPlatformJScrollPane = new JScrollPane();
			listActivityPlatformJScrollPane
					.setViewportView(getActivityPlatformJList());
		}
		return listActivityPlatformJScrollPane;
	}

	/**
	 * This method initializes platformAddressjTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPlatformAddressjTextField() {
		if (platformAddressjTextField == null) {
			platformAddressjTextField = new JTextField();
		}
		return platformAddressjTextField;
	}

	/**
	 * This method initializes platformNamejTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPlatformNamejTextField() {
		if (platformNamejTextField == null) {
			platformNamejTextField = new JTextField();
		}
		return platformNamejTextField;
	}

	private RecordPlatform getRecordSelect() {
		RecordPlatform record = null;
		int index = knowPlatformJTable.getSelectedRow();
		if (index != -1) {
			Vector row = (Vector) knowPlatformTableModel.getDataVector()
					.elementAt(index);
			String platformName = (String) row.elementAt(0);
			String platformAddress = (String) row.elementAt(1);
			record = new RecordPlatform(platformName, platformAddress);
		}
		return record;
	}

	/**
	 * This method initializes refreshJButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRefreshJButton() {
		if (refreshJButton == null) {
			refreshJButton = new JButton();
			refreshJButton.setText("Refresh");
			refreshJButton.setFont(new java.awt.Font("Comic Sans MS",
					java.awt.Font.BOLD, 10));
			refreshJButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if(guiAgent==null)return;
							refreshJButton.setEnabled(false);
							guiAgent
									.doAction(ConstantBehaviourAction.LOAD_ACTIVITY_PLATFORM);
						}
					});
		}
		return refreshJButton;
	}

	/**
	 * This method initializes removejButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemovejButton() {
		if (removejButton == null) {
			removejButton = new JButton();
			removejButton.setText("Remove");
			removejButton.setFont(new java.awt.Font("Comic Sans MS",
					java.awt.Font.BOLD, 10));
			removejButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if(guiAgent==null)return;
							String platformName = platformNamejTextField
									.getText();
							String platformAddress = platformAddressjTextField
									.getText();
							if (platformName != null && platformName != null) {
								boolean ris = FactoryUtil
										.checkName(platformName);
								if (ris) {
									ris = FactoryUtil
											.checkAddress(platformAddress);
									if (ris) {
										RecordPlatform record = new RecordPlatform(
												platformName, platformAddress);
										if (!removeToKnowPlatform(record)) {
											JOptionPane
													.showMessageDialog(
															InterPlatformManagerPanel.this,
															"Row not found ",
															"error",
															JOptionPane.ERROR_MESSAGE);
										}
										knowPlatformJTable.repaint();
									} else {
										JOptionPane.showMessageDialog(
												InterPlatformManagerPanel.this,
												"error to Platform address ",
												"error",
												JOptionPane.ERROR_MESSAGE);
									}
								} else {
									JOptionPane.showMessageDialog(
											InterPlatformManagerPanel.this,
											"error to Platform name", "error",
											JOptionPane.ERROR_MESSAGE);
								}
							} else {
								statusBar.setText("Select row first!");
							}
						}
					});
		}
		return removejButton;
	}

	private int getRowRecord(RecordPlatform record) {
		boolean found = false;
		Vector table = knowPlatformTableModel.getDataVector();
		String platformName = record.getPlatformName();
		int i;
		for (i = 0; i < table.size() && !found; i++)
			if (((Vector) table.elementAt(i)).elementAt(0).equals(platformName)
					&& ((Vector) table.elementAt(i)).elementAt(0).equals(
							record.getPlatformAddress())) {
				found = true;
			}
		i--;
		if (found)
			return i;
		return -1;
	}

	/**
	 * This method initializes statusBarJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getStatusBarJPanel() {
		if (statusBarJPanel == null) {
			statusBar = new JLabel();
			statusBar.setText("Status Bar");
			statusBar.setFont(new java.awt.Font("Comic Sans MS",
					java.awt.Font.PLAIN, 12));
			statusBarJPanel = new JPanel();
			statusBarJPanel.setLayout(new BorderLayout());
			statusBarJPanel.setBorder(javax.swing.BorderFactory
					.createLineBorder(java.awt.SystemColor.desktop, 1));
			statusBarJPanel.add(statusBar, java.awt.BorderLayout.NORTH);
		}
		return statusBarJPanel;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(555, 289);
		this.add(getStatusBarJPanel(), java.awt.BorderLayout.SOUTH);
		this.add(getCenterJSplitPane(), java.awt.BorderLayout.CENTER);
	}

	private boolean removeToKnowPlatform(RecordPlatform record) {
		boolean found = false;
		Vector table = knowPlatformTableModel.getDataVector();
		String platformName = record.getPlatformName();
		int i;
		for (i = 0; i < table.size() && !found; i++)
			if (((Vector) table.elementAt(i)).elementAt(0).equals(platformName)
					&& ((Vector) table.elementAt(i)).elementAt(0).equals(
							record.getPlatformAddress())) {
				found = true;
			}
		i--;
		if (!found) {
			return false;
		} else {
			table.remove(i);
			return true;
		}

	}

	public void setGuiAgent(GuiAgent guiAgent) {
		this.guiAgent = guiAgent;
		guiAgent.addObserver(this);
	}

	/**
	 * @param platformName
	 */
	@SuppressWarnings("unchecked") private void setKnowPlatformRecordState(
			String platformName, String platformAddress, String state) {
		Vector table = knowPlatformTableModel.getDataVector();
		for (int i = 0; i < table.size(); i++)
			if (((Vector) table.elementAt(i)).elementAt(0).equals(platformName)
					&& ((Vector) table.elementAt(i)).elementAt(0).equals(
							platformAddress)) {
				((Vector) table.elementAt(i)).add(2, state);
				break;
			}
	}

	@SuppressWarnings("unchecked") public void update(Observable o, Object arg) {
		// centerJSplitPane.setDividerLocation(0.6);
		if (arg instanceof LoadPlatformAction) {
			LoadPlatformAction action = (LoadPlatformAction) arg;
			ActivePlatform ap = action.getActivePlatform();
			for (Iterator<RecordPlatform> it = ap.iterator(); it.hasNext();) {
				RecordPlatform record = it.next();
				if (!activityPlatformJlistModel.contains(record)) {
					activityPlatformJlistModel.add(record);
				}
				int row = getRowRecord(record);
				if (row == -1)
					addToKnowPlatform(record, "federate");
			}
			for (Iterator it = knowPlatformTableModel.getDataVector()
					.iterator(); it.hasNext();) {
				{
					Vector v = (Vector) it.next();
					RecordPlatform r = new RecordPlatform((String) v.get(0),
							(String) v.get(1));
					if (!ap.contains(r)) {
						v.set(3, "not federate");
					}
				}

			}
			refreshJButton.setEnabled(true);
		} else if (arg instanceof RemoveAction) {
			RemoveAction action = (RemoveAction) arg;
			if (activityPlatformJlistModel.contains(action.getRecord())) {
				activityPlatformJlistModel.removeElement(action.getRecord());
			}
		} else if (arg instanceof AddAction) {
			AddAction action = (AddAction) arg;
			if (!activityPlatformJlistModel.contains(action.getRecord())) {
				activityPlatformJlistModel.add(action.getRecord());
				addToKnowPlatform(action.getRecord(), "federate");
			}
		} else if (arg instanceof SubscribeAction) {
			SubscribeAction action = (SubscribeAction) arg;
			statusBar.setText(action.getMessage());
			if (action.isOK()) {
				refreshJButton.setSelected(true);
				stateAutoRefresh = AutomaticState.AUTOREFRESHON;
			} else {
				refreshJButton.setSelected(false);
			}
			inProgress = false;
		} else if (arg instanceof DesubscribeAction) {
			DesubscribeAction action = (DesubscribeAction) arg;
			statusBar.setText(action.getMessage());
			if (action.isOK()) {
				refreshJButton.setSelected(false);
				stateAutoRefresh = AutomaticState.AUTOREFRESHOFF;
			} else {
				refreshJButton.setSelected(true);
			}
			inProgress = false;
			refreshJButton.repaint();
		} else if (arg instanceof FederationAction) {
			FederationAction action = (FederationAction) arg;
			if (action.isOK()) {
				statusBar.setText(action.getMessage());
				String platformName = action.getRecord().getPlatformName();
				setKnowPlatformRecordState(platformName, action.getRecord()
						.getPlatformAddress(), "federate");
			} else {
				JOptionPane.showMessageDialog(this, action.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (arg instanceof DefederationAction) {
			FederationAction action = (FederationAction) arg;
			if (action.isOK()) {
				statusBar.setText(action.getMessage());
				String platformName = action.getRecord().getPlatformName();
				setKnowPlatformRecordState(platformName, action.getRecord()
						.getPlatformAddress(), "not federate");
			} else {
				JOptionPane.showMessageDialog(this, action.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		// revalidate();
	}
} // @jve:decl-index=0:visual-constraint="10,10"
