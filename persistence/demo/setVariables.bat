@echo off

SET JADE_HOME=..\..\..
SET HIB_HOME=C:\tools\hibernate-2.1

SET JADE_CP=%JADE_HOME%\classes;%JADE_HOME%\add-ons\persistence\classes

SET HIB_LIB=%HIB_HOME%\hibernate2.jar

SET HIB_SUPPORT_LIBS=%HIB_HOME%\lib\commons-logging-1.0.3.jar;%HIB_HOME%\lib\ehcache-0.6.jar;%HIB_HOME%\lib\cglib-2.0-rc2.jar;%HIB_HOME%\lib\jta.jar;%HIB_HOME%\lib\commons-collections-2.1.jar;%HIB_HOME%\lib\commons-beanutils.jar;%HIB_HOME%\lib\commons-lang-1.0.1.jar;%HIB_HOME%\lib\cglib-asm.jar;%HIB_HOME%\lib\bcel.jar;%HIB_HOME%\lib\odmg-3.0.jar;%HIB_HOME%\lib\jdom.jar;%HIB_HOME%\lib\xml-apis.jar;%HIB_HOME%\lib\xerces-2.4.0.jar;%HIB_HOME%\lib\xalan.jar;%HIB_HOME%\lib\dom4j-1.4.jar

SET JDBC_DRV=C:\tools\hsqldb\lib\hsqldb.jar

