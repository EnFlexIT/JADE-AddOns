
Instructions for the example: "ssl"
-----------------------------------


Summary:
-------
 - Overview
 - How to run it
 - Quick start for Windows users
 - Note for Linux/Unix users


Overview:
--------
This example shows how to start a platform that uses SSL 
for intra-platform communication.


How to run it:
-------------
Open a command prompt windows, and change 
directory to: security\runExamples\ssl\main 

Open a second command prompt window, and change 
directory to: security\runExamples\ssl\cont 

In the first window, type:  main.bat
wait a few seconds (until the IOR appears), 
then, in the other command prompt window, 
type:  cont.bat

You just setup a two-containers platform where 
all the communication between Main-Container and 
Container-1 goes through an SSL channel.

Each directory contains the proper keys, configuration 
and policy files.

Note the line: imtp=jade.security.impl.RMISSLIMTPManager 
into the configuration files.

This substitutes the default RMI-IMTP with a RMI-SSL-IMTP.
This IMTP can be used also without the Security Manager enabled.

See the Security Administrator Guide for details, 
at http://jade.cselt.it -> "On-line Documentation".


Quick start for Windows users:
-----------------------------
If you are using Microsoft Windows, you can 
easly double-click on main\main.bat file, 
wait a few seconds (until the IOR appears), 
and double-click on cont\cont.bat.

The batch files set the appropriate 
CLASSPATH for you.


Note for Linux/Unix users:
-------------------------
Some minor modifications might be needed 
in the .bat files in order to set the 
appropriate CLASSPATH variable.




