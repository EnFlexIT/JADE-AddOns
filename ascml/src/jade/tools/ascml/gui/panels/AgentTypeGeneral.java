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

import java.awt.event.*;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.gui.dialogs.StartAgentInstanceDialog;
import jade.tools.ascml.repository.loader.ImageIconLoader;

public class AgentTypeGeneral extends AbstractPanel implements ActionListener
{
	private JButton buttonStartInstance;

	private IAgentType model;

	/**
	 * @param model the AgentModel to show in the dialog, this may be an
	 *              AgentInstanceModel or an AgentTypeModel
	 */
	public AgentTypeGeneral(AbstractMainPanel parentPanel, IAgentType model)
	{
		super(parentPanel);
		this.model = model;

		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		// toDO: repository.addModelChangedListener(this);

		// ... and add the content (content is layout out in it's own panel)
		this.add(createAttributePanel(), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		buttonStartInstance = new JButton("Start new Instance");
		buttonStartInstance.addActionListener(this);

		this.add(buttonStartInstance, new GridBagConstraints(0, 1, 1, 1, 1, 0.01, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createAttributePanel()
	{
		// first create all the components

		// prepare Name
		JTextField textName = new JTextField(model.getFullyQualifiedName(), 20);
		textName.setEditable(false);
		textName.setBackground(Color.WHITE);

		// prepare File-Name
		JTextField textFileName = new JTextField((String)model.getDocument().getSource(), 20);
		textFileName.setEditable(false);
		textFileName.setBackground(Color.WHITE);
		JButton buttonFileName = new JButton("...");
		buttonFileName.setMargin(new Insets(1,3,1,3));
		buttonFileName.setPreferredSize(new Dimension(30,21));
		buttonFileName.setMaximumSize(new Dimension(30,21));

		// prepare Type-Name
		JTextField textClass = new JTextField(model.getClassName(), 20);
		textClass.setEditable(false);
		textClass.setBackground(Color.WHITE);
		JButton buttonClass = new JButton("...");
		buttonClass.setMargin(new Insets(1,3,1,3));
		buttonClass.setPreferredSize(new Dimension(30,21));
		buttonClass.setMaximumSize(new Dimension(30,21));

		// prepare Description
		JTextArea textDesc = new JTextArea(model.getDescription(), 5, 20);
		textDesc.setFont(new Font("Arial", Font.PLAIN, 12));
		textDesc.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		textDesc.setEditable(false);
		textDesc.setLineWrap(true);
		textDesc.setWrapStyleWord(true);
		textDesc.setBackground(Color.WHITE);

		// put the textarea into a scrollpane
		JScrollPane textDescScrollPane = new JScrollPane(textDesc);
		textDescScrollPane.getViewport().setBackground(Color.WHITE);
		textDescScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);
		
		// prepare Main-Panel
		attributePanel.add(new JLabel(ImageIconLoader.scaleImageIcon(model.getIcon(), 32, 32)), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("<html><h2>General Settings</h2>Here, you see all the main-settings for this agent-type.</html>"), new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Type-File:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFileName, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Type-Name:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textName, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        attributePanel.add(new JLabel("Type-Class:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textClass, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Description:"), new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        attributePanel.add(textDescScrollPane, new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == buttonStartInstance)
		{
            mainPanel.showDialog(new StartAgentInstanceDialog(mainPanel, model));
		}
	}
}
