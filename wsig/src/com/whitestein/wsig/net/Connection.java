/*
 * Created on Aug 12, 2004
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

import java.net.*;
import java.io.*;
import java.util.*;
import javax.xml.soap.*;
import org.apache.axis.Message;
import org.apache.log4j.Category;

import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.struct.*;
import com.whitestein.wsig.test.TestAgent001;
import com.whitestein.wsig.ws.*;
//import com.whitestein.wsigs.*;

//import org.apache.axis.transport.http.HTTPConstants;

/**
 * @author jna
 *
 * serves one HTTP connection
 */
public class Connection implements Runnable, ReturnMessageListener {
	
	protected boolean isRunning = false;
	protected Socket socket;
	private Writer writer;
	private CalledMessage returnedMessage;
	private int timeOut = 0;
	private Category cat = Category.getInstance(Connection.class.getName());
	private int buff_size = 255;
	//private boolean isListenerInformed = false;
	private Call call;
	protected HTTPServer httpServer = null;
	
	// http constants
	public static final String HTTP_GET = "GET";
	public static final String HTTP_METHOD = "method";
	public static final String HTTP_LOCATION = "location";
	public static final String HTTP_VERSION = "version";
	public static final String HTTP_LAST = "last";
	
	/**
	 * creates an connection.
	 * The socket is used for a communication.
	 * 
	 * @param socket a network connection
	 */
	public Connection( Socket socket, HTTPServer httpServer) {
		setSocket(socket);
		setHTTPServer(httpServer);
	}
	
	public Connection() {
	}

	/**
	 * sets a socket served
	 * 
	 * @param socket
	 */
	public synchronized void setSocket( Socket socket ) {
		if ( ! isRunning ) {
			this.socket = socket;
			setIsRunning( this.socket != null );
		}
	}
	
	/**
	 * sets a HTTPServer as an originator
	 * 
	 * @param httpServer
	 */
	public synchronized void setHTTPServer( HTTPServer httpServer ) {
		//if ( ! isRunning ) {
			this.httpServer = httpServer;
			//setIsRunning( this.httpServer != null );
		//}
	}
	
	protected synchronized void setIsRunning( boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	/**
	 * sets message returned by a service
	 * 
	 * @param retMsg message returned
	 */
	public synchronized void setReturnedMessage( CalledMessage retMsg ) {
		returnedMessage = retMsg;
		//isListenerInformed = true;
		this.notifyAll(); // weak up waiters
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		WSMessage msg = (WSMessage) returnedMessage;
		try {
			msg.getSOAPMessage().writeTo(baos);
		}catch (Exception e) {
			cat.error(e);
		}
		cat.debug("A SOAP returned to a client: " + baos.toString());

	}
	
	/*
	 * returns value of the isListenerInformed
	 * 
	 * @return
	 */
	//private boolean isListenerInformed() {
	//	return isListenerInformed;
	//}
	
	
	/**
	 * splits input string on spaces
	 * 
	 * @param str a string to split
	 * @return string's parts
	 */
	public static String[] split( String str ) {
		if ( str != null ) {
			return str.split( "\\s" );
		}else {
			return new String[0];
		}
	}
	
	/**
	 * parses the first line in HTTP request.
	 * Fields are stored into properties.
	 * 
	 * @param line the first line
	 * @param prop properties affected
	 */
	public static void parseTheFirstLine( String line, Properties prop ) {
		String[] part = split(line);
		if ( part.length != 3 ) {
			return;
		}
		prop.put( HTTP_METHOD, part[0]);
		prop.put( HTTP_LOCATION, part[1]);
		prop.put( HTTP_VERSION, part[2]);
	}
	
	/**
	 * parse header's lines.
	 * Lines are stored into equivalent properties. 
	 * 
	 * @param line a line to parse
	 * @param prop properties' store
	 */
	private void parse(String line, Properties prop) {
		String str;
		if ( line.startsWith("\t") || line.startsWith(" ")) {
			// a line is a continuation
			str = prop.getProperty( prop.getProperty( HTTP_LAST ));
			str += line;
			prop.put( prop.getProperty(HTTP_LAST), str );
		}
		int pos = line.indexOf(":");
		if ( pos < 1 || pos >= line.length()) {
			return;
		}
		// store a property
		prop.put( line.substring(0,pos), line.substring(pos+1).trim() );
		// store a last line at a case, when next lines are continuations
		prop.put(HTTP_LAST, line.substring(0,pos));
	}
	
	/**
	 * reads line from imput stream
	 * 
	 * @param is a source for lines
	 * @return a line
	 * @throws IOException if reading error is occured
	 */
	private String readLine( InputStream is ) throws IOException {
		int r;
		String res = new String();
		byte buff[] = new byte[1];
		while ( (r = is.read()) >= 0 ) {
			// carriage return is a start of the line terminator
			if ( r != '\r' ) {
				buff[0] = (byte) r;
				try {
					res += new String( buff, 0, 1, "US-ASCII");
				}catch ( Exception e ) {
					cat.error(e);
				}
			}else {
				// an end of line
				is.read();   // one '\n' is expected
				return res;
			}
		}
		return res;
	}
	
	/**
	 * reads header's lines
	 * 
	 * @param is a source
	 * @return properties gathered
	 * @throws IOException if reading error is occured
	 */
	public Properties readHeader( InputStream is ) throws IOException {
		String line;
		Properties prop = new Properties();
		// simply read line, ASCII bytes expected are converted to chars
		line = readLine(is);
		parseTheFirstLine(line, prop);
		//cat.debug( "A line received: " + line );
		
		// rest lines
		line = readLine(is);
		while( line.length() > 0) {
			//cat.debug( "A line received: " + line );
			parse(line, prop);
			line = readLine(is);
		}
		return prop;
	}
	
	/**
	 * proces body of a request
	 * 
	 * @param is a source
	 * @param prop header's informations
	 * @return a SOAP message built
	 * @throws IOException if an input stream reading error is occured
	 */
	public SOAPMessage readBody( InputStream is, Properties prop ) throws IOException {
		String contentType = prop.getProperty("Content-Type","application/soap+xml; charset=\"utf-8\"");
		String contentLocation = prop.getProperty(HTTP_LOCATION, "/");
		//BufferedReader r = new BufferedReader( new InputStreamReader( is )); 

		cat.debug("A HTTP SOAP is receiving ... ");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Message msg = null;

		String str = readInputStream( is, getLength(prop) );
		msg = new Message( str, false, contentType, contentLocation );
		// When an inputStream is used in a Message construction directly,
		// then something like stream closure is expected in the Message constructor.
		try {
			baos = new ByteArrayOutputStream();
			msg.writeTo(baos);
			cat.debug("A SOAP received: " + baos.toString());
		} catch (SOAPException e) {
			cat.error(e);
		}
		cat.debug("The end of the HTTP SOAP's receiving.");
		return msg;
	}

	/**
	 * reads input stream.
	 * 
	 * @param is an input stream
	 * @return content of input stream
	 */
	private String readInputStream( InputStream is, int length ) {
		//cat.debug("A length of bytes expected is " + length + ".");
		int c;
		int count = 0;
		String str = "";
		byte buff[] = new byte[buff_size];
		try {
			while ( count < length && (c = is.read(buff)) != -1 ) {
				if ( 0 == c ) {
					Thread.yield();
					continue;
				}
				count += c;
				str += (new String( buff )).substring(0,c);
				// a proper character's encoding must be choosen
			}
		}catch (IOException ioe) {
			cat.error(ioe);
		}
		//cat.debug(str);
		return str;
	}

	
	/**
	 * sends a WSMessage.
	 * The WSMessage must be set correctly.
	 * 
	 * @param msg a message
	 * @param listener listener for a message returned
	 */
	public void sendWSMessage(WSMessage msg, ReturnMessageListener listener) {
		ServedOperation so = ServedOperationStore.getInstance().find(
				msg.getTheFirstUDDIOperationId() );
		//isAnswering = so.isAnswering();
		call = so.createCall();
		try {
			cat.debug(" WSIGS is called by a http access point now.");
			call.setMessage( msg );
			call.setReturnMessageListener( listener );
			call.invoke();
		}catch (Exception e) {
			cat.error(e);
		}
	}
	
	/**
	 * extracts Content-Length HTTP's property
	 * 
	 * @param prop a http header as properties
	 * @return a content's length 
	 */
	private int getLength( Properties prop ) {
		int length = 0;
		try {
			length = Integer.parseInt( prop.getProperty("Content-Length","0"));
		}catch(NumberFormatException e) {
			length = 0;
		}
		return length;
	}

	/**
	 * returns a network port's part for a server address
	 * 
	 * @param prop properties
	 * @return
	 */
	private String parsePort(Properties prop) {
		if ( prop.getProperty("Host","").indexOf(':') > -1 ) {
			return "";
		}
		if ( 80 == socket.getLocalPort() ) {
			return "";
		}
		return ":" + socket.getLocalPort();
	}
	
	public static String createAccessPoint( Properties prop ) {
		// if a request arrived, then a gateway is listen on a host and a port configured
		return Configuration.getInstance().getHostURI()
			+ Configuration.getInstance().getAccessPointPath();
		
		/*
		String location = prop.getProperty(HTTP_LOCATION, "");
		if ( location != null && location.startsWith("http:") ) {
			return location;
		}
		return "http://" + prop.getProperty("Host","localhost").toLowerCase()
			+ prop.getProperty(HTTP_LOCATION, "/wsig");
		*/	
	}
	
	public static String createAbsolutePath( Properties prop ) {
		String location = prop.getProperty(HTTP_LOCATION, "/wsig");
		if ( location != null && location.startsWith("/") ) {
			return location;
		}else {
			return location.substring(location.indexOf('/',7));
		}
	}
	
	/**
	 * serves this connection.
	 */
	public void run() {
		boolean isTimeOut = false;
		while( isRunning ) {
			try {
				InputStream is = new BufferedInputStream(
					socket.getInputStream() );
				Properties prop = readHeader( is );
				SOAPMessage soap = readBody( is, prop );
				SOAPMessage retSOAP = null;

				// find and inform a handler
				SOAPHandler handler = httpServer.findHandler( createAbsolutePath(prop));
				TimeoutWatcher guard = null; 
				if ( timeOut > 0 ) {
					guard = new TimeoutWatcher( handler, timeOut );
				}
				if ( null != handler ) {
					retSOAP = handler.doRequest( prop, soap );
				}
				if ( guard != null ) {
					// stop the guard
					guard.interrupt();
				}

				// prepare an answer and send it back to client
				// has not been implemented yet
				OutputStream os = socket.getOutputStream();
				// http connection as correctly used
				if ( null == handler ) {
					os.write(new String("HTTP/1.1 400 Bad Request\r\n").getBytes()); // HTTP's code 504, Gateway Timeout
					os.write(new String("Connection: close\r\n\r\n").getBytes());
				}else if ( null == retSOAP && guard != null && guard.isInterrupted() ) {
					// service's call is time out
					os.write(new String("HTTP/1.1 504 Gateway Timeout\r\n").getBytes()); // HTTP's code 504, Gateway Timeout
					os.write(new String("Connection: close\r\n\r\n").getBytes());
				}else if ( retSOAP != null ) {
					// send return
					try {
						// send back a response
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						retSOAP.writeTo(baos);
						byte[] content = baos.toByteArray();
						/*
						os.write(new String("HTTP/1.1 200 OK\r\n").getBytes());
						os.write(new String("Connection: close\r\n").getBytes());
						os.write(new String("Content-Type: application/soap+xml; charset=\"utf-8\"\r\n").getBytes());
						os.write(new String("Content-Length: "+ content.length +"\r\n\r\n").getBytes());
						os.write( content );
						*/
						sendBackSOAPContent( content, os );
						cat.debug("A SOAP returned to a client: " + baos.toString());
					} catch (SOAPException e) {
						cat.error(e);
					}
				}else {
					// no SOAP's content is generated
					os.write(new String("HTTP/1.1 204 No Content\r\n").getBytes()); // HTTP's code 204, No Content
					os.write(new String("Connection: close\r\n\r\n").getBytes());
					//os.write(new String("HTTP/1.1 200 OK\r\n").getBytes());
					//os.write(new String("Content-Length: 0\r\n\r\n").getBytes());
				}
				setIsRunning(false);
				os.flush();
				os.close();
				socket.close();
			}catch (IOException ioe) {
				ioe.printStackTrace();
				setIsRunning(false);
				return;
			}
		}
	}

	/**
	 * stop execution of this connection
	 * 
	 */
	public void close() {
		setIsRunning( false );
	}
	

	public static void sendBackSOAPContent( byte[] content, OutputStream os ) throws IOException {
		/*
		os.write(new String("HTTP/1.1 200 OK\r\n").getBytes());
		os.write(new String("Connection: close\r\n").getBytes());
		os.write(new String("Content-Type: application/soap+xml; charset=\"utf-8\"\r\n").getBytes());
		os.write(new String("Content-Length: "+ content.length +"\r\n\r\n").getBytes());
		os.write( content );
		*/
		sendBackContent( content, "application/soap+xml; charset=\"utf-8\"", os );
	}

	public static void sendBackContent( byte[] content, String type, OutputStream os ) throws IOException {
		os.write(new String("HTTP/1.1 200 OK\r\n").getBytes());
		os.write(new String("Connection: close\r\n").getBytes());
		// a type with new line control sequences is treated as divided into multiple lines
		type = type.replaceAll("\n","\n ");
		os.write(new String("Content-Type: " + type + "\r\n").getBytes());
		os.write(new String("Content-Length: "+ content.length +"\r\n\r\n").getBytes());
		os.write( content );
	}

}
