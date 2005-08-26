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
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelActionEvent;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.onto.*;

public class RunnableGeneral extends AbstractPanel implements ActionListener, ModelChangedListener
{
	private final static String RESTART	= "restart";
	private final static String START	= "start";
	private final static String STOP	= "stop";

	private JButton button;
    private ImageIcon lifeCycleIcon;
    private JLabel statusIconLabel;
	private IAbstractRunnable model;
    private JTextField textName;
    private JTextField textParentName;
    private JTextArea textDetailedStatus;

	/**
	 * @param model the AgentModel to show in the dialog, this may be an
	 *              AgentInstanceModel or an AgentTypeModel
	 */
	public RunnableGeneral(AbstractMainPanel mainPanel, IAbstractRunnable model)
	{
		super(mainPanel);
		this.model = model;
		init();
	}

	private void init()
	{
		this.removeAll();
		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		// ... and add the content (content is layout out in it's own panel)
		this.add(createAttributePanel(), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createAttributePanel()
	{
		button = new JButton();
		button.addActionListener(this);
        Status status = model.getStatus();

        initButton(status);
		// prepare instance name
		textName = new JTextField(model.getName(), 20);
		textName.setEditable(false);
		textName.setBackground(Color.WHITE);

		// prepare parentModel's name
		textParentName = new JTextField(model.getParentModel().toString(), 20);
		textParentName.setEditable(false);
		textParentName.setBackground(Color.WHITE);

		// prepare detailed status
		textDetailedStatus = new JTextArea(model.getDetailedStatus(), 5, 20);
		textDetailedStatus.setFont(new Font("Arial", Font.PLAIN, 12));
		textDetailedStatus.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		textDetailedStatus.setEditable(false);
		textDetailedStatus.setLineWrap(true);
		textDetailedStatus.setWrapStyleWord(true);
		textDetailedStatus.setBackground(Color.WHITE);

		initLifeCycleIcon(status);

		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(Color.WHITE);

        statusIconLabel = new JLabel(ImageIconLoader.createRunnableStatusIcon(status, 50, 50));
		mainPanel.add(statusIconLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(new JLabel("<html><h2>Runnable Instance</h2>Here, you see the status of this runnable Model.</html>"), new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(new JLabel("Instance-Name:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(textName, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(new JLabel("Type-Name:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(textParentName, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(new JLabel("Status-Details:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(textDetailedStatus, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(button, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        mainPanel.add(new JLabel(lifeCycleIcon), new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		return mainPanel;
	}

    private void initLifeCycleIcon(Status status)
    // These are String comparisons XXX FIXME
    {
        if (status instanceof Known)
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_CREATED);
        }
        else if (status instanceof Starting)
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_STARTING);
        }
        else if (status instanceof Functional)
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_RUNNING);
        }
        /*else if (status.equals(IAbstractRunnable.STATUS_PARTLY_RUNNING))
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_PARTLY_RUNNING);
        }*/
        else if (status instanceof Stopping)
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_STOPPING);
        }
        else if (status instanceof jade.tools.ascml.onto.Error)
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_ERROR);
        }
        /*else if (status.equals(IAbstractRunnable.STATUS_NOT_RUNNING))
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_NOT_RUNNING);
        }*/

        ImageIconLoader.scaleImageIcon(lifeCycleIcon, 300, 138);
    }

	private void initButton(Status status)
	{
		if (status instanceof Functional)
		{
			button.setText("Stop");
			button.setActionCommand(STOP);
		}
		/*else if (status == IAbstractRunnable.STATUS_NOT_RUNNING)
		{
			button.setText("Restart");
			button.setActionCommand(RESTART);
		}*/
		else if (status instanceof Known)
		{
			button.setText("Start");
			button.setActionCommand(START);
		}
		else if (status instanceof jade.tools.ascml.onto.Error)
		{
			button.setText("Try Restart");
			button.setActionCommand(RESTART);
		}

	}

    public void updateAllComponents(Status status)
    {
        // the elements may be null at first call
        if (textName != null)
        {
            textName.setText(model.getName());
            textParentName.setText(model.getParentModel().toString());
            textDetailedStatus.setText(model.getDetailedStatus());
            initButton(status);
	    initLifeCycleIcon(status);
            statusIconLabel.setIcon(ImageIconLoader.createRunnableStatusIcon(status, 50, 50));

            this.repaint();
        }
    }

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == button)
		{
			String cmd = evt.getActionCommand();
			if (cmd.equals(RESTART))
			{
				try
				{
					getRepository().getRunnableManager().addRunnable(model);
					model.setStatus(new Starting());
					model.setDetailedStatus("Restarting instance");
				}
				catch(ModelException me)
				{
					System.err.println("RunnableGeneral.actionPerformed: Warning, readded runnable failed.");
				}

				if (model instanceof IRunnableAgentInstance)
				{
					ModelActionEvent event = new ModelActionEvent(ModelActionEvent.CMD_START_AGENTINSTANCE, model);
					throwModelActionEvent(event);
				}
				else if (model instanceof IRunnableSocietyInstance)
				{
					ModelActionEvent event = new ModelActionEvent(ModelActionEvent.CMD_START_SOCIETYINSTANCE, model);
					throwModelActionEvent(event);
				}
				else
					System.err.println("RunnableGeneral.actionPerformed: try to restart " + model + " not supported");

				/*Object parentModel = model.getParentModel();
				if (parentModel instanceof ISocietyInstance)
				{
					mainPanel.showDialog(new StartSocietyInstanceDialog(mainPanel, (ISocietyInstance)parentModel));
				}
				else if (parentModel instanceof IAgentInstance)
				{
					model.setStatus(IAbstractRunnable.STATUS_CREATED);
					model.setDetailedStatus("Model is going to restart");

					mainPanel.selectModel(model);

					// create an ModelActionEvent to inform the ModelActionHandler that a new agent has to be started
					ModelActionEvent event = new ModelActionEvent(ModelActionEvent.CMD_START_AGENTINSTANCE, model);
					throwModelActionEvent(event);
				}
				else
				{
					System.err.println("RunnableGeneral: actionPerformed RESTART of "+parentModel+", implement me !!!");
				}*/
				/*model.getType().createRunnableInstance(model.getName(), model.getModelListener());
				ModelActionEvent event = new ModelActionEvent(ModelActionEvent.CMD_START_AGENTINSTANCE, model);
				throwModelActionEvent(event);
				parentPanel.selectModel(model);
				*/
			}
			else if (cmd.equals(START))
			{
				System.err.println("RunnableGeneral: actionPerformed START, implement me !!!");
				/*
				ModelActionEvent event = new ModelActionEvent(ModelActionEvent.CMD_START_AGENTINSTANCE, model);
				throwModelActionEvent(event);
				parentPanel.selectModel(model);
				*/
			}
			else if (cmd.equals(STOP))
			{
                ModelActionEvent actionEvent = null;

                if (model instanceof IRunnableAgentInstance)
                    actionEvent = new ModelActionEvent(ModelActionEvent.CMD_STOP_AGENTINSTANCE, model);
                else if (model instanceof IRunnableSocietyInstance)
                    actionEvent = new ModelActionEvent(ModelActionEvent.CMD_STOP_SOCIETYINSTANCE, model);

				mainPanel.throwModelActionEvent(actionEvent);
			}

		}
	}

	public void modelChanged(ModelChangedEvent event)
	{

        // System.err.println("RunnableGeneral.modelChanged: (" + counter + ") model = "+event.getModel() + " event=" + event.getEventCode());

        if ((event.getModel() == model))
		{
			updateAllComponents(model.getStatus());
		}
	}
}
