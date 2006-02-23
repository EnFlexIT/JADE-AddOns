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
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import jade.tools.ascml.absmodel.*;

public class ReferenceGeneral extends AbstractPanel
{
    private JTable addressTable;
    private ISocietyInstanceReference model;

	public ReferenceGeneral(AbstractMainPanel mainPanel, ISocietyInstanceReference model)
	{
		super(mainPanel);
        this.model = model;

		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

		this.add(new JLabel("<html><h2>&nbsp;<i>Remote SocietyInstance-Reference</i></h2></html>"), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		this.add(new JLabel("Reference-Name: "), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JTextField(model.getName()), new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		this.add(new JLabel("Referenced-SocietyType: "), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JTextField(model.getTypeName()), new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		this.add(new JLabel("Referenced-SocietyInstance: "), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JTextField(model.getInstanceName()), new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		this.add(new JLabel("<html>&nbsp;</html>"), new GridBagConstraints(0, 4, 2, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		this.add(new JLabel("Launcher-Name: "), new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JTextField(model.getLauncherName()), new GridBagConstraints(1, 5, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		this.add(createAddressTable(model.getLauncherAddresses()), new GridBagConstraints(0, 6, 2, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
	}
	
	private JScrollPane createAddressTable(String[] launcherAddresses)
	{
		DefaultTableModel addressTableModel = new DefaultTableModel(new String[] {"Addresses"}, 0);

		for(int i=0; i < launcherAddresses.length; i++)
		{
			String[] oneRow = new String[1];
            oneRow[0] = launcherAddresses[i];
			addressTableModel.addRow(oneRow);
		}

		addressTable = new JTable(addressTableModel);
		addressTable.setRowSelectionAllowed(false);
		addressTable.setColumnSelectionAllowed(false);
		// initColumnSizes();

		JPanel addressTablePanel = new JPanel(new BorderLayout());
		addressTablePanel.setBorder(BorderFactory.createTitledBorder(" Launcher-Addresses "));
		addressTablePanel.setBackground(Color.WHITE);
		addressTablePanel.add(addressTable.getTableHeader(), BorderLayout.PAGE_START);
		addressTablePanel.add(addressTable, BorderLayout.CENTER);

		JScrollPane tableScrollPane = new JScrollPane(addressTablePanel);
		tableScrollPane.setWheelScrollingEnabled(true);
		tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

		return tableScrollPane;
	}

	/*
	* This method picks good column sizes.
	* If all column heads are wider than the column's cells'
	* contents, then you can just use column.sizeWidthToFit().
	*/
	private void initColumnSizes()
	{
		DefaultTableModel model = (DefaultTableModel)addressTable.getModel();
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;

		TableCellRenderer headerRenderer = addressTable.getTableHeader().getDefaultRenderer();

		for (int i = 0; i < addressTable.getColumnCount(); i++)
		{
			column = addressTable.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;

			Class columnClass = model.getColumnClass(i);
			TableCellRenderer tableRenderer = addressTable.getDefaultRenderer(columnClass);
			comp = tableRenderer.getTableCellRendererComponent(addressTable, addressTable.getModel().getValueAt(0, i), false, false, 0, i);
			cellWidth = comp.getPreferredSize().width;

			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}
}
