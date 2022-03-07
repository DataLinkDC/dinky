package com.dlink.daemon.constant;

public interface FlinkTaskConstant {

    /**
     * 检测停顿时间
     */
    int TIME_SLEEP = 1000;

    /**
     * 启动线程轮询日志时间，用于设置work等信息
     */
    int MAX_POLLING_GAP = 1000;
    /**
     * 最小
     */
    int MIN_POLLING_GAP = 50;

}
