java-cardiag
============

Description
------------
Car diagnostic software for vehicles with OBD2 compatible interface.

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
- only available car for testing - Lada Kalina 1.6 8V 2007.
- need help with a selection of the license ... GNU GPL v3? EPL? BSD?

Current stage
-------------
 - begining development.

Useful links
------------
- http://code.google.com/p/java-simple-serial-connector/
- http://en.wikipedia.org/wiki/OBD-II_PIDs#Bitwise_encoded_PIDs
- http://www.obd-codes.com/trouble_codes/
- http://www.obd-codes.com/faq/obd2-codes-explained.php

Maybe useful links
------------------
These libraries were not selected due to license or incompatibilities or other reason:
- http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-misc-419423.html
- http://www.oracle.com/technetwork/java/index-139971.html
- http://rxtx.qbang.org/wiki/index.php/Download
