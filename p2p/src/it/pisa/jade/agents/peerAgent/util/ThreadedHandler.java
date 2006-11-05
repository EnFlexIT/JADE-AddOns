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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * 
 * @author Fabrizio Marozzo
 * @author Domenico Trimboli
 *
 */
public class ThreadedHandler extends Thread {
	private Socket incoming = null;
	private JFrame parent=null;

	public ThreadedHandler(Socket incoming, JFrame parent) {
		this.incoming = incoming;
		this.parent=parent;
	}

	@Override
	public void run() {
		try {
			System.out.println("Ricezione file in corso...");
			InputStream in = incoming.getInputStream();
			ObjectInputStream ois=new ObjectInputStream(in);
			File info=(File)ois.readObject();
						
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(new File(info.getName()));
			int ret = chooser.showSaveDialog(parent);
			if (ret == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				FileOutputStream out = new FileOutputStream(
						f);
				byte buffer[] = new byte[4096];
				int letti;

				while (true) {
					letti = in.read(buffer);
					if (letti > 0) {
						out.write(buffer, 0, letti);
					} else {
						break;
					}
				}
				out.flush();
				out.close();
				in.close();
				incoming.close();
				System.out.println("Ricezione completata!");

			}

		} catch (Exception e) {

		}
	}

}
