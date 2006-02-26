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
import jade.tools.ascml.events.ModelActionEvent;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.absmodel.dependency.IDependency;

public class AgentInstanceGeneral extends AbstractPanel implements ActionListener
{
	private JButton buttonStart;
	private JButton buttonApply;
	private JButton buttonAddAgentInstance;
	private JButton buttonRemoveAgentInstance;
    private JButton buttonAddParameter;
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

		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		this.add(createLeftSide(), new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(5,5,5,5), 0, 0));
		this.add(createRightSide(), new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
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

		attributePanel.add(new JLabel("<html>Click on an AgentInstance<brbelow to edit the settings !</html>"), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(createAgentInstanceTablePane(), new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(buttonRemoveAgentInstance, new GridBagConstraints(0, 2, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0,5,5,5), 0, 0));
		attributePanel.add(buttonAddAgentInstance, new GridBagConstraints(1, 2, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,5,5,5), 0, 0));

		attributePanel.add(new JLabel(""), new GridBagConstraints(0, 3, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	private JPanel createRightSide()
	{
		// first create all the components
        buttonStart = new JButton("Start Instance", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_START, 16, 16));
		buttonStart.addActionListener(this);
		buttonStart.setMargin(new Insets(1,1,1,1));
		buttonStart.setPreferredSize(new Dimension(120,22));
		buttonStart.setMinimumSize(new Dimension(120,22));
		buttonStart.setMaximumSize(new Dimension(120,22));

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

		buttonRemoveParameter = new JButton("Remove", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_REMOVE, 16, 16));
		buttonRemoveParameter.addActionListener(this);
		buttonRemoveParameter.setMargin(new Insets(1,1,1,1));
		buttonRemoveParameter.setPreferredSize(new Dimension(80,20));
		buttonRemoveParameter.setMinimumSize(new Dimension(80,20));
		buttonRemoveParameter.setMaximumSize(new Dimension(80,20));

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

		textFieldInstanceName = new JTextField(agentInstance.getName(), 20);
		textFieldInstanceName.setMinimumSize(new Dimension(150, (int)textFieldInstanceName.getPreferredSize().getHeight()));
		textFieldInstanceName.setBackground(Color.WHITE);

		textFieldTypeName = new JTextField(agentInstance.getType().getFullyQualifiedName(), 20);
		textFieldTypeName.setMinimumSize(new Dimension(150, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldTypeName.setBackground(Color.WHITE);

		textFieldScheme = new JTextField(agentInstance.getNamingScheme(), 10);
		textFieldScheme.setMinimumSize(new Dimension(100, (int)textFieldScheme.getPreferredSize().getHeight()));
		textFieldScheme.setBackground(Color.WHITE);

		spinnerQuantity = new JSpinner(new SpinnerNumberModel(agentInstance.getQuantity(), 0, 10000, 1));
		spinnerQuantity.setPreferredSize(new Dimension(60, (int)spinnerQuantity.getPreferredSize().getHeight()));
		spinnerQuantity.setMinimumSize(new Dimension(60, (int)spinnerQuantity.getPreferredSize().getHeight()));
		spinnerQuantity.setMaximumSize(new Dimension(60, (int)spinnerQuantity.getPreferredSize().getHeight()));
		spinnerQuantity.setBackground(Color.WHITE);

		toolOptionSniffer = new JCheckBox("Sniffer");
		toolOptionSniffer.addActionListener(this);
		toolOptionSniffer.setSelected(agentInstance.hasToolOption(IToolOption.TOOLOPTION_SNIFF));
		toolOptionSniffer.setBackground(Color.WHITE);

		toolOptionLogger = new JCheckBox("Logger");
		toolOptionLogger.addActionListener(this);
		toolOptionLogger.setSelected(agentInstance.hasToolOption(IToolOption.TOOLOPTION_LOG));
		toolOptionLogger.setBackground(Color.WHITE);

		toolOptionIntrospector = new JCheckBox("Introspector");
		toolOptionIntrospector.addActionListener(this);
		toolOptionIntrospector.setSelected(agentInstance.hasToolOption(IToolOption.TOOLOPTION_INTROSPECTOR));
		toolOptionIntrospector.setBackground(Color.WHITE);

		toolOptionBenchmark = new JCheckBox("Benchmarker");
		toolOptionBenchmark.addActionListener(this);
		toolOptionBenchmark.setSelected(agentInstance.hasToolOption(IToolOption.TOOLOPTION_BENCHMARK));
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

		attributePanel.add(createParameterTablePane(), new GridBagConstraints(0, 6, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,2,1,2), 0, 0));

		attributePanel.add(buttonRemoveParameter, new GridBagConstraints(0, 7, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));
		attributePanel.add(buttonAddParameter, new GridBagConstraints(1, 7, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));

		attributePanel.add(createDependencyTablePane(), new GridBagConstraints(0, 8, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,2,1,2), 0, 0));

		attributePanel.add(buttonRemoveDependency, new GridBagConstraints(0, 9, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));
		attributePanel.add(buttonAddDependency, new GridBagConstraints(1, 9, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));

		attributePanel.add(buttonApply, new GridBagConstraints(0, 10, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,2,5,2), 0, 0));
		attributePanel.add(buttonStart, new GridBagConstraints(1, 10, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,2,5,2), 0, 0));


		return attributePanel;
	}

	private JScrollPane createAgentInstanceTablePane()
	{
		String[] tableHeaderEntries = new String[] {"AgentInstance", "AgentType"};

		final IAgentInstance[] agentInstances = societyInstance.getAgentInstanceModels();
		DefaultTableModel agentInstanceTableModel = new DefaultTableModel(tableHeaderEntries, 0);

		for(int i=0; i<agentInstances.length; i++)
		{
			String[] oneRow = new String[2];

			oneRow[0] = agentInstances[i].getName();
			oneRow[1] = ""+agentInstances[i].getType();
			agentInstanceTableModel.addRow(oneRow);
		}

		JTable agentInstanceTable = new JTable(agentInstanceTableModel);
		agentInstanceTable.setRowSelectionAllowed(true);
		agentInstanceTable.setColumnSelectionAllowed(false);
        agentInstanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        agentInstanceTable.setToolTipText("Click on a row to edit the AgentInstance !");

		//Ask to be notified of selection changes.
		ListSelectionModel rowSM = agentInstanceTable.getSelectionModel();
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
					agentInstance = agentInstances[selectedRow];
				}
			}
		});

		if (agentInstances.length > 0)
			rowSM.setSelectionInterval(0,0);

		initColumnSizes(agentInstanceTable);

		JPanel agentInstancePanel = new JPanel(new BorderLayout());
		agentInstancePanel.setBackground(Color.WHITE);
		agentInstancePanel.add(agentInstanceTable.getTableHeader(), BorderLayout.PAGE_START);
		agentInstancePanel.add(agentInstanceTable, BorderLayout.CENTER);

		JScrollPane agentInstanceScrollPane = new JScrollPane(agentInstancePanel);
		agentInstanceScrollPane.setWheelScrollingEnabled(true);
		// agentInstanceScrollPane.setBorder(BorderFactory.createEmptyBorder());
        agentInstanceScrollPane.setPreferredSize(new Dimension(190, (int)agentInstanceScrollPane.getPreferredSize().getHeight()));
		agentInstanceScrollPane.setMinimumSize(new Dimension(190, (int)agentInstanceScrollPane.getPreferredSize().getHeight()));
		agentInstanceScrollPane.setMaximumSize(new Dimension(190, (int)agentInstanceScrollPane.getPreferredSize().getHeight()));

		return agentInstanceScrollPane;
	}

	private JScrollPane createParameterTablePane()
	{
		String[] tableHeaderEntries = new String[] {"Parameter-Name", "Parameter-Value"};

		DefaultTableModel tableModel = new DefaultTableModel(tableHeaderEntries, 0);
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

		JTable table = new JTable(tableModel);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//Ask to be notified of selection changes.
		ListSelectionModel rowSM = table.getSelectionModel();
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
					System.err.println("show Dialog");
				}
			}
		});

		if ((parameters.length > 0) || (parameterSets.length > 0))
			rowSM.setSelectionInterval(0,0);

		initColumnSizes(table);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(Color.WHITE);
		tablePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tablePanel.add(table, BorderLayout.CENTER);

		JScrollPane tableScrollPane = new JScrollPane(tablePanel);
		tableScrollPane.setWheelScrollingEnabled(true);
		// agentInstanceScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.setPreferredSize(new Dimension(190, (int)tableScrollPane.getPreferredSize().getHeight()));
		tableScrollPane.setMinimumSize(new Dimension(190, (int)tableScrollPane.getPreferredSize().getHeight()));
		tableScrollPane.setMaximumSize(new Dimension(190, (int)tableScrollPane.getPreferredSize().getHeight()));

		return tableScrollPane;
	}

	private JScrollPane createDependencyTablePane()
	{
		String[] tableHeaderEntries = new String[] {"Type of Dependency"};

		DefaultTableModel tableModel = new DefaultTableModel(tableHeaderEntries, 0);
		IDependency[] dependencies = agentInstance.getDependencies();
		for(int i=0; i < dependencies.length; i++)
		{
			String[] oneRow = new String[1];

			oneRow[0] = dependencies[i].getType();
			tableModel.addRow(oneRow);
		}

		JTable table = new JTable(tableModel);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setToolTipText("Click on a row to bring up an edit-dialog !");

		//Ask to be notified of selection changes.
		ListSelectionModel rowSM = table.getSelectionModel();
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
					System.err.println("show Dialog");
				}
			}
		});

		if (dependencies.length > 0)
			rowSM.setSelectionInterval(0,0);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(Color.WHITE);
		tablePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tablePanel.add(table, BorderLayout.CENTER);

		JScrollPane tableScrollPane = new JScrollPane(tablePanel);
		tableScrollPane.setWheelScrollingEnabled(true);
		// agentInstanceScrollPane.setBorder(BorderFactory.createEmptyBorder());
		tableScrollPane.setPreferredSize(new Dimension(190, (int)tableScrollPane.getPreferredSize().getHeight()));
		tableScrollPane.setMinimumSize(new Dimension(190, (int)tableScrollPane.getPreferredSize().getHeight()));
		tableScrollPane.setMaximumSize(new Dimension(190, (int)tableScrollPane.getPreferredSize().getHeight()));

		return tableScrollPane;
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
		if (evt.getSource() == toolOptionSniffer)
		{

		}
		else if (evt.getSource() == buttonStart)
		{
            try
			{
				// create the model
				IAbstractRunnable[] runnableModels = (IAbstractRunnable[])getRepository().getRunnableManager().createRunnable(societyInstance.getName(), societyInstance, 1);

				// and select the newly created instance
				mainPanel.selectModel(runnableModels[0]);

				// Throw the start-event
				for (int i=0; i < runnableModels.length; i++)
				{
					ModelActionEvent actionEvent = new ModelActionEvent(ModelActionEvent.CMD_START_AGENTINSTANCE, runnableModels[i]);
					mainPanel.throwModelActionEvent(actionEvent);
				}
			}
			catch(ModelException exc)
			{
				getRepository().throwExceptionEvent(exc);
			}
		}
	}
}
