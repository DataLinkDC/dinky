package com.dlink.gateway.exception;

/**
 * GatewayException
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public class GatewayException extends RuntimeException {

    public GatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayException(String message) {
        super(message);
    }
}