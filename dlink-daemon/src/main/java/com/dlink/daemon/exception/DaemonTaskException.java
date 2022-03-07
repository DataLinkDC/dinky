package com.dlink.daemon.exception;

public class DaemonTaskException extends RuntimeException {

    public DaemonTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaemonTaskException(String message) {
        super(message);
    }
}
