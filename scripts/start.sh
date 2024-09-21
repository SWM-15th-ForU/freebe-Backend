#!/bin/bash

# 환경 변수 로드
set -o allexport
source /home/ubuntu/freebe-backend/.env
set +o allexport

# 경로 및 파일 설정
ROOT_PATH="/home/ubuntu/freebe-backend"
JAR="$ROOT_PATH/application.jar"

APP_LOG="$ROOT_PATH/application.log"
ERROR_LOG="$ROOT_PATH/error.log"
START_LOG="$ROOT_PATH/start.log"

NOW=$(date +%c)

# JAR 파일 복사
echo "[$NOW] $JAR 복사 중..." >> $START_LOG
if cp $ROOT_PATH/build/libs/freebe-0.0.1-SNAPSHOT.jar $JAR; then
  echo "[$NOW] JAR 파일 복사 완료" >> $START_LOG
else
  echo "[$NOW] JAR 파일 복사 실패" >> $START_LOG
  exit 1
fi

# 애플리케이션 실행
echo "[$NOW] > $JAR 실행" >> $START_LOG
nohup java -jar $JAR > $APP_LOG 2> $ERROR_LOG &

# 실행된 프로세스의 PID 확인
NEW_SERVICE_PID=$(pgrep -f $JAR)
if [ ! -z "$NEW_SERVICE_PID" ]; then
  echo "[$NOW] > 새로운 서비스 PID: $NEW_SERVICE_PID" >> $START_LOG
else
  echo "[$NOW] > 서비스 시작 실패" >> $START_LOG
  exit 1
fi
