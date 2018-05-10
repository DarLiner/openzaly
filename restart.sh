#!/bin/bash
PORT=$1
PORT2=$2

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

##set tcp port
if [ -n $PORT ]; then
	PORT=2021
fi
	
##set http port
if [ -n $PORT2 ]; then
	PORT2=8080 
fi

sh stop.sh $PORT 1
sh start.sh $PORT $PORT2 1