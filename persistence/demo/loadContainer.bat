@echo off
@setlocal

call setVariables

java -cp %HIB_LIB%;%HIB_SUPPORT_LIBS%;%JDBC_DRV%;%JADE_CP% jade.Boot %1 %2 %3 %4 %5 -container -services jade.core.persistence.PersistenceService;jade.core.event.NotificationService;jade.core.mobility.AgentMobilityService -meta-db file:hibernate_Container-1.properties -load-from JADE-DB
pause
	
@endlocal
