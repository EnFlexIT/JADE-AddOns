package it.pisa.jade.agents.peerAgent.util;

import it.pisa.jade.util.WrapperErrori;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
/**
 * 
 * @author Fabrizio Marozzo
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
