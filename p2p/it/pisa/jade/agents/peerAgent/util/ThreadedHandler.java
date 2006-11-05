/**
 * 
 */
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
