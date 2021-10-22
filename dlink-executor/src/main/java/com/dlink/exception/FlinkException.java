package com.dlink.exception;

/**
 * FlinkException
 *
 * @author wenmo
 * @since 2021/10/22 11:13
 **/
public class FlinkException extends RuntimeException {

    public FlinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlinkException(String message) {
        super(message);
    }
}
