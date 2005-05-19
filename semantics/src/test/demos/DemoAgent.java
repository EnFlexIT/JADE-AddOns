/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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

/*
 * NiceDemoAgent.java
 * Created on 14 déc. 2004
 * Author : Vincent Pautret
 */
package test.demos;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.util.leap.HashMap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Class that represents a classical jade agent. It is possible to specify 
 * contents and receivers for the messages to send. A text area shows the 
 * received messages. 
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class DemoAgent extends Agent 
{	
    /*********************************************************************/
    /**				 			INTERNALS	**/
    /*********************************************************************/
    /**
     * The associated gui
     */
    DemoAgentGui gui = null;

    /*********************************************************************/
    /**				 			METHODS		**/
    /*********************************************************************/
    /**
     * Setup og this agent
     */
    public void setup() {
    	Object[] args = getArguments();
	if ( args == null || args.length < 1 ) {
	    System.err.println("ERROR : agent should be run like this <agent name>:DemosAgent\\(<file_name>\\)");
	    System.err.print("ERROR : current args are :");
	    for (int i=0; i<args.length; i++) {
		System.err.print(args[i]+" ");
	    }
	    System.err.println();
	    System.exit(1);
	} else {
	    System.err.println("setup of DemoAgent("+ args[0]+")");
	    gui = new DemoAgentGui(getName(), args[0].toString(), this, false,  false);
	}
    } // End of setup/0
    
} // End of class 
