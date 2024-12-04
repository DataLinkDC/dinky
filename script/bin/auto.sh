#!/bin/bash

# debug mode
#set -x

FLINK_VERSION=${2}

DINKY_HOME=${DINKY_HOME:-$(cd "$(dirname "$0")"; cd ..; pwd)}
JAVA_VERSION=$(java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}' | awk -F'.' '{print $1"."$2}')

APP_HOME="${DINKY_HOME}"

DINKY_LOG_PATH="${APP_HOME}/logs"
if [ ! -d "${DINKY_LOG_PATH}" ]; then
  mkdir -p "${DINKY_LOG_PATH}"
fi

CUSTOMER_JAR_PATH="${APP_HOME}/customJar"
if [ ! -d "${CUSTOMER_JAR_PATH}" ]; then
  mkdir -p "${CUSTOMER_JAR_PATH}"
fi

EXTENDS_HOME="${APP_HOME}/extends"

JAR_NAME="dinky-admin"


if [ -z "${FLINK_VERSION}" ]; then
  # Obtain the Flink version under EXTENDSHOME, only perform recognition and do not perform any other operations, for prompt purposes
  FLINK_VERSION_SCAN=$(ls -n ${EXTENDS_HOME} |  grep '^d' | grep flink | awk -F 'flink' '{print $2}')
  # If FLINK_VERSION-SCAN is not empty, assign FLINK_VERSION-SCAN to FLINK_VERSION
  if [ -n "${FLINK_VERSION_SCAN}" ]; then
    FLINK_VERSION=${FLINK_VERSION_SCAN}
  fi
fi

echo "DINKY_HOME : ${APP_HOME} , JAVA_VERSION : ${JAVA_VERSION} , FLINK_VERSION : ${FLINK_VERSION}"

# Check whether the flink version is specified
assertIsInputVersion() {
  # If FLINK_VERSION is still empty, prompt the user to enter the Flink version
  if [ -z "${FLINK_VERSION}" ]; then
    echo "The flink version is not specified and the flink version cannot be found under the extends directory. Please specify the flink version."
    exit 1
  fi
}

# Use FLINK_HOME:
CLASS_PATH="${APP_HOME}:${APP_HOME}/lib/*:${APP_HOME}/config:${EXTENDS_HOME}/*:${CUSTOMER_JAR_PATH}/*:${EXTENDS_HOME}/flink${FLINK_VERSION}/dinky/*:${EXTENDS_HOME}/flink${FLINK_VERSION}/flink/*:${EXTENDS_HOME}/flink${FLINK_VERSION}/*"
PID_FILE="dinky.pid"

# Log configuration file path
LOG_CONFIG=${APP_HOME}/config/log4j2.xml

if [ ${JAVA_VERSION} == "1.8" ];then
  # JVM options G1GC and OOM dump ; Note: Do not set the DisableExplicitGC parameter. Because there is a call to System. gc() in the code.
   GC_OPT="-XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -XX:+PrintGCCause -Xloggc:${APP_HOME}/logs/gc-%t.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=20M"
else
   GC_OPT="-XX:+UseG1GC -XX:+PrintGCDetails -Xloggc:${APP_HOME}/logs/gc-%t.log"
fi

# OOM dump path
OOM_OPT="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${APP_HOME}/logs/heapdump.hprof"
# JVM parameters and log path
PARAMS_OPT="-Ddinky.logs.path=${DINKY_LOG_PATH} -Ddinky.root.path=${APP_HOME} -Ddruid.mysql.usePingMethod=false -Dlog4j2.isThreadContextMapInheritable=true"
# JAR parameters
JAR_PARAMS_OPT="--logging.config=${LOG_CONFIG}"
# JMX path
JMX="-javaagent:$APP_HOME/lib/jmx_prometheus_javaagent-0.20.0.jar=10087:$APP_HOME/config/jmx/jmx_exporter_config.yaml"
#JVM OPTS
JVM_OPTS="-Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M"

# Check whether the pid path exists
PID_PATH="${APP_HOME}/run"

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
    nohup java ${PARAMS_OPT} ${JVM_OPTS} ${OOM_OPT} ${GC_OPT} -Xverify:none -cp "${CLASS_PATH}" org.dinky.Dinky ${JAR_PARAMS_OPT}  > ${DINKY_LOG_PATH}/dinky-start.log 2>&1 &
    echo $! >"${PID_PATH}"/${PID_FILE}
    echo "........................................Start Dinky Done........................................"
    echo "current log path : ${DINKY_LOG_PATH}/dinky-start.log , you can execute tail -fn1000 ${DINKY_LOG_PATH}/dinky-start.log to watch the log"
  else
    echo "Dinky pid $pid is in ${PID_PATH}/${PID_FILE}, Please stop first !!!"
  fi
}

startOnPending() {
  assertIsInputVersion
  updatePid
  if [ -z "$pid" ]; then
    java ${PARAMS_OPT} ${JVM_OPTS} ${OOM_OPT} ${GC_OPT} -Xverify:none -cp "${CLASS_PATH}" org.dinky.Dinky  ${JAR_PARAMS_OPT}
    echo "........................................Start Dinky Successfully........................................"
  else
    echo "Dinky pid $pid is in ${PID_PATH}/${PID_FILE}, Please stop first !!!"
  fi
}

startWithJmx() {
  assertIsInputVersion
  updatePid
  if [ -z "$pid" ]; then
    nohup java ${PARAMS_OPT} ${JVM_OPTS} ${OOM_OPT} ${GC_OPT} -Xverify:none "${JMX}" -cp "${CLASS_PATH}" org.dinky.Dinky  ${JAR_PARAMS_OPT}  > ${DINKY_LOG_PATH}/dinky-start.log 2>&1 &
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
