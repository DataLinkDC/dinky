#!/bin/bash

FLINK_VERSION=${2:-1.14}

JAR_NAME="dlink-admin"

# Use FLINK_HOME:
# CLASS_PATH="./lib/*:./plugins/*:./plugins/flink${FLINK_VERSION}/*:$FLINK_HOME/lib/*"
CLASS_PATH="./lib/*:config:./plugins/*:./plugins/flink${FLINK_VERSION}/*"

PID_FILE="dinky.pid"

# JMX path
APP_HOME="$(cd `dirname $0`; pwd)"
JMX="-javaagent:$APP_HOME/lib/jmx_prometheus_javaagent-0.16.1.jar=10087:$APP_HOME/config/jmx/jmx_exporter_config.yaml"

# Check whether the pid path exists
PID_PATH="$(cd "$(dirname "$0")";pwd)/run"

if [ -d ${PID_PATH} ];then
    echo "${PID_PATH} is already exist." >> /dev/null
else
    mkdir -p  ${PID_PATH}
fi

# Check whether the pid file exists
if [ -f "${PID_PATH}/${PID_FILE}" ];then
    echo "${PID_PATH}/${PID_FILE} is already exist." >> /dev/null
else
    touch ${PID_PATH}/${PID_FILE}
fi

tips() {
  echo ""
  echo "WARNING!!!......Tips, please use command: sh auto.sh [start|startWithJmx|stop|restart|status].   For example: sh auto.sh start  "
  echo ""
  exit 1
}

start() {
  pid=$(cat ${PID_PATH}/${PID_FILE})
  if [ -z $pid ]; then
    nohup java -Ddruid.mysql.usePingMethod=false -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M -XX:+HeapDumpOnOutOfMemoryError -Xverify:none -cp ${CLASS_PATH} org.dinky.Dinky >/dev/null 2>&1 &
    echo $! >${PID_PATH}/${PID_FILE}
    echo "FLINK VERSION : $FLINK_VERSION"
    echo "........................................Start Dinky Successfully........................................"
  else
    echo "Dinky pid $pid is in ${PID_PATH}/${PID_FILE}, Please stop first !!!"
  fi
}

startWithJmx() {
  pid=$(cat ${PID_PATH}/${PID_FILE})
  if [ -z $pid ]; then
    nohup java -Ddruid.mysql.usePingMethod=false -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M -XX:+HeapDumpOnOutOfMemoryError -Xverify:none ${JMX} -cp ${CLASS_PATH} org.dinky.Dinky >/dev/null 2>&1 &
    echo $! >${PID_PATH}/${PID_FILE}
    echo "........................................Start Dlink Successfully........................................"
  else
    echo "Dlink pid $pid is in ${PID_PATH}/${PID_FILE}, Please stop first !!!"
  fi
}

stop() {
  pid=$(cat ${PID_PATH}/${PID_FILE})
  if [ -z $pid ]; then
    echo "Dinky pid is not exist in ${PID_PATH}/${PID_FILE}"
  else
    kill -9 $pid
    sleep 1
    echo "........................................Stop Dinky Successfully....................................."
    echo " " >${PID_PATH}/${PID_FILE}
  fi
}

status() {
  pid=$(cat ${PID_PATH}/${PID_FILE})
  if [ -z $pid ]; then
    echo ""
    echo "Service ${JAR_NAME} is not running!"
    echo ""
    exit 1
  else
    echo ""
    echo "Service ${JAR_NAME} is running. It's pid=${pid}"
    echo ""
  fi
}

restart() {
  echo ""
  stop
  start
  echo "........................................Restart Successfully........................................"
}

case "$1" in
"start")
  start
  ;;
"startWithJmx")
  startWithJmx
  ;;
"stop")
  stop
  ;;
"status")
  status
  ;;
"restart")
  restart
  ;;
*)
  tips
  ;;
esac
