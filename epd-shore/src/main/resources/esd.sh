#!/bin/sh

CP=.
for i in `ls lib/*.jar`
do
  CP=${CP}:${i}
done

java.exe -cp $CP -Xmn256M -Xms512M -Xmx1024M -Dfile.encoding=ISO-8859-1 -Djava.library.path=./navicon/native/ dk.frv.enav.esd.ESD
