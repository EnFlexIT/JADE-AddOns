set JADE_HOME=../../..
set JADE_LIB_DIR=%JADE_HOME%/lib
set LIB=../lib
set MYCLPATH=%JADE_LIB_DIR%\jade.jar;%JADE_LIB_DIR%\commons-codec\commons-codec-1.3.jar;%LIB%\migration.jar

java -cp %MYCLPATH%  jade.Boot -conf P2.conf 

pause
