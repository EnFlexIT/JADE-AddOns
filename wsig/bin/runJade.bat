
SET JADECP=../../../lib/jade.jar
SET PORT=1099

java -classpath %JADECP% jade.Boot -name "WSIGTestPlatform" -port %PORT% -gui