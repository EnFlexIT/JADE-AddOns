/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB S.p.A. 

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

package jade.security.impl;

import java.io.*;
import java.net.*;
import java.rmi.server.*;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

public class SecureSocketFactory implements RMIClientSocketFactory, RMIServerSocketFactory, Serializable {

	transient SSLSocketFactory clientSocketFactory;
	transient SSLServerSocketFactory serverSocketFactory;

	public SecureSocketFactory() {
	}

	/**
		Creates the client socket, which will be used
		to instantiate a <code>UnicastRemoteObject</code>.
		@param host The host to connect to.
		@param port The port to connect to.
		@return The client socket.
	*/
	public Socket createSocket(String host, int port) throws IOException {
		//System.out.println("creating client socket (" + host + ", " + port + ")");
		if (clientSocketFactory == null) {
			//clientSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
	    try {
				if (System.getProperty("javax.net.ssl.keyStore") == null)
					System.setProperty("javax.net.ssl.keyStore", "keystore");
				if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
					System.setProperty("javax.net.ssl.keyStorePassword", "passphrase");
				if (System.getProperty("javax.net.ssl.trustStore") == null)
					System.setProperty("javax.net.ssl.trustStore", "truststore");

				char[] passphrase = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
				KeyStore ks = KeyStore.getInstance("JKS");
				ks.load(new FileInputStream(System.getProperty("javax.net.ssl.keyStore")), passphrase);

				KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, passphrase);
	
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(kmf.getKeyManagers(), null, null);
				clientSocketFactory = ctx.getSocketFactory();
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
				throw new IOException(e.getMessage());
	    }
    }
    return clientSocketFactory.createSocket(host, port);
	}

	/**
		Creates the server socket, which will be used
		to instantiate a <code>UnicastRemoteObject</code>.
		@param port The port to listen on.
		@return The server socket.
	*/
	public ServerSocket createServerSocket(int port) throws IOException { 
		System.out.println("creating SSL socket ");
		//System.out.println("creating server socket (" + port + ")");
		if (serverSocketFactory == null) {
			//serverSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			try {
				if (System.getProperty("javax.net.ssl.keyStore") == null)
					System.setProperty("javax.net.ssl.keyStore", "keystore");
				if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
					System.setProperty("javax.net.ssl.keyStorePassword", "passphrase");
				if (System.getProperty("javax.net.ssl.trustStore") == null)
					System.setProperty("javax.net.ssl.trustStore", "truststore");

				char[] passphrase = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
				KeyStore ks = KeyStore.getInstance("JKS");
				ks.load(new FileInputStream(System.getProperty("javax.net.ssl.keyStore")), passphrase);

				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, passphrase);

				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(kmf.getKeyManagers(), null, null);
				serverSocketFactory = ctx.getServerSocketFactory();
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
		}

		ServerSocket serverSocket = serverSocketFactory.createServerSocket(port);
		((SSLServerSocket)serverSocket).setNeedClientAuth(true);
		return serverSocket;
	}

}

