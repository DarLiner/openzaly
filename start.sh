#!/bin/bash

#### echo akaxin logo and desc
echo "
    _      _  __     _     __  __  ___   _   _ 
   / \    | |/ /    / \    \ \/ / |_ _| | \ | |
  / _ \   | ' /    / _ \    \  /   | |  |  \| |
 / ___ \  | . \   / ___ \   /  \   | |  | |\  |
/_/   \_\ |_|\_\ /_/   \_\ /_/\_\ |___| |_| \_|
     
Akaxin is an open source and free proprietary IM software，you can build private openzaly-server for everyone in any server.
openzaly-version : 0.5.4
java-version : JDK 1.8+
maven-version : 3.0+

"

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

#### echo server is starting
echo "【OK】openzaly-server is starting 【tcp-port:"$PORT" http-port:"$PORT2"】"

JAVA_JAR="openzaly-server"
PID=$(ps -ef|grep $JAVA_JAR|grep $PORT |head -1| awk '{printf $2}')

###if server is running, exit and echo error
if [ $PID > 0 ]; then
    echo "【ERROR】openzaly-server is running PID:"$PID
    echo "【ERROR】openzaly-server start failure"
    exit
fi

java -Dsite.port=$PORT -Dhttp.port=$PORT2 -jar openzaly-server.jar >>stdout.log 2>&1 &

PID=$(ps -ef|grep $JAVA_JAR|grep $PORT |head -1| awk '{printf $2}')

if [ $? -eq 0 ]; then
    echo "【OK】openzaly-server tcp-port:"$PORT",http-port:$PORT2,PID:$PID"
    echo "【OK】openzaly-server is started successfully 【PID:"$PID"】"
else
    echo "【ERROR】openzaly-server is started failed"
    echo "exit..."
    exit
fi

echo ""
echo ""