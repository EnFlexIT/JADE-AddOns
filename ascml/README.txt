README file for the Agent Society and Configuration Manager and Launcher (ASCML)

INTRODUCTION
============
The ASCML is a JADE Agent which facilitates the configuration and deployment of
agent societies.

LICENSE
=======
see file License.

FEEDBACK
=======
A Wiki has been set up at http://www-i4.informatik.rwth-aachen.de/ascml.

For further questions, suggestions and bugreports you can send email to
ascml@i4.informatik.rwth-aachen.de.

SYSTEM REQUIREMENTS
===================
The ASCML was developed using the Sun JDK version 1.5 and JADE 3.2.
It can be built using apache ant, using the supplied build.xml, or Eclipse.
It should work with newer versions of JADE/JDK but is yet untested.
If you experience problems with your setup, please consult the Wiki and if
necesarry supply us with a bug report.
Older versions of JADE/JDK will not be supported.

KNOWN BUGS
==========
See: http://www-i4.informatik.rwth-aachen.de/ascml/pmwiki.php?n=Main.Bugs

CONTACT
=======
Karl-Heinz Krempels - I4 RWTH Aachen
e-mail: krempels@i4.informatik.rwth-aachen.de

INSTALLATION and LAUNCH
=======================
You can either get the ASCML sources from the JADE AddOns svn repository
or download a tarball from [1] 
and compile them or download a precompiled package from [1].
For instructions on how to build ASCML please see

[1] http://www-i4.informatik.rwth-aachen.de/ascml

1 Software requirements
=======================
To use all features of the ASCML you need:

- JDK 1.5 or newer (JRE 1.5 to run binary packages)
  [java.sun.com]
- JADE 3.2 or newer
  [jade.tilab.com]

To build the ASCML you need either eclipse or ANT.


2 Compiling the ASCML
=====================
Get the ASCML sources as mentioned above.
Use 'ant lib' to generate the ASCML jar.
It will be called ASCML-bin-<version>.jar in the lib subdirectory

e.g. ASCML-bin-0.1.jar.

other build targets are

 run : Launch the ASCML. 
       This requires all necesarry libraries to be in the claspath

 doc : generate the javadoc for the ASCML

 clean : delete the built classfiles and jar/zip archives


3 Launching and using the ASCML
===============================
The most simple way to start an ASCML agent:
     java -cp lib/jade/jade.jar;lib/jade/jadeTools.jar;ASCML-bin-0.1.jar
          jade.Boot -nomtp -gui -name test
          ascml:jade.tools.ascml.launcher.AgentLauncher

You can now try to launch the example files in the src/examples
subdirectory of the ASCML sources.
There are scripts to facilitate the startup of JADE platform.

MORE INFORMATION
================
See http://www-i4.informatik.rwth-aachen.de/ascml/ for more information
