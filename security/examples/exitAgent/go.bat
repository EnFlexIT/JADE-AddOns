set JADEROOT=../../../..
set LIBJADEDIR=%jaderoot%\lib
set MYCLPATH=%LIBJADEDIR%\jade.jar;%LIBJADEDIR%\Base64.jar;%LIBJADEDIR%\jadeTools.jar;%JADEROOT%\add-ons\security\lib\jadeSecurity.jar;%JADEROOT%\add-ons\security\lib\examples.jar;

java -Duser.language=en -cp %MYCLPATH%  jade.Boot -conf main.conf


pause
