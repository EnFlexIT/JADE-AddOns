/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package test.common.testSuite.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GenericChooser {
	public GenericChooser() {
	}

	public int showChoiceDlg(Container parent,
    	String caption,
    	String okLabel,
    	String cancelLabel,
		String choices[]) {
		// FIXME 
		// Should throw an exception if choices == null
		if (choices == null) {
			throw new IllegalArgumentException("Null choices array");
		}
		
		final IntRetValue ret = new IntRetValue();
		ret.setValue(-1);
		
		// The dialog window
		final JDialog dlg = new JDialog((JFrame)parent, caption);

		// The data model holding the choices
		DefaultListModel lm = new DefaultListModel();
		for (int i = choices.length-1;i >= 0; --i) {
			lm.add(0, (Object) choices[i]);
		}
		// The JList visualizing the choices
		final JList l = new JList(lm);
    l.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
				JList list,
				Object value,            // value to display
				int index,               // cell index
				boolean isSelected,      // is the cell selected
				boolean cellHasFocus)    // the list and the cell have the focus
			{
				String s = value.toString();
				setText(s);
				setBackground(isSelected ? new Color(160,160,255) : Color.white);
				setForeground(Color.black);
				return this;
			}
    } );
    
    // The scroll pane used to scroll the list
		JScrollPane pane = new JScrollPane();
		pane.setPreferredSize(new Dimension(250, 300));
		pane.getViewport().setView(l);
		dlg.getContentPane().add(pane, BorderLayout.CENTER);

		// The buttons
		JPanel p = new JPanel();
		JButton bOk = new JButton(okLabel);
		JButton bCancel = new JButton(cancelLabel);
		if (okLabel.length() > cancelLabel.length()) {
			bCancel.setPreferredSize(bOk.getPreferredSize());
		}
		else {
			bOk.setPreferredSize(bCancel.getPreferredSize());
		}
		bOk.addActionListener(new	ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				int i = l.getSelectedIndex();
				ret.setValue(i);
				dlg.dispose();
			}	
		} );
		p.add(bOk);

		bCancel.addActionListener(new	ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dlg.dispose();
			}
		} );							
		p.add(bCancel);
		dlg.getContentPane().add(p, BorderLayout.SOUTH);

		/*JTextPane hlp = new JTextPane();
		hlp.setText(help);
		hlp.setEditable(false);
		hlp.setBackground(new Color(200,200,200));
		JScrollPane pane1 = new JScrollPane();
		pane1.setPreferredSize(new Dimension(150, 300));
		pane1.getViewport().setView(hlp);
		dlg.getContentPane().add(pane1, BorderLayout.EAST);*/
		
		dlg.setModal(true);
		dlg.setResizable(false);
		dlg.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		dlg.setLocation(centerX - dlg.getWidth() / 2, centerY - dlg.getHeight() / 2);
		dlg.show();

		return(ret.getValue());
	}

}
