#!/bin/bash
java -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -classpath .:../resources:../bin:../lib/sleep.jar rero.test.WindowTest
