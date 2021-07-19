package com.dlink.exception;


/**
 * JobException
 *
 * @author wenmo
 * @since 2021/6/27
 **/
public class JobException extends RuntimeException {

    public JobException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobException(String message) {
        super(message);
    }
}