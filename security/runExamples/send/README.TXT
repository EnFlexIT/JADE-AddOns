
Istruction for the example: "send"
----------------------------------


Summary:
-------
 - Overview
 - How to run it
 - Quick start for Windows users
 - Note for Linux/Unix users


Overview:
--------
This example shows the usage of JADE permissions 
for sending/receiving messages between 
agents belonging to different users.


How to run it:
-------------
Open two command prompt windows, 
Change dir for both at: security\examples\send

In the first window, type:  main-yes.bat
wait a few seconds (until the IOR appears), 
then, in the other command prompt window, 
type:  cont.bat
Then press "enter" on the first window.

The agents "Anja" will start sending messages to "Barbara".
No exception will be fired.

Now close both windows.
Repeat the whole process, using main-not.bat 
instead of main-yes.bat

Exception will be fired because in the two cases 
a different policy file is used.


Quick start for Windows users:
-----------------------------
If you are using Microsoft Windows, you can 
easly double-click on main-yes.bat file, 
wait a few seconds (until the IOR appears), 
and double-click on cont.bat.

The batch files set the appropriate 
CLASSPATH for you.


Note for Linux/Unix users:
-------------------------
Some minor modifications might be needed 
in the .bat files in order to set the 
appropriate CLASSPATH variable.




