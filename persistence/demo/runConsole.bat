@echo off
@setlocal

call setVariables

java -cp %HIB_LIB%;%HIB_SUPPORT_LIBS%;%JDBC_DRV%;%JADE_CP% jade.tools.persistence.PersistenceManagerGUI
pause

@endlocal
