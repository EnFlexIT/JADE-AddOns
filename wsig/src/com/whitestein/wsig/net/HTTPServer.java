/*
 * Created on Jun 18, 2004
 *
 */

/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is WebService Integration Gateway (WSIG).
 *
 * The Initial Developer of the Original Code is
 * Whitestein Technologies AG.
 * Portions created by the Initial Developer are Copyright (C) 2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Jozef Nagy (jna at whitestein.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.whitestein.wsig.net;

import java.lang.Thread;
import java.lang.ThreadGroup;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.log4j.Category;

import com.whitestein.wsig.Configuration;

/**
 * Provides simple HTTP server for SOAP messages.
 * 
 * @author jna
 *
 */
public class HTTPServer extends Thread{

	private boolean isRunning = true;
	private ServerSocket server;
	private int timeout = 10000;
	private ThreadGroup group = null;
	private String connectionClass = "com.whitestein.wsig.net.Connection";
	private Category cat = Category.getInstance( HTTPServer.class.getName());
	private String hostName = null;
	private String queryPath = null;
	private String publishPath = null;
	private String accessPointPath = null;
	
	private void setup() {
	    queryPath = Configuration.getInstance().getQueryManagerPath();
	    publishPath = Configuration.getInstance().getLifeCycleManagerPath();
	    accessPointPath = Configuration.getInstance().getAccessPointPath();
	}

	/**
	 * creates new server based on given ServerSocket.
	 * A connection class is used to create a new connection arrived.
	 *  
	 * @param server base server socket
	 * @param connectionClass a class for a new connection
	 */
	public HTTPServer( ServerSocket server, String connectionClass ) {
		this.server = server;
		this.connectionClass = connectionClass;
		setup();
		this.start();
	}
	
	/**
	 * creates new server based on given ServerSocket.
	 *  
	 * @param server base server socket
	 */
	public HTTPServer( ServerSocket server ) {
		this.server = server;
		setup();
		this.start();
	}
	
	/**
	 * cretes new server listenning on any free available network port
	 *
	 */
	public HTTPServer() {
		server = createServerSocket();
		setup();
		this.start();
	}
	
	///**
	// * sets a class name used for connections.
	// * The class must to derive/implement Connection class and to implement Runnable interface.
	// * 
	// * @param className a full class name
	// */
	//public void setConnectionClass( String className ) {
	//	connectionClass = className;
	//}
	
	/**
	 * finds and creates the first available ServerSocket
	 * 
	 * @return null pointer is returned when there is not available port.
	 *
	 */
	public static ServerSocket createServerSocket() {
		try {
			// create a server on any free network port
			return new ServerSocket(0);
		} catch (IOException e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * returns a server socket used
	 * @return
	 */
	public ServerSocket getServerSocket() {
		return server;
	}
	
	
	/**
	 * sets a host name
	 * @param hostName
	 */
	public void setHostName( String hostName) {
	    this.hostName = hostName;
	}
	
	/**
	 * returns a host name
	 * @return
	 */
	public String getHostName() {
	    if ( null != hostName ) {
	        return hostName;
	    }else {
	        return server.getInetAddress().getCanonicalHostName();
	    }
	}
	
	/**
	 * stop execution of this server
	 * 
	 */
	public void close() {
		isRunning = false;
	}
	
	/**
	 * serve the socket connection
	 * 
	 * @param socket connection to serve
	 */
	private void serveConnection( Socket socket ) {
		cat.debug("A new HTTP request is going to serve.");
		Connection c;
		try {
			c = (Connection) Class.forName(connectionClass).newInstance();
			c.setSocket( socket );
			c.setHTTPServer( this );
			//c = new Connection(socket, this);
			(new Thread( c )).start();
			//(new Thread( group, new Connection(socket))).start();
		}catch (Exception e) {
			cat.error(e);
		}
	}
	
	/**
	 * makes the server to serve in a thread
	 */
	public void run(){
		cat.debug("HTTP server is started.");
		Socket socket;
		isRunning = server != null;
		try {
			server.setSoTimeout( timeout );
		}catch ( SocketException e ) {
			cat.error(e);
			isRunning = false;
		}
		try {
			group = new ThreadGroup("Connections");
		}catch (SecurityException e) {
			cat.error(e);
			isRunning = false;
		}
		while (isRunning) {
			try {
				socket = server.accept();
				serveConnection( socket );
			}catch (SocketTimeoutException ste){
				// nothing bad is done, only test isRunning variable
			}catch (Exception e) {
				cat.error(e);
			}
		}
		try {
			server.close();
		} catch ( IOException ioe ) {
			cat.error(ioe);
		}
			
	}
	
	/**
	 * finds a handler.
	 * A new instance is returned.
	 *
	 * @param absolutePath a searching criteria
	 * @return a new instance of the handler
	 */
	public SOAPHandler findHandler( String absolutePath ) {
		SOAPHandler h = null;
		
		cat.debug("A HTTPServer is finding a handler for an absolute path " + absolutePath );
		
		// find a handler according a request's path
		//   do also startWith() search in the future
		if ( accessPointPath.equalsIgnoreCase( absolutePath )) {
			h = (SOAPHandler) new WSClient();
		}else if ( queryPath.equalsIgnoreCase( absolutePath )) {
			// query is sent transparently
			Redirector r = new Redirector();
			try {
				r.setEndPoint(new URL(Configuration.getInstance().getQueryManagerURL()));
				h = (SOAPHandler) r;
			}catch (MalformedURLException mfe) {
				h = null;
			}
		}else if ( publishPath.equalsIgnoreCase( absolutePath )) {
			// usefull informations may be appeared in a request
			
			UDDIRegistrationHandler r = new UDDIRegistrationHandler();
			try {
				r.setEndPoint(new URL(Configuration.getInstance().getLifeCycleManagerURL()));
				h = (SOAPHandler) r;
			}catch (MalformedURLException mfe) {
				h = null;
			}
		}
		return h;
	}
}
