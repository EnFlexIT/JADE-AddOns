set JADE_HOME=..\..\..\..\
set MYCLPATH=..\..\lib\jadeS.jar;..\..\classes\;%JADE_HOME%\lib\jade.jar;%JADE_HOME%\lib\jadeTools.jar;%JADE_HOME%\lib\iiop.jar;
java -classpath %MYCLPATH% -Djava.security.manager -Djava.security.policy=policy-not jade.Boot -conf main.conf -owner alice:wannapass anja:examples.security.send.Anja 



