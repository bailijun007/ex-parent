#!/bin/bash

remote_debug_port=8329
rmi_server_hostname=18.177.137.233
jmx_remote_port=8327
config_path=config
main_class=com.hp.sh.expv3.ExBbMatchApplication

java \
-cp "libs/*" \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=~/${remote_debug_port}.hprof \
-Xdebug -Xrunjdwp:transport=dt_socket,suspend=n,server=y,address=${remote_debug_port} \
-Djava.rmi.server.hostname=${rmi_server_hostname} \
-Dcom.sun.management.jmxremote.port=${jmx_remote_port} \
-Dcom.sun.management.jmxremote.rmi.port=${jmx_remote_port} \
-Dcom.sun.management.jmxremote=true \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dspring.config.location=${config_path}/application.properties \
-Dlogging.config=${config_path}/logback.xml \
-Dbbmatch.id.serverId=1 \
-Dbbmatch.matchGroupId=1 \
${main_class}

# notice
# 1. change bbmatch.id.serverId
# 2. bbmatch.matchGroupId
# java \
# -cp "libs/*" \
# -javaagent:agent/skywalking-agent.jar \
# -Dspring.config.location=config/application.properties \
# -Dlogging.config=config/log4j2.xml \
# com.hupa.exp.tds.TdsMainEntrance