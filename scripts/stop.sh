#!/bin/bash

ROOT_PATH="/home/ubuntu/freebe-backend"
JAR="$ROOT_PATH/application.jar"
STOP_LOG="$ROOT_PATH/stop.log"
NOW=$(date "+%Y %b %d %a %H:%M:%S")

echo "--------------------------------------------------" >> $STOP_LOG

# 실행 중인 모든 프로세스의 PID를 가져옴
SERVICE_PIDS=$(pgrep -f $JAR)

if [ -z "$SERVICE_PIDS" ]; then
  echo "[$NOW] 실행 중인 기존 프로세스 없음" >> $STOP_LOG
else
  echo "[$NOW] 기존 프로세스 종료 시도: PID $SERVICE_PIDS" >> $STOP_LOG

  for PID in $SERVICE_PIDS; do
    echo "[$NOW] kill $PID 실행 중" >> $STOP_LOG
    kill "$PID"
    sleep 1

    if kill -0 "$PID" 2>/dev/null; then
      echo "[$NOW] 프로세스 강제 종료 시도: PID $PID" >> $STOP_LOG
      kill -9 "$PID"
      echo "[$NOW] 프로세스 강제 종료됨: PID $PID" >> $STOP_LOG
    else
      echo "[$NOW] 프로세스가 성공적으로 종료됨: PID $PID" >> $STOP_LOG
    fi
  done
fi
