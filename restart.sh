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

sh stop.sh $PORT
sh start.sh $PORT $PORT2