#!/bin/sh

JADECP=../../../lib/jade.jar
PORT=1099

java -classpath $JADECP jade.Boot -name "WSIGTestPlatform" -port $PORT -gui
