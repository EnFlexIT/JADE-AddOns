set JADEROOT=../../../../..
set LIBJADEDIR=%jaderoot%\lib


set MYCLPATH=%LIBJADEDIR%\jade.jar;%LIBJADEDIR%\jadeTools.jar;%JADEROOT%\add-ons\security\lib\jadeSecurity.jar;

java -cp %MYCLPATH% jade.Boot -conf main.conf



