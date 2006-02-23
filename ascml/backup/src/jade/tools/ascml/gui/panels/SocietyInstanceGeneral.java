/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.gui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.gui.dialogs.StartSocietyInstanceDialog;

public class SocietyInstanceGeneral extends AbstractPanel implements ActionListener
{
	private JButton buttonStartInstance;

	private ISocietyInstance model;

	public SocietyInstanceGeneral(AbstractMainPanel mainPanel, ISocietyInstance model)
	{
		super(mainPanel);

		this.model = model;
				
		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

		buttonStartInstance = new JButton("Start Instance");
		buttonStartInstance.addActionListener(this);

		this.add(new JLabel("<html><h2>&nbsp;<i>" + model.getName() + "</i> - Details</h2></html>"), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(createAttributePanel(), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(createAgentInstanceTabelPane(), new GridBagConstraints(0, 2, 1, 1, 1, 0.5, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		this.add(createSocietyInstanceReferenceTablePane(), new GridBagConstraints(0, 3, 1, 1, 1, 0.5, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		this.add(buttonStartInstance, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}
	
	private JPanel createAttributePanel()
	{
		// first prepare all the components to display

		// prepare Name
		JTextField textName = new JTextField("" + model.getName(), 20);
		textName.setEditable(false);
		textName.setBackground(Color.WHITE);
				
		// prepare Description
		JTextArea textDesc = new JTextArea(model.getDescription(), 3, 20);
		textDesc.setFont(new Font("Arial", Font.PLAIN, 12));
		textDesc.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		textDesc.setEditable(false);
		textDesc.setLineWrap(true);
		textDesc.setWrapStyleWord(true);
		textDesc.setBackground(Color.WHITE);


		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);
		attributePanel.add(new JLabel("Society-Instance name:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textName, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Description:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textDesc, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	private JScrollPane createAgentInstanceTabelPane()
	{
		String[] tableHeaderEntries = new String[] {"Agent-Name", "Agent-Type"};

		IAgentInstance[] agentInstances = model.getAgentInstanceModels();
		DefaultTableModel agentInstanceTableModel = new DefaultTableModel(tableHeaderEntries, 0);

		for(int i=0; i<agentInstances.length; i++)
		{
			String[] oneRow = new String[2];

			oneRow[0] = agentInstances[i].getName();
			oneRow[1] = ""+agentInstances[i].getType();
			agentInstanceTableModel.addRow(oneRow);
		}

		JTable agentInstanceTable = new JTable(agentInstanceTableModel);
		agentInstanceTable.setRowSelectionAllowed(false);
		agentInstanceTable.setColumnSelectionAllowed(false);

		JPanel agentInstancePanel = new JPanel(new BorderLayout());
		agentInstancePanel.setBorder(BorderFactory.createTitledBorder(" Agent-Instances "));
		agentInstancePanel.setBackground(Color.WHITE);
		agentInstancePanel.add(agentInstanceTable.getTableHeader(), BorderLayout.PAGE_START);
		agentInstancePanel.add(agentInstanceTable, BorderLayout.CENTER);

		JScrollPane agentInstanceScrollPane = new JScrollPane(agentInstancePanel);
		agentInstanceScrollPane.setWheelScrollingEnabled(true);
		agentInstanceScrollPane.setBorder(BorderFactory.createEmptyBorder());

		return agentInstanceScrollPane;
	}

	private JScrollPane createSocietyInstanceReferenceTablePane()
	{
		String[] tableHeaderEntries = new String[] {"Type-Name", "Instance Name", "Launcher"};
		ISocietyInstanceReference[] socInstRefs = model.getSocietyInstanceReferences();

		DefaultTableModel societyRefTableModel = new DefaultTableModel(tableHeaderEntries, 0);

		for (int i = 0; i < socInstRefs.length; i++)
		{
			ISocietyInstanceReference oneSocInstRef = socInstRefs[i];

			String[] oneRow = new String[3];
			String typeName = oneSocInstRef.getTypeName();
			String instanceName = oneSocInstRef.getInstanceName();
			String launcherName = oneSocInstRef.getLauncherName();
			String[] launcherAddresses = oneSocInstRef.getLauncherAddresses();
			if (launcherAddresses.length > 0)
				launcherName += "(";
			for (int j=0; j < launcherAddresses.length-1; j++)
			{
				launcherName += launcherAddresses[j] + ",";
			}
			if (launcherAddresses.length > 0)
			{
				launcherName += launcherAddresses[launcherAddresses.length-1];
				launcherName += ")";
			}
			oneRow[0] = typeName;
			oneRow[1] = instanceName;
			oneRow[2] = launcherName;
			societyRefTableModel.addRow(oneRow);
		}

		JTable societyRefTable = new JTable(societyRefTableModel);
		societyRefTable.setRowSelectionAllowed(false);
		societyRefTable.setColumnSelectionAllowed(false);

		JPanel societyRefPanel = new JPanel(new BorderLayout());
		societyRefPanel.setBorder(BorderFactory.createTitledBorder(" Society-Instance References "));
		societyRefPanel.setBackground(Color.WHITE);
		societyRefPanel.add(societyRefTable.getTableHeader(), BorderLayout.PAGE_START);
		societyRefPanel.add(societyRefTable, BorderLayout.CENTER);

		JScrollPane societyRefScrollPane = new JScrollPane(societyRefPanel);
		societyRefScrollPane.setWheelScrollingEnabled(true);
		societyRefScrollPane.setBorder(BorderFactory.createEmptyBorder());

		return societyRefScrollPane;
	}

	// ---------- actionListener-methods --------------
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == buttonStartInstance)
		{
            mainPanel.showDialog(new StartSocietyInstanceDialog(mainPanel, model));
		}
	}

}
