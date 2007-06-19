#!/bin/sh

JADECP=../../../lib/jade.jar
WSIGEXAMPLESCP=../examples:../examples/lib/wsigExamples.jar:../lib/log4j-1.2.14.jar
PORT=1099

java -classpath $JADECP:$WSIGEXAMPLESCP jade.Boot -container "$1:com.tilab.wsig.examples.MathAgent($2 $3 $4)" -name "WSIGTestPlatform" -port $PORT 