#!/bin/sh

if [ "$1" = "" ]
	then target=$1;
	else target=j2se;
fi

export CLASSPATH=../$target/demo/jsaDemo.jar:../$target/lib/jsa.jar:../../lib/jade.jar:../../lib/JadeTools.jar

echo Running the JSA demo in $target environment

java -cp $CLASSPATH jade.Boot -name test -gui -nomtp dfagent:demo.DFAgent bestsensor:demo.SensorAgent\(2 dfagent@test\) sensor:demo.SensorAgent\(0 dfagent@test\) bettersensor:demo.SensorAgent\(1 dfagent@test\) display:demo.DisplayAgent\(dfagent@test\) son:demo.ManAgent\(son.txt display@test mother@test showkb\) mother:demo.DemoAgent\(mother.txt\) daughter:demo.DemoAgent\(daughter.txt\) 
