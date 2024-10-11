#!/bin/bash

# 환경 변수 로드
set -o allexport
source /home/ubuntu/freebe-backend/.env
set +o allexport

# 경로 및 파일 설정
ROOT_PATH="/home/ubuntu/freebe-backend"
JAR="$ROOT_PATH/application.jar"
NEW_RELIC_CONFIG_FILE="$ROOT_PATH/newrelic/newrelic.yml"
NEW_RELIC_JAR_FILE="$ROOT_PATH/newrelic/newrelic.jar"

APP_LOG="$ROOT_PATH/application.log"
ERROR_LOG="$ROOT_PATH/error.log"
START_LOG="$ROOT_PATH/start.log"
NOW=$(date "+%Y %b %d %a %H:%M:%S")

echo "--------------------------------------------------" >> $START_LOG

# JAR 파일 복사
echo "[$NOW] $JAR 복사 중..." >> $START_LOG
if cp $ROOT_PATH/build/libs/freebe-0.0.1-SNAPSHOT.jar $JAR; then
  echo "[$NOW] JAR 파일 복사 완료" >> $START_LOG
else
  echo "[$NOW] JAR 파일 복사 실패" >> $START_LOG
  exit 1
fi

# New Relic YML 파일이 존재하는지 확인
if [ ! -f "$NEW_RELIC_CONFIG_FILE" ]; then
    echo "New Relic 설정 파일이 존재하지 않습니다." >> $START_LOG
    exit 1
fi

# 애플리케이션 실행
echo "[$NOW] > $JAR 실행" >> $START_LOG
nohup java -javaagent:$NEW_RELIC_JAR_FILE -jar $JAR > $APP_LOG 2> $ERROR_LOG &

# 실행된 프로세스의 PID 확인
NEW_SERVICE_PID=$(pgrep -f $JAR)
if [ ! -z "$NEW_SERVICE_PID" ]; then
  echo "[$NOW] > 새로운 서비스 PID: $NEW_SERVICE_PID" >> $START_LOG
else
  echo "[$NOW] > 서비스 시작 실패" >> $START_LOG
  exit 1
fi