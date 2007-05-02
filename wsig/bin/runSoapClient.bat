
SET WSIGEXAMPLESCP=../examples/lib/wsigExamples.jar

java -classpath %WSIGEXAMPLESCP% com.tilab.wsig.soap.SoapClient %1 %2
