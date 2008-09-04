jade4android add on readme file

In order to build the add-on and the DummyAgent app simply go to the home dir of the project and type ant
For details see the guide in the <home>/doc dir

WARNING and WORKAROUND

Due to changes into the dx utility that converts Java bytecode to the Dalvik JVM format (creates the dex file), the DummyAgent ant build shall eventually raise an OutOfMemoryException when  converting a large amount of classes.
 This seems to be a limitation of the dx tool. The easiest workaround for this issue is to modify the dx.bat file that can be found in <ANDROID_SDK_FOLDER>/tools, increasing the heap size for the JVM.
In particular you could change the following line of dx.bat:
call java -Djava.ext.dirs=%frameworkdir% -jar %jarpath% %*
into something like this:
call java -Xms512m -Xmx1024m -Djava.ext.dirs=%frameworkdir% -jar %jarpath% %*
Please note that exact values for minimum and maximum heap size depend on the amount of memory available on your machine.
