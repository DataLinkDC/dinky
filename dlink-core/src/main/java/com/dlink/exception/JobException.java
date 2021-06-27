package com.dlink.exception;

import org.apache.flink.annotation.PublicEvolving;

/**
 * JobException
 *
 * @author wenmo
 * @since 2021/6/27
 **/
@PublicEvolving
public class JobException extends RuntimeException {
    public JobException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobException(String message) {
        super(message);
    }
}