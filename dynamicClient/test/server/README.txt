MATH FUNCTIONS WEBSERVICES SERVER
---------------------------------

Caratteristiche wsdl implementato:
- stile document/literal wrapped
- utilizzo di tipi complessi
- utilizzo di array
- utilizzo di header (espliciti/impliciti)

Software richiesto:
- Java JRE 5 o superiore
- Apache Tomcat 5.5 o superiore
- Apache Axis2 1.4 o superiore
- Apache Ant 1.7 o superiore (solo per lo sviluppo)

Installazione:
- installare Java e Tomcat seguendo la procedura normale
- deployare la webapp di Axis2 sotto Tomcat 
  (copiare axis2.war nella cartella webapps di Tomcat)
- deployare il servizio MathFunctions sotto Axis2
  (copiare il file MathFunctionsService.aar nella cartella webapps\axis2\WEB-INF\services di Tomcat)
- lanciare Tomcat
  
URL disponibili:
- console Axis2: http://localhost:8080/axis2/  
- endpoint servizi MathFunctions: http://localhost:8080/axis2/services/MathFunctionsService
- wsdl servizi MathFunctions: http://localhost:8080/axis2/services/MathFunctionsService?wsdl

Sviluppo:
- la cartella sources contiene i sorgenti del progetto
- settare la variabile d'ambiente AXIS2_HOME (deve puntare alla cartella di installazione di Axis2)
- per compilare e realizzare il .aar eseguire "ant"


