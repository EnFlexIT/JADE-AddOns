#!/bin/sh

# set classpath for wsig libraries
. ./wsigcp.sh

# set classpath for JADE libraries
. ./jadecp.sh


java -classpath .:$WSIGCP:$JADECP jade.Boot -name "WSIGTestPlatform" $*
