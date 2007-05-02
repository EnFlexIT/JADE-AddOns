REM script for \bin\sh converted to bat

REM  set classpath for wsig libraries
call .\wsigcp.bat

java  -classpath .;%WSIGCP% com.whitestein.wsig.test.TestFindPlaceRegistration
