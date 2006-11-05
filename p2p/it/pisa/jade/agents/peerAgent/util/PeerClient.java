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
