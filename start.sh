PORT=$1
PORT2=$2

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

echo "start PORT="$PORT",PID=$PID success"