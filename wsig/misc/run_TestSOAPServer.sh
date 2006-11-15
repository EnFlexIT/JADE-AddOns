#!/bin/sh

# set classpath for wsig libraries
. ./wsigcp.sh

java  -classpath .:$WSIGCP com.whitestein.wsig.test.TestSOAPServer 23333
