package com.dlink.exception;

import org.apache.flink.annotation.PublicEvolving;

/**
 * SqlException
 *
 * @author wenmo
 * @since 2021/6/22
 **/
@PublicEvolving
public class SqlException extends RuntimeException {
    public SqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlException(String message) {
        super(message);
    }
}