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
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import java.awt.event.*;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.gui.dialogs.ParameterDialog;
import jade.tools.ascml.model.jibx.AgentInstance;

public class AgentInstanceGeneral extends AbstractPanel implements ActionListener
{
	private JButton buttonApply;
	private JButton buttonAddAgentInstance;
	private JButton buttonRemoveAgentInstance;
    private JButton buttonAddParameter;
	private JButton buttonEditParameter;
	private JButton buttonRemoveParameter;
    private JButton buttonAddDependency;
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
	public AgentInstanceGeneral(AbstractMainPanel parentPanel, ISocietyInstance model)
	{
		super(parentPanel);
		this.societyInstance = model;
		if (societyInstance.getAgentInstanceModels().length > 0)
        	agentInstance = societyInstance.getAgentInstanceModels()[0];
        else
			agentInstance = new AgentInstance();

		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		this.add(createLeftSide(), new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(5,5,5,5), 0, 0));
		this.add(createRightSide(), new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		if (tableAgentInstances.getRowCount() > 0)
		{
			tableAgentInstances.getSelectionModel().setSelectionInterval(0,0);
			initColumnSizes(tableAgentInstances);
		}

		setAgentInstanceData();
	}

	private JPanel createLeftSide()
	{
		buttonAddAgentInstance = new JButton("Add New", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_ADD, 16, 16));
		buttonAddAgentInstance.addActionListener(this);
		buttonAddAgentInstance.setMargin(new Insets(1,1,1,1));
		buttonAddAgentInstance.setPreferredSize(new Dimension(80,20));
		buttonAddAgentInstance.setMinimumSize(new Dimension(80,20));
		buttonAddAgentInstance.setMaximumSize(new Dimension(80,20));

		buttonRemoveAgentInstance = new JButton("Remove", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_REMOVE, 16, 16));
		buttonRemoveAgentInstance.addActionListener(this);
		buttonRemoveAgentInstance.setMargin(new Insets(1,1,1,1));
		buttonRemoveAgentInstance.setPreferredSize(new Dimension(80,20));
		buttonRemoveAgentInstance.setMinimumSize(new Dimension(80,20));
		buttonRemoveAgentInstance.setMaximumSize(new Dimension(80,20));

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
        buttonApply = new JButton("Apply Changes", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_APPLY, 16, 16));
		buttonApply.addActionListener(this);
		buttonApply.setMargin(new Insets(1,1,1,1));
		buttonApply.setPreferredSize(new Dimension(120,22));
		buttonApply.setMinimumSize(new Dimension(120,22));
		buttonApply.setMaximumSize(new Dimension(120,22));

		buttonAddParameter = new JButton("Add New", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_ADD, 16, 16));
		buttonAddParameter.addActionListener(this);
		buttonAddParameter.setMargin(new Insets(1,1,1,1));
		buttonAddParameter.setPreferredSize(new Dimension(80,20));
		buttonAddParameter.setMinimumSize(new Dimension(80,20));
		buttonAddParameter.setMaximumSize(new Dimension(80,20));

		buttonEditParameter = new JButton("Edit", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_EDIT, 16, 16));
		buttonEditParameter.addActionListener(this);
		buttonEditParameter.setMargin(new Insets(1,1,1,1));
		buttonEditParameter.setPreferredSize(new Dimension(80,20));
		buttonEditParameter.setMinimumSize(new Dimension(80,20));
		buttonEditParameter.setMaximumSize(new Dimension(80,20));

		buttonRemoveParameter = new JButton("Remove", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_REMOVE, 16, 16));
		buttonRemoveParameter.addActionListener(this);
		buttonRemoveParameter.setMargin(new Insets(1,1,1,1));
		buttonRemoveParameter.setPreferredSize(new Dimension(80,20));
		buttonRemoveParameter.setMinimumSize(new Dimension(80,20));
		buttonRemoveParameter.setMaximumSize(new Dimension(80,20));

		JPanel panelParameterButtons = new JPanel();
		panelParameterButtons.setBackground(Color.WHITE);
		panelParameterButtons.add(buttonRemoveParameter);
		panelParameterButtons.add(buttonEditParameter);
		panelParameterButtons.add(buttonAddParameter);

		buttonAddDependency = new JButton("Add New", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_ADD, 16, 16));
		buttonAddDependency.addActionListener(this);
		buttonAddDependency.setMargin(new Insets(1,1,1,1));
		buttonAddDependency.setPreferredSize(new Dimension(80,20));
		buttonAddDependency.setMinimumSize(new Dimension(80,20));
		buttonAddDependency.setMaximumSize(new Dimension(80,20));

		buttonRemoveDependency = new JButton("Remove", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_REMOVE, 16, 16));
		buttonRemoveDependency.addActionListener(this);
		buttonRemoveDependency.setMargin(new Insets(1,1,1,1));
		buttonRemoveDependency.setPreferredSize(new Dimension(80,20));
		buttonRemoveDependency.setMinimumSize(new Dimension(80,20));
		buttonRemoveDependency.setMaximumSize(new Dimension(80,20));

		textFieldInstanceName = new JTextField(20);
		textFieldInstanceName.setMinimumSize(new Dimension(150, (int)textFieldInstanceName.getPreferredSize().getHeight()));
		textFieldInstanceName.setBackground(Color.WHITE);

		textFieldTypeName = new JTextField(20);
		textFieldTypeName.setMinimumSize(new Dimension(150, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldTypeName.setBackground(Color.WHITE);

		textFieldScheme = new JTextField(10);
		textFieldScheme.setMinimumSize(new Dimension(100, (int)textFieldScheme.getPreferredSize().getHeight()));
		textFieldScheme.setBackground(Color.WHITE);

		spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
		spinnerQuantity.setPreferredSize(new Dimension(60, (int)spinnerQuantity.getPreferredSize().getHeight()));
		spinnerQuantity.setMinimumSize(new Dimension(60, (int)spinnerQuantity.getPreferredSize().getHeight()));
		spinnerQuantity.setMaximumSize(new Dimension(60, (int)spinnerQuantity.getPreferredSize().getHeight()));
		spinnerQuantity.setBackground(Color.WHITE);

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

		attributePanel.add(buttonRemoveDependency, new GridBagConstraints(0, 9, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));
		attributePanel.add(buttonAddDependency, new GridBagConstraints(1, 9, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));

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
		}
	}

	private JScrollPane createAgentInstanceTablePane()
	{

		tableAgentInstances = new JTable(createAgentInstanceTableModel());
		tableAgentInstances.setPreferredSize(new Dimension(220, 200));
		tableAgentInstances.setMinimumSize(new Dimension(220, (int)tableAgentInstances.getPreferredSize().getHeight()));
		tableAgentInstances.setMaximumSize(new Dimension(220, (int)tableAgentInstances.getPreferredSize().getHeight()));
		tableAgentInstances.setRowSelectionAllowed(true);
		tableAgentInstances.setColumnSelectionAllowed(false);
        tableAgentInstances.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableAgentInstances.setToolTipText("Click on a row to edit the AgentInstance !");

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

		JPanel agentInstancePanel = new JPanel(new BorderLayout());
		agentInstancePanel.setBackground(Color.WHITE);
		agentInstancePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		agentInstancePanel.setPreferredSize(new Dimension(220, 200));
		agentInstancePanel.setMinimumSize(new Dimension(220, (int)agentInstancePanel.getPreferredSize().getHeight()));
		agentInstancePanel.setMaximumSize(new Dimension(220, (int)agentInstancePanel.getPreferredSize().getHeight()));
		agentInstancePanel.add(tableAgentInstances.getTableHeader(), BorderLayout.PAGE_START);
		agentInstancePanel.add(tableAgentInstances, BorderLayout.CENTER);

		JScrollPane agentInstanceScrollPane = new JScrollPane(agentInstancePanel);
		agentInstanceScrollPane.setWheelScrollingEnabled(true);
		// agentInstanceScrollPane.setBorder(BorderFactory.createEmptyBorder());
        agentInstanceScrollPane.setPreferredSize(new Dimension(220, 200));
		agentInstanceScrollPane.setMinimumSize(new Dimension(190, (int)agentInstanceScrollPane.getPreferredSize().getHeight()));
		agentInstanceScrollPane.setMaximumSize(new Dimension(190, (int)agentInstanceScrollPane.getPreferredSize().getHeight()));

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
		DefaultTableModel tableModel = createParameterTableModel();

		tableParameter = new JTable(tableModel);
		tableParameter.setRowSelectionAllowed(true);
		tableParameter.setColumnSelectionAllowed(false);
        tableParameter.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		if (tableParameter.getModel().getRowCount() > 0)
		{
			tableParameter.getSelectionModel().setSelectionInterval(0,0);
			initColumnSizes(tableParameter);
		}

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		tablePanel.setBackground(Color.WHITE);
		tablePanel.setPreferredSize(new Dimension(190, 100));
		tablePanel.setMinimumSize(new Dimension(190, (int)tablePanel.getPreferredSize().getHeight()));
		tablePanel.setMaximumSize(new Dimension(190, (int)tablePanel.getPreferredSize().getHeight()));
		tablePanel.add(tableParameter.getTableHeader(), BorderLayout.PAGE_START);
		tablePanel.add(tableParameter, BorderLayout.CENTER);

		JScrollPane tableScrollPane = new JScrollPane(tablePanel);
		tableScrollPane.setWheelScrollingEnabled(true);
		// agentInstanceScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.setPreferredSize(new Dimension(190, 100));
		tableScrollPane.setMinimumSize(new Dimension(190, (int)tableScrollPane.getPreferredSize().getHeight()));
		tableScrollPane.setMaximumSize(new Dimension(190, (int)tableScrollPane.getPreferredSize().getHeight()));

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
		DefaultTableModel tableModel = createDependencyTableModel();

		tableDependencies = new JTable(tableModel);
		tableDependencies.setRowSelectionAllowed(true);
		tableDependencies.setColumnSelectionAllowed(false);
		tableDependencies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		if (tableDependencies.getModel().getRowCount() > 0)
		{
			tableDependencies.getSelectionModel().setSelectionInterval(0,0);
			initColumnSizes(tableDependencies);
		}

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(Color.WHITE);
		tablePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		tablePanel.setPreferredSize(new Dimension(190, 100));
		tablePanel.setMinimumSize(new Dimension(190, (int)tablePanel.getPreferredSize().getHeight()));
		tablePanel.setMaximumSize(new Dimension(190, (int)tablePanel.getPreferredSize().getHeight()));
		tablePanel.add(tableDependencies.getTableHeader(), BorderLayout.PAGE_START);
		tablePanel.add(tableDependencies, BorderLayout.CENTER);

		JScrollPane tableScrollPane = new JScrollPane(tablePanel);
		tableScrollPane.setWheelScrollingEnabled(true);
		// agentInstanceScrollPane.setBorder(BorderFactory.createEmptyBorder());
		tableScrollPane.setPreferredSize(new Dimension(190, 100));
		tableScrollPane.setMinimumSize(new Dimension(190, (int)tableScrollPane.getPreferredSize().getHeight()));
		tableScrollPane.setMaximumSize(new Dimension(190, (int)tableScrollPane.getPreferredSize().getHeight()));

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

	private void initColumnSizes(JTable table)
	{
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;

		TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

		for (int i = 0; i < table.getColumnCount(); i++)
		{
			column = table.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;

			Class columnClass = table.getModel().getColumnClass(i);
			TableCellRenderer tableRenderer = table.getDefaultRenderer(columnClass);
			comp = tableRenderer.getTableCellRendererComponent(table, table.getModel().getValueAt(0, i), false, false, 0, i);
			cellWidth = comp.getPreferredSize().width;

			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
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
			agentInstance.setQuantity(spinnerQuantity.getValue()+"");
			agentInstance.setTypeName(textFieldTypeName.getText());
			agentInstance.setType((IAgentType)getRepository().getModelIndex().getModel(textFieldTypeName.getText()));

			if (tableAgentInstances.getSelectedRow() == -1)
			{
				// a new model has been created
				agentInstance.setParentSocietyInstance(societyInstance);
				societyInstance.addAgentInstance(agentInstance);
				tableAgentInstances.setModel(createAgentInstanceTableModel());
				tableAgentInstances.getSelectionModel().setSelectionInterval(tableAgentInstances.getRowCount()-1, tableAgentInstances.getRowCount()-1);
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
			agentInstance = new AgentInstance();
			tableAgentInstances.getSelectionModel().removeSelectionInterval(0, tableAgentInstances.getSelectedRow());
			setAgentInstanceData();
		}
		else if (evt.getSource() == buttonRemoveAgentInstance)
		{
			societyInstance.removeAgentInstance(societyInstance.getAgentInstanceModel(tableAgentInstances.getSelectedRow()));
			if (societyInstance.getAgentInstanceModels().length > 0)
			{
				agentInstance = societyInstance.getAgentInstanceModel(0);
				tableAgentInstances.setModel(createAgentInstanceTableModel());
				tableAgentInstances.getSelectionModel().setSelectionInterval(0,0);
			}
			else
			{
				agentInstance = new AgentInstance();
				tableAgentInstances.setModel(createAgentInstanceTableModel());
			}
			setAgentInstanceData();
		}
	}
}
