
Istructions for the example: "myfirst"
--------------------------------------


Summary:
-------
 - Overview
 - How to run it
 - Quick start for Windows users
 - Note for Linux/Unix users


Overview:
--------
This example shows how to start a JADE secure platform 
with two JADE Dummy Agents belonging to different owners, 
and shows how messaging permissions are used 
into a policy file.

Full description of this example can be found in 
the "Security Administrator Guide", at:
http://jade.cselt.it  ("On-line Documentation" section).


How to run it:
-------------
Open two command prompt windows, 
Change dir for both at: security\runExamples\myfirst

In the first window, type:  main.bat
wait a few seconds (until the IOR appears), 
then, in the other command prompt window, 
type:  cont.bat

Two Dummy Agents, named: 'da0' and 'da1' will appear.

Try to send a message from 'da0' to 'da1', 
an exception is fired because 'da0' doesn't 
have the permission to do so.

Edit the file: "myfirst.policy", 
and uncomment the needed permission.

Now close both windows.
Repeat the whole process, starting main.bat and cont.bat, 
and sending a message from 'da0' to 'da1'.

No exception is fired now because agent 'da0' has 
got the required permissions. The message is normally 
delivered to 'da1'.


Quick start for Windows users:
-----------------------------
If you are using Microsoft Windows, you can 
easly double-click on main.bat file, 
wait a few seconds (until the IOR appears), 
and double-click on cont.bat.

The batch files set the appropriate 
CLASSPATH for you.

Then into the 'da0' window, you can send 
a message to 'da1'.



Note for Linux/Unix users:
-------------------------
Some minor modifications might be needed 
in the .bat files in order to set the 
appropriate CLASSPATH variable.




