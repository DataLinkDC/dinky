#!/bin/bash
# 定义变量
# 要运行的jar包路径，加不加引号都行。 注意：等号两边不能有空格，否则会提示command找不到
JAR_NAME="dlink-admin"
# 如果需要将 FLINK 依赖直接加入启动脚本，在 CLASS_PATH 中末尾追加 :$FLINK_HOME/lib/*
CLASS_PATH="./lib/*:config:html:./plugins/*"
#if [ ! -d ${HADOOP_HOME} ]; then
#  echo 'WARNING!!!...not find HADOOP_HOME for CLASSPATH.'
#else
#  export HADOOP_HOME="${HADOOP_HOME}"
#fi

PIDFILE="dlink.pid"
#检查PID path 是否存在
PIDPATH="$(cd "$(dirname "$0")";pwd)/run"
if [ -d ${PIDPATH} ];then
    echo "${PIDPATH} is already exist" >> /dev/null
else
    #echo "create ${PIDPATH}"
    mkdir -p  ${PIDPATH}
fi

#检查PID File 是否存在
if [ -f "${PIDPATH}/${PIDFILE}" ];then
    echo "${PIDPATH}/${PIDFILE} is already exist" >> /dev/null
else
    touch ${PIDPATH}/${PIDFILE}
fi

# 首次启动时候自动创建plugins文件夹和引用flink\lib包！
if [ ! -d "./plugins" ]; then
  echo 'mkdir plugins now'
  mkdir plugins
#  cd plugins
#  if [ ! -d ${FLINK_HOME} ]; then
#    echo 'WARNING!!!...没有找到FLINK_HOME环境变量，无法引用Flink/lib到plugins，请手动引用或复制Flink jar到plugins文件夹'
#    echo 'WARNING!!!...not find FLINK_HOME environment variable to reference Flink/lib to plugins, please reference or copy Flink jar to the plugins folder manually!!'
#  else
#    ln -s ${FLINK_HOME}/lib
#    cd ..
#  fi
fi

# 如果输入格式不对，给出提示！
tips() {
  echo ""
  echo "WARNING!!!......Tips, please use command: sh auto.sh [start|stop|restart|status].   For example: sh auto.sh start  "
  echo ""
  exit 1
}


# 启动方法
start() {

  pid=$(cat ${PIDPATH}/${PIDFILE})
  if [ -z $pid ]; then
    nohup java -Ddruid.mysql.usePingMethod=false -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M -XX:+HeapDumpOnOutOfMemoryError -Xverify:none -cp ${CLASS_PATH} com.dlink.Dlink >/dev/null 2>&1 &
    echo $! >${PIDPATH}/${PIDFILE}
    echo "........................................Start Dlink Successfully........................................"

  else
    echo "Dlink pid $pid is in ${PIDPATH}/${PIDFILE}, Please stop first !!!"
  fi
}

# 停止方法
stop() {
  pid=$(cat ${PIDPATH}/${PIDFILE})
  if [ -z $pid ]; then
    echo "Dlink pid is not exist in ${PIDPATH}/${PIDFILE}"
  else
    kill -9 $pid
    sleep 1
    echo "........................................Stop Dlink Successfully....................................."
    echo " " >${PIDPATH}/${PIDFILE}
  fi
}

# 输出运行状态方法
status() {
  # 重新获取一下pid，因为其它操作如stop、restart、start等会导致pid的状态更新
  pid=$(cat ${PIDPATH}/${PIDFILE})
  if [ -z $pid ]; then
    echo ""
    echo "Service ${JAR_NAME} is not running!"
    echo ""
  else
    echo ""
    echo "Service ${JAR_NAME} is running. It's pid=${pid}"
    echo ""
  fi
}

# 重启方法
restart() {
  echo ""
  stop
  start
  echo "........................................Restart Successfully........................................"
}

# 根据输入参数执行对应方法，不输入则执行tips提示方法
case "$1" in
"start")
  start
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
