package com.dlink.exception;

/**
 * SqlException
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class SqlException extends RuntimeException {

    public SqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlException(String message) {
        super(message);
    }
}