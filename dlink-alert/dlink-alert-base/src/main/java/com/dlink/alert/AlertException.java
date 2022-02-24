package com.dlink.alert;

/**
 * AlertException
 *
 * @author wenmo
 * @since 2022/2/23 19:19
 **/
public class AlertException extends RuntimeException {

    public AlertException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlertException(String message) {
        super(message);
    }
}
