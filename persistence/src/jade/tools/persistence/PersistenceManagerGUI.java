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


package jade.tools.persistence;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.ProfileException;
import jade.core.Specifier;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import jade.util.leap.Properties;
import jade.wrapper.*;


/**
 *
 * @author  Giovanni Rimassa - FRAMeTech s.r.l.
 */
public class PersistenceManagerGUI extends javax.swing.JFrame implements GUIConstants {

    /** Creates new form PersistenceManagerGUI */
    public PersistenceManagerGUI() {
        myActionProcessor = new ActionProcessor();
        registerActions();

        initComponents();
    }

    public MetaDBWindow getActiveMetaDB() {
        JInternalFrame frame = myDesktop.getSelectedFrame();
        if(frame instanceof MetaDBWindow) {
            return (MetaDBWindow)frame;
        }
        else {
            return null;
        }
    }

    public AgentPlatformWindow getActivePlatform() {
        JInternalFrame frame = myDesktop.getSelectedFrame();
        if(frame instanceof AgentPlatformWindow) {
            return (AgentPlatformWindow)frame;
        }
        else {
            return null;
        }
    }

    public void showCorrect() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int)screenSize.getWidth() / 2;
        int centerY = (int)screenSize.getHeight() / 2;
        setSize(centerX, centerY);
        setLocation(centerX / 2, centerY / 2);
        setVisible(true);
    }

    public void shutdown() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Exit Application", JOptionPane.YES_NO_OPTION);
        if(option == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        myDesktop = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        connectToDB = new javax.swing.JMenuItem();
        connectToPlatform = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        exitApp = new javax.swing.JMenuItem();
        metaDBMenu = new javax.swing.JMenu();
        createRepository = new javax.swing.JMenuItem();
        deleteRepository = new javax.swing.JMenuItem();
        addMapping = new javax.swing.JMenuItem();
        removeMapping = new javax.swing.JMenuItem();
        addProperty = new javax.swing.JMenuItem();
        removeProperty = new javax.swing.JMenuItem();
        agentMenu = new javax.swing.JMenu();
        loadAgent = new javax.swing.JMenuItem();
        saveAgent = new javax.swing.JMenuItem();
        reloadAgent = new javax.swing.JMenuItem();
        freezeAgent = new javax.swing.JMenuItem();
        thawAgent = new javax.swing.JMenuItem();
        deleteAgent = new javax.swing.JMenuItem();
        containerMenu = new javax.swing.JMenu();
        saveContainer = new javax.swing.JMenuItem();
        reloadContainer = new javax.swing.JMenuItem();
        deleteContainer = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("JADE Persistence Management Console");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        getContentPane().add(myDesktop, java.awt.BorderLayout.CENTER);

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");
        connectToDB.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        connectToDB.setMnemonic('M');
        connectToDB.setText("Meta-DB...");
        connectToDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectToDBActionPerformed(evt);
            }
        });

        fileMenu.add(connectToDB);

        connectToPlatform.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        connectToPlatform.setMnemonic('P');
        connectToPlatform.setText("Platform...");
        connectToPlatform.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectToPlatformActionPerformed(evt);
            }
        });

        fileMenu.add(connectToPlatform);

        fileMenu.add(jSeparator1);

        exitApp.setAction(myActionProcessor.getAction(ACTION_EXIT));
        exitApp.setMnemonic('x');
        fileMenu.add(exitApp);

        menuBar.add(fileMenu);

        metaDBMenu.setMnemonic('M');
        metaDBMenu.setText("Meta-DB");
        createRepository.setAction(myActionProcessor.getAction(ACTION_ADDREPOSITORY));
        metaDBMenu.add(createRepository);

        deleteRepository.setAction(myActionProcessor.getAction(ACTION_REMOVEREPOSITORY));
        metaDBMenu.add(deleteRepository);

        addMapping.setAction(myActionProcessor.getAction(ACTION_ADDMAPPING));
        metaDBMenu.add(addMapping);

        removeMapping.setAction(myActionProcessor.getAction(ACTION_REMOVEMAPPING));
        metaDBMenu.add(removeMapping);

        addProperty.setAction(myActionProcessor.getAction(ACTION_ADDPROPERTY));
        metaDBMenu.add(addProperty);

        removeProperty.setAction(myActionProcessor.getAction(ACTION_REMOVEPROPERTY));
        metaDBMenu.add(removeProperty);

        menuBar.add(metaDBMenu);

        agentMenu.setMnemonic('A');
        agentMenu.setText("Agent");
        agentMenu.add(loadAgent);

        saveAgent.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveAgent.setAction(myActionProcessor.getAction(ACTION_SAVEAGENT));
        saveAgent.setMnemonic('S');
        saveAgent.setText("Save Agent");
        agentMenu.add(saveAgent);

        reloadAgent.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        reloadAgent.setAction(myActionProcessor.getAction(ACTION_RELOADAGENT));
        reloadAgent.setMnemonic('R');
        reloadAgent.setText("Reload Agent");
        agentMenu.add(reloadAgent);

        freezeAgent.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        freezeAgent.setAction(myActionProcessor.getAction(ACTION_FREEZEAGENT));
        freezeAgent.setMnemonic('F');
        freezeAgent.setText("Freeze Agent");
        agentMenu.add(freezeAgent);

        thawAgent.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        thawAgent.setAction(myActionProcessor.getAction(ACTION_THAWAGENT));
        thawAgent.setMnemonic('T');
        thawAgent.setText("Thaw Agent");
        agentMenu.add(thawAgent);

        deleteAgent.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        deleteAgent.setAction(myActionProcessor.getAction(ACTION_DELETEAGENT));
        deleteAgent.setMnemonic('D');
        deleteAgent.setText("Delete Agent");
        agentMenu.add(deleteAgent);

        menuBar.add(agentMenu);

        containerMenu.setMnemonic('C');
        containerMenu.setText("Container");
        saveContainer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveContainer.setAction(myActionProcessor.getAction(ACTION_SAVECONTAINER));
        saveContainer.setMnemonic('S');
        saveContainer.setText("Save Container");
        containerMenu.add(saveContainer);

        reloadContainer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        reloadContainer.setAction(myActionProcessor.getAction(ACTION_LOADCONTAINER));
        reloadContainer.setMnemonic('R');
        reloadContainer.setText("Reload Container");
        containerMenu.add(reloadContainer);

        deleteContainer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        deleteContainer.setAction(myActionProcessor.getAction(ACTION_DELETECONTAINER));
        deleteContainer.setMnemonic('D');
        deleteContainer.setText("Delete Container");
        containerMenu.add(deleteContainer);

        menuBar.add(containerMenu);

        setJMenuBar(menuBar);

        pack();
    }//GEN-END:initComponents

    private void connectToPlatformActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectToPlatformActionPerformed

        JDialog dlg = new JDialog(this, "Connect to JADE Platform", true);
        ConnectPlatformDlgBody panel = new ConnectPlatformDlgBody(dlg);
        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setLocation(getX() + (getWidth() - dlg.getWidth()) / 2, getY() + (getHeight() - dlg.getHeight()) /2);
        dlg.setVisible(true);

        if(panel.isAccepted()) {

            try {
                String nickname = panel.getAgentNickname();
                Profile p = null;
                switch(panel.getProfileKind()) {
                    case ConnectPlatformDlgBody.PROFILE_USEDEFAULT:
                        p = new ProfileImpl();
                        break;
                    case ConnectPlatformDlgBody.PROFILE_USEHOSTANDPORT:
                        p = new ProfileImpl(panel.getProfileHost(), panel.getProfilePort(), panel.getProfileName());
                        break;
                    case ConnectPlatformDlgBody.PROFILE_USEPROPERTIES:
                        Properties props = new Properties();
                        props.load(new URL(panel.getPropertiesURL()).openStream());
                        p = new ProfileImpl(props);
                        break;
                    default:
                        p = new ProfileImpl();
                        break;
                }

                // Add the persistence service if not already listed in the profile
                addPersistenceService(p);

                PersistenceManagerAgent ag = new PersistenceManagerAgent(myActionProcessor);
                AgentPlatformWindow wnd = ag.getGUI();
                wnd.setSize(myDesktop.getWidth()*9/10, myDesktop.getHeight()*8/10);

                myDesktop.add(wnd);
                wnd.activate(nickname, p, panel.isMainContainer());
            }
            catch(IOException ioe) {
                JOptionPane.showMessageDialog(this, ioe.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
            }
            catch(ProfileException pe) {
                JOptionPane.showMessageDialog(this, pe.getMessage(), "Profile Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_connectToPlatformActionPerformed

    private void connectToDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectToDBActionPerformed
        ConnectMetaDBDlgBody panel = new ConnectMetaDBDlgBody();
        int result = JOptionPane.showConfirmDialog(this, panel, "Connect to a Meta-DB", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION) {
            String alias = panel.getAlias();
            String url = panel.getURL();

            addAlias(alias, url);

            MetaDBWindow wnd = new MetaDBWindow(alias, url, myActionProcessor);
            wnd.setSize(myDesktop.getWidth()*9/10, myDesktop.getHeight()*8/10);
            final String aliasName = alias;
            wnd.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {

                public void internalFrameClosed(InternalFrameEvent e) {
                    removeAlias(aliasName);
                }

            });

            myDesktop.add(wnd);
            wnd.activate();
        }
    }//GEN-LAST:event_connectToDBActionPerformed

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        shutdown();
    }//GEN-LAST:event_exitForm

    private void addAlias(String alias, String url) {
        aliases.put(alias, url);
    }
    
    private void removeAlias(String alias) {
        aliases.remove(alias);
    }

    private void registerActions() {
        myActionProcessor.registerAction(GUIConstants.ACTION_ADDREPOSITORY, new AddRepositoryAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_REMOVEREPOSITORY, new RemoveRepositoryAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_ADDMAPPING, new AddMappingAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_REMOVEMAPPING, new RemoveMappingAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_ADDPROPERTY, new AddPropertyAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_REMOVEPROPERTY, new RemovePropertyAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_RELOADAGENT, new ReloadAgentAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_SAVEAGENT, new SaveAgentAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_SAVECONTAINER, new SaveContainerAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_LOADAGENT, new LoadAgentAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_LOADCONTAINER, new LoadContainerAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_DELETEAGENT, new DeleteAgentAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_DELETECONTAINER, new DeleteContainerAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_FREEZEAGENT, new FreezeAgentAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_THAWAGENT, new ThawAgentAction(this));
        myActionProcessor.registerAction(GUIConstants.ACTION_EXIT, new ExitAction(this));
    }

    private void addPersistenceService(Profile p) throws ProfileException {
        List l = p.getSpecifiers(Profile.SERVICES);
        Object[] serviceSpecifiers = l.toArray();
        for(int i = 0; i < serviceSpecifiers.length; i++) {
            Specifier s = (Specifier)serviceSpecifiers[i];
            String serviceClass = s.getClassName();
            if(serviceClass.equals(jade.core.persistence.PersistenceService.class.getName())) {
                return;
            }
	}

	// Not found -- Add the Persistence Service to the list
	Specifier spec = new Specifier();
	spec.setClassName(jade.core.persistence.PersistenceService.class.getName());
	l.add(spec);
	p.setSpecifiers(Profile.SERVICES, l);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new PersistenceManagerGUI().showCorrect();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addMapping;
    private javax.swing.JMenuItem addProperty;
    private javax.swing.JMenu agentMenu;
    private javax.swing.JMenuItem connectToDB;
    private javax.swing.JMenuItem connectToPlatform;
    private javax.swing.JMenu containerMenu;
    private javax.swing.JMenuItem createRepository;
    private javax.swing.JMenuItem deleteAgent;
    private javax.swing.JMenuItem deleteContainer;
    private javax.swing.JMenuItem deleteRepository;
    private javax.swing.JMenuItem exitApp;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem freezeAgent;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenuItem loadAgent;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu metaDBMenu;
    private javax.swing.JDesktopPane myDesktop;
    private javax.swing.JMenuItem reloadAgent;
    private javax.swing.JMenuItem reloadContainer;
    private javax.swing.JMenuItem removeMapping;
    private javax.swing.JMenuItem removeProperty;
    private javax.swing.JMenuItem saveAgent;
    private javax.swing.JMenuItem saveContainer;
    private javax.swing.JMenuItem thawAgent;
    // End of variables declaration//GEN-END:variables

    private Map aliases = new HashMap();
    private ActionProcessor myActionProcessor;
    
}