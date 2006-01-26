README file for the JADE Semantics Add-On

INTRODUCTION
============
This package contains an add-on to the JADE framework to build agents
-based systems according to FIPA standard specifications.

LICENSE
=======
see file License.

FEEDBACK
=======
As you know already, this is still an on-going project. 
We are still working on the framework and new versions will be distributed 
as soon as available.
Your feedback as users is very important to us. Please, if you have new 
requirements that you would like to see implemented or if you have examples 
of usage or if you discover some bugs, send us information.
Check the website http://jade.tilab.com/
for how to report bugs and send suggestions.  

SYSTEM REQUIREMENTS
===================
To build the Semantics framework a complete Java programming environment and
a complete JADE environment are needed.
At least a Java Development Kit version 1.4 is required. 
The framework has been developed upon the JADE 3.4 release, however previous
releases should work as well (JADE 3.3 works, but previous JADE releases
have not been tested).

Note that the future planned versions of the Semantics framework will require
evolutions within the JADE core framework, so that they are likely not to be
compatible any longer with old JADE versions.


KNOWN BUGS
==========
  see http://jade.tilab.com/  ('Bugs' page)  for the full list of reported bugs


CONTACT
=======
Vincent Louis - France Telecom
e-mail: vincent.louis@francetelecom.com 


INSTALLATION AND TEST
=====================
You can download the JADE Semantics Add-On in source form and recompile it
yourself, or get the pre-compiled binaries (actually they are JAR files). 

1 Software requirements
=======================
The required software to build and run agents upon the JADE Semantics
Add-On are:
- the Java Development Kit version 1.4,
- the JADE framework version 3.3
Additionally, the ANT program is needed to build (compile the source code
and generate the javadoc) the Semantics Add-On framework from the build.xml
file (ANT is available from http://jakarta.apache.org).

2 Getting the software
======================
All the software is distributed under the LGPL license limitations. 
It can be downloaded from the JADE web site

3 Compiling the JADE Semantics Add-On
=====================================
First, uncompress the archive file in the parent directory of the JADE root
directory.
Then, go to the 'jade/add-ons/semantics' subdirectory.
The available commands to compile the JSA can be seen by executing
'ant help'.
For example, execute the 'ant j2se lib' command to compile in a standard
Java environment.
The source files will be compiled, and a jsa.jar file will be generated
in the 'j2se/lib' subdirectory.
In order to additionally generate the javadoc in the 'doc' subdirectory,
simply execute the 'ant doc' command.

4 Testing the JADE Semantics Add-On
===================================
The simplest way to test the framework is to launch a JADE platform with
a SemanticAgent. For example, you can execute the following command  in
the JADE root directory:
    java -cp lib/jade.jar;lib/jadeTools.jar;add-ons/semantics/j2se/lib/jsa.jar
         jade.Boot -nomtp -gui -name test
         myFirstSemanticAgent:jade.semantics.interpreter.SemanticAgentBase
Create a "DummyAgent" (select the Tools/Start DummyAgent menu on the JADE
GUI). And then experiment interacting with MyFirstSemanticAgent by sending
to it FIPA-ACL messages and reading its answers (note that the FIPA-SL
content language is mandatory in this version).
For example, send an INFORM message with the content "((foo 1))", and then
a QUERY-REF message with the content "((some ?X (foo ?X)))". The Semantic
Agent will tell you (through an INFORM message) that the predicate 'foo' has
the value 1. If you further send an INFORM message with the content
"((foo 2))" and re-send the same QUERY-REF message, you will receive the
new set of values for the predicate 'foo'. Enjoy!

A complete demonstration of the JADE Semantics Add-On is also provided within
the distribution file.
In order to build it, first go to the 'jade/add-ons/semantics' directory and
execute the 'ant j2se demo' command.
Then, launch the 'runDemo.bat' (Windows systems) or the 'runDemo.sh' (Unix
systems) in the 'demo' subdirectory.
You may need to edit these files to adapt them to your operating system
settings (for example if the Java Runtime is not available from the 'PATH'
environment variable).
A short tutorial for this demo is available (as a PDF file) in the 'doc'
subdirectory.


LIMITATIONS
===========
- Semantic Agents built upon the JADE Semantics Add-On currently support only
the FIPA-SL content language. However the software architecture already
enables to plug other content languages support. Future versions will include
a direct connection to the JADE Content Manager, so that all available content
languages (RDF, LEAP, ...) will be reusable.
- The current JADE Semantics Add-On does not at all take into account the Proxy
and Propagate FIPA performatives. This will be added in future versions.