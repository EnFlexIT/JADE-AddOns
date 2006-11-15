REM script for \bin\sh converted to bat

set AXIS=..\lib\axis.jar;

REM  "new port for a redirection" "host name" "original port of an application"
java -classpath %AXIS% org.apache.axis.utils.tcpmon %1 %2 %3
