#!/bin/sh

WSIGEXAMPLESCP=../examples/lib/wsigExamples.jar

java -classpath $WSIGEXAMPLESCP com.tilab.wsig.examples.SoapClient $1 $2
