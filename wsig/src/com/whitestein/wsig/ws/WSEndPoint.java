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
 * Portions created by the Initial Developer are Copyright (C) 2004, 2005
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
package com.whitestein.wsig.ws;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.Message;
import org.apache.log4j.Category;

import com.whitestein.wsig.fipa.GatewayAgent;
import com.whitestein.wsig.net.Connection;
import com.whitestein.wsig.struct.CalledMessage;
import com.whitestein.wsig.struct.EndPoint;
import com.whitestein.wsig.struct.EndPointImpl;
import com.whitestein.wsig.struct.ReturnMessageListener;

/**
 * @author jna
 *
 * provides WS end point
 *
 */
public class WSEndPoint extends EndPointImpl implements EndPoint {
	
	/**
	 * provides unique type for this WSEndPoint.
	 * The reference of WSMessage.type is used.
	 */
	public static final String TYPE = WSMessage.TYPE;
	
	//private Gateway gw;
	private UDDIOperationIdentificator uddiId;
	private static Category cat = Category.getInstance(WSEndPoint.class.getName());

	public WSEndPoint( UDDIOperationIdentificator uddiId ) {
		super(WSEndPoint.TYPE);
		this.uddiId = uddiId;
	}
	
	public UDDIOperationIdentificator getUDDIOperationId(){
		return uddiId;
	}
	
	/**
	 * sends a native message
	 *  
	 * @param cMsg a sending message
	 */
	protected void nativeSend( CalledMessage cMsg, ReturnMessageListener listener ) {
		cat.debug(" WS sending.");

		// inform also GatewayAgent's GUI
		GatewayAgent myGateway = GatewayAgent.getInstance();
		myGateway.addMessageToLog( cMsg.getServedOperation(), cMsg );

		if ( null == cMsg || ! (cMsg instanceof WSMessage )) {
			// is not native message, never will be happen
			// checking for type is done in send method
			throw new Error("WSMessage is expected.");
		}
		WSMessage msg = (WSMessage) cMsg;
		URL url = null;
		HttpURLConnection httpConn;
		WSMessage retMsg = null;
		try {
			url = new URL( uddiId.getAccessPoint());
			if (url.getProtocol().compareToIgnoreCase("http") != 0 ) {
				// inform the listener
				return;
			}
			
			/*
			// prepare a header
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("SOAPAction", "\"\"");
			httpConn.setRequestProperty("Connection", "close");
			httpConn.setRequestProperty("Content-Type","text/xml; charset=\"utf-8\"");
			String portPart = (url.getPort()!=-1) ? (":"+url.getPort()) : "";
			httpConn.setRequestProperty("Host",url.getHost() + portPart);
			httpConn.setAllowUserInteraction(false);
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.connect();
			
			// send SOAP
			OutputStream os = httpConn.getOutputStream();
			msg.getSOAPMessage().writeTo( os );
			os.flush();
			*/
			
			/*
			// read a response
			java.util.Map responseHeader = httpConn.getHeaderFields();
			InputStream is = httpConn.getInputStream();
			BufferedReader r = new BufferedReader( new InputStreamReader( is ));
			String line, retStr = "";
			while ( (line = r.readLine()) != null ) {
				retStr += line;
				cat.debug( " a line received: " + line );
			}
			
			
			String contentType;
			String contentLocation;
			int length;
			
			try {
				contentType = responseHeader.get("Content-Type").toString();
		    	// "application/soap+xml; charset=\"utf-8\""
			}catch (NullPointerException e) {
				contentType = "";
			}
			contentLocation = "";
			try {
				if ( responseHeader.get("Content-Length") != null ) {
					length = Integer.parseInt((String)responseHeader.get("Content-Length"));
				}else {
					// some responses does not have a Content-Length header
					length = 0;
				}
			}catch (NullPointerException e) {
				length = 0;
			}
			Message soap = null;
			soap = new Message( retStr, false, contentType, contentLocation );

			// construct a WSMessage returned
			retMsg = new WSMessage( soap );
			retMsg.setAccessPoint("");
			retMsg.setResponse(true);
			
			// debug to write down
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				baos = new ByteArrayOutputStream();
				soap.writeTo(baos);
				cat.debug("A SOAP received: " + baos.toString());
			} catch (SOAPException e) {
				cat.error(e);
			}
			*/

			// send a request
			httpConn = sendHTTPRequest( url, msg.getSOAPMessage());

			OutputStream os = httpConn.getOutputStream();

			// receive a response
			SOAPMessage soap;
			soap = receiveHTTPResponse( httpConn );
			
			// construct a WSMessage returned
			retMsg = new WSMessage( soap );
			retMsg.setAccessPoint("");
			retMsg.setResponse(true);

			// release resources
			//httpConn.getOutputStream().close();
			os.close();
			httpConn.getInputStream().close();
			httpConn.disconnect();
			
		}catch (MalformedURLException mfe) {
			cat.error( mfe );
			// inform the listener
		}catch  (IOException ioe) {
			cat.info( "An end point's call has been broken.");
			cat.debug( ioe );
			// try to check UDDI for updates, try to use them again
			// in a failure inform the listener then
		}catch (SOAPException se) {
			cat.error(se);
			// inform the listener
		}
		
		// inform the listener
		listener.setReturnedMessage( retMsg );
	}
	
	/**
	 * sends a SOAP request.
	 * 
	 * @param url an end point for a request
	 * @param soap a soap to be sent
	 * @return a connection established to send and to receive soaps
	 * @throws MalformedURLException if url is bad
	 * @throws IOException when a communication will be broken
	 * @throws SOAPException a soap is bad
	 */
	public static HttpURLConnection sendHTTPRequest( URL url, SOAPMessage soap )
	throws MalformedURLException, IOException, SOAPException {
		HttpURLConnection httpConn = null;
		
		// prepare a header
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod("POST");
		httpConn.setRequestProperty("SOAPAction", "\"\"");
		httpConn.setRequestProperty("Connection", "close");
		httpConn.setRequestProperty("Content-Type","text/xml; charset=\"utf-8\"");
		String portPart = (url.getPort()!=-1) ? (":"+url.getPort()) : "";
		httpConn.setRequestProperty("Host",url.getHost() + portPart);
		cat.debug(" host sets to " + url.getHost() );
		httpConn.setAllowUserInteraction(false);
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true); // check a wsdl

		// debug
		Iterator it = httpConn.getRequestProperties().keySet().iterator();
		Object key, value;
		while( it.hasNext() ) {
			key = it.next();
			value = httpConn.getRequestProperties().get( key );
			cat.debug(" key: " + key + " value: " + value );
		}

		httpConn.connect();
		
		// send a SOAP
		OutputStream os = httpConn.getOutputStream();
		soap.writeTo( os );
		os.flush();
		
		return httpConn;
	}

	/**
	 * receives a SOAP response.
	 * 
	 * @param httpConn a connection where a soap response is expected
	 * @return a soap received
	 * @throws IOException if something is wrong during a communication
	 */
	public static SOAPMessage receiveHTTPResponse(HttpURLConnection httpConn) throws IOException {
		// read a response
		java.util.Map responseHeader = httpConn.getHeaderFields();

		// get a response's length
		//  is not used
		int length = -1;
		try {
			if ( responseHeader.get("Content-Length") != null ) {
				Object obj = responseHeader.get("Content-Length");
				if ( obj instanceof Collection && !((Collection) obj).isEmpty() ){
					obj = ((Collection) obj).toArray()[0];
				}
				if ( obj instanceof String ) {
					length = Integer.parseInt((String) obj);
				}else if ( obj instanceof Integer ) {
					length = ((Integer) obj).intValue();
				}else {
					length = 0;
				}
				cat.debug(" Content-Length = " + length);
			}else {
				String code = httpConn.getHeaderField(0);
				String[] part = Connection.split( code );
				
				// some responses does not have a Content-Length header
				if ( 0 == "204".compareTo( part[1] ) ) {
					// no content, OK
					length = 0;
				}else {
					// other than 204
					length = 1; // read anyway
					// fix up a bad HTTP's response, when Content-Length field is missed
				}
			}
		}finally{
			if ( length < 0 ) {
				length = 0;
			}
		}

		String retStr = "";
		if ( length > 0 ) {
			InputStream is = httpConn.getInputStream();
			BufferedReader r = new BufferedReader( new InputStreamReader( is ));
			String line;
			while ( (line = r.readLine()) != null ) {
				retStr += line;
				cat.debug( " a line received: " + line );
			}
		}
				
		String contentType;
		String contentLocation;
		
		try {
			contentType = responseHeader.get("Content-Type").toString();
	    	// "application/soap+xml; charset=\"utf-8\""
		}catch (NullPointerException e) {
			contentType = "";
		}
		contentLocation = "";
		
		// use AXIS's implementation of a SOAPMessage
		Message soap = null;
		if ( retStr.length() > 0 ) {
			// not empty response
			soap = new Message( retStr, false, contentType, contentLocation );
		}else {
			// soap is empty
			//  is null
		}

		// debug to write down
		try {
			if ( null != soap ) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				soap.writeTo(baos);
				cat.debug("A SOAP received: " + baos.toString());
			}else {
				cat.debug("A SOAP received: null ");
			}
		} catch (SOAPException e) {
			cat.error(e);
		} catch (IOException ioe) {
			cat.error(ioe);
		}

		return (SOAPMessage) soap;
	}
}
