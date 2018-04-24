#!/bin/bash

PORT=$1
JAVA_JAR="openzaly-boot-jar-with-dependencies"

##set tcp port
if [ -n $PORT ]; then
	PORT=2021
fi
	
echo "------------stop process PORT="$PORT

PID=$(ps -ef|grep $JAVA_JAR|grep $PORT |head -1| awk '{printf $2}')

if [ $? -eq 0 ]; then
    echo "PID="$PID
else
    echo "Process PORT=$PORT not exit"
    exit
fi


kill -9 ${PID}

if [ $? -eq 0 ];then
    echo "kill PORT=$PORT PID=$PID success"
else
    echo "kill $PORT fail"
fi
