jade.osgi.test.MyBundleAgent[bundle-name=testBundle.testBundle]


- Far partire il main-container con jadeosgi\startJadeOsgi.bat
- Far partire un container periferico con jadeosgi\osgi\equinox\start.bat
- Oppure far partire il main container con jadeosgi\osgi\equinox\start.bat modificando config.ini

- mvn clean cancella la cartella target
- Per far partire un bundle mettere 

osgi.bundles=../../target/jadeOsgi-1.0-SNAPSHOT.jar@start,../../test/testBundle/target/testBundle-1.0-SNAPSHOT.jar@start

nel config.ini oppure da prompt

install file:../../test/testBundle/target/testBundle-1.0-SNAPSHOT.jar
install file:../../test/testBundle2/target/testBundle2-1.0-SNAPSHOT.jar


mvn -o package per ricreare il jar sotto target e poi fare l'update

1245421862912 testBundle.testBundle STOPPED
1245421862959 testBundle.testBundle STARTED

stop 3
