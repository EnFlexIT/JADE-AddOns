set AXIS=..\lib\axis-1_1\lib\axis.jar;

REM "new port for a redirection" "host name" "original port of an application"
java  -classpath %AXIS% org.apache.axis.utils.tcpmon %1 %2 %3
