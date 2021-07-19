package com.dlink.exception;


/**
 * JobException
 *
 * @author wenmo
 * @since 2021/6/27
 **/
public class MetaDataException extends RuntimeException {

    public MetaDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetaDataException(String message) {
        super(message);
    }
}