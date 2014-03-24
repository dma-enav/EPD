#!/bin/bash

# Genereate new key from keystore. Only done once.
# NOTE! All JAR:s must be signed with same key, if there are multiple Jar:s
#keytool -genkey -alias epd -keystore epd.key -validity 10000

# Sign epd-ship jar
jarsigner -keystore epd.key -storepass epdepd ../EPD/distribution/EPD-Ship-Singlejar/target/epd-ship-dist-0.1-SNAPSHOT.jar epd
