set JADEROOT=../../../../..
set LIBJADEDIR=%jaderoot%\lib


rem MYCLPATH=%JADEROOT%\add-ons\coresec\classes;%JADEROOT%\classes\;

set MYCLPATH=%LIBJADEDIR%\jade.jar;%LIBJADEDIR%\jadeTools.jar;%JADEROOT%\add-ons\coresec\lib\coresec.jar;

java -cp %MYCLPATH% jade.Boot -conf main.conf



