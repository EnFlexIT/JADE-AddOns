

Why does this directory exist:  /add-ons/benchmark/jadelib 


In order to test different versions of JADE, just copy the 
file jade.jar (of your preferred version) into this directory.


Many .bat files in the Benchmark add-on use a CLASSPATH as the following: 

 java -cp ..\..\jadelib\jade.jar;..\..\..\..\lib\jade.jar;..\..\classes jade.Boot 

This way, if a jar file is not provided into add-ons/benchmark/jadelib/ 
the standard jade\lib\jade.jar is used.


---
GV 2003-08-27 

