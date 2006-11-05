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
package it.pisa.jade.agents.peerAgent.gui;

/*
 * Created on 5-set-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


import it.pisa.jade.agents.peerAgent.ontologies.Found;

import java.util.LinkedList;

import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 * 
 * @author Fabrizio Marozzo
 * @author Domenico Trimboli
 *
 */
@SuppressWarnings("serial")
public class ResearchPane extends JScrollPane {
    private JList jList = null;

    String id = null;

    private FramePeerAgent frame = null;

    /**
     * This is the default constructor
     */
    public ResearchPane(String id) {
        super();
        this.id = id;
        initialize();
    }

    public ResearchPane(String s, FramePeerAgent frame) {
        super();
        this.id = s;
        this.frame = frame;
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setViewportView(getJList());
        this.setSize(380, 200);
    }

    /**
     * This method initializes jList
     * 
     * @return javax.swing.JList
     */
    private JList getJList() {
        if (jList == null) {
            jList = new JList();
            jList.setCellRenderer(new MyCellRenderer());
            jList.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() > 1) {
                        System.out.println("mouseDoubleClicked"); 
                        int pos = jList.getSelectedIndex();
                        if (pos == -1)
                            return;
                        frame.chooseFile(id, pos);
                    }
                }
            });
        }
        return jList;
    }

    void setListData(Found[] info) {
        jList.setListData(info);
    }

    void setListData(LinkedList info) {
        jList.setListData(info.toArray());
    }

} 