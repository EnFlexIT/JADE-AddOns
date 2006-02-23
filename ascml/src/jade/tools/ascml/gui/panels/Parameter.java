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
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import jade.tools.ascml.gui.models.ParameterTableModel;
import jade.tools.ascml.absmodel.IAgentInstance;
import jade.tools.ascml.absmodel.IAgentType;

public class Parameter extends AbstractPanel implements ActionListener
{

	private jade.tools.ascml.absmodel.IParameter[] parameter;
	private JTable table;
	
	/**
	 * @param model the AgentModel to show in the dialog, this may be an
	 *              AgentInstanceModel or an AgentTypeModel
	 */
	public Parameter(AbstractMainPanel mainPanel, Object model)
	{
		super(mainPanel);

        // toDo: repository.addModelChangedListener(this);
		// initialize model-objects
		if(model instanceof IAgentType)
		{
			this.parameter = ((IAgentType)model).getParameters();
		}
		else
		{
			this.parameter = ((IAgentInstance)model).getParameters();;
		}

		// set global layout-parameters
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());
		
		// create the constraints for this layout
		// this.add(new JLabel("<html><h2>Parameter</h2>&nbsp;&nbsp;Here, you may edit or add parameters, that are passed to the agent at startup.<br>&nbsp;&nbsp;Just edit the table-cells below or add a new parameter in the empty table-row.<br></html>"), BorderLayout.NORTH);
		this.add(new JLabel("<html><h2>&nbsp;Parameter</h2></html>"), BorderLayout.NORTH);

		// ... and add the content step by step with special constraint-options
		this.add(createParameterPanel(), BorderLayout.CENTER);
	}
	
	private JPanel createParameterPanel()
	{
		// prepare parameterTable and scrollPane
		table = new JTable(new ParameterTableModel(parameter, false));
		table.setBackground(Color.WHITE);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		initColumnSizes();
		
		JPanel paramTablePanel = new JPanel(new BorderLayout());
		paramTablePanel.setBackground(Color.WHITE);
		paramTablePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		paramTablePanel.add(table, BorderLayout.CENTER);

		return paramTablePanel;
	}

	/*
	* This method picks good column sizes.
	* If all column heads are wider than the column's cells'
	* contents, then you can just use column.sizeWidthToFit().
	*/
	private void initColumnSizes() 
	{
		ParameterTableModel model = (ParameterTableModel)table.getModel();
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
			
			Class columnClass = model.getColumnClass(i);
			TableCellRenderer tableRenderer = table.getDefaultRenderer(columnClass);
			comp = tableRenderer.getTableCellRendererComponent(table, table.getModel().getValueAt(0, i), false, false, 0, i);
			cellWidth = comp.getPreferredSize().width;
			
			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}
	
	// ---------- actionListener-methods --------------
	public void actionPerformed(ActionEvent e)
	{
		/*String action = e.getActionCommand();
		ModelActionEvent actionEvent = null;

		if(action.equals("editparameter"))
		{
			// create new ParameterDialog
			String parameterName = action.substring(action.indexOf(':')+1, action.length());

			if(instanceModel==null)
			{
				ParameterDialog dialog = new ParameterDialog(this, model);
			}
			else
			{
				ParameterDialog dialog = new ParameterDialog(this, instanceModel);
			}
		}
		else if(action.equals("debug"))
		{
			if(instanceModel==null)
			{
				JOptionPane.showMessageDialog(this.getParent(),
						model.toFormattedString());
			}
			else
			{
				JOptionPane.showMessageDialog(this.getParent(),
						instanceModel.toFormattedString());
			}
		}*/
	}
}
