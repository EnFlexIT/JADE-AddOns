/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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
package com.tilab.wsig.wsdl;

import jade.content.onto.Ontology;

import java.lang.reflect.Method;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.store.WSIGService;

public class WSDLGenerator {

	public void generate(String serviceName, String servicePrefix, String ontoClassname, String mapperClassName, boolean hierarchicalComplexType, String wsigConfPath, String wsdlDirectory) throws Exception {
		// Check arguments
		if (serviceName == null || serviceName.length() == 0) {
			throw new Exception("No service name specified");
		}
		if (ontoClassname == null || ontoClassname.length() == 0) {
			throw new Exception("No ontology class name specified");
		}
		if (wsdlDirectory == null || wsdlDirectory.length() == 0) {
			wsdlDirectory = ".";
		}

		// Initialize WSIGConfiguration 
		WSIGConfiguration.init(wsigConfPath);
		WSIGConfiguration.getInstance().setWsdlWriteEnable(true);
		WSIGConfiguration.getInstance().setWsdlDirectory(wsdlDirectory);
		
		// Get ontology class 
		Class ontoClass;
		try {
			ontoClass = Class.forName(ontoClassname);
		} catch (Exception e) {
			throw new Exception("Ontology class "+ontoClassname+" not present in WSIG classpath", e);
		}

		// Get ontology instance
		Ontology onto = null;
		try {
			// Try to create by constructor
			onto = (Ontology)ontoClass.newInstance();
		} catch (Exception e) {
			try {
				// Try to create by getInstance() method 
				Method getInstanceMethod = ontoClass.getMethod("getInstance", null);
				onto = (Ontology)getInstanceMethod.invoke(null, null);
			} catch (Exception e1) {
				throw new Exception("Ontology class "+ontoClassname+" not instantiable", e);
			}
		}

		// Get mapper class 
		Class mapperClass = null;
		if (mapperClassName != null) {
			try {
				mapperClass = Class.forName(mapperClassName);
			} catch (ClassNotFoundException e) {
				throw new Exception("Class "+mapperClassName+" not found!", e);
			}
		}
		
		// Create new WSIGService
		WSIGService wsigService = new WSIGService();
		wsigService.setServiceName(serviceName);
		wsigService.setServicePrefix(servicePrefix);
		wsigService.setAgentOntology(onto);
		wsigService.setMapperClass(mapperClass);
		wsigService.setHierarchicalComplexType(hierarchicalComplexType);

		// Create wsdl
		JadeToWSDL jadeToWSDL = new JadeToWSDL();  
		jadeToWSDL.createWSDL(wsigService);
	}
	

	
	public static void main(String[] args) {
		try {
			String serviceName = null;
			String servicePrefix = null;
			String ontoClassname = null;
			String mapperClassName = null;
			boolean hierarchicalComplexType = true;
			String wsigConfPath = null;
			String wsdlDirectory = null;

			// Parse arguments
			if (args.length == 0) {
				printUsage();
				System.exit(0);
			}
			int i = 0;
			while (i < args.length) {
				if (args[i].startsWith("-")) {
					if (args[i].equalsIgnoreCase("-help")) {
						printUsage();
						System.exit(0);
					}
					if (args[i].equalsIgnoreCase("-name")) {
						if (++i < args.length) {
							serviceName = args[i];
						}
						else {
							throw new IllegalArgumentException("No service name specified after \"-name\" option");
						}
					}
					else if (args[i].equalsIgnoreCase("-onto")) {
						if (++i < args.length) {
							ontoClassname = args[i];
						}
						else {
							throw new IllegalArgumentException("No ontology class name specified after \"-onto\" option");
						}
					}
					else if (args[i].equalsIgnoreCase("-prefix")) {
						if (++i < args.length) {
							servicePrefix = args[i];
						}
						else {
							throw new IllegalArgumentException("No service prefix specified after \"-prefix\" option");
						}
					}
					else if (args[i].equalsIgnoreCase("-mapper")) {
						if (++i < args.length) {
							mapperClassName = args[i];
						}
						else {
							throw new IllegalArgumentException("No mapper class name specified after \"-mapper\" option");
						}
					}
					else if (args[i].equalsIgnoreCase("-hierarchical")) {
						if (++i < args.length) {
							hierarchicalComplexType = Boolean.parseBoolean(args[i]);
						}
						else {
							throw new IllegalArgumentException("No hierarchical complex type flag specified after \"-hierarchical\" option");
						}
					}
					else if (args[i].equalsIgnoreCase("-conf")) {
						if (++i < args.length) {
							wsigConfPath = args[i];
						}
						else {
							throw new IllegalArgumentException("No property file to load WSIG configuration properties from specified after \"-conf\" option");
						}
					}
					else if (args[i].equalsIgnoreCase("-out")) {
						if (++i < args.length) {
							wsdlDirectory = args[i];
						}
						else {
							throw new IllegalArgumentException("No wsdl output folder specified after \"-out\" option");
						}
					}
				}
				++i;
			}
			
			// Generate WSDL
			WSDLGenerator gen = new WSDLGenerator();
			gen.generate(serviceName, servicePrefix, ontoClassname, mapperClassName, hierarchicalComplexType, wsigConfPath, wsdlDirectory);
			
			System.out.println("WSDL for service "+serviceName+" successfully generated in "+wsdlDirectory);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("java -cp <classpath> com.tilab.wsig.wsdl.WSDLGenerator options");
		System.out.println("Options:");
		System.out.println("    -name <service name>");
		System.out.println("    -onto <ontology class name>");
		System.out.println("    [-prefix <service prefix>]");
		System.out.println("    [-mapper <mapper class name>]");
		System.out.println("    [-hierarchical <true|false>] (default=true)");
		System.out.println("    [-conf <property file to load WSIG configuration properties from>] (default=see WSIG guide)");
		System.out.println("    [-out <wsdl output folder>] (default=current folder)");
	}
}
