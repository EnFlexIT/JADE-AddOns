set JADE_HOME=..\..\..\..\
java -classpath ..\..\lib\jadeS.jar;..\..\classes\;%JADE_HOME%\lib\jade.jar;%JADE_HOME%\lib\jadeTools.jar;%JADE_HOME%\lib\iiop.jar; -Djava.compiler="" jade.Boot -gui -conf main.conf -owner alice:wannapass Anja:examples.security.delegation.Anja 

