#!/bin/bash

FLINK_VERSION=${2}


APP_HOME="$(cd `dirname $0`; pwd)"

EXTENDS_HOME="${APP_HOME}/extends"

JAR_NAME="dinky-admin"


if [ -z "${FLINK_VERSION}" ]; then
  # Obtain the Flink version under EXTENDSHOME, only perform recognition and do not perform any other operations, for prompt purposes
  FLINK_VERSION_SCAN=$(ls ${EXTENDS_HOME} | grep flink | awk -F 'flink' '{print $2}')
  # If FLINK_VERSION-SCAN is not empty, assign FLINK_VERSION-SCAN to FLINK_VERSION
  if [ -n "${FLINK_VERSION_SCAN}" ]; then
    FLINK_VERSION=${FLINK_VERSION_SCAN}
  fi
fi

# Check whether the flink version is specified
assertIsInputVersion() {
  # If FLINK_VERSION is still empty, prompt the user to enter the Flink version
  if [ -z "${FLINK_VERSION}" ]; then
    echo "The flink version is not specified and the flink version cannot be found under the extends directory. Please specify the flink version."
    exit 1
  fi
}

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
  assertIsInputVersion
  updatePid
  if [ -z "$pid" ]; then
    nohup java -Ddruid.mysql.usePingMethod=false -Dlog4j2.isThreadContextMapInheritable=true -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M -XX:+HeapDumpOnOutOfMemoryError -Xverify:none -cp "${CLASS_PATH}" org.dinky.Dinky  > /dev/null 2>&1 &
    echo $! >"${PID_PATH}"/${PID_FILE}
    echo "FLINK VERSION : $FLINK_VERSION"
    echo "........................................Start Dinky Successfully........................................"
  else
    echo "Dinky pid $pid is in ${PID_PATH}/${PID_FILE}, Please stop first !!!"
  fi
}

startOnPending() {
  assertIsInputVersion
  updatePid
  if [ -z "$pid" ]; then
    java -Ddruid.mysql.usePingMethod=false -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M -XX:+HeapDumpOnOutOfMemoryError -Xverify:none -cp "${CLASS_PATH}" org.dinky.Dinky
    echo "........................................Start Dinky Successfully........................................"
  else
    echo "Dinky pid $pid is in ${PID_PATH}/${PID_FILE}, Please stop first !!!"
  fi
}

startWithJmx() {
  assertIsInputVersion
  updatePid
  if [ -z "$pid" ]; then
    nohup java -Ddruid.mysql.usePingMethod=false -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M -XX:+HeapDumpOnOutOfMemoryError -Xverify:none "${JMX}" -cp "${CLASS_PATH}" org.dinky.Dinky  > /dev/null 2>&1 &
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
  assertIsInputVersion
  stop
  start
  echo "........................................Restart Successfully........................................"
}

restartWithJmx() {
  echo ""
  assertIsInputVersion
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
