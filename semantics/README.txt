README file for the JADE Semantics Add-On, version 1.5

INTRODUCTION
============
This package contains an add-on to the JADE framework to build agent-based
systems according to the formal part of the FIPA standard specifications.

LICENSE
=======
see file License.

FEEDBACK
========
As you know already, this is still an on-going project.
We are still working on the framework and new versions will be distributed as
soon as available.
Your feedback as users is very important to us. Please, if you have new
requirements that you would like to see implemented, if you have examples of
usage, or if you discover some bugs, send us information.
Check the website http://jade.tilab.com/ for how to report bugs and send
suggestions.

SYSTEM REQUIREMENTS
===================
To build the Semantics framework a complete Java programming environment and a
complete JADE environment are needed.
At least a Java Development Kit version 1.5 is required (1.4 is enough if you do
not compile the javadoc).
The framework has been developed upon the JADE 3.4 release, however previous
releases should work as well (JADE 3.3 works, previous JADE releases have not
been tested).

KNOWN BUGS
==========
See http://jade.tilab.com/  ('Bugs' page)  for the full list of reported bugs.

CONTACT
=======
Vincent Louis - France Telecom
e-mail: vincent.louis@orange-ftgroup.com


INSTALLATION AND TEST
=====================
You can download the JADE Semantics Add-On in source form and recompile it
yourself, or get the pre-compiled binaries (actually they are JAR files).

1 Software requirements
=======================
The required software to build and run agents upon the JADE Semantics Add-On
are:
- the Java Development Kit version 1.5 (1.4 is enough if the javadoc is not
compiled),
- the JADE framework version 3.3 or higher
Additionally, the ANT program is needed to build (compile the source code and
generate the javadoc) the Semantics Add-On framework from the build.xml file
(ANT is available from http://jakarta.apache.org).

2 Getting the software
======================
All the software is distributed under the LGPL license limitations. It can be
downloaded from the JADE web site

3 Compiling the JADE Semantics Add-On
=====================================
First, uncompress the archive file in the parent directory of the JADE root
directory.
Then, go to the 'jade/add-ons/semantics' subdirectory.
The available commands to compile the JSA can be seen by executing 'ant help'.
For example, execute the 'ant j2se lib' command to compile in a standard Java
environment.
The source files will be compiled, and a jsa.jar file will be generated in the
'j2se/lib' subdirectory.
In order to additionally generate the javadoc in the 'doc/api' subdirectory,
simply execute the 'ant doc' command.

4 Testing the JADE Semantics Add-On
===================================
The simplest way to test the framework is to launch a JADE platform with a
SemanticAgent. For example, you can execute the following command  in the JADE
root directory:
    java -cp lib/jade.jar;lib/jadeTools.jar;add-ons/semantics/j2se/lib/jsa.jar
         jade.Boot -nomtp -gui -name test
         myFirstSemanticAgent:jade.semantics.interpreter.SemanticAgent
Create a "DummyAgent" (select the Tools/Start DummyAgent menu on the JADE GUI).
And then experiment interacting with MyFirstSemanticAgent by sending to it FIPA-
ACL messages and reading its answers (note that the FIPA-SL content language is
mandatory in this version).
For example, send an INFORM message with the content "((foo 1))", and then a
QUERY-REF message with the content "((some ?X (foo ?X)))". The Semantic Agent
will tell you (through an INFORM message) that the predicate 'foo' has the value
1. If you further send an INFORM message with the content "((foo 2))" and re-
send the same QUERY-REF message, you will receive the new set of values for the
predicate 'foo'. Enjoy!

The distribution file also includes the "jsademos" package providing two
demonstrations of the JADE Semantics Add-On (the temperature demo and the
booktrading demo, which is described in the JADE book published by Wiley).
In order to compile and test it, first go to the 'jade/add-ons/semantics'
directory and execute the 'ant j2se demo' command. Before running the demos,
make sure a JADE-Leap version is properly compiled in your JADE directory.
Then, launch the 'run.bat' (Windows systems) or the 'run.sh' (Unix systems)
commands in the 'src/jsademos/temperature' or 'src/jsademos/booktrading'
subdirectories.
You may need to edit these files to adapt them to your operating system settings
(for example if the Java Runtime is not available from the 'PATH' environment
variable).
In order to test the new 2 extensions of this JSA release (emotional agents
and SQL database interface), please read the package-info.java files in the
src/jade/semantics/ext/emotion and src/jade/semantics/ext/sqlwrapper
directories).


DOCUMENTATION
=============
The overview of all available pieces of documentation can be browsed from the
'doc/welcome.html' file. In particular, you can find some scientific references,
a new tutorial covering the main features of the JSA framework, some guidelines
to migrate applications from JSAv1.2 to JSAv1.4, the complete API reference
(javadoc) and a "cookbook", which will be completed online on the subversion
server.
(see https://avalon.cselt.it/svn/jade_add-ons/trunk/semantics/doc/cookbook)


LIMITATIONS
===========
- Semantic Agents built upon the JADE Semantics Add-On currently support only
the FIPA-SL content language. However the software architecture already enables
to plug other content languages support. Future versions will include a direct
connection to the JADE Content Manager, so that all available content languages
(RDF, LEAP, ...) will be reusable.
- The current JADE Semantics Add-On does not at all take into account the Proxy
and Propagate FIPA performatives. This will be added in future versions.
