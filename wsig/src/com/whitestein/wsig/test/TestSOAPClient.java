/*
 * Created on Aug 15, 2004
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
package com.whitestein.wsig.test;

import java.net.*;
import java.io.*;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.Message;
import org.apache.log4j.Category;

import com.whitestein.wsig.struct.*;
import com.whitestein.wsig.ws.*;

/**
 * @author jna
 *
 * tests a SOAP message sending
 *
 */
public class TestSOAPClient implements Runnable {

	private static int port = 2222;  // go through a watcher
	// private static int port = 2222;
	public static String ACCESS_POINT = "http://localhost:2222/wsig/";
	//public static String ACCESS_POINT = "http://t20java:2222/wsig/";
	public static String OPERATION = "operation0"; 
	public static String OPERATION_2 = "operation1"; 
	public static String requestStr = "";

	private boolean isRunning =  true;
	private static Category cat = Category.getInstance(TestSOAPClient.class.getName());
	private static MessageFactory mf;
	private static SOAPFactory soapFactory;
	private CalledMessage returnedMessage;

	static {
		try {
			mf = MessageFactory.newInstance();
			soapFactory = SOAPFactory.newInstance();
		}catch (SOAPException e) {
			e.printStackTrace();
		}
	}

	public static String generateSOAP( String op_name ) {
		String str = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
			"	<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">  " +
			"	 <soapenv:Body> " +
			"	  <tns:" + op_name + " xmlns:tns=\"uri://localhost:2222/test/mywsdl.wsdl\" >   " +
			"	   <tns:name >  " +
			"	    <tns:BO_String>a string</tns:BO_String>   " +
			"	   </tns:name>   " +
			"	  </tns:" + op_name + ">  " +
			"	 </soapenv:Body>   " +
			"	</soapenv:Envelope>\r\n";
		return str;
	}
	
	private void test1(){
		URL serverURL = null;
		HttpURLConnection c = null;

		SOAPMessage retSOAP = null;
		
		// generate a test's message
		String str;
		str = generateSOAP( OPERATION_2 );
		//str = generateSOAP( OPERATION );

		SOAPMessage soap;
		soap = new Message( str, false, "application/soap+xml; charset=\"utf-8\"", "" );

		// debug to write down
		ByteArrayOutputStream baos;
		try {
			baos = new ByteArrayOutputStream();
			soap.writeTo(baos);
			cat.info("A SOAP sent: \n  " + baos.toString());
		} catch (SOAPException e) {
			cat.error(e);
		} catch (IOException ioe) {
			cat.error(ioe);
		}

		try {
			serverURL = new URL(
					"http",
					"localhost",
					port,
					"/wsig");

			// send a request
			c = WSEndPoint.sendHTTPRequest( serverURL, soap );
			
			// read a response
			// receive a response
			retSOAP = WSEndPoint.receiveHTTPResponse( c );
			
			// debug to write down
			if ( retSOAP != null ) {
				try {
					baos = new ByteArrayOutputStream();
					retSOAP.writeTo(baos);
					cat.info("A SOAP received: \n  " + baos.toString());
				} catch (SOAPException e) {
					cat.error(e);
				} catch (IOException ioe) {
					cat.error(ioe);
				}
			}else {
				cat.info("A SOAP received: null.");
			}


			// release resources
			/*
			 * problems with java.net.ProtocolException
			if ( c.getDoOutput() ) {
				//c.getOutputStream().close();
			}
			if ( c.getDoInput() ) {
				//c.getInputStream().close();
			}
			*/
			c.disconnect();
	
		}catch (MalformedURLException mfe) {
			cat.error( mfe );
		}catch (SOAPException se) {
			cat.error(se);
		}catch (IOException ioe) {
			cat.error(ioe);
		}finally{
			if (c != null) {
				c.disconnect();
			}
			isRunning = false;
			//return;
		}
	
	}
	
	public void run() {
		cat.debug(" Test SOAP Client starts. ");

		int count = 0;
		while ( isRunning ) {
			test1();
			count ++;
			isRunning = isRunning && count < 1;  // in case >1 is set, then request is mallformed by duplicit bytes
		}
		cat.debug(" SOAP test client ends. ");
		
	}
	
	public static void main(String[] args) {
		new Thread( new TestSOAPClient()).start();
	}
}
