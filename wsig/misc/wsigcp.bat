REM script for \bin\sh converted to bat

set WSDL=..\lib\wsdl4j-1.6.2.jar;..\lib\qname-1.6.2.jar
set UDDI4j=..\lib\uddi4j-2.0.5.jar
set LOG4J=..\lib\log4j-1.2.14.jar
set AXIS=..\lib\axis-1.4.jar;..\lib\saaj-1.2.jar;..\lib\jaxrpc-1.1.jar
set AC=..\lib\commons-logging-1.1.jar;..\lib\commons-discovery-0.2.jar
set GW=..\dist\wsig.jar
set XI=..\lib\xercesImpl-2.8.1.jar

set WSIGCP=%XI%;%GW%;%LOG4J%;%AC%;%AXIS%;%UDDI4j%;%WSDL%

