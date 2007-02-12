#!/bin/sh

WSDL=../lib/wsdl4j-1.6.2.jar:../lib/qname-1.6.2.jar
UDDI4j=../lib/uddi4j-2.0.5.jar
LOG4J=../lib/log4j-1.2.14.jar
AXIS=../lib/axis-1.4.jar:../lib/saaj-1.2.jar:../lib/jaxrpc-1.1.jar
AC=../lib/commons-logging-1.1.jar:../lib/commons-discovery-0.2.jar
GW=../dist/wsig.jar
XI=../lib/xercesImpl-2.8.1.jar

XSD=../lib/org.eclipse.emf.common_2.3.0.v200612211251.jar:../lib/org.eclipse.emf.ecore.xmi_2.3.0.v200612211251.jar:../lib/org.eclipse.emf.ecore_2.3.0.v200612211251.jar:../lib/org.eclipse.emf_2.2.0.v200612211251.jar:../lib/org.eclipse.xsd_2.3.0.v200612211251.jar:

WSIGCP=$XI:$GW:$LOG4J:$AC:$AXIS:$UDDI4j:$WSDL:$XSD

