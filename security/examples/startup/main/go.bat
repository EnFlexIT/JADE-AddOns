set LIBJADEDIR=../../../../../lib
set LIB=../../../lib

set MYCLPATH=%LIBJADEDIR%\jade.jar;%LIBJADEDIR%\jadeTools.jar;%LIBJADEDIR%\Base64.jar;%LIB%\jadeSecurity.jar;

java -cp %MYCLPATH% jade.Boot -conf main.conf

pause



