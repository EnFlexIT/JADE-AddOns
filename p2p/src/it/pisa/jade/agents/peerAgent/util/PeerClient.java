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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
/**
 * 
 * @author Fabrizio Marozzo
 * @author Domenico Trimboli
 *
 */
public class PeerClient extends Thread {
	private File f=null;
	private InetAddress server=null;
	private int port;
	
	public  PeerClient(File f, InetAddress server, int port){
		this.f=f;
		this.server=server;
		this.port=port;
	}

	public void run() {
		try{
		System.out.println("Connessione...");
		Socket s = new Socket(server, port);

		OutputStream out = s.getOutputStream();
		ObjectOutputStream oos=new ObjectOutputStream(out);
		oos.writeObject(f);
		FileInputStream in = new FileInputStream(f);
		byte buffer[] = new byte[4096];
		long totale = 0L;

		System.out.println("Invio file in corso...");
		while (true) {
			try {
				int letti = in.read(buffer);
				if (letti > 0) {
					out.write(buffer, 0, letti);
					totale += letti;
				} else {
					break;
				}
			} catch (IOException e) {
				break;
			}
		}
		in.close();
		out.flush();
		out.close();
		s.close();

		System.out.println("Invio file terminato [" + totale
				+ " bytes inviati]");
		}catch(Exception e){
			WrapperErrori.wrap("Failed registering with DF! Shutting down...",
					e);
		}
	}

}
