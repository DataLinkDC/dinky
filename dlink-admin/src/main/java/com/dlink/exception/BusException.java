package com.dlink.exception;

/**
 * BusException
 *
 * @author wenmo
 * @since 2021/5/28 14:21
 **/
public class BusException extends RuntimeException {

    private static final long serialVersionUID = -2955156471454043812L;

    public BusException(String message) {
        super(message);
    }
}
