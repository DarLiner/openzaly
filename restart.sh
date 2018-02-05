#!/bin/bash
PORT=$1
PORT2=$2
sh stop.sh $PORT
sh start.sh $PORT $PORT2