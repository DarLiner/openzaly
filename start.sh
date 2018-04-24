#!/bin/bash

PORT=$1
PORT2=$2

##set tcp port
if [ -n $PORT ]; then
	PORT=2021
fi
	
##set http port
if [ -n $PORT2 ]; then
	PORT2=8080 
fi

echo "------------start openzaly tcp:port="$PORT" http port="$PORT2

JAVA_JAR="openzaly-boot-jar-with-dependencies"
PID=$(ps -ef|grep $JAVA_JAR|grep $PORT |head -1| awk '{printf $2}')

#如果存在PID，直接退出，不存在继续执行
if [ $PID > 0 ]; then
    echo "Exist PID:"$PID
    exit
else
    echo "execute Java -P="$PORT
fi

java -Dsite.port=$PORT -Dhttp.port=$PORT2 -jar openzaly-boot-jar-with-dependencies.jar >>stdout.log 2>&1 &

PID=$(ps -ef|grep $JAVA_JAR|grep $PORT |head -1| awk '{printf $2}')

if [ $? -eq 0 ]; then
    echo "Process id:$PID"
else
    echo "Process $PORT not exit"
    exit
fi

echo "start TCP PORT="$PORT",HTTP PORT=$PORT2,PID=$PID success"