package com.dlink.alert.email.exception;

/**
 * @Author: zhumingye
 * @date: 2022/4/3
 * @Description: 告警邮件异常类
 */

 public class AlertEmailException extends RuntimeException {
    public AlertEmailException(String errMsg) {
        super(errMsg);
    }

    public AlertEmailException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }
}
