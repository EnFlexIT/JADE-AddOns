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
package it.pisa.jade.agents.peerAgent.util;

import it.pisa.jade.util.WrapperErrori;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
/**
 * 
 * @author Fabrizio Marozzo
 * @author Domenico Trimboli
 *
 */
public class PeerServer extends Thread{
	private ServerSocket serverSocket;
	private int port;
	private JFrame parent=null;
	
	
	public PeerServer(int port, JFrame parent){
		this.port=port;
		this.setDaemon(true);
		this.parent=parent;
	}
	@Override
	public void run() {
		try {
			serverSocket=new ServerSocket(port);
			System.out.println("Start server on port: "+port);
			while(true){
				Socket incoming=serverSocket.accept();
				
				ThreadedHandler t=new ThreadedHandler(incoming, parent);
				t.start();
				
			}
		} catch (IOException e) {
			WrapperErrori.wrap("ServerSocket",
					e);
		}
		
	}

}
