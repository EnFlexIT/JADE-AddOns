README file for JADE-S, the JADE Security Plug-in.


INTRODUCTION
============

This package contains the JADE Security Plug-in.
JADE-S is formed by the combination of the standard version of JADE with 
this JADE Security plug-in. It includes features such as user/agent 
authentication, authorization and secure communication inside an 
agent platform.


LICENSE
=======
This plug-in is distributed by TILAB under the LGPL license.
See file COPYING for details.
The package jade.security.impl.DES of this plug-in uses the file Crypt.java 
by Michael Glad and Pawel Veselov distributed itself under the LGPL license.
Included library BouncyCastle licence is into: security/lib/bc/license.txt


SYSTEM REQUIREMENTS
===================
In order to use the JADE security plug-in you need JADE 3.2 or later, 
and Sun Java SDK 1.4 or later. 
The file jadeS.jar, which contains the JADE security plug-in, needs 
to be in your CLASSPATH variable.


INSTALLATION
============
Unpack the file jadesecurity.zip in the same location of your current JADE 
installation. All the needed files will be created in the directory 
jade/add-ons/security. 
In addition to all JADE jar files, you also need to add 
jade/add-ons/security/lib/jadeS.jar to your Java CLASSPATH. 


USING JADE-S
============
Detailed explaination of JADE-S is provided in the 
JADE Security Guide, available at:
http://jade.tilab.com/doc/


DEMOS
========
Several working examples are in the directory: jade/add-ons/security/demos 
If you have Ant installed on your system, you can start each demo 
just by typing 'ant'. (the build.xml file contains the starting class and parameters).



---
by Giosue.Vitaglione@tilab.com
2004-07-12
