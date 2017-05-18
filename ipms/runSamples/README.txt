This directory contains .bat and configuration files to try the MobileAgent example provided together with the JADE Inter-Platform-Migration-Service (IPMS).

Such files assume the IPMS add-on has been unzipped or checked-out from the SVN repository in the JADE home directory thus obtaining a directory structure like

jade/
  |--...
  |--add-ons/
        |--...
        |--ipms/
               |--...

If this is not the case the paths in the .bat and configuration files must be modified to reflect your directory structure!!!!


The example comprises two platforms P1 and P2 and a MobileAgent moving across them. Such MobileAgent gets the hops to perform from
the itinerary.properties file.

To try the example do the followings:
- if you checked out the ipms add-on from the SVN repository, compile it typing 
  
  ant clean lib

  NOTE 1: This step requires ANT 1.7 or later properly installed
  NOTE 2: If you got the ipms add-on from the download area of the JADE web site this step is not necessary since the add-on distribution
  package already contains the compiled code. 

- launch runP2.bat to start platform P2
- wait a bit until the RMA GUI of platform P2 appears
- launch runP1.bat to start platform P1 with the MobileAgent on top
- look at the messages printed by the MobileAgent to check it is actually moving across P1 and P2 as described in the itinerary.properties file
 