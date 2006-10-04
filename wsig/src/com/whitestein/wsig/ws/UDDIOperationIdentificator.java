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
package com.whitestein.wsig.ws;

/**
 * Identifies UDDI operation.
 * 
 * @author jna
 *
 * Pair ( UDDI bindingTemplate accessPoint, operation name) identifies operation.
 */
public class UDDIOperationIdentificator {
	
	private String accessPoint;
	private String wsdlOperation;
	
	/**
	 * constructs new identificator for WSDL operation
	 * 
	 * @param accessPoint access point for UDDI bindingTemplate to provide WSDL operation 
	 * @param wsdlOperation WSDL operation name
	 */
	public UDDIOperationIdentificator(String accessPoint, String wsdlOperation) {
		this.accessPoint = accessPoint;
		this.wsdlOperation = wsdlOperation;
	}
	
	/**
	 * get access point
	 * 
	 * @return
	 */
	public String getAccessPoint(){
		return accessPoint;
	}
	
	/**
	 * get WSDL operation name
	 * 
	 * @return
	 */
	public String getWSDLOperation() {
		return wsdlOperation;
	}
	
	/**
	 * gives hash code for this.
	 * 
	 * @return hash code
	 */
	public int hashCode() {
		String str = getAccessPoint() + getWSDLOperation();
		return str.hashCode();
	}
	
	/**
	 * indicates equal identificators.
	 * The same access point and the same WSDL operation
	 *  give an equal UDDIOperationIdentificator.
	 * 
	 * @return if operations are equal
	 */
	public boolean equals( Object o ) {
		if ( (o != null)
			&& (o instanceof UDDIOperationIdentificator) ) {
			UDDIOperationIdentificator id = (UDDIOperationIdentificator) o;
			return
				(	(	( getAccessPoint() == null && id.getAccessPoint() == null )
						|| getAccessPoint().equalsIgnoreCase( id.getAccessPoint()) )
					&& (( getWSDLOperation() == null && id.getWSDLOperation() == null )
						|| getWSDLOperation().equalsIgnoreCase( id.getWSDLOperation()) ));
		}
		return false;
	}
	
}