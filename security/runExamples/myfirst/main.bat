set JADE_HOME=..\..\..\..\
set MYCLPATH=..\..\lib\jadeS.jar;..\..\classes\;%JADE_HOME%\lib\jade.jar;%JADE_HOME%\lib\jadeTools.jar;%JADE_HOME%\lib\iiop.jar;
java -classpath %MYCLPATH% jade.Boot -gui -conf main.conf -owner alice:wannapass da0:jade.tools.DummyAgent.DummyAgent


