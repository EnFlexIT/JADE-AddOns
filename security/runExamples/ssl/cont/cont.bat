set JADE_HOME=..\..\..\..\..\
set MYCLPATH=..\..\..\lib\jadeS.jar;..\..\classes\;%JADE_HOME%\lib\jade.jar;%JADE_HOME%\lib\jadeTools.jar;%JADE_HOME%\lib\iiop.jar;

java -classpath %MYCLPATH% jade.Boot -container -conf cont.conf -owner bob:letmepass da1:jade.tools.DummyAgent.DummyAgent 



pause
