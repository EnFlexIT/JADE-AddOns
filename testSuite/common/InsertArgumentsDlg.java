package test.common;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import jade.util.leap.List;

public class InsertArgumentsDlg extends JDialog {

	private List argSpecs;
	private JButton okB;
	private JLabel[] labels;
	private JTextField[] values;
	private JTextField[] defaults;
	
	private InsertArgumentsDlg(Frame parent, List l) {
		super(parent);
		setModal(true);
		setTitle("Test group arguments");
		setResizable(false);
		
		// Text fields for inserting values in the CENTER part 
		argSpecs = l;
		final int nArgs = argSpecs.size();
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(nArgs+1, 3));
		p.add(new JLabel(""));
		p.add(new JLabel("Value"));
		p.add(new JLabel("Default"));
		
		labels = new JLabel[nArgs];
		values = new JTextField[nArgs];
		defaults = new JTextField[nArgs];
		for (int i = 0; i < nArgs; ++i) {
			ArgumentSpec a = (ArgumentSpec) argSpecs.get(i);
			labels[i] = new JLabel(a.getLabel());
			values[i] = new JTextField();
			values[i].setText(a.getValue());
			defaults[i] = new JTextField();
			defaults[i].setText(a.getDefaultValue());
			defaults[i].setEnabled(false);
			p.add(labels[i]);
			p.add(values[i]);
			p.add(defaults[i]);
		}
		
		getContentPane().add(p, BorderLayout.CENTER);
		
		// OK Button in the SOUTH part
		p = new JPanel();
		
		okB = new JButton("OK");
		okB.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e)
    		{
    			for (int i = 0; i < nArgs; ++i) {
    				ArgumentSpec a = (ArgumentSpec) argSpecs.get(i);
    				String v = values[i].getText();
    				System.out.println("Value in text field is "+v);
    				if (v != null && !v.equals("")) {
    					System.out.println("Use inserted value");
    					a.setValue(v);
    				}
    				else {
    					if (a.isMandatory()) {
    						System.out.println("Missing value for mandatory argument "+a.getLabel());
    						return;
    					}
    					else {
    						System.out.println("Use default value");
    						a.setValue(a.getDefaultValue());
    					}
    				}
    			}
    			
    			//okFlag = true;
    			InsertArgumentsDlg.this.dispose();
    		}
		} );
		p.add(okB);

		/*cancelB = new JButton("Annulla");
		cancelB.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e)
    		{
    			InsertArgumentsDlg.this.dispose();
    		}
		} );
		p.add(cancelB);

		// Adjust the buttons so that they have the same size
		okB.setPreferredSize(cancelB.getPreferredSize());*/
		
		getContentPane().add(p, BorderLayout.SOUTH);
	}
	
	public static void insertValues(List argSpecs) {
		InsertArgumentsDlg dlg = new InsertArgumentsDlg(null, argSpecs);
		dlg.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		dlg.setLocation(centerX - dlg.getWidth() / 2, centerY - dlg.getHeight() / 2);
		dlg.show();
	}
}
