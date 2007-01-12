/*
 * Created on May 18, 2004
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
package com.whitestein.wsig.translator;

import jade.content.onto.OntologyException;
import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import javax.wsdl.*;
import javax.wsdl.factory.*;


/**
 * Translates description of services between ACL and WSDL.
 * 
 * @author Jozef Nagy for Whitestein Technologies
 *
 *
 *
 */
public class ServiceDescriptionTranslator {
	
	/**
	 * Translates ACL description into WSDL description.
	 * Object classes must be chnged to proper types.
	 * 
	 * @param acl description in ACL format
	 * @return generated WSDL
	 * @throws WSDLException for WSDL document parsing
	 */
	public static Definition produceWSDLfromACL( Object acl ) throws WSDLException {
		WSDLFactory wsdlFactory = WSDLFactory.newInstance();
		Definition wsdlDef = wsdlFactory.newDefinition();
		return wsdlDef;
	}
	
	/**
	 * produces WSDL description for an agent description.
	 * A WSDL description is created for the agent description.
	 * 
	 * @param dfad description of the agent
	 * @return generated WSDL
	 * @throws WSDLException for WSDL document parsing
	 */
	public static Definition produceWSDLfromDFAgentDescription( DFAgentDescription dfad ) throws WSDLException {
		// new WSDL definition is created and then used
		WSDLFactory wsdlFactory = WSDLFactory.newInstance();
		Definition wsdlDef = wsdlFactory.newDefinition();
		return wsdlDef;
	}
	
	/**
	 * Translates WSDL description into ACL description.
	 * Object classes must be chnged to proper types.
	 * 
	 * @param wsdlDef description in WSDL rofmat
	 * @return generated ACL
	 */
	public java.lang.Object produceACLfromWSDL( Definition wsdlDef ) {
		return new Object();
	}
	
	/**
	 * Used for testing purpose.
	 * 
	 * @param args testing arguments
	 */	
	public static void main(String[] args) {
		/*
		 * Test1: read a WSDL file, translate it
		 */
		ServiceDescriptionTranslator sdTranslator = new ServiceDescriptionTranslator();
		/*
		try{
			WSDLFactory wsdlFactory = WSDLFactory.newInstance();
			WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
			
			wsdlReader.setFeature("javax.wsdl.verbose", true);
			wsdlReader.setFeature("javax.wsdl.importDocuments", true);
			
			Definition def = wsdlReader.readWSDL(null, "sample.wsdl");
			sdTranslator.produceACLfromWSDL( def );
		}catch (WSDLException e) {
			e.printStackTrace();
		}
		*/
		try {
			String str;
			String name = new String("register");
			Ontology onto=FIPAManagementOntology.getInstance();
			ObjectSchema osch = onto.getSchema(name);
			str = osch.getTypeName();
			System.out.println(" returned type for " + name + " is: " + str);
			System.out.println("   " + osch.toString());
			
			String [] names = osch.getNames();
			for(int i = 0; i < names.length; i ++ ) {
				System.out.println("   names[" + i + "] : " + names[i]);
			}
			name = "description";
			str = osch.getSchema(name).toString();
			System.out.println(" returned type for " + name + " is: " + str);
		}catch (OntologyException e) {
			e.printStackTrace();
		}
	}
}
