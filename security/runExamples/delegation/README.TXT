
Istructions for the example: "delegation"
-----------------------------------------


Summary:
-------
 - Overview
 - How to run it
 - Quick start for Windows users
 - Note for Linux/Unix users


Overview:
--------
This example shows the usage of JADE Delegation Certificates.


How to run it:
-------------
Open two command prompt windows, 
Change dir for both of them to: security\runExamples\delegation

In the first window, type:  main.bat
wait a few seconds (until the IOR appears), 
then, in the other command prompt window, 
type:  cont.bat

Now you have agent 'Anja' on the Main-Container and 
agent 'Barbara' on Container-1.

Step 1)
Into the console window of the Main Container, press "enter".

The agent "Anja" will try once to move to Container-1, 
but this attempt fails since Anja doesn't have enough 
permissions (see the 'delegation.policy' file).

Then Anja will wait for a Delegation Certificate.

Step 2)
Into the console window of the Container-1, press "enter".

Barbara prepares and sends a Delegation Certificate to Anja.
Immedietaly, Anja tries again to move to Container-1 and, 
this time, she succeeds since she has got enough permissions.
You can follow the move in the RMA window, as well.


Quick start for Windows users:
-----------------------------
If you are using Microsoft Windows, you can 
easly double-click on main.bat file, 
wait a few seconds (until the IOR appears), 
and double-click on cont.bat.

The batch files set the appropriate 
CLASSPATH for you.


Note for Linux/Unix users:
-------------------------
Some minor modifications might be needed 
in the .bat files in order to set the 
appropriate CLASSPATH variable.




