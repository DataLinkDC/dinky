package com.dlink.exception;

/**
 * FlinkClientException
 *
 * @author wenmo
 * @since 2022/4/12 21:21
 **/
public class FlinkClientException extends RuntimeException {

    public FlinkClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlinkClientException(String message) {
        super(message);
    }

}
