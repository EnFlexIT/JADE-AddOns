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
import jade.tools.ascml.gui.dialogs.StartSocietyInstanceDialog;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.model.jibx.Launcher;

public class ParameterOverview extends AbstractPanel implements ActionListener
{
	private JButton buttonApply;
    private JButton buttonAddValue;
	private JButton buttonRemoveValue;

	private JTextField textFieldName;

	private JComboBox comboBoxType;

	private JTextArea textAreaDescription;

	private JCheckBox checkBoxOptional;

	private JList listValues;

	private Object parameter;

	public ParameterOverview(AbstractMainPanel mainPanel, IParameter[] instanceParameters, IParameterSet[] instanceParameterSets, IParameter[] typeParameters, IParameterSet[] typeParameterSets)
	{
		super(mainPanel);
		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

		buttonApply = new JButton("Apply Changes", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_APPLY, 16, 16));
		buttonApply.addActionListener(this);
		buttonApply.setPreferredSize(new Dimension(145,22));
		buttonApply.setMinimumSize(new Dimension(145,22));
		buttonApply.setMaximumSize(new Dimension(145,22));

        this.add(createAttributePanel(), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(buttonApply, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}

	/**
	 * Set the instance-parameter to edit.
	 * If this method is called, only the name and value-fields are editable and filled with the instance-parameter-values.
	 * All other fields are filled with typeParameter-values.
	 * @param instanceParameter  The instance-parameter to edit.
	 * @param typeParameter  The type-parameter belonging to the instance-parameter
	 */
	public void setInstanceParameter(IParameter instanceParameter, IParameter typeParameter)
	{
        textFieldName.setText(instanceParameter.getName());
		comboBoxType.setEnabled(false);
		textAreaDescription.setText(typeParameter.getDescription());
		textAreaDescription.setEnabled(false);
		checkBoxOptional.setSelected(typeParameter.isOptional());
		checkBoxOptional.setEnabled(false);

		DefaultListModel listModel = (DefaultListModel)listValues.getModel();
		listModel.addElement(instanceParameter.getValue());
	}

	/**
	 * Set the instance-parameter to edit.
	 * If this method is called, only the name and value-fields are editable and filled with the instance-parameter-values.
	 * All other fields are filled with typeParameter-values.
	 * @param instanceParameter  The instance-parameter to edit.
	 * @param typeParameter  The type-parameter belonging to the instance-parameter
	 */
	public void setInstanceParameterSet(IParameterSet instanceParameter, IParameterSet typeParameter)
	{
        textFieldName.setText(instanceParameter.getName());
		comboBoxType.setEnabled(false);
		textAreaDescription.setText(typeParameter.getDescription());
		textAreaDescription.setEnabled(false);
		checkBoxOptional.setSelected(typeParameter.isOptional());
		checkBoxOptional.setEnabled(false);

		DefaultListModel listModel = (DefaultListModel)listValues.getModel();
		String[] values = instanceParameter.getValues();
		for (int i=0; i < values.length; i++)
		{
			listModel.addElement(values[i]);
		}
	}

	/**
	 * Set the type-parameter to edit.
	 * @param typeParameter  The type-parameter to edit.
	 */
	public void setTypeParameter(IParameter typeParameter)
	{
        textFieldName.setText(typeParameter.getName());
		textAreaDescription.setText(typeParameter.getDescription());
		checkBoxOptional.setSelected(typeParameter.isOptional());
		DefaultListModel listModel = (DefaultListModel)listValues.getModel();
		listModel.addElement(typeParameter.getValue());
	}

	/**
	 * Set the type-parameterSet to edit.
	 * @param typeParameterSet  The type-parameterSet to edit.
	 */
	public void setTypeParameterSet(IParameterSet typeParameterSet)
	{
        textFieldName.setText(typeParameterSet.getName());
		textAreaDescription.setText(typeParameterSet.getDescription());
		checkBoxOptional.setSelected(typeParameterSet.isOptional());

		DefaultListModel listModel = (DefaultListModel)listValues.getModel();
		String[] values = typeParameterSet.getValues();
		for (int i=0; i < values.length; i++)
		{
			listModel.addElement(values[i]);
		}
	}

	private JPanel createAttributePanel()
	{
		textFieldName = new JTextField("", 30);
		textFieldName.setMinimumSize(new Dimension(320, (int)textFieldName.getPreferredSize().getHeight()));
		textFieldName.setBackground(Color.WHITE);

		comboBoxType = new JComboBox(new String[] { "String" });
		comboBoxType.setBackground(Color.WHITE);

		// prepare Description
		textAreaDescription = new JTextArea("", 3, 20);
		textAreaDescription.setFont(new Font("Arial", Font.PLAIN, 12));
		textAreaDescription.setEditable(true);
		textAreaDescription.setLineWrap(true);
		textAreaDescription.setWrapStyleWord(true);
		textAreaDescription.setBackground(Color.WHITE);

		// put the textarea into a scrollpane
		JScrollPane textDescScrollPane = new JScrollPane(textAreaDescription);
		textDescScrollPane.getViewport().setBackground(Color.WHITE);
		textDescScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		textDescScrollPane.setPreferredSize(new Dimension((int)textAreaDescription.getPreferredSize().getWidth(), 50));
		textDescScrollPane.setMinimumSize(new Dimension((int)textAreaDescription.getPreferredSize().getWidth(), 50));

		checkBoxOptional = new JCheckBox("optional");
		// checkBoxOptional.setSelected(agentInstance.hasToolOption(IToolOption.TOOLOPTION_SNIFF));
		checkBoxOptional.setBackground(Color.WHITE);

		listValues = new JList();
		listValues.setBackground(Color.WHITE);
		listValues.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listValues.setLayoutOrientation(JList.VERTICAL);
        listValues.setVisibleRowCount(-1);

		JScrollPane listScrollPane = new JScrollPane(listValues);
		listScrollPane.getViewport().setBackground(Color.WHITE);
		listScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		listScrollPane.setPreferredSize(new Dimension((int)listScrollPane.getPreferredSize().getWidth(), 50));
		listScrollPane.setMinimumSize(new Dimension((int)listScrollPane.getPreferredSize().getWidth(), 50));

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		// prepare Main-Panel
		attributePanel.add(new JLabel("Parameter-Name:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldName, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Parameter-Type:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(comboBoxType, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(checkBoxOptional, new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Description:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textAreaDescription, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Values:"), new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(listScrollPane, new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(buttonApply, new GridBagConstraints(1, 5, 0, 1, 0, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	// ---------- actionListener-methods --------------
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == buttonApply)
		{

		}
	}

}
