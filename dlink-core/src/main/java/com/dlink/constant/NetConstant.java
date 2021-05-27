package com.dlink.constant;

public interface NetConstant {
    /**
     * http://
     */
    String HTTP = "http://";
    /**
     * 冒号:
     */
    String COLON = ":";
    /**
     * 斜杠/
     */
    String SLASH = "/";
    /**
     * Flink默认端口
     */
    String PORT = "8081";
    /**
     * 连接运行服务器超时时间  1000
     */
    Integer SERVER_TIME_OUT_ACTIVE = 1000;
    /**
     * 连接FLINK历史服务器超时时间  2000
     */
    Integer SERVER_TIME_OUT_HISTORY = 3000;
}
