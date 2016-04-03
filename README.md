java-cardiag
============

Description
-----------
Car diagnostic software for vehicles with OBD2 compatible interface.

Example
-------
- now we can run two actions. Program looks for serial interfaces and lets the user to select one.
  Then it runs the action.
- report creates a text file in the current directory containing all implemented values.
- clear_trouble_codes erases the saved errors.

java -jar ./java-cardiag-0.0.1-SNAPSHOT.one-jar.jar report
java -jar ./java-cardiag-0.0.1-SNAPSHOT.one-jar.jar CLEAR_TROUBLE_CODES

Targets
-------
- support for most of today's operating systems
- communication via USB ELM327/OBD2 car interface, probably other in future
- stability
- simple startup, no system modifications needed (only JRE6 and higher)
- pretty maintainable code covered by tests, both unit (without real serial interface)
  and integration (with interface and car)
- open source free code

Problems
---------
- how
- only available hardware for testing - ELM327 clone (1.5a)
- only few available cars for testing - Lada Kalina 1.6 8V 2007, Škoda Fabia 2010, Ford Focus 2007.
- need help with a selection of the license ... GNU GPL v3? EPL? BSD?

Troubleshooting
---------------
Port name - /dev/ttyUSB0; Method name - openPort(); Exception type - Permission denied. (Linux)
- sudo gpasswd --add ${USER} dialout
- or run the program with sudo.

Current stage
-------------
- early development.
- can reset error codes
- can produce a report file with the current values
- version 0.0.1 will be released after I will fix my problem with my car (P300, P303, P304) ;)
- if someone will start implementing GUI, many people will be finally happy ;)

Useful links
------------
- http://code.google.com/p/java-simple-serial-connector/
- http://en.wikipedia.org/wiki/OBD-II_PIDs#Bitwise_encoded_PIDs
- http://www.obd-codes.com/trouble_codes/
- http://www.obd-codes.com/faq/obd2-codes-explained.php
- http://www.multitronics.ru/kody_vaz/
- http://www.multitronics.ru/kody_obd2/
- http://www.multitronics.ru/terminy_obd2/
- http://www.obdii.com/obdii_library.asp
- http://www.outilsobdfacile.com/obd-mode-pid.php
- http://www.genisysotc.com/pdfs/DriveabilityDiagnostics.pdf

Maybe useful links
------------------
These libraries were not selected due to license or incompatibilities or other reason:
- http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-misc-419423.html
- http://www.oracle.com/technetwork/java/index-139971.html
- http://rxtx.qbang.org/wiki/index.php/Download
