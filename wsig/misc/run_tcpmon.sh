#!/bin/sh

AXIS=../lib/axis.jar;

# "new port for a redirection" "host name" "original port of an application"
java -classpath $AXIS org.apache.axis.utils.tcpmon $1 $2 $3
