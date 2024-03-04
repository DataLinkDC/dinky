#!/bin/bash


APP_HOME="$(cd `dirname $0`; pwd)"

EXTENDS_HOME="${APP_HOME}/extends"

# 暂时使用自动扫描版本拿到第一个, 目前每个版本的 extends 下只有一个 flink版本
AUTO_SCAN_FLINK_VERSION=$(ls -A | sed 's/flink//g' | grep -E "^[0-9]+(\.[0-9]+)?$" | head -1)

if [ -z "$AUTO_SCAN_FLINK_VERSION" ]; then
  echo "No flink version found in ${EXTENDS_HOME} directory, please check it."
  exit 1
fi

FLINK_VERSION=${2:-$AUTO_SCAN_FLINK_VERSION}

# 检测当前输入的flink版本是否存在
if [ ! -d "${EXTENDS_HOME}/flink${FLINK_VERSION}" ]; then
  echo "flink${FLINK_VERSION} is not exist in ${EXTENDS_HOME} directory, please check it."
  exit 1
fi

JAR_NAME="dinky-admin"



# Use FLINK_HOME:
CLASS_PATH="${APP_HOME}:${APP_HOME}/lib/*:${APP_HOME}/config:${EXTENDS_HOME}/*:${EXTENDS_HOME}/flink${FLINK_VERSION}/dinky/*:${EXTENDS_HOME}/flink${FLINK_VERSION}/*"
PID_FILE="dinky.pid"

# JMX path
JMX="-javaagent:$APP_HOME/lib/jmx_prometheus_javaagent-0.20.0.jar=10087:$APP_HOME/config/jmx/jmx_exporter_config.yaml"

# Check whether the pid path exists
PID_PATH="$(cd "$(dirname "$0")";pwd)/run"

if [ -d "${PID_PATH}" ];then
    echo "${PID_PATH} is already exist." >> /dev/null
else
    mkdir -p  "${PID_PATH}"
fi

# Check whether the pid file exists
if [ -f "${PID_PATH}/${PID_FILE}" ];then
    echo "${PID_PATH}/${PID_FILE} is already exist." >> /dev/null
else
    touch "${PID_PATH}"/${PID_FILE}
fi

tips() {
  echo ""
  echo "WARNING!!!......Tips, please use command: sh auto.sh [start|startOnPending|startWithJmx|stop|restart|restartWithJmx|status].   For example: sh auto.sh start  "
  echo ""
  exit 1
}

updatePid() {
  pid=$(ps -ef | grep [D]inky  | awk '{print $2}' | head -1)
  echo $pid >"${PID_PATH}"/${PID_FILE}
}

start() {
  updatePid
  if [ -z "$pid" ]; then
    nohup java -Ddruid.mysql.usePingMethod=false -Dlog4j2.isThreadContextMapInheritable=true -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M -XX:+HeapDumpOnOutOfMemoryError -Xverify:none -cp "${CLASS_PATH}" org.dinky.Dinky  > /dev/null
    echo $! >"${PID_PATH}"/${PID_FILE}
    echo "FLINK VERSION : $FLINK_VERSION"
    echo "........................................Start Dinky Successfully........................................"
  else
    echo "Dinky pid $pid is in ${PID_PATH}/${PID_FILE}, Please stop first !!!"
  fi
}

startOnPending() {
  updatePid
  if [ -z "$pid" ]; then
    java -Ddruid.mysql.usePingMethod=false -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M -XX:+HeapDumpOnOutOfMemoryError -Xverify:none -cp "${CLASS_PATH}" org.dinky.Dinky
    echo "........................................Start Dinky Successfully........................................"
  else
    echo "Dinky pid $pid is in ${PID_PATH}/${PID_FILE}, Please stop first !!!"
  fi
}

startWithJmx() {
  updatePid
  if [ -z "$pid" ]; then
    nohup java -Ddruid.mysql.usePingMethod=false -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M -XX:+HeapDumpOnOutOfMemoryError -Xverify:none "${JMX}" -cp "${CLASS_PATH}" org.dinky.Dinky  > /dev/null
#    echo $! >"${PID_PATH}"/${PID_FILE}
    updatePid
    echo "........................................Start Dinky with Jmx Successfully.....................................
    ..."
  else
    echo "Dinky pid $pid is in ${PID_PATH}/${PID_FILE}, Please stop first !!!"
  fi
}

stop() {
  updatePid
  pid=$(cat "${PID_PATH}"/${PID_FILE})
  if [ -z $pid ]; then
    echo "Dinky pid is not exist in ${PID_PATH}/${PID_FILE} ,skip stop."
  else
    kill -9 $pid
    sleep 1
    echo "........................................Stop Dinky Successfully....................................."
    rm -f "${PID_PATH}"/${PID_FILE}
  fi
}

status() {
  updatePid
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

restartWithJmx() {
  echo ""
  stop
  startWithJmx
  echo "........................................Restart with Jmx Successfully........................................"
}

case "$1" in
"start")
  start
  ;;
"startOnPending")
  startOnPending
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
"restartWithJmx")
  restartWithJmx
  ;;
*)
  tips
  ;;
esac
