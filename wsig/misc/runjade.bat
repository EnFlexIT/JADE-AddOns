set JADE=..\lib\jade\base64.jar;..\lib\jade\http.jar;..\lib\jade\iiop.jar;..\lib\jade\jade.jar;..\lib\jade\jadeTools.jar
set WSDL=..\lib\wsdl4j-1_4\lib\wsdl4j.jar;..\lib\wsdl4j-1_4\lib\qname.jar
set UDDI4j=..\lib\uddi4j-2_0_2\lib\uddi4j.jar
set LOG4J=..\lib\axis-1_1\lib\log4j-1.2.8.jar
set AXIS=..\lib\axis-1_1\lib\axis.jar;..\lib\axis-1_1\lib\commons-logging.jar;..\lib\axis-1_1\lib\commons-discovery.jar;..\lib\axis-1_1\lib\saaj.jar;..\lib\axis-1_1\lib\jaxrpc.jar
set GW=..\bin
REM set GW=..\distribution\wsig.beta.0.4.bin.jar

java  -classpath %GW%;%LOG4J%;%JADE%;%AXIS%;%UDDI4j%;%WSDL% jade.Boot %1 %2 %3 %4 %5 %6 %7 %8 %9
