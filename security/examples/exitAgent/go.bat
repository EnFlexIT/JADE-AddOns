set JADEROOT=../../../..
set LIBJADEDIR=%jaderoot%\lib
set MYCLPATH=%LIBJADEDIR%\jade.jar;%LIBJADEDIR%\jadeTools.jar;%JADEROOT%\add-ons\coresec\lib\coresec.jar;%JADEROOT%\add-ons\coresec\lib\examples.jar;

java -Djava.util.logging.config.file=logging.properties -cp %MYCLPATH%  jade.Boot -conf main.conf


pause
