set JADE_HOME=..\..\..\..
java -classpath ..\..\lib\jadeS.jar;..\..\classes\;%JADE_HOME%\lib\jade.jar;%JADE_HOME%\lib\jadeTools.jar;  -Djava.security.manager -Djava.security.policy=basic.policy  -Djava.compiler="" jade.Boot -container -owner bob:letmepass Barbara:examples.security.delegation.Barbara 


