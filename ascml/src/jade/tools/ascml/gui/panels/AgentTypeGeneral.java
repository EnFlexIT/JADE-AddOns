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
import java.io.File;

import jade.tools.ascml.gui.dialogs.StartAgentInstanceDialog;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.absmodel.IAgentType;

public class AgentTypeGeneral extends AbstractPanel implements ActionListener
{
    private JLabel iconLabel;

	private JButton buttonApply;
    private JButton buttonSave;
	private JButton buttonStart;

	private JButton buttonChangeImage;
	private JButton buttonChangeSourcePath;
	private JButton buttonChangeSourceName;
	private JButton buttonChangeTypeClass;

	private JTextField textFieldSourcePath;
	private JTextField textFieldSourceName;
	private JTextField textFieldTypeName;
	private JTextField textFieldTypePackage;
	private JTextField textFieldTypeClass;

	private JTextArea textAreaDescription;
	private JComboBox comboBoxPlatform;

    private String iconName;
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

		buttonStart = new JButton("Start Instance", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_START, 16, 16));
		buttonStart.addActionListener(this);
		buttonStart.setPreferredSize(new Dimension(145,22));
		buttonStart.setMinimumSize(new Dimension(145,22));
		buttonStart.setMaximumSize(new Dimension(145,22));

		buttonApply = new JButton("Apply Changes", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_APPLY, 16, 16));
		buttonApply.addActionListener(this);
		buttonApply.setPreferredSize(new Dimension(145,22));
		buttonApply.setMinimumSize(new Dimension(145,22));
		buttonApply.setMaximumSize(new Dimension(145,22));

		buttonSave = new JButton("Save AgentType", ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_SAVE, 16, 16));
		buttonSave.addActionListener(this);
		buttonSave.setPreferredSize(new Dimension(145,22));
		buttonSave.setMinimumSize(new Dimension(145,22));
		buttonSave.setMaximumSize(new Dimension(145,22));

		// ... and add the content (content is layout out in it's own panel)
		this.add(createAttributePanel(), new GridBagConstraints(0, 0, 3, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		this.add(buttonApply, new GridBagConstraints(0, 1, 1, 1, 0.33, 0.01, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(buttonSave, new GridBagConstraints(1, 1, 1, 1, 0.33, 0.01, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(buttonStart, new GridBagConstraints(2, 1, 1, 1, 0.33, 0.01, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createAttributePanel()
	{
		// first create all the components
		iconLabel = new JLabel(ImageIconLoader.scaleImageIcon(model.getIcon(), 32, 32));
        iconName = model.getIconName();

		textFieldSourcePath = new JTextField(model.getDocument().getSourcePath(), 30);
		textFieldSourcePath.setMinimumSize(new Dimension(320, (int)textFieldSourcePath.getPreferredSize().getHeight()));
		textFieldSourcePath.setBackground(Color.WHITE);

		textFieldSourceName = new JTextField(model.getDocument().getSourceName(), 30);
		textFieldSourceName.setMinimumSize(new Dimension(320, (int)textFieldSourceName.getPreferredSize().getHeight()));
		textFieldSourceName.setBackground(Color.WHITE);

		textFieldTypeName = new JTextField(model.getName(), 30);
		textFieldTypeName.setBackground(Color.WHITE);
		textFieldTypeName.setMinimumSize(new Dimension(320, (int)textFieldTypeName.getPreferredSize().getHeight()));

		textFieldTypePackage = new JTextField(model.getPackageName(), 30);
		textFieldTypePackage.setBackground(Color.WHITE);
		textFieldTypePackage.setMinimumSize(new Dimension(320, (int)textFieldTypePackage.getPreferredSize().getHeight()));

		textFieldTypeClass = new JTextField(model.getClassName(), 30);
		textFieldTypeClass.setBackground(Color.WHITE);
		textFieldTypeClass.setMinimumSize(new Dimension(320, (int)textFieldTypeClass.getPreferredSize().getHeight()));

		buttonChangeImage = new JButton("Change Icon");
		buttonChangeImage.addActionListener(this);
		buttonChangeImage.setMargin(new Insets(1,1,1,1));
		buttonChangeImage.setPreferredSize(new Dimension(80,18));
		buttonChangeImage.setMinimumSize(new Dimension(80,18));
		buttonChangeImage.setMaximumSize(new Dimension(80,18));

		buttonChangeSourcePath = new JButton("...");
		buttonChangeSourcePath.addActionListener(this);
		buttonChangeSourcePath.setMargin(new Insets(1,3,1,3));
		buttonChangeSourcePath.setPreferredSize(new Dimension(30,20));
		buttonChangeSourcePath.setMinimumSize(new Dimension(30,20));
		buttonChangeSourcePath.setMaximumSize(new Dimension(30,20));

		buttonChangeSourceName = new JButton("...");
		buttonChangeSourceName.addActionListener(this);
		buttonChangeSourceName.setMargin(new Insets(1,3,1,3));
		buttonChangeSourceName.setPreferredSize(new Dimension(30,20));
		buttonChangeSourceName.setMinimumSize(new Dimension(30,20));
		buttonChangeSourceName.setMaximumSize(new Dimension(30,20));

		buttonChangeTypeClass = new JButton("...");
		buttonChangeTypeClass.addActionListener(this);
		buttonChangeTypeClass.setMargin(new Insets(1,3,1,3));
		buttonChangeTypeClass.setPreferredSize(new Dimension(30,20));
		buttonChangeTypeClass.setMinimumSize(new Dimension(30,20));
		buttonChangeTypeClass.setMaximumSize(new Dimension(30,20));

		comboBoxPlatform = new JComboBox(new String[] { "JADE" });
		comboBoxPlatform.setBackground(Color.WHITE);

		textAreaDescription = new JTextArea(model.getDescription(), 5, 20);
		textAreaDescription.setFont(new Font("Arial", Font.PLAIN, 12));
		// textAreaDescription.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		textAreaDescription.setPreferredSize(new Dimension((int)textAreaDescription.getPreferredSize().getWidth(), 100));
		textAreaDescription.setMinimumSize(new Dimension((int)textAreaDescription.getPreferredSize().getWidth(), 100));
		textAreaDescription.setLineWrap(true);
		textAreaDescription.setWrapStyleWord(true);
		textAreaDescription.setBackground(Color.WHITE);

		// put the textarea into a scrollpane
		JScrollPane textDescScrollPane = new JScrollPane(textAreaDescription);
		textDescScrollPane.getViewport().setBackground(Color.WHITE);
		textDescScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		textDescScrollPane.setPreferredSize(new Dimension((int)textAreaDescription.getPreferredSize().getWidth(), 100));
		textDescScrollPane.setMinimumSize(new Dimension((int)textAreaDescription.getPreferredSize().getWidth(), 100));

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);
		
		// prepare Main-Panel
		attributePanel.add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(buttonChangeImage, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("<html><h2>General Settings</h2>Here, you see all the main-settings for this AgentType.</html>"), new GridBagConstraints(1, 0, 2, 2, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Source-Path:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldSourcePath, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(buttonChangeSourcePath, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Source-File:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldSourceName, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        attributePanel.add(buttonChangeSourceName, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Type-Name:"), new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldTypeName, new GridBagConstraints(1, 4, 2, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Type-Package:"), new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldTypePackage, new GridBagConstraints(1, 5, 2, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Type-Class:"), new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldTypeClass, new GridBagConstraints(1, 6, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(buttonChangeTypeClass, new GridBagConstraints(2, 6, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Platform:"), new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(comboBoxPlatform, new GridBagConstraints(1, 7, 2, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Description:"), new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textDescScrollPane, new GridBagConstraints(1, 8, 2, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	private void applyChanges()
	{
		String dialogMessage = "";

		// check if type-name or type-package have changed
		// if the type-name has changed automatically also change the source-name
		if (!model.getName().equals(textFieldTypeName.getText()) ||
			!model.getPackageName().equals(textFieldTypePackage.getText()))
		{
			dialogMessage+="<h3>Changing the name or package of an AgentType leads to a new source-name or -path!</h3><p><i>The names and locations of description-files are constructed out of the AgentType-name and -package,<br>changing the name or package also changes the name or path of the source-file.</i><p>&nbsp;";
			textFieldSourceName.setText(textFieldTypeName.getText() + ".agent.xml");
			model.setName(textFieldTypeName.getText());
			model.setPackageName(textFieldTypePackage.getText());
		}

		// check if source-name or source-path have changed
		// if the source-name has changed automatically also change the type-name
		if (!model.getDocument().getSourceName().equals(textFieldSourceName.getText()) ||
			!model.getDocument().getSourcePath().equals(textFieldSourcePath.getText()))
		{
			dialogMessage+="<h3>Changing the sourcepath or -file of an AgentType leads to a new AgentType-name or -package!</h3><p><i>The AgentType-name has to match the sourcefile-name (+ '.agent.xml')<br>and the package has to match the location of this sourcefile within the file-system.</i>";
			textFieldTypeName.setText(textFieldSourceName.getText().substring(0, textFieldSourceName.getText().indexOf(".agent.xml")));
			model.setName(textFieldTypeName.getText());
			model.getDocument().setSourcePath(textFieldSourcePath.getText());
			model.getDocument().setSourceName(textFieldSourceName.getText());
		}

		if (!dialogMessage.equals(""))
			JOptionPane.showMessageDialog(parentFrame, "<html>"+dialogMessage+"<p>&nbsp;<p>Please make sure to save the AgentType, so that the new file will be created.</html>");

		model.setClassName(textFieldTypeClass.getText());
		model.setDescription(textAreaDescription.getText());
		model.setPlatformType((String)comboBoxPlatform.getSelectedItem());
		model.setIconName(iconName);
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == buttonChangeImage)
		{
			String fileName = (String)mainPanel.showDialog(AbstractMainPanel.CHOOSE_ICON_DIALOG);
			iconName = fileName;
			iconLabel.setIcon(ImageIconLoader.createImageIcon(iconName, 32, 32));
		}
		else if (evt.getSource() == buttonChangeSourcePath)
		{
			String directoryName = (String)mainPanel.showDialog(AbstractMainPanel.CHOOSE_DIRECTORY_DIALOG) + File.separator;
			textFieldSourcePath.setText(directoryName);
		}
		else if (evt.getSource() == buttonChangeSourceName)
		{
			String name = (String)mainPanel.showDialog(AbstractMainPanel.CHOOSE_AGENTTYPE_FILE_DIALOG);
			textFieldSourceName.setText(name);
		}
		else if (evt.getSource() == buttonChangeTypeClass)
		{
			String name = (String)mainPanel.showDialog(AbstractMainPanel.CHOOSE_JAVA_FILE_DIALOG);
			name = name.substring(0, name.lastIndexOf("."));
			textFieldTypeClass.setText(name);
		}
		else if (evt.getSource() == buttonStart)
		{
            mainPanel.showDialog(new StartAgentInstanceDialog(mainPanel, model));
		}
		else if (evt.getSource() == buttonApply)
		{
			applyChanges();
		}
		else if (evt.getSource() == buttonSave)
		{
            applyChanges();
			System.err.println("AgentTypeGeneral.actionPerformed: Save AgentType, implement me !!!");
		}

	}
}
