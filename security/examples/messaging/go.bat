set LIBJADEDIR=../../../../lib
set LIB=../../lib
set MYCLPATH=%LIBJADEDIR%\jade.jar;%LIBJADEDIR%\jadeTools.jar;%JADEROOT%\Base64.jar;%LIB%\jadeSecurity.jar;%LIB%\examples.jar;

java -cp %MYCLPATH%  jade.Boot -conf main.conf 

pause
