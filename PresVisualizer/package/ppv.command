#!/bin/sh

cd `dirname $0`
java -Xms512m -Xmx1024m -Dsun.jnu.encoding=UTF-8 -jar ppv.jar

