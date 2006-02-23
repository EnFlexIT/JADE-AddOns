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
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.gui.dialogs.StartSocietyInstanceDialog;
import jade.tools.ascml.repository.loader.ImageIconLoader;

public class SocietyTypeGeneral extends AbstractPanel implements ActionListener
{
	private JTextField textName;
	private JTextField textFileName;
	private JButton buttonFileName;
	private JTextArea textDesc;
	private JButton buttonStartInstance;

	private ISocietyType model;

	/**
	 * @param model the SocietyTypeModel to show in the dialog, this may be an
	 *              AgentInstanceModel or an AgentTypeModel
	 */
	public SocietyTypeGeneral(AbstractMainPanel mainPanel, ISocietyType model)
	{
		super(mainPanel);
		// toDO: repository.addModelChangedListener(this);
		this.model = model;

		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());
		
		// ... and add the content step by step with special constraint-options
		this.add(createAttributePanel(), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));

		buttonStartInstance = new JButton("Start Default-Instance");
		buttonStartInstance.addActionListener(this);

		// ... and add the button in the next row
		this.add(buttonStartInstance, new GridBagConstraints(0, 1, 1, 1, 1, 0.01, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
	}

	private JPanel createAttributePanel()
	{
		// first, prepare all the components to display

		// prepare Name
		textName = new JTextField(model.getFullyQualifiedName(), 20);
		textName.setEditable(false);
		textName.setBackground(Color.WHITE);

		// prepare File-Name
		textFileName = new JTextField(model.getDocument().getSource().toString(), 20);
		textFileName.setEditable(false);
		textFileName.setBackground(Color.WHITE);
		buttonFileName = new JButton("...");
		buttonFileName.setMargin(new Insets(1,3,1,3));
		buttonFileName.setPreferredSize(new Dimension(30,21));
		buttonFileName.setMaximumSize(new Dimension(30,21));

		// prepare fileName-panel
		JPanel fileNamePanel = new JPanel(new GridBagLayout());
		fileNamePanel.setBackground(Color.WHITE);
		fileNamePanel.add(textFileName, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		// uncomment if it's possible to write to ADFs
		// fileNamePanel.add(buttonFileName, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		
		// prepare Description
		textDesc = new JTextArea(model.getDescription(), 10, 20);
		textDesc.setFont(new Font("Arial", Font.PLAIN, 12));
		textDesc.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		textDesc.setEditable(false);
		textDesc.setLineWrap(true);
		textDesc.setWrapStyleWord(true);
		textDesc.setBackground(Color.WHITE);

		// prepare Default-SocietyInstance-combobox
		JComboBox defaultInstanceBox = new JComboBox(model.getSocietyInstances());
		defaultInstanceBox.setEnabled(false);
		// defaultInstanceBox.addActionListener(this); // uncomment if it's possible to write to ADFs
		for (int i=0; i < defaultInstanceBox.getItemCount(); i++)
		{
			if (defaultInstanceBox.getItemAt(i) == model.getDefaultSocietyInstance())
			{
				defaultInstanceBox.setSelectedIndex(i);
				break;
			}
		}

		// put the textarea into a scrollpane
		JScrollPane textDescScrollPane = new JScrollPane(textDesc);
		textDescScrollPane.getViewport().setBackground(Color.WHITE);
		textDescScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		// prepare Main-Panel
		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		attributePanel.add(new JLabel(ImageIconLoader.scaleImageIcon(model.getIcon(), 32, 32)), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("<html><h2>General Settings</h2>Here, you see all the main-settings for this society.</html>"), new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Type-File:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(fileNamePanel, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Type-Name:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textName, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Default-Instance:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(defaultInstanceBox, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Description:"), new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        attributePanel.add(textDescScrollPane, new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == buttonStartInstance)
		{
            mainPanel.showDialog(new StartSocietyInstanceDialog(mainPanel, model.getDefaultSocietyInstance()));
		}
	}
}
