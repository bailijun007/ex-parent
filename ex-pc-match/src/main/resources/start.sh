#!/bin/bash

remote_debug_port=8019
rmi_server_hostname=192.168.0.88
jmx_remote_port=8017
config_path=config
main_class=com.hp.sh.expv3.ExMatchApplication

java \
-Dagent.instance_uuid=1 \
-javaagent:/root/hupa/pcmatch/agent/skywalking-agent.jar \
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
-Dlogging.config=/root/hupa/pcmatch/config/logback.xml \
-Dpcmatch.id.serverId=1 \
-Dpcmatch.matchGroupId=1 \


${main_class}

# -javaagent:/root/hupa/pcmatch/agent/skywalking-agent.jar \
# java \
# -cp "libs/*" \
# -javaagent:agent/skywalking-agent.jar \
# -Dspring.config.location=config/application.properties \
# -Dlogging.config=config/log4j2.xml \
# com.hupa.exp.tds.TdsMainEntrance