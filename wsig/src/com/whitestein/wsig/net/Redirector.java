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

import java.util.Properties;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;

import org.apache.log4j.Category;

import com.whitestein.wsig.ws.WSEndPoint;

/**
 * @author jna
 *
 * Redirects incomming requests into another end point.
 *
 *
 */
public class Redirector implements SOAPHandler {

	protected URL target = null;
	private Category cat = Category.getInstance(Redirector.class.getName());
	
	public void setEndPoint( URL target ) {
		this.target = target;
		cat.debug("An end point is set to " + target );
	}
	
	/**
	 * performs a request.
	 * The close method is used to close a call blocked.
	 * 
	 * @param header HTTP request's header
	 * @param soap  HTTP request's body
	 * @return a response
	 */
	public SOAPMessage doRequest( Properties header, SOAPMessage soap ) {
	    cat.debug("A redirector does a request on a target: " + target + ".");
		SOAPMessage retSOAP = null;
		HttpURLConnection c = null;
		try {
			// send a request
			c = WSEndPoint.sendHTTPRequest( target, soap );
			
			// read a response
			// receive a response
			retSOAP = WSEndPoint.receiveHTTPResponse( c );
			
			// release resources
			//c.getOutputStream().close();  // exception: Cannot write output after reading input
			c.getInputStream().close();
			c.disconnect();
	
		}catch (MalformedURLException mfe) {
			cat.error( mfe );
		}catch (SOAPException se) {
			cat.error(se);
		}catch (IOException ioe) {
			cat.error(ioe);
		}

		return retSOAP;
	}

	/**
	 * closes a request processed
	 *
	 */
	public void close() {
		
	}
	

}