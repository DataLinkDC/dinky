#!/bin/bash
# 定义变量
# 要运行的jar包路径，加不加引号都行。 注意：等号两边 不能 有空格，否则会提示command找不到
JAR_NAME="./dlink-admin.jar"
SETTING="-Djava.ext.dirs=./lib"
 
# 如果输入格式不对，给出提示！
tips() {
	echo ""
	echo "WARNING!!!......Tips, please use command: sh auto.sh [start|stop|restart|status].   For example: sh auto.sh start  "
	echo ""
	exit 1
}
 
 
# 启动方法
start() {
        # 重新获取一下pid，因为其它操作如stop会导致pid的状态更新
	pid=`ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}'`
        # -z 表示如果$pid为空时执行
	if [ -z $pid ]; then
        nohup java $SETTING -jar -Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M $JAR_NAME > dlink.log 2>&1 &
        pid=`ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}'`
		echo ""
        echo "Service ${JAR_NAME} is starting！pid=${pid}"
		echo "........................Start successfully！........................."
	else
		echo ""
		echo "Service ${JAR_NAME} is already running,it's pid = ${pid}. If necessary, please use command: sh auto.sh restart."
		echo ""
	fi
}
 
# 停止方法
stop() {
		# 重新获取一下pid，因为其它操作如start会导致pid的状态更新
	pid=`ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}'`
        # -z 表示如果$pid为空时执行。 注意：每个命令和变量之间一定要前后加空格，否则会提示command找不到
	if [ -z $pid ]; then
		echo ""
        echo "Service ${JAR_NAME} is not running! It's not necessary to stop it!"
		echo ""
	else
		kill -9 $pid
		echo ""
		echo "Service stop successfully！pid:${pid} which has been killed forcibly!"
		echo ""
	fi
}
 
# 输出运行状态方法
status() {
        # 重新获取一下pid，因为其它操作如stop、restart、start等会导致pid的状态更新
	pid=`ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}'`
        # -z 表示如果$pid为空时执行。注意：每个命令和变量之间一定要前后加空格，否则会提示command找不到
	if [ -z $pid ];then
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
	echo ".............................Restarting.............................."
	echo "....................................................................."
		# 重新获取一下pid，因为其它操作如start会导致pid的状态更新
	pid=`ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}'`
        # -z 表示如果$pid为空时执行。 注意：每个命令和变量之间一定要前后加空格，否则会提示command找不到
	if [ ! -z $pid ]; then
		kill -9 $pid
	fi
	start
	echo "....................Restart successfully！..........................."
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