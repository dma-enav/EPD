@echo off

set CLASSPATH=.;lib/*

java.exe -Xmn256M -Xms512M -Xmx1024M -Dfile.encoding=ISO-8859-1 -Djava.library.path=./navicon/native/ dk.frv.enav.esd.ESD %*
