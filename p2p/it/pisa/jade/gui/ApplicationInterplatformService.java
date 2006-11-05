/**
 * 
 */
package it.pisa.jade.gui;

import it.pisa.jade.agents.guiAgent.GuiAgent;
import it.pisa.jade.agents.guiAgent.action.ErrorAction;
import it.pisa.jade.agents.guiAgent.action.KillAction;
import it.pisa.jade.agents.guiAgent.behaviours.ConstantBehaviourAction;
import it.pisa.jade.util.AgentName;
import it.pisa.jade.util.JadeObserver;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

/**
 * @author Domenico Trimboli
 * 
 */
@SuppressWarnings("serial") public class ApplicationInterplatformService extends
		JFrame implements JadeObserver {

	private JPanel jContentPane = null;

	private JMenuBar jJMenuBar = null;

	private JMenu fileMenu = null;

	private JMenu editMenu = null;

	private JMenu helpMenu = null;

	private JMenuItem exitMenuItem = null;

	private JMenuItem aboutMenuItem = null;

	private JMenuItem cutMenuItem = null;

	private JMenuItem copyMenuItem = null;

	private JMenuItem pasteMenuItem = null;

	private JMenuItem saveMenuItem = null;

	private JTabbedPane mainjTabbedPane = null;

	private AgentManagerPanel agentManagerPanel = null;

	private GuiAgent guiAgent = null;

	private InterPlatformManagerPanel interPlatformManagerPanel = null;

	private boolean exit = false;

	/**
	 * This is the default constructor
	 */
	public ApplicationInterplatformService() {
		super();
		initialize();
	}

	public ApplicationInterplatformService(GuiAgent agent) {
		super();
		this.guiAgent = agent;
		guiAgent.addObserver(this);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				exit = true;
				setVisible(true);
				guiAgent.doAction(ConstantBehaviourAction.KILLAGENT);
			}

		});
		this.setJMenuBar(getJJMenuBar());
		this.setSize(649, 368);
		this.setContentPane(getJContentPane());
		this.setTitle(guiAgent.getName());
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
			jContentPane
					.add(getMainjTabbedPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getEditMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.add(getCutMenuItem());
			editMenu.add(getCopyMenuItem());
			editMenu.add(getPasteMenuItem());
		}
		return editMenu;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new JDialog(ApplicationInterplatformService.this, "About",
							true).setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCutMenuItem() {
		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem();
			cutMenuItem.setText("Cut");
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					Event.CTRL_MASK, true));
		}
		return cutMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
			copyMenuItem.setText("Copy");
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					Event.CTRL_MASK, true));
			copyMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

				}
			});
		}
		return copyMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem();
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					Event.CTRL_MASK, true));
		}
		return pasteMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save");
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK, true));
		}
		return saveMenuItem;
	}

	/**
	 * This method initializes mainjTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getMainjTabbedPane() {
		if (mainjTabbedPane == null) {
			mainjTabbedPane = new JTabbedPane();
			mainjTabbedPane.setFont(new java.awt.Font("Comic Sans MS",
					java.awt.Font.BOLD, 12));
			mainjTabbedPane.addTab("Agent manager", null,
					getAgentManagerPanel(), null);
			mainjTabbedPane.addTab("platform manager", null,
					getInterPlatformManagerPanel(), null);
		}
		return mainjTabbedPane;
	}

	/**
	 * This method initializes agentManagerPanel
	 * 
	 * @return it.pisa.jade.gui.AgentManagerPanel
	 */
	private AgentManagerPanel getAgentManagerPanel() {
		if (agentManagerPanel == null) {
			agentManagerPanel = new AgentManagerPanel();
			agentManagerPanel
					.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			agentManagerPanel.setGuiAgent(guiAgent);
		}
		return agentManagerPanel;
	}

	/**
	 * This method initializes interPlatformManagerPanel
	 * 
	 * @return it.pisa.jade.gui.InterPlatformManagerPanel
	 */
	private InterPlatformManagerPanel getInterPlatformManagerPanel() {
		if (interPlatformManagerPanel == null) {
			interPlatformManagerPanel = new InterPlatformManagerPanel();
			interPlatformManagerPanel.setGuiAgent(this.guiAgent);
		}
		return interPlatformManagerPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof ErrorAction) {
			ErrorAction e = (ErrorAction) arg1;
			JDialogMessage.showMessage(this, e.getAclMessage(), e.getMessage());
			if (exit) {
				int ris = JOptionPane.showConfirmDialog(this,
						"Message Exit don't send!Do you want exit anyway?",
						"Quit", JOptionPane.YES_NO_OPTION);
				if (ris == JOptionPane.YES_OPTION) {
					System.exit(0);
				} else {
					exit = false;
					setVisible(true);
				}
			}
		}else if(arg1 instanceof KillAction&&exit){
			if(((KillAction)arg1).getAgentKilled().getLocalName().equals(AgentName.multicastManagerAgent.name()));
			{
				if(((KillAction)arg1).isOK())System.exit(0);
			}
			
		}
	}
} // @jve:decl-index=0:visual-constraint="6,12"
