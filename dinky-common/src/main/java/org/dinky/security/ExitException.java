package org.dinky.security;

public class ExitException extends SecurityException {
    private static final long serialVersionUID = 1L;
    public final int status;

    public ExitException(int status) {
        this.status = status;
    }
}
