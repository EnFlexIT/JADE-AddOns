set JADEROOT=../../../..
set LIBJADEDIR=%jaderoot%\lib
set MYCLPATH=%LIBJADEDIR%\jade.jar;%LIBJADEDIR%\Base64.jar;%LIBJADEDIR%\jadeTools.jar;%JADEROOT%\add-ons\security\lib\jadeSecurity.jar;%JADEROOT%\add-ons\security\lib\examples.jar;

java -cp %MYCLPATH%  jade.Boot -conf main.conf


pause
