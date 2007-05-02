REM script for \bin\sh converted to bat

REM  set classpath for wsig libraries
call .\wsigcp.bat

REM  set classpath for JADE libraries
call .\jadecp.bat


java -classpath .;%WSIGCP%;%JADECP% jade.Boot -name "WSIGTestPlatform" %1 %2 %3 %4 %5 %6 %7 %8 %9
