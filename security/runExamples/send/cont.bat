set JADE_HOME=..\..\..\..\
set MYCLPATH=..\..\lib\jadeS.jar;..\..\classes\;%JADE_HOME%\lib\jade.jar;%JADE_HOME%\lib\jadeTools.jar;%JADE_HOME%\lib\iiop.jar;
java -classpath %MYCLPATH% -Djava.security.manager -Djava.security.policy=basic.policy jade.Boot -container -owner bob:letmepass barbara:examples.security.send.Barbara
