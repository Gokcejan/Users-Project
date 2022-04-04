#!/bin/sh
XMX=${XMX:-512m}
if test "$DEBUG" = "true"
then
    exec java -Xmx${XMX} -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n -Dspring.profiles.active="$PASSED_ENVIRONMENT" -jar /server.jar
fi
exec java -Xmx${XMX} -Dspring.profiles.active="$PASSED_ENVIRONMENT" -jar /server.jar
