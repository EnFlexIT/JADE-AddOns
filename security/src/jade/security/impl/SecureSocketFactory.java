package jade.security.impl;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.Socket;
import java.net.ServerSocket;

import java.io.IOException;
import java.io.Serializable;
import java.io.FileInputStream;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

import java.security.KeyStore;

import javax.security.cert.X509Certificate;

import javax.net.*;
import javax.net.ssl.*;

import com.sun.net.ssl.*;


public class SecureSocketFactory implements RMIClientSocketFactory, RMIServerSocketFactory, Serializable {

	/**
		Creates the client socket, which will be used
		to instantiate a <code>UnicastRemoteObject</code>.
		@param host The host to connect to.
		@param port The port to connect to.
		@return The client socket.
	*/
	public Socket createSocket(String host, int port) throws IOException {
    SSLSocketFactory factory = null;
    /*try {
			System.setProperty("javax.net.ssl.trustStore", "trustkeys");

			javax.net.ssl.SSLContext ctx;
			javax.net.ssl.KeyManagerFactory kmf;
			KeyStore ks;
			char[] passphrase = "passphrase".toCharArray();

			ctx = javax.net.ssl.SSLContext.getInstance("TLS");
			kmf = javax.net.ssl.KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream("clientkeys"), passphrase);

			kmf.init(ks, passphrase);
			ctx.init(kmf.getKeyManagers(), null, null);

			factory = ctx.getSocketFactory();
    }
    catch (Exception e) {
			throw new IOException(e.getMessage());
    }*/
		factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
    return factory.createSocket(host, port);
	}

	/**
		Creates the server socket, which will be used
		to instantiate a <code>UnicastRemoteObject</code>.
		@param port The port to listen on.
		@return The server socket.
	*/
	public ServerSocket createServerSocket(int port) throws IOException { 
		SSLServerSocketFactory ssf = null;
		try {
			System.setProperty("javax.net.ssl.trustStore", "trustkeys");

			// set up key manager to do server authentication
			javax.net.ssl.SSLContext ctx;
			javax.net.ssl.KeyManagerFactory kmf;
			KeyStore ks;
			char[] passphrase = "passphrase".toCharArray();
			
			ctx = javax.net.ssl.SSLContext.getInstance("TLS");
			kmf = javax.net.ssl.KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance("JKS");
			
			ks.load(new FileInputStream("serverkeys"), passphrase);
			kmf.init(ks, passphrase);
			ctx.init(kmf.getKeyManagers(), null, null);
			
			ssf = ctx.getServerSocketFactory();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		ServerSocket ss = ssf.createServerSocket(port);
		//((SSLServerSocket)ss).setNeedClientAuth(true);
		return ss;
	}

}

