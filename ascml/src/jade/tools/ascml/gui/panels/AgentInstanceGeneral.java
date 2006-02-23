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
import jade.tools.ascml.events.ModelActionEvent;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.absmodel.IAgentInstance;
import jade.tools.ascml.absmodel.IToolOption;
import jade.tools.ascml.absmodel.IAbstractRunnable;

public class AgentInstanceGeneral extends AbstractPanel implements ActionListener
{
	private JButton buttonStartInstance;
	private JCheckBox toolOptionSniffer;
	private JCheckBox toolOptionLogger;
	private JCheckBox toolOptionIntrospector;
	private JCheckBox toolOptionBenchmark;


	private IAgentInstance model;

	/**
	 * @param model the AgentModel to show in the dialog, this may be an
	 *              AgentInstanceModel or an AgentTypeModel
	 */
	public AgentInstanceGeneral(AbstractMainPanel parentPanel, IAgentInstance model)
	{
		super(parentPanel);
		this.model = model;

		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		// toDO: repository.addModelChangedListener(this);

		// ... and add the content (content is layout out in it's own panel)
		this.add(createAttributePanel(), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		buttonStartInstance = new JButton("Start Instance");
		buttonStartInstance.addActionListener(this);

		this.add(buttonStartInstance, new GridBagConstraints(0, 1, 1, 1, 1, 0.01, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createAttributePanel()
	{
		// first create all the components

		// prepare instance-Name
		JTextField textInstanceName = new JTextField(model.getName(), 20);
		textInstanceName.setEditable(false);
		textInstanceName.setBackground(Color.WHITE);

		// prepare type-Name
		JTextField textTypeName = new JTextField(model.getType().getFullyQualifiedName(), 20);
		textTypeName.setEditable(false);
		textTypeName.setBackground(Color.WHITE);

		toolOptionSniffer = new JCheckBox("Sniffer");
		toolOptionSniffer.addActionListener(this);
		toolOptionSniffer.setSelected(model.hasToolOption(IToolOption.TOOLOPTION_SNIFF));
		toolOptionSniffer.setBackground(Color.WHITE);

		toolOptionLogger = new JCheckBox("Logger");
		toolOptionLogger.addActionListener(this);
		toolOptionLogger.setSelected(model.hasToolOption(IToolOption.TOOLOPTION_LOG));
		toolOptionLogger.setBackground(Color.WHITE);

		toolOptionIntrospector = new JCheckBox("Introspector");
		toolOptionIntrospector.addActionListener(this);
		toolOptionIntrospector.setSelected(model.hasToolOption(IToolOption.TOOLOPTION_INTROSPECTOR));
		toolOptionIntrospector.setBackground(Color.WHITE);

		toolOptionBenchmark = new JCheckBox("Benchmark");
		toolOptionBenchmark.addActionListener(this);
		toolOptionBenchmark.setSelected(model.hasToolOption(IToolOption.TOOLOPTION_BENCHMARK));
		toolOptionBenchmark.setBackground(Color.WHITE);

		JPanel toolOptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		toolOptionPanel.setBackground(Color.WHITE);
		toolOptionPanel.add(toolOptionBenchmark);
		toolOptionPanel.add(toolOptionIntrospector);
		toolOptionPanel.add(toolOptionSniffer);
		toolOptionPanel.add(toolOptionLogger);

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);
		
		// prepare Main-Panel
		attributePanel.add(new JLabel(ImageIconLoader.createImageIcon(ImageIconLoader.AGENTINSTANCE)), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("<html><h2>General Settings</h2>Here, you see all the main-settings for this agent-instance.</html>"), new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Instance-Name:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textInstanceName, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Type-Name:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textTypeName, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Tool-Options:"), new GridBagConstraints(0, 3, 1, 1, 0, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(toolOptionPanel, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		/*attributePanel.add(toolOptionIntrospector, new GridBagConstraints(0, 4, 1, 1, 0.5, 0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(toolOptionSniffer, new GridBagConstraints(0, 5, 1, 1, 0.5, 0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(toolOptionLogger, new GridBagConstraints(0, 6, 1, 1, 0.5, 1, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
        */
		return attributePanel;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == toolOptionSniffer)
		{
			if (toolOptionSniffer.isSelected())
				model.addToolOption(IToolOption.TOOLOPTION_SNIFF);
			else
				model.removeToolOption(IToolOption.TOOLOPTION_SNIFF);
		}
		else if (evt.getSource() == toolOptionLogger)
		{
			if (toolOptionLogger.isSelected())
				model.addToolOption(IToolOption.TOOLOPTION_LOG);
			else
				model.removeToolOption(IToolOption.TOOLOPTION_LOG);
		}
		else if (evt.getSource() == toolOptionIntrospector)
		{
			if (toolOptionIntrospector.isSelected())
				model.addToolOption(IToolOption.TOOLOPTION_INTROSPECTOR);
			else
				model.removeToolOption(IToolOption.TOOLOPTION_INTROSPECTOR);
		}
		else if (evt.getSource() == toolOptionBenchmark)
		{
			if (toolOptionBenchmark.isSelected())
				model.addToolOption(IToolOption.TOOLOPTION_BENCHMARK);
			else
				model.removeToolOption(IToolOption.TOOLOPTION_BENCHMARK);
		}
		else if (evt.getSource() == buttonStartInstance)
		{
            try
			{
				// create the model
				IAbstractRunnable[] runnableModels = (IAbstractRunnable[])getRepository().getRunnableManager().createRunnable(model.getName(), model, 1);

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
