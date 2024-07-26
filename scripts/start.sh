#!/bin/bash

chmod 777 ./scripts/start.sh
chmod 777 /home/ubuntu/freebe-backend/start.log
chmod 777 /home/ubuntu/freebe-backend/application.jar
chmod 777 /home/ubuntu/freebe-backend/application.log

ROOT_PATH="/home/ubuntu/freebe-backend"
JAR="$ROOT_PATH/application.jar"

APP_LOG="$ROOT_PATH/application.log"
ERROR_LOG="$ROOT_PATH/error.log"
START_LOG="$ROOT_PATH/start.log"

NOW=$(date +%c)

echo "[$NOW] $JAR 복사" >> $START_LOG
cp $ROOT_PATH/build/libs/freebe-0.0.1-SNAPSHOT.jar $JAR

echo "[$NOW] > $JAR 실행" >> $START_LOG
sudo nohup java -jar $JAR > $APP_LOG 2> $ERROR_LOG &

SERVICE_PID=$(pgrep -f $JAR)
sudo echo "[$NOW] > 서비스 PID: $SERVICE_PID" >> $START_LOG
