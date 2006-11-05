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
package it.pisa.jade.agents.federatorAgent;



import it.pisa.jade.util.FactoryUtil;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 * 
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 *
 */
public class FederatorFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JTextField jTextFieldSubAdd = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JTextField jTextFieldParentAdd = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JTextField jTextFieldSubRemove = null;
	private JTextField jTextFieldParentRemove = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private FederatorAgent federatorAgent=null;
	private JButton jButtonAdd = null;
	private JButton jButtonRemove = null;
	/**
	 * This is the default constructor
	 */
	public FederatorFrame(FederatorAgent federatorAgent) {
		super();
		this.federatorAgent=federatorAgent;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(479, 289);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(2);
			jContentPane = new JPanel();
			jContentPane.setLayout(gridLayout);
			jContentPane.add(getJPanel1(), null);
			jContentPane.add(getJPanel(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel5 = new JLabel();
			jLabel5.setBounds(new java.awt.Rectangle(23,59,70,23));
			jLabel5.setText("parentDF");
			jLabel4 = new JLabel();
			jLabel4.setBounds(new java.awt.Rectangle(24,32,69,22));
			jLabel4.setText("sunDF");
			jLabel3 = new JLabel();
			jLabel3.setBounds(new java.awt.Rectangle(143,5,126,22));
			jLabel3.setText("REMOVE FEDERATION");
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(jLabel3, null);
			jPanel.add(getJTextFieldSubRemove(), null);
			jPanel.add(getJTextFieldParentRemove(), null);
			jPanel.add(jLabel4, null);
			jPanel.add(jLabel5, null);
			jPanel.add(getJButtonRemove(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel2 = new JLabel();
			jLabel2.setBounds(new java.awt.Rectangle(141,10,125,18));
			jLabel2.setText("ADD FEDERATION");
			jLabel1 = new JLabel();
			jLabel1.setBounds(new java.awt.Rectangle(17,59,92,19));
			jLabel1.setText("parentDF");
			jLabel = new JLabel();
			jLabel.setBounds(new java.awt.Rectangle(16,33,92,17));
			jLabel.setText("subDF");
			jPanel1 = new JPanel();
			jPanel1.setLayout(null);
			jPanel1.add(getJTextField(), null);
			jPanel1.add(jLabel2, null);
			jPanel1.add(getJButtonAdd(), null);
			jPanel1.add(jLabel, null);
			jPanel1.add(jLabel1, null);
			jPanel1.add(getJTextFieldParentAdd(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextFieldSubAdd == null) {
			jTextFieldSubAdd = new JTextField();
			jTextFieldSubAdd.setBounds(new java.awt.Rectangle(118,34,306,19));
			jTextFieldSubAdd.setText("df@luna:1099/JADE, http://luna:7778/acc");
		}
		return jTextFieldSubAdd;
	}

	/**
	 * This method initializes jTextFieldParentAdd	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldParentAdd() {
		if (jTextFieldParentAdd == null) {
			jTextFieldParentAdd = new JTextField();
			jTextFieldParentAdd.setBounds(new java.awt.Rectangle(118,57,305,21));
			jTextFieldParentAdd.setText("df@luna:1099/JADE, http://luna:7778/acc");
		}
		return jTextFieldParentAdd;
	}

	/**
	 * This method initializes jTextFieldSubRemove	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldSubRemove() {
		if (jTextFieldSubRemove == null) {
			jTextFieldSubRemove = new JTextField();
			jTextFieldSubRemove.setBounds(new java.awt.Rectangle(120,34,303,22));
			jTextFieldSubRemove.setText("df@luna:1099/JADE, http://luna:8088/acc");
		}
		return jTextFieldSubRemove;
	}

	/**
	 * This method initializes jTextFieldParentRemove	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldParentRemove() {
		if (jTextFieldParentRemove == null) {
			jTextFieldParentRemove = new JTextField();
			jTextFieldParentRemove.setBounds(new java.awt.Rectangle(121,58,301,25));
			jTextFieldParentRemove.setText("df@luna:1099/JADE, http://luna:8088/acc");
		}
		return jTextFieldParentRemove;
	}

	/**
	 * This method initializes jButtonAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setBounds(new java.awt.Rectangle(159,86,105,31));
			jButtonAdd.setText("ADD");
			jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					federatorAgent.requestFederation(FactoryUtil.createAID(jTextFieldSubAdd.getText()),FactoryUtil.createAID(jTextFieldParentAdd.getText()));
					//federatorAgent.myGui.addDF(federatorAgent.createAID(jTextFieldSubAdd.getText()));
					//federatorAgent.myGui.addDF(federatorAgent.createAID(jTextFieldParentAdd.getText()));
					
				}
			});
		}
		return jButtonAdd;
	}

	/**
	 * This method initializes jButtonRemove	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonRemove() {
		if (jButtonRemove == null) {
			jButtonRemove = new JButton();
			jButtonRemove.setBounds(new java.awt.Rectangle(164,89,114,34));
			jButtonRemove.setText("Remove");
			jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					federatorAgent.requestFederationRemoval(FactoryUtil.createAID(jTextFieldSubRemove.getText()),FactoryUtil.createAID(jTextFieldParentRemove.getText()));
				}
			});
		}
		return jButtonRemove;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
