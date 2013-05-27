# e-Navigation Prototype Displays #

## Introduction ##
   
EPD (e-Navigation Prototype Display) is an ECDIS like
application for demonstrating possible e-Navigation services.

This project contains two module, EPD-Ship and EPD-Shore each focusing on various services from the point of view of the Ship and of the Shore control center.
   
The application is in Java and uses OpenMap(tm) for presenting geospatial
information, and as a JavaBeans(tm) component framework.

For detailed description see Wiki.

## Prerequisites ##

* JDK 1.7 (http://www.oracle.com/technetwork/java/javase/)
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
	

## ENC layer ##

EPD-ship does not come with an ENC layer but with the possibility to add one as 
a plugin. Currently the only known of OpenMap ENC layer is a commercial one
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
   
1. Copy `.ant.properties` to `ant.properties`.

1. Edit `ant.properties` and set `navicon_enc=true` and set the path to SDK, e.g. 
   `navicon_enc_path=../navicon`.

1. Build with `ant`

1. Edit `dist/enc.properties` and set S-57/S-63 settings.

1. Run application

1. Enter Setup -> Map and enable ENC. Restart.


## Eclipse development ##

Use Maven eclipse target

    mvn eclipse:eclipse

or m2 Eclipse plugin.

## Contribution ##

Fork the project and make pull requests. 

Try to use the component architecture as much as possible. Implement components and 
hook up to other components with the `findAndInit` method rather than hard-wiring.
Try to follow the coding standards already used in the project and document within
the code with Javadoc comments. For more extensive documentation use the Wiki.
