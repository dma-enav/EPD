# e-Navigation Prototype Displays #

## Introduction ##
   
EPD (e-Navigation Prototype Display) is an ECDIS like
application for demonstrating possible e-Navigation services.

This project contains two module, EPD-Ship and EPD-Shore each focusing on various services from the point of view of the Ship and of the Shore control center.
   
The application is in Java and uses OpenMap(tm) for presenting geospatial
information, and as a JavaBeans(tm) component framework.

For detailed description see Wiki.

## Prerequisites ##

* JDK 1.6+ (http://www.oracle.com/technetwork/java/javase/)
* Maven

## Quick start ##

To be able to see AIS targets and possible own ship, the sensor should be
configurated. In the main window press the Setup button in the top and 
go to the Sensor tab. Choose either TCP or serial connection type for AIS and
configure TCP host/port or serial port.

If a separate sensor is used for GPS this can be configured the same way.

If the AIS source is not a transponder providing own ship information, an 
own ship can be simulated by choosing a vessel target present in the AIS stream.
In to bottom of the sensor tab enable Simulated GPS and enter MMSI. If the 
AIS source provides lots of targets, the targets shown can be limited by 
selecting a sensor range so targets farther than this distance away not will
be shown.

Press OK and restart application.
	
## Project structure ###

	|-- build
	|-- dist
	|-- extlib
	`-- src
	    `-- main
	        |-- java
	        `-- resources

* `build` - generated directory with compiled class files
* `extlib` - third party jar files
* `src/main/java` - source root
* `src/main/resources` - Resources like images, default settings, etc.
* `dist` - a generated directory with a compiled distributable version of the application.
  The application is run from within this directory.   

## Versioning ##

The version is controlled in `build.xml` as a property. The convention is to
use the format `<major>.<minor>-<dev version>` for non-final versions, and 
`<major>.<minor>` for final releases. E.g.

	<property name="version" value="2.0-PRE1" />
	
for first pre-version of 2.0 and

	<property name="version" value="2.0" />
	
for the final version. 

Minor versions are for fixes and small improvements, while a major version is
for the introduction of new functionality. Major and minor versions are
reflected in the branching of the project. Branching should be done in the 
following way.

    -|-- * -- * -- * 2.x  (master branch version 2.x)
     |             |
     |             `-- 2.y (branch for fix or small improvement)  
     |
	 `-- * -- * -- 3.0 (branch for new major version)
         |
	     `-- dev (branch for individual task in new version)
 

## ENC layer ##

ee-INS does not come with an ENC layer but with the possibility to add one as 
a plugin. Currently the only known of OpenMap ENC layer is a commercial one
from the danish company [Navicon](http://www.navicon.dk).

To use Navicon ENC layer with ee-INS please follow the steps below

1. Contact [Navicon sales](mailto:sales@navicon.dk) regarding a purchase of their 
   [ENC/S52 rendering engine](http://navicon.dk/site/products.html). Mention the following:
   * To be used with DMA ee-INS
   * Version for use with OpenMap 5
   * If you are using a 64-bit machine, ask for 64-bit dongle drivers
   
1. After purchasing you will receive a SDK and a USB dongle.
   Unpack the SDK in a directory. The SDK should at least contain the following folders
   `lib`, `native` and `data`.
   
1. Copy `.ant.properties` to `ant.properties`.

1. Edit `ant.properties` and set `navicon_enc=true` and set the path to SDK, e.g. 
   `navicon_enc_path=../navicon`.

1. Build with `ant`

1. Edit `dist/enc.properties` and set S-57/S-63 settings.

1. Run application

1. Enter Setup -> Map and enable ENC. Restart.


## Eclipse development ##

To use Eclipse as IDE just import project. Eclipse `.project` and settings files
are included.

Launch configuration `EeINS.launch` is included, so it is possible to run as Java 
application from Eclipse. You will need to do a manual build before running.

## Contribution ##

Fork the project and make pull requests. 

Try to use the component architecture as much as possible. Implement components and 
hook up to other components with the `findAndInit` method rather than hard-wiring.
Try to follow the coding standards already used in the project and document within
the code with Javadoc comments. For more extensive documentation use the Wiki.
