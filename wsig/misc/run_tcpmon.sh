AXIS=../lib/axis-1_1/lib/axis.jar;

# "new port for a redirection" "host name" "original port of an application"
/usr/lib/j2sdk1.4.2_01/bin/java -classpath $AXIS org.apache.axis.utils.tcpmon $1 $2 $3
