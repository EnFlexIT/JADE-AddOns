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

/**
 * The Authors
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra </a>
 * @author <a href="mailto:Jordi.Cucurull@uab.es">Jordi Cucurull Juan</a>
 *  
 */

package samples.mobilegui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

public class ItineraryGui {
	public ItineraryGui(ActionListener listener){
		_listener = listener;
	}
	
	public Vector getTableContent(){
		return ((ItineraryTableModel)_table.getModel()).getRows();
	}
	
	public String getTextToDisplay(){
		try{
			return _jtf.getText();
		}catch(NullPointerException ne){
			return "";
		}
	}
   
        public boolean getUseGUI() {
        
	       try
	       {
		  return _jcb.isSelected();
	       }
	       catch(NullPointerException ne)
	       {
		  return false;
	       }
	   
	}
   
	
	public void display(){
		//JFrame.setDefaultLookAndFeelDecorated(true);

	   //Create and set up the window.
	   frame.setSize(550,500);
	   frame.setLocation(350,250);
	   frame.setBackground(Color.WHITE);
	   //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
	   frame.getContentPane().setLayout(new GridLayout(3,1));
		
	   final JPanel textP = new JPanel();
	   final JLabel jlb = new JLabel("Text to display: ");
	   
	   final JButton jbMove = new JButton();
	   final JButton jbExit = new JButton();
	   _jtf.setPreferredSize(new Dimension(160,20));
   	   jbMove.setPreferredSize(new Dimension(90,30));
   	   jbExit.setPreferredSize(new Dimension(90,30));
	   _jcb.setBackground(Color.WHITE);
	   textP.setBackground(Color.WHITE);	
	   textP.add(jlb);
	   textP.add(_jtf);
			
	   jbMove.addActionListener(_listener);
	   jbMove.setText("Move");
		
	   jbExit.setText("Exit");
	   jbExit.addActionListener(new ActionListener()
	     {
		public void actionPerformed(ActionEvent e) 
		  {
		     //System.exit(0);
		     hide();
		  }
		
	     });
				    
	   final JPanel checkBoxP = new JPanel();
	   checkBoxP.setBackground(Color.WHITE);
	   checkBoxP.add(_jcb);
	   
	   final JPanel buttonP = new JPanel();
	   buttonP.setBackground(Color.WHITE);
	   buttonP.add(jbMove);
	   buttonP.add(jbExit);

	   final JPanel inf = new JPanel();
           inf.setLayout(new BoxLayout(inf,BoxLayout.Y_AXIS)/*new GridLayout(3,2)*/);
           inf.setBackground(Color.WHITE);
           inf.setBorder(BorderFactory.createTitledBorder("Display Message"));
	   inf.add(textP);
	   inf.add(checkBoxP);
	   inf.add(buttonP);
		
	   final JButton addRow = new JButton();
	   addRow.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			((ItineraryTableModel)_table.getModel()).addEmptyRow();
		}
	   });
	   addRow.setText("New Hop");
		
	   final JPanel sup = new JPanel();
	   sup.setLayout(new BoxLayout(sup,BoxLayout.Y_AXIS));
           JScrollPane scp = new JScrollPane(_table);
           scp.setPreferredSize(new Dimension(600,400));
	   sup.setBorder(BorderFactory.createTitledBorder("Agent's Itinerary"));
	   sup.setBackground(Color.WHITE);
	   sup.add(scp);
	   sup.add(addRow);
		
           frame.getContentPane().add(sup);
  	   frame.getContentPane().add(inf);
	   frame.getContentPane().add(createTextArea());

           frame.setVisible(true);
	}
	
	private static class ItineraryTableModel extends AbstractTableModel{
		
		public ItineraryTableModel(){
			super();
			_columnes = new String[]{"Machine Name / IP", "AMS AID", "Platform Transport Address"};
			String[] r0 = new String[]{"hostname", "ams@hostname:1099/JADE", "http://hostname:7778/acc"};
			_rows = new Vector();
			_rows.add(r0);
			addTableModelListener(new TListener(this));
		}
		
		public String getColumnName(int column) {
			if(column<_columnes.length)
				return _columnes[column];
			else
				return ""; // Throw an error
		}
		
		public int getColumnCount() {
			return _columnes.length;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}
		
		public String getHostName(int row){
			String[] data = (String[])_rows.get(row);
			return data[0];
		}
		
		public Vector getRows(){
			return _rows;
		}
		
		public void updateHop(String hostName, String aid, String addr, int row){
			String[] data = new String[]{hostName, aid, addr};
			_rows.set(row,data);
			fireTableRowsUpdated(row,row);
		}

		public int getRowCount() {
			return _rows.size();
		}
		
		public void addEmptyRow(){
			_rows.add(new String[]{"","",""});
			this.fireTableRowsInserted(_rows.size()-1,_rows.size()-1);
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			String[] row = (String[])_rows.get(rowIndex);
			if (row != null)
				return row[columnIndex];
			else 
				return null;
		}
		
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(rowIndex<_rows.size() && columnIndex < _columnes.length){
				String[] row = (String[])_rows.get(rowIndex);
				row[columnIndex] = aValue.toString();
				fireTableCellUpdated(rowIndex,columnIndex);
			}
		}
	
		private String[] _columnes;
		private Vector _rows;
	}
	
	private static class TListener implements TableModelListener{
		public TListener(ItineraryTableModel t){
			_tableModel = t;
		}
		public void tableChanged(TableModelEvent e) {
			if(TableModelEvent.UPDATE == e.getType() && e.getColumn() == 0)
			{
				String hostName = _tableModel.getHostName(e.getFirstRow());
				_tableModel.updateHop(hostName,"ams@"+hostName+":1099/JADE","http://"+hostName+":7778/acc",e.getFirstRow());
			}
		}
		private ItineraryTableModel _tableModel;
	}
	
	private JTable createTable(){
		JTable table = new JTable(new ItineraryTableModel());
		int[] sizes = new int[]{60,100,100};
		for(int i=1; i<3; i++){
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(sizes[i]);
			column.setMinWidth(sizes[i]);
		}
		table.setPreferredScrollableViewportSize(new Dimension(600,400));
		return table;
	}
	
	private JTextArea createTextArea(){
		JTextArea jtext = new JTextArea();
	        jtext.setPreferredSize(new Dimension(400,200));
		jtext.setText(HELP_TEXT);
		jtext.setEditable(false);
		jtext.setBorder(BorderFactory.createTitledBorder("About Itinerant Agent"));
		return jtext;
	}
   
        public void hide() {
          frame.setVisible(false);
	}


   	private final JFrame frame = new JFrame("Itinerant Mobile Agent");
	private final ActionListener _listener;
	private final JTextField _jtf = new JTextField();
        private final JCheckBox _jcb = new JCheckBox("Use GUI to display message");
	private final JTable _table = createTable();
	private final String HELP_TEXT = "This Itinerant Agent is a kind of mobile agent\n" +
    						  "that moves across a list of pre-established agent\n" +
    						  "platforms showing some text in the console of each one. \n" +
    						  "In order to use this agent you must previously be sure that\n" +
    						  "all the platforms of its itinerary have been correctly started" +
    						  "and that inter-platform mobility service has also been loaded" +
    						  "in each one.\n" + 
    						  "In order to specify the agent itinerary you must create ";

}
