#!/bin/bash

WSDC_HOME=..
JADE_HOME=../../..

ant -f %WSDC_HOME%\build.xml lib

WSDC_LIB=${JADE_HOME}/lib/jade.jar:${WSDC_HOME}/lib/wsdc.jar:${WSDC_HOME}/lib/axisPlus.jar:${WSDC_HOME}/lib/commons-discovery-0.2.jar:${WSDC_HOME}/lib/commons-logging-1.1.1.jar:${WSDC_HOME}/lib/jaxrpc-1.1.jar:${WSDC_HOME}/lib/saaj-1.2.jar:${WSDC_HOME}/lib/wsdl4j-1.5.1.jar:${WSDC_HOME}/lib/jadeMisc.jar:${WSDC_HOME}/lib/wss4j-1.5.1.jar:${WSDC_HOME}/lib/xmlsec-1.3.0.jar:.

SAVECLASSPATH=${CLASSPATH}

export CLASSPATH=${WSDC_LIB}

java jade.webservice.dynamicClient.DynamicClientShell $*

export CLASSPATH=${SAVECLASSPATH}




