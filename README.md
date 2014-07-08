# e-Navigation Prototype Displays #

## Introduction ##
   
EPD (e-Navigation Prototype Displays) is consisting of two applications for demonstrating potential e-navigation solutions. An ECDIS like ship side application and a shore side application.
   
The applications is in Java and uses OpenMap(tm) for presenting geospatial
information, and as a JavaBeans(tm) component framework.

## Prerequisites ##

  * Java 8
  * Maven 3

## Building ##

    mvn clean install
    
## Running ##

Runnable jar files are located here

    distribution/EPD-Ship-Singlejar/target/epd-ship-dist-X.Y-SNAPSHOT.jar
    distribution/EPD-Shore-Singlejar/target/epd-shore-dist-X.Y-SNAPSHOT.jar
    
To run from command line

    java -jar distribution/EPD-Ship-Singlejar/target/epd-ship-dist-X.Y-SNAPSHOT.jar

Folders with settings files are created in the home folder

    <home folder>/.epd-ship
    <home folder>/.epd-shore

## Creating Windows EXE files ##

Windows execulateables can be created by using the followig Maven profile

    mvn clean install -Pexe

The executables will be located in here

    distribution/EPD-Ship-Singlejar/target/epd-ship.exe
    distribution/EPD-Shore-Singlejar/target/epd-shore.exe

## Eclipse development ##

Use M2 Eclipse plugin or use Maven eclipse target

    mvn eclipse:eclipse

## Virtual AIS transponder ##

The virtual transponder can provide a live anonymized AIS feed. The following settings can be used for the transponder:

Host:port: `ais.e-navigation.net:8002` 
Username/password: `anon/anon`

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

## Design ##

EPD uses a component based design to facilitate an event driven architecture. Functionality is encapsulated in components. Events come from sensor and user input.

Using components allows easy collaborative development as components can be developed independently. Components automatically identify each others in the BeanContext, so no hard wiring is necessary. Components can also be moved between different projects using the same component framework. E.g. OpenMap.

## Contribution ##

Fork the project and make pull requests. 

Try to use the component architecture as much as possible. Implement components and 
hook up to other components with the `findAndInit` method rather than hard-wiring.
Try to follow the coding standards already used in the project and document within
the code with Javadoc comments. For more extensive documentation use the Wiki.

## ENC layer ##

EPD does not come with an ENC layer but with the possibility to add one as 
a plugin. Currently the only known OpenMap ENC layer is a commercial one
from the danish company [Navicon](http://www.navicon.dk).

To use Navicon ENC layer with EPD-ship please follow the steps below

1. Contact [Navicon sales](mailto:sales@navicon.dk) regarding a purchase of their 
   [ENC/S52 rendering engine](http://navicon.dk/site/products.html). Mention the following:
   * To be used with DMA E-navigation Prototype Display
   * Version for use with OpenMap 5
   * If you are using a 64-bit machine, ask for 64-bit dongle drivers
   
1. After purchasing you will receive a SDK and a USB dongle.
   Unpack the SDK in a directory. The SDK should at least contain the following folders
   `lib`, `native` and `data`.

1. Run installer in `native`.

1. Place the `lib` folder in `<home folder>/.epd-ship`

1. Create the folder: `<home folder>/.epd-ship/navion`

1. Place the folders `native` and `data` in the newly created folder.

1. In on a 32-bit system. Remove `native/keylock.dll` and rename `native/keylock32.dll` -> `native/keylock.dll`

1. Copy `<home folder>/.epd-ship/enc_navicon.properties` to `<home folder>/.epd-ship/enc.properties`.
 
1. Edit `<home folder>/.epd-ship/enc.properties` and set charts location in `enc.s57PathLocation`.

1. Run application

1. Enter Setup -> Map and enable ENC. Restart.

## Utilities ##

The module `epd-util` contains some utlities to use with EPD. 

### Route from AIS generation ###

A route file can be generated given an AIS file given MMSI number. Example:

    java -jar epd-util/target/epd-util-0.1-SNAPSHOT.jar aistoroute -in ~/tmp/aisdump.txt -out ~/tmp/route.txt -mmsi 304913000

## Attribution

Some icons by Yusuke Kamiyamane. Licensed under a Creative Commons Attribution 3.0 License.
