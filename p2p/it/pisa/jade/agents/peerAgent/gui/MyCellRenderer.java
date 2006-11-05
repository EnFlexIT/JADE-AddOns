package it.pisa.jade.agents.peerAgent.gui;

/*
 * Created on 5-set-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import it.pisa.jade.agents.peerAgent.ontologies.Found;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * 
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class MyCellRenderer extends JLabel implements ListCellRenderer {
	private final static Font titleFont = new Font("SansSerif", Font.PLAIN, 16);

	private final static Font infoFont = new Font("SansSerif", Font.ITALIC, 12);

	private final static Font urlFont = new Font("SansSerif", Font.PLAIN, 12);

	private Found found = null;

	public MyCellRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		//Component c = this;

		found = (Found) value;
		this.setPreferredSize(new Dimension(10, 70));
		// setText(value.toString()+"\t"+" Fabrizio");
		setBackground(isSelected ? Color.lightGray : Color.white);
		// setForeground(isSelected ? Color.white : Color.black);
		// this.repaint();
		return this;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.blue);
		g2.drawRect(1, 1, this.getWidth() - 1, this.getHeight() - 5);
		g2.setColor(Color.blue);
		g2.setFont(titleFont);
		g2.drawString(found.getName(), 5, 20);
		g2.setColor(Color.black);
		g2.setFont(infoFont);
		String s = found.getSummary();
		String info1 = "", info2 = "";
		if (s != null) {
			if (s.length() > 90) {
				info1 = s.substring(0, 90);
				info2 = s.substring(90);
			} else {
				info1 = s;
				info2 = "";
			}
		}
		g2.drawString(info1, 10, 35);
		g2.drawString(info2, 10, 50);
		g2.setFont(urlFont);
		g2.drawString(found.getAgent() + " > " + found.getUrl(), 10, 65);
	}

}
