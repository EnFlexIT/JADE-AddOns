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
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.awt.event.*;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.gui.dialogs.ParameterDialog;
import jade.tools.ascml.gui.components.ComponentFactory;
import jade.tools.ascml.model.jibx.AgentInstance;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.repository.ModelIntegrityChecker;

public class AgentInstanceGeneral extends AbstractPanel implements ActionListener
{
	private JButton buttonApply;
	private JButton buttonAddAgentInstance;
	private JButton buttonRemoveAgentInstance;
    private JButton buttonAddParameter;
	private JButton buttonEditParameter;
	private JButton buttonRemoveParameter;
    private JButton buttonAddDependency;
	private JButton buttonEditDependency;
	private JButton buttonRemoveDependency;

	private JTextField textFieldInstanceName;
	private JTextField textFieldTypeName;
	private JTextField textFieldScheme;

	private JSpinner spinnerQuantity;

	private JCheckBox toolOptionSniffer;
	private JCheckBox toolOptionLogger;
	private JCheckBox toolOptionIntrospector;
	private JCheckBox toolOptionBenchmark;

	private JTable tableAgentInstances;
	private JTable tableParameter;
	private JTable tableDependencies;

	private ISocietyInstance societyInstance;
	private IAgentInstance agentInstance;

	/**
	 * @param model the AgentModel to show in the dialog, this may be an
	 *              AgentInstanceModel or an AgentTypeModel
	 */
	public AgentInstanceGeneral(AbstractMainPanel parentPanel, IAgentInstance model, ISocietyInstance societyInstance)
	{
		super(parentPanel);
		this.societyInstance = societyInstance;

		if (model != null)
        	agentInstance = model;
        else if (societyInstance.getAgentInstanceModels().length > 0)
			agentInstance = societyInstance.getAgentInstanceModels()[0];
		else
			agentInstance = new AgentInstance(societyInstance);

		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		this.add(createLeftSide(), new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(5,5,5,5), 0, 0));
		this.add(createRightSide(), new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		// initially select the model, to view
		if (tableAgentInstances.getRowCount() > 0)
		{
			for (int i=0; i < tableAgentInstances.getRowCount(); i++)
			{
				String agentInstanceName = (String)((DefaultTableModel)tableAgentInstances.getModel()).getValueAt(i, 0);
				if (agentInstanceName.equals(agentInstance.getName()))
					tableAgentInstances.getSelectionModel().setSelectionInterval(i,i);
			}
			if (tableAgentInstances.getSelectedRow() == -1) // no row selected yet
				tableAgentInstances.getSelectionModel().setSelectionInterval(0,0);
		}

		setAgentInstanceData();
	}

	private JPanel createLeftSide()
	{
		buttonAddAgentInstance = ComponentFactory.createAddButton("Create");
		buttonAddAgentInstance.addActionListener(this);

		buttonRemoveAgentInstance = ComponentFactory.createRemoveButton("Remove");
		buttonRemoveAgentInstance.addActionListener(this);

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		attributePanel.add(new JLabel("<html>Click on an AgentInstance<br>below to edit the settings !</html>"), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(createAgentInstanceTablePane(), new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(buttonRemoveAgentInstance, new GridBagConstraints(0, 2, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0,5,5,5), 0, 0));
		attributePanel.add(buttonAddAgentInstance, new GridBagConstraints(1, 2, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,5,5,5), 0, 0));

		attributePanel.add(new JLabel(""), new GridBagConstraints(0, 3, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	private JPanel createRightSide()
	{
		// first create all the components
        buttonApply = ComponentFactory.createApplyButton("Apply Changes");
		buttonApply.addActionListener(this);

		buttonAddParameter = ComponentFactory.createAddButton("Create");
		buttonAddParameter.addActionListener(this);

		buttonEditParameter = ComponentFactory.createEditButton("Edit");
		buttonEditParameter.addActionListener(this);

		buttonRemoveParameter = ComponentFactory.createRemoveButton("Remove");
		buttonRemoveParameter.addActionListener(this);

		JPanel panelParameterButtons = new JPanel();
		panelParameterButtons.setBackground(Color.WHITE);
		panelParameterButtons.add(buttonRemoveParameter);
		panelParameterButtons.add(buttonEditParameter);
		panelParameterButtons.add(buttonAddParameter);

		buttonAddDependency = ComponentFactory.createAddButton("Add New");
		buttonAddDependency.addActionListener(this);

		buttonEditDependency = ComponentFactory.createEditButton("Edit");
		buttonEditDependency.addActionListener(this);

		buttonRemoveDependency = ComponentFactory.createRemoveButton("Remove");
		buttonRemoveDependency.addActionListener(this);

		textFieldInstanceName = new JTextField(20);
		textFieldInstanceName.setMinimumSize(new Dimension(150, (int)textFieldInstanceName.getPreferredSize().getHeight()));
		textFieldInstanceName.setBackground(Color.WHITE);

		textFieldTypeName = new JTextField(20);
		textFieldTypeName.setMinimumSize(new Dimension(150, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldTypeName.setBackground(Color.WHITE);

		textFieldScheme = new JTextField(10);
		textFieldScheme.setMinimumSize(new Dimension(100, (int)textFieldScheme.getPreferredSize().getHeight()));
		textFieldScheme.setBackground(Color.WHITE);

		spinnerQuantity = ComponentFactory.createQuantitySpinner(1);

		toolOptionSniffer = new JCheckBox("Sniffer");
		toolOptionSniffer.setBackground(Color.WHITE);

		toolOptionLogger = new JCheckBox("Logger");
		toolOptionLogger.setBackground(Color.WHITE);

		toolOptionIntrospector = new JCheckBox("Introspector");
		toolOptionIntrospector.setBackground(Color.WHITE);

		toolOptionBenchmark = new JCheckBox("Benchmarker");
		toolOptionBenchmark.setBackground(Color.WHITE);

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);
		
		// prepare Main-Panel
		attributePanel.add(new JLabel("Instance-Name:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldInstanceName, new GridBagConstraints(1, 0, 3, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("AgentType:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldTypeName, new GridBagConstraints(1, 1, 3, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Quantity:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(spinnerQuantity, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Naming-Scheme:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldScheme, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(toolOptionBenchmark, new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(toolOptionIntrospector, new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));

		attributePanel.add(toolOptionSniffer, new GridBagConstraints(0, 5, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(1,5,5,5), 0, 0));
		attributePanel.add(toolOptionLogger, new GridBagConstraints(1, 5, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(1,5,5,5), 0, 0));

		attributePanel.add(createParameterTablePane(), new GridBagConstraints(0, 6, 2, 1, 1, 0.5, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,2,1,2), 0, 0));
		attributePanel.add(panelParameterButtons, new GridBagConstraints(0, 7, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));

		attributePanel.add(createDependencyTablePane(), new GridBagConstraints(0, 8, 2, 1, 1, 0.5, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,2,1,2), 0, 0));

		// attributePanel.add(buttonRemoveDependency, new GridBagConstraints(0, 9, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));
		// attributePanel.add(buttonAddDependency, new GridBagConstraints(1, 9, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));

		attributePanel.add(buttonApply, new GridBagConstraints(1, 10, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,2,5,2), 0, 0));


		return attributePanel;
	}

	private void setAgentInstanceData()
	{
		if (agentInstance != null)
		{
			textFieldInstanceName.setText(agentInstance.getName());
			textFieldScheme.setText(agentInstance.getNamingScheme());
			if (agentInstance.getType() == null)
				textFieldTypeName.setText("Unknown");
			else
				textFieldTypeName.setText((agentInstance.getType().getFullyQualifiedName()));
			spinnerQuantity.setValue(agentInstance.getQuantity());
			toolOptionBenchmark.setSelected(agentInstance.hasToolOption(IToolOption.TOOLOPTION_BENCHMARK));
			toolOptionIntrospector.setSelected(agentInstance.hasToolOption(IToolOption.TOOLOPTION_INTROSPECTOR));
			toolOptionLogger.setSelected(agentInstance.hasToolOption(IToolOption.TOOLOPTION_LOG));
			toolOptionSniffer.setSelected(agentInstance.hasToolOption(IToolOption.TOOLOPTION_SNIFF));

			tableParameter.setModel(createParameterTableModel());

			if (tableParameter.getModel().getRowCount() > 0)
			{
				tableParameter.getSelectionModel().setSelectionInterval(0,0);
			}
		}
	}

	private JScrollPane createAgentInstanceTablePane()
	{
		tableAgentInstances = new JTable(createAgentInstanceTableModel());
		tableAgentInstances.setToolTipText("Click on a row to edit the AgentInstance !");
        JScrollPane agentInstanceScrollPane = ComponentFactory.createTableScrollPane(tableAgentInstances);

		//Ask to be notified of selection changes.
		ListSelectionModel rowSM = tableAgentInstances.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				//Ignore extra messages.
				if (e.getValueIsAdjusting()) return;

				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (!lsm.isSelectionEmpty())
				{
					int selectedRow = lsm.getMinSelectionIndex();
					agentInstance = societyInstance.getAgentInstanceModels()[selectedRow];
					setAgentInstanceData();
				}
			}
		});

		return agentInstanceScrollPane;
	}

	private DefaultTableModel createAgentInstanceTableModel()
	{
		String[] tableHeaderEntries = new String[] {"AgentInstance", "AgentType"};
		DefaultTableModel tableModel = new DefaultTableModel(tableHeaderEntries, 0);

		IAgentInstance[] agentInstances = societyInstance.getAgentInstanceModels();

		for(int i=0; i < agentInstances.length; i++)
		{
			String[] oneRow = new String[2];

			oneRow[0] = agentInstances[i].getName();
			if (agentInstances[i].getType() == null)
				oneRow[1] = "Unknown";
			else
				oneRow[1] = ""+agentInstances[i].getType();
			tableModel.addRow(oneRow);
		}

		return tableModel;
	}

	private JScrollPane createParameterTablePane()
	{
		tableParameter = new JTable(createParameterTableModel());
		JScrollPane tableScrollPane = ComponentFactory.createTableScrollPane(tableParameter);

		if (tableParameter.getModel().getRowCount() > 0)
			tableParameter.getSelectionModel().setSelectionInterval(0,0);

		return tableScrollPane;
	}

	private DefaultTableModel createParameterTableModel()
	{
		String[] tableHeaderEntries = new String[] {"Parameter-Name", "Parameter-Value"};
		DefaultTableModel tableModel = new DefaultTableModel(tableHeaderEntries, 0);

		if (agentInstance != null)
		{
			IParameter[] parameters = agentInstance.getParameters();
			for(int i=0; i < parameters.length; i++)
			{
				String[] oneRow = new String[2];

				oneRow[0] = parameters[i].getName();
				oneRow[1] = parameters[i].getValue();
				tableModel.addRow(oneRow);
			}

			IParameterSet[] parameterSets = agentInstance.getParameterSets();
			for(int i=0; i < parameterSets.length; i++)
			{
				String[] oneRow = new String[2];

				oneRow[0] = parameterSets[i].getName();
				oneRow[1] = parameterSets[i].getValueList().toString();
				tableModel.addRow(oneRow);
			}
		}

		return tableModel;
	}

	private JScrollPane createDependencyTablePane()
	{
		tableDependencies = new JTable(createDependencyTableModel());
		JScrollPane tableScrollPane = ComponentFactory.createTableScrollPane(tableDependencies);

		if (tableDependencies.getModel().getRowCount() > 0)
			tableDependencies.getSelectionModel().setSelectionInterval(0,0);

		return tableScrollPane;
	}

	private DefaultTableModel createDependencyTableModel()
	{
		String[] tableHeaderEntries = new String[] {"Type of Dependency"};
		DefaultTableModel tableModel = new DefaultTableModel(tableHeaderEntries, 0);

		if (agentInstance != null)
		{
			IDependency[] dependencies = agentInstance.getDependencies();
			for(int i=0; i < dependencies.length; i++)
			{
				String[] oneRow = new String[1];

				oneRow[0] = dependencies[i].getType();
				tableModel.addRow(oneRow);
			}
		}

		return tableModel;
	}

	public void actionPerformed(ActionEvent evt)
	{
        if (evt.getSource() == buttonAddParameter)
		{
			ParameterDialog dialog = new ParameterDialog(null, null);
			Object result = dialog.showDialog(parentFrame);

			if (result != null)
			{
				if (result instanceof IParameter)
					agentInstance.addParameter((IParameter)result);
				else
					agentInstance.addParameterSet((IParameterSet)result);
			}
			tableParameter.setModel(createParameterTableModel());
			tableParameter.getSelectionModel().setSelectionInterval(tableParameter.getModel().getRowCount()-1,tableParameter.getModel().getRowCount()-1);
		}
		else if (evt.getSource() == buttonRemoveParameter)
		{
			int selectedRow = tableParameter.getSelectedRow();
			if (tableParameter.getModel().getRowCount() > 0)
			{
				String parameterName = (String)tableParameter.getModel().getValueAt(selectedRow, 0);

				if (selectedRow <= agentInstance.getParameters().length-1)
					agentInstance.removeParameter(parameterName);
				else
					agentInstance.removeParameterSet(parameterName);

				tableParameter.setModel(createParameterTableModel());
				if (tableParameter.getModel().getRowCount() > 0)
					tableParameter.getSelectionModel().setSelectionInterval(0,0);
			}
		}
		else if (evt.getSource() == buttonEditParameter)
		{
			if (tableParameter.getModel().getRowCount() > 0)
			{
				int selectedRow = tableParameter.getSelectedRow();
				if (selectedRow <= agentInstance.getParameters().length-1)
				{
					IParameter instanceParameter = agentInstance.getParameters()[selectedRow];
					IParameter typeParameter = agentInstance.getType().getParameter(instanceParameter.getName());
					ParameterDialog dialog = new ParameterDialog(instanceParameter, typeParameter);

					Object result = dialog.showDialog(parentFrame);
					if (result != null)
					{
						agentInstance.removeParameter(instanceParameter.getName());
						if (result instanceof IParameter)
							agentInstance.addParameter((IParameter)result);
						else
							agentInstance.addParameterSet((IParameterSet)result);
					}
					tableParameter.setModel(createParameterTableModel());
					tableParameter.getSelectionModel().setSelectionInterval(selectedRow,selectedRow);
				}
				else
				{
					IParameterSet instanceParameterSet = agentInstance.getParameterSets()[selectedRow-agentInstance.getParameters().length];
					IParameterSet typeParameterSet = agentInstance.getType().getParameterSet(instanceParameterSet.getName());
					ParameterDialog dialog = new ParameterDialog(instanceParameterSet, typeParameterSet);

					Object result = dialog.showDialog(parentFrame);
					if (result != null)
					{
						agentInstance.removeParameterSet(instanceParameterSet.getName());
						if (result instanceof IParameter)
							agentInstance.addParameter((IParameter)result);
						else
							agentInstance.addParameterSet((IParameterSet)result);
					}
					tableParameter.setModel(createParameterTableModel());
				}
			}
		}
		else if (evt.getSource() == buttonApply)
		{
			agentInstance.setName(textFieldInstanceName.getText());
			agentInstance.setNamingScheme(textFieldScheme.getText());
			agentInstance.setQuantity(((Number)spinnerQuantity.getValue()).intValue()+"");
			agentInstance.setTypeName(textFieldTypeName.getText());
			agentInstance.setType((IAgentType)getRepository().getModelIndex().getModel(textFieldTypeName.getText()));

			if (tableAgentInstances.getSelectedRow() == -1)
			{
				// a new model has been created
				agentInstance.setParentSocietyInstance(societyInstance);
				societyInstance.addAgentInstance(agentInstance);

				ModelIntegrityChecker checker = new ModelIntegrityChecker();
				checker.checkIntegrity(societyInstance.getParentSocietyType());
				
				getRepository().getProject().throwProjectChangedEvent(new ProjectChangedEvent(ProjectChangedEvent.AGENTINSTANCE_SELECTED, agentInstance, getRepository().getProject()));

				// tableAgentInstances.setModel(createAgentInstanceTableModel());
				// tableAgentInstances.getSelectionModel().setSelectionInterval(tableAgentInstances.getRowCount()-1, tableAgentInstances.getRowCount()-1);
			}
			else
			{
				int selectedRow = tableAgentInstances.getSelectedRow();
				tableAgentInstances.setModel(createAgentInstanceTableModel());
				tableAgentInstances.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
			}
		}
		else if (evt.getSource() == buttonAddAgentInstance)
		{
			agentInstance = new AgentInstance(societyInstance);
			tableAgentInstances.getSelectionModel().removeSelectionInterval(0, tableAgentInstances.getSelectedRow());
			setAgentInstanceData();
		}
		else if (evt.getSource() == buttonRemoveAgentInstance)
		{
			societyInstance.removeAgentInstance(societyInstance.getAgentInstanceModel(tableAgentInstances.getSelectedRow()));
			// maybe it's better to just throw a ProjectChangedEvent, AGENTINSTANCE_SELECTED,
			// because this way the whole panel is newly instantiated.

			if (societyInstance.getAgentInstanceModels().length > 0)
			{
				agentInstance = societyInstance.getAgentInstanceModel(0);
				tableAgentInstances.setModel(createAgentInstanceTableModel());
				tableAgentInstances.getSelectionModel().setSelectionInterval(0,0);
			}
			else
			{
				agentInstance = new AgentInstance(societyInstance);
				tableAgentInstances.setModel(createAgentInstanceTableModel());
			}
			setAgentInstanceData();
		}
	}
}
